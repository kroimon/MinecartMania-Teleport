package net.sradonia.bukkit.minecartmania.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRightClickEvent;

public class SignBlockListener extends BlockListener {
	private final TeleporterList teleporters;

	public SignBlockListener(TeleporterList teleporters) {
		this.teleporters = teleporters;
	}

	private boolean isSignEvent(BlockEvent event) {
		Material blockType = event.getBlock().getType();
		return blockType.equals(Material.SIGN_POST) || blockType.equals(Material.WALL_SIGN);
	}

	@Override
	public void onBlockRightClick(BlockRightClickEvent event) {
		if (isSignEvent(event)) {
			Location location = event.getBlock().getLocation();
			Player player = event.getPlayer();

			if (teleporters.search(location) != null) {
				player.sendMessage("Yepp, this is a teleporter sign!");
				Location trackLocation = MinecartActionListener.findTrackAround(location);
				if (trackLocation == null)
					player.sendMessage("But it doesn't have any tracks around!");
			} else {
				// Not (yet) a teleporter... Create one?
				Sign sign = (Sign) event.getBlock().getState();

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
					if (name == null) {
						player.sendMessage("Your minecart teleporter needs a name!");
					} else {
						Teleporter teleporter = teleporters.get(name);
						
						if (teleporter == null) {
							// first sign
							teleporters.put(name, new Teleporter(name, location));
							sign.setLine(magicLine, "[CartTeleport]");
							sign.update();
							player.sendMessage("Created first sign for teleporter '" + name + "'.");
							player.sendMessage("Now go create a second sign!");
						} else if (!teleporter.isComplete()) {
							// second sign
							teleporter.add(location);
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

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (isSignEvent(event)) {
			Location location = event.getBlock().getLocation();
			Player player = event.getPlayer();

			Teleporter teleporter = teleporters.search(location);
			if (teleporter != null) {
				// destroyed a teleporter

				teleporter.remove(location);
				if (!teleporter.isEmpty()) {
					teleporters.trySave();
					player.sendMessage("Destroyed one sign of teleporter '" + teleporter.getName() + "'! The other is still around!");
				} else {
					teleporters.remove(teleporter);
					player.sendMessage("Destroyed last sign of teleporter '" + teleporter.getName() + "'!");
				}
			}
		}
	}

	@Override
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (isSignEvent(event)) {
			// changed block around the sign
			if (teleporters.search(event.getBlock().getLocation()) != null) {
				// teleporter sign - prevent breakage!
				event.setCancelled(true);
			}
        }
	}

}
