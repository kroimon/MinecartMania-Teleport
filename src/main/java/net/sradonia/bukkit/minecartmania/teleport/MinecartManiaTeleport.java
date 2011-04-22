package net.sradonia.bukkit.minecartmania.teleport;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.afforess.minecartmaniacore.MinecartManiaCore;

public class MinecartManiaTeleport extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");

	private final static String TELEPORTERS_FILE = "Teleporters.dat";
	private TeleporterList teleporters;

	public void onEnable() {
		PluginDescriptionFile pdf = getDescription();
		PluginManager pluginManager = getServer().getPluginManager();

		// Get Minecart Mania Core plugin - should already be laoded by Bukkit
		Plugin minecartMania = pluginManager.getPlugin("MinecartManiaCore");
		if (minecartMania == null) {
			log.severe(pdf.getName() + " requires MinecartManiaCore to function! Disabled.");
			setEnabled(false);
			return;
		}

		// Load teleporters
		File teleporterFile = new File(MinecartManiaCore.dataDirectory, TELEPORTERS_FILE);
		teleporters = new TeleporterList(teleporterFile);

		if (teleporterFile.exists())
			try {
				int signCount = teleporters.load();
				log.info("[" + pdf.getName() + "] Successfully loaded " + signCount + " teleporter signs");
			} catch (IOException e) {
				log.severe("[" + pdf.getName() + "] Error loading existing teleporters: " + e.getMessage());
			}

		// Register listeners
		final SignBlockListener blockListener = new SignBlockListener(teleporters);
		pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
		pluginManager.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.High, this);
		final SignPlayerListener playerListener = new SignPlayerListener(teleporters);
		pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);

		final MinecartActionListener actionListener = new MinecartActionListener(this, teleporters);
		pluginManager.registerEvent(Event.Type.CUSTOM_EVENT, actionListener, Priority.Low, this);

		log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " enabled!" );
	}

	public void onDisable() {
		// nothing to do
	}

}
