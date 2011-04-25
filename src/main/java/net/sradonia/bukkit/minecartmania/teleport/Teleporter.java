package net.sradonia.bukkit.minecartmania.teleport;

import org.bukkit.Location;

/**
 * This class represents a pair of {@link WorldNameLocation}s representing a
 * teleporter.
 * 
 * Warning: The crappy <code>a != null && a.equals(b)</code> statements all over
 * this file are by intention as we have to work around Bukkit's faulty
 * {@link Location#equals(Object)} method.
 */
public class Teleporter {

	private String name;
	private WorldNameLocation first;
	private WorldNameLocation second;

	public Teleporter(String name, WorldNameLocation first) {
		this.name = name;
		this.first = first;
	}

	public Teleporter(String name, WorldNameLocation first, WorldNameLocation second) {
		this(name, first);
		this.second = second;
	}

	public String getName() {
		return name;
	}

	public WorldNameLocation getFirst() {
		return first;
	}

	public WorldNameLocation getSecond() {
		return second;
	}

	public WorldNameLocation getOther(Location location) {
		if (first != null && first.equals(location))
			return second;
		else if (second != null && second.equals(location))
			return first;
		else
			throw new IllegalArgumentException("This location is not part of this teleporter!");
	}

	public void add(WorldNameLocation location) {
		if (first == null)
			first = location;
		else if (second == null)
			second = location;
		else
			throw new IllegalStateException("Both locations are already set!");
	}

	public boolean remove(Location location) {
		if (first != null && first.equals(location)) {
			first = null;
			return true;
		} else if (second != null && second.equals(location)) {
			second = null;
			return true;
		} else
			return false;
	}

	public boolean isEmpty() {
		return (first == null) && (second == null);
	}

	public boolean isComplete() {
		return (first != null) && (second != null);
	}

	public boolean contains(Location location) {
		return (first != null && first.equals(location)) || (second != null && second.equals(location));
	}
}
