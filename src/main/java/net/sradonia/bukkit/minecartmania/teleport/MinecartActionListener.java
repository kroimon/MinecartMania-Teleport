package net.sradonia.bukkit.minecartmania.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.afforess.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.minecartmaniacore.event.MinecartManiaListener;
import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.utils.MinecartUtils;

public class MinecartActionListener extends MinecartManiaListener {
	private final MinecartManiaTeleport plugin;

	public MinecartActionListener(MinecartManiaTeleport plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (event.isActionTaken())
			return;
		Block blockAhead = event.getMinecart().getBlockTypeAhead();
		if (blockAhead != null) {
			Material type = blockAhead.getType();
			if (type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
				// Minecart is going to crash into a sign...

				Location signLocation = blockAhead.getLocation();
				Teleporter teleporter = plugin.getTeleporters().search(signLocation);
				if (teleporter != null) {
					// ... which is a teleporter!

					MinecartManiaMinecart minecart = event.getMinecart();

					// If there is a passenger: check his permission!
					if (minecart.hasPlayerPassenger()) {
						Player player = minecart.getPlayerPassenger();
						if (!plugin.hasPermission(player, "minecartmania.teleport.use")) {
							player.sendMessage("You are not allowed to use a teleporter!");
							return;
						}
					}

					// We can now try to teleport the minecart
					WorldNameLocation targetLocation = teleporter.getOther(signLocation);
					if (targetLocation == null) {
						// a) but we're missing the second waypoint!
						if (minecart.hasPlayerPassenger())
							minecart.getPlayerPassenger().sendMessage("You just crashed into an unconnected teleporter sign ;-)");
					} else if (targetLocation.getWorld() == null) {
						// b) but the target world isn't loaded!
						if (minecart.hasPlayerPassenger())
							minecart.getPlayerPassenger().sendMessage(
									"The target world '" + targetLocation.getWorldName() + "' is currently not loaded.");
					} else {
						// c) and we have a lift-off!
						teleportMinecart(minecart, targetLocation);
						event.setActionTaken(true);
					}
				}
			}
		}
	}

	private void teleportMinecart(MinecartManiaMinecart minecart, Location targetLocation) {
		// search for minecart tracks around the target waypoint
		Location trackLocation = findTrackAround(targetLocation);
		if (trackLocation == null) {
			if (minecart.hasPlayerPassenger())
				minecart.getPlayerPassenger().sendMessage("Couldn't find tracks at target sign.");
			return;
		}

		// check it's speed and calculate new velocity
		final Minecart cart = minecart.minecart;
		double speed = cart.getVelocity().length();
		if (targetLocation.getX() > trackLocation.getX())
			cart.setVelocity(new Vector(-speed, 0, 0));
		else if (targetLocation.getX() < trackLocation.getX())
			cart.setVelocity(new Vector(speed, 0, 0));
		else if (targetLocation.getZ() > trackLocation.getZ())
			cart.setVelocity(new Vector(0, 0, -speed));
		else if (targetLocation.getZ() < trackLocation.getZ())
			cart.setVelocity(new Vector(0, 0, speed));

		// teleport minecart...
		minecart.teleport(trackLocation);
	}

	public static Location findTrackAround(Location center) {
		Block centerBlock = center.getBlock();

		Block block;
		block = centerBlock.getRelative(BlockFace.NORTH);
		if (MinecartUtils.isTrack(block))
			return block.getLocation();
		block = centerBlock.getRelative(BlockFace.SOUTH);
		if (MinecartUtils.isTrack(block))
			return block.getLocation();
		block = centerBlock.getRelative(BlockFace.EAST);
		if (MinecartUtils.isTrack(block))
			return block.getLocation();
		block = centerBlock.getRelative(BlockFace.WEST);
		if (MinecartUtils.isTrack(block))
			return block.getLocation();

		return null;
	}
}
