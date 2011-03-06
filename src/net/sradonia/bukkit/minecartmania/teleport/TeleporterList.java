package net.sradonia.bukkit.minecartmania.teleport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class TeleporterList {
	private static final Logger log = Logger.getLogger("Minecraft");

	private final Server server;
	private final File teleporterFile;
	private final Map<String, Teleporter> teleporters = new ConcurrentHashMap<String, Teleporter>();

	public TeleporterList(Server server, File teleporterFile) {
		this.server = server;
		this.teleporterFile = teleporterFile;
	}

	public int load() throws IOException {
		int loadedLocations = 0;
		BufferedReader file = new BufferedReader(new FileReader(teleporterFile));

		String line;
		while ((line = file.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("#"))
				continue;

			// split on "=", not on "\="
			String[] split = line.split("(?<!\\\\)=");
			if (split.length == 2) {
				String name = split[0].replace("\\#", "#").replace("\\=", "=");
				String[] values = split[1].split(",");

				Teleporter teleporter = loadTeleporter(name, values);
				if (teleporter != null) {
					teleporters.put(name, teleporter);
					loadedLocations += teleporter.isComplete() ? 2 : 1;
				}
			}
		}

		file.close();

		return loadedLocations;
	}

	private Teleporter loadTeleporter(String name, String[] values) {
		Location firstLocation = null, secondLocation = null;
		// first sign location
		if (values.length >= 4) {
			World world = server.getWorld(values[0].trim());
			if (world != null) {
				try {
					int x = Integer.valueOf(values[1].trim());
					int y = Integer.valueOf(values[2].trim());
					int z = Integer.valueOf(values[3].trim());
					firstLocation = new Location(world, x, y, z);
				} catch (NumberFormatException e) {
				}
			}
		}
		// second sign location
		if (values.length >= 8) {
			World world = server.getWorld(values[4].trim());
			if (world != null) {
				try {
					int x = Integer.valueOf(values[5].trim());
					int y = Integer.valueOf(values[6].trim());
					int z = Integer.valueOf(values[7].trim());
					secondLocation = new Location(world, x, y, z);
				} catch (NumberFormatException e) {
				}
			}
		}
		// build teleporter
		if (firstLocation != null || secondLocation != null)
			return new Teleporter(name, firstLocation, secondLocation);
		else
			return null;
	}

	public void trySave() {
		try {
			save();
		} catch (IOException e) {

			log.severe("Could not save teleporters to file: " + e.getMessage());
		}
	}

	public void save() throws IOException {
		BufferedWriter file = new BufferedWriter(new FileWriter(teleporterFile, false));

		file.append("#name=world1,x1,y1,z1,world2,x2,y2,z2");
		file.newLine();

		for (Teleporter teleporter : teleporters.values()) {
			String name = teleporter.getName().replace("#", "\\#").replace("=", "\\=");
			file.append(name).append('=');

			Location location = teleporter.getFirst();
			if (location != null) {
				file.append(location.getWorld().getName()).append(',');
				file.append(String.valueOf(location.getBlockX())).append(',');
				file.append(String.valueOf(location.getBlockY())).append(',');
				file.append(String.valueOf(location.getBlockZ())).append(',');
			}
			location = teleporter.getSecond();
			if (location != null) {
				file.append(location.getWorld().getName()).append(',');
				file.append(String.valueOf(location.getBlockX())).append(',');
				file.append(String.valueOf(location.getBlockY())).append(',');
				file.append(String.valueOf(location.getBlockZ()));
			}

			file.newLine();
		}

		file.close();
	}

	public Teleporter search(Location location) {
		for (Teleporter teleporter : teleporters.values()) {
			if (teleporter.contains(location))
				return teleporter;
		}
		return null;
	}

	public Teleporter get(String name) {
		return teleporters.get(name);
	}

	public void put(String name, Teleporter teleporter) {
		teleporters.put(name, teleporter);
		trySave();
	}

	public void remove(Teleporter teleporter) {
		teleporters.remove(teleporter.getName());
		trySave();
	}
}
