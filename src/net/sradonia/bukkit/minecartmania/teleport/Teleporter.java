package net.sradonia.bukkit.minecartmania.teleport;

import org.bukkit.Location;

public class Teleporter {

	private String name;
	private Location first;
	private Location second;

	public Teleporter(String name, Location first) {
		this.name = name;
		this.first = first;
	}

	public Teleporter(String name, Location first, Location second) {
		this(name, first);
		this.second = second;
	}

	public String getName() {
		return name;
	}

	public Location getFirst() {
		return first;
	}

	public Location getSecond() {
		return second;
	}

	public Location getOther(Location location) {
		if (location.equals(first))
			return second;
		else if (location.equals(second))
			return first;
		else
			throw new IllegalArgumentException("This location is not part of this teleporter!");
	}

	public void add(Location location) {
		if (first == null)
			first = location;
		else if (second == null)
			second = location;
		else
			throw new IllegalStateException("Both locations are already set!");
	}

	public boolean remove(Location location) {
		if (location.equals(first)) {
			first = null;
			return true;
		} else if (location.equals(second)) {
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
		return location.equals(first) || location.equals(second);
	}

}
