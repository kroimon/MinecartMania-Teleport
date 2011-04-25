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

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Location))
			return false;
		final Location other = (Location) obj;

		final World world = this.getWorld();
		final World otherWorld = other.getWorld();
		if (world == null && otherWorld == null) {
			// check names
			if (other instanceof WorldNameLocation) {
				final String otherWorldName = ((WorldNameLocation) other).getWorldName();
				if (worldName != otherWorldName && (worldName == null || !worldName.equalsIgnoreCase(otherWorldName)))
					return false;
			} else if (worldName != null && worldName.length() > 0)
				return false;
		} else if (world != otherWorld && (world == null || !world.equals(otherWorld)))
			return false;

		if (Double.doubleToLongBits(this.getX()) != Double.doubleToLongBits(other.getX()))
			return false;
		if (Double.doubleToLongBits(this.getY()) != Double.doubleToLongBits(other.getY()))
			return false;
		if (Double.doubleToLongBits(this.getZ()) != Double.doubleToLongBits(other.getZ()))
			return false;
		if (Float.floatToIntBits(this.getPitch()) != Float.floatToIntBits(other.getPitch()))
			return false;
		if (Float.floatToIntBits(this.getYaw()) != Float.floatToIntBits(other.getYaw()))
			return false;
		return true;
	}
}
