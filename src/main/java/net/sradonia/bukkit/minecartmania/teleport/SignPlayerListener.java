package net.sradonia.bukkit.minecartmania.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class SignPlayerListener extends PlayerListener {
	private final MinecartManiaTeleport plugin;
	private final TeleporterList teleporters;

	public SignPlayerListener(MinecartManiaTeleport plugin, TeleporterList teleporters) {
		this.plugin = plugin;
		this.teleporters = teleporters;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled() || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.hasBlock())
			return;

		Material blockType = event.getClickedBlock().getType();
		if (blockType.equals(Material.SIGN_POST) || blockType.equals(Material.WALL_SIGN)) {
			Location location = event.getClickedBlock().getLocation();
			Player player = event.getPlayer();

			if (teleporters.search(location) != null) {
				player.sendMessage("Yepp, this is a teleporter sign!");
				Location trackLocation = MinecartActionListener.findTrackAround(location);
				if (trackLocation == null)
					player.sendMessage("But it doesn't have any tracks around!");
			} else {
				// Not (yet) a teleporter... Create one?
				Sign sign = (Sign) event.getClickedBlock().getState();

				// Exact line numbering doesn't matter - line order is important!
				String magic = null, name = null;
				int magicLine = -1;
				for (int i = 0; i < 4; i++) {
					String line = sign.getLine(i).trim();
					if (line.length() == 0)
						continue;
					if (magic == null) {
						magic = line.toLowerCase();
						magicLine = i;
					} else if (name == null) {
						name = line;
					}
				}

				if (magic != null && magic.contains("cart") && magic.contains("teleport")) {
					// found cart teleporter!
					if (!plugin.hasPermission(player, "minecartmania.teleport.create")) {
						player.sendMessage("You are not allowed to create a teleporter!");
					} else if (name == null) {
						player.sendMessage("Your minecart teleporter needs a name!");
					} else {
						Teleporter teleporter = teleporters.get(name);

						if (teleporter == null) {
							// first sign
							teleporters.put(name, new Teleporter(name, new WorldNameLocation(location)));
							sign.setLine(magicLine, "[CartTeleport]");
							sign.update();
							player.sendMessage("Created first sign for teleporter '" + name + "'.");
							player.sendMessage("Now go create a second sign!");
						} else if (!teleporter.isComplete()) {
							// second sign
							teleporter.add(new WorldNameLocation(location));
							teleporters.trySave();
							sign.setLine(magicLine, "[CartTeleport]");
							sign.update();
							player.sendMessage("Teleporter '" + name + "' completed.");
							player.sendMessage("Connection established!");
						} else {
							// too much signs
							player.sendMessage("There are already two signs for teleporter '" + name + "'!");
							player.sendMessage("Please choose a different name!");
						}
					}
				}
			}
		}
	}
}
