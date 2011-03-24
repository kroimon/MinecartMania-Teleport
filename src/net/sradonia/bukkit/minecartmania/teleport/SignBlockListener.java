package net.sradonia.bukkit.minecartmania.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

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
