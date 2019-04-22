package com.thevoxelbox.voxelsniper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfigLoader;
import com.thevoxelbox.voxelsniper.listener.PlayerInteractListener;
import com.thevoxelbox.voxelsniper.listener.PlayerJoinListener;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit extension point.
 */
public class VoxelSniperPlugin extends JavaPlugin {

	private VoxelSniperConfig voxelSniperConfig;
	private BrushRegistry brushRegistry;
	private SniperRegistry sniperRegistry;

	@Override
	public void onEnable() {
		this.voxelSniperConfig = loadConfig();
		this.brushRegistry = loadBrushRegistry();
		this.sniperRegistry = new SniperRegistry();
		loadCommands();
		loadListeners();
	}

	private VoxelSniperConfig loadConfig() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		VoxelSniperConfigLoader voxelSniperConfigLoader = new VoxelSniperConfigLoader(config);
		int undoCacheSize = voxelSniperConfigLoader.getUndoCacheSize();
		boolean messageOnLoginEnabled = voxelSniperConfigLoader.isMessageOnLoginEnabled();
		int litesniperMaxBrushSize = voxelSniperConfigLoader.getLitesniperMaxBrushSize();
		List<Material> litesniperRestrictedMaterials = voxelSniperConfigLoader.getLitesniperRestrictedMaterials()
			.stream()
			.map(Material::matchMaterial)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		return new VoxelSniperConfig(undoCacheSize, messageOnLoginEnabled, litesniperMaxBrushSize, litesniperRestrictedMaterials);
	}

	private BrushRegistry loadBrushRegistry() {
		BrushRegistry brushRegistry = new BrushRegistry();
		BrushRegistrar brushRegistrar = new BrushRegistrar(brushRegistry);
		brushRegistrar.registerBrushes();
		return brushRegistry;
	}

	private void loadCommands() {
		CommandRegistry commandRegistry = new CommandRegistry(this);
		CommandRegistrar commandRegistrar = new CommandRegistrar(this, commandRegistry);
		commandRegistrar.registerCommands();
	}

	private void loadListeners() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new PlayerJoinListener(this), this);
		pluginManager.registerEvents(new PlayerInteractListener(this), this);
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
	 * Returns {@link SniperRegistry} for current instance.
	 *
	 * @return SniperRegistry
	 */
	public SniperRegistry getSniperRegistry() {
		return this.sniperRegistry;
	}
}
