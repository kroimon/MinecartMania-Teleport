package net.sradonia.bukkit.minecartmania.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldNameLocation extends Location {

	private String worldName;

	public WorldNameLocation(Location location) {
		super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public WorldNameLocation(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public WorldNameLocation(World world, double x, double y, double z, float yaw, float pitch) {
		super(world, x, y, z, yaw, pitch);
	}

	public WorldNameLocation(String worldName, double x, double y, double z) {
		super(null, x, y, z);
		this.worldName = worldName;
	}

	public WorldNameLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
		super(null, x, y, z, yaw, pitch);
		this.worldName = worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
		setWorld(null);
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		worldName = null;
	}

	public String getWorldName() {
		World world = getWorld();
		return (world == null) ? worldName : world.getName();
	}

	@Override
	public World getWorld() {
		World world = super.getWorld();
		if (world == null) {
			world = Bukkit.getServer().getWorld(worldName);
			setWorld(world);
		}
		return world;
	}
}
