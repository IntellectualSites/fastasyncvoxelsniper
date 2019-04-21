package com.thevoxelbox.voxelsniper;

import java.util.logging.Logger;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit extension point.
 */
public class VoxelSniperPlugin extends JavaPlugin {

	private VoxelSniperConfig voxelSniperConfig;
	private BrushRegistry brushRegistry;
	private SniperManager sniperManager;

	@Override
	public void onEnable() {
		this.voxelSniperConfig = loadConfig();
		this.brushRegistry = loadBrushRegistry();
		this.sniperManager = new SniperManager(this);
		loadCommands();
		loadListeners();
	}

	private VoxelSniperConfig loadConfig() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		return new VoxelSniperConfig(config);
	}

	private BrushRegistry loadBrushRegistry() {
		BrushRegistry brushRegistry = new BrushRegistry();
		BrushRegistrar brushRegistrar = new BrushRegistrar(brushRegistry);
		brushRegistrar.registerBrushes();
		Logger logger = getLogger();
		logger.info("Registered " + brushRegistry.getBrushesCount() + " brushes with " + brushRegistry.getHandlesCount() + " handles.");
		return brushRegistry;
	}

	private void loadCommands() {
		CommandRegistry commandRegistry = new CommandRegistry(this);
		CommandRegistrar commandRegistrar = new CommandRegistrar(this, commandRegistry);
		commandRegistrar.registerCommands();
	}

	private void loadListeners() {
		VoxelSniperListener listener = new VoxelSniperListener(this);
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(listener, this);
		Logger logger = getLogger();
		logger.info("Registered Sniper Listener.");
	}

	/**
	 * Returns object for accessing global VoxelSniper options.
	 *
	 * @return {@link VoxelSniperConfig} object for accessing global VoxelSniper options.
	 */
	public VoxelSniperConfig getVoxelSniperConfig() {
		return this.voxelSniperConfig;
	}

	/**
	 * Returns {@link BrushRegistry} for current instance.
	 *
	 * @return Brush Manager for current instance.
	 */
	public BrushRegistry getBrushRegistry() {
		return this.brushRegistry;
	}

	/**
	 * Returns {@link com.thevoxelbox.voxelsniper.SniperManager} for current instance.
	 *
	 * @return SniperManager
	 */
	public SniperManager getSniperManager() {
		return this.sniperManager;
	}
}
