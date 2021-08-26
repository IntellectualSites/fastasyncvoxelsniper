package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfigLoader;
import com.thevoxelbox.voxelsniper.listener.PlayerInteractListener;
import com.thevoxelbox.voxelsniper.listener.PlayerJoinListener;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import io.papermc.lib.PaperLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.serverlib.ServerLib;

import java.io.File;

public class VoxelSniperPlugin extends JavaPlugin {

    public static VoxelSniperPlugin plugin;
    private VoxelSniperConfig voxelSniperConfig;
    private BrushRegistry brushRegistry;
    private PerformerRegistry performerRegistry;
    private SniperRegistry sniperRegistry;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.voxelSniperConfig = loadConfig();
        this.brushRegistry = loadBrushRegistry();
        this.performerRegistry = loadPerformerRegistry();
        this.sniperRegistry = new SniperRegistry();
        loadCommands();
        loadListeners();
        new FastAsyncVoxelSniper(this);
        // Enable metrics
        Metrics metrics = new Metrics(this, 6405);
        // Check if we are in a safe environment
        ServerLib.checkUnsafeForks();
        ServerLib.checkJavaLTS();
        PaperLib.suggestPaper(this);
    }

    private VoxelSniperConfig loadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        VoxelSniperConfigLoader voxelSniperConfigLoader = new VoxelSniperConfigLoader(config);

        return new VoxelSniperConfig(
                voxelSniperConfigLoader.isMessageOnLoginEnabled(),
                voxelSniperConfigLoader.getDefaultBlockMaterial(),
                voxelSniperConfigLoader.getDefaultReplaceBlockMaterial(),
                voxelSniperConfigLoader.getDefaultBrushSize(),
                voxelSniperConfigLoader.getLitesniperMaxBrushSize(),
                voxelSniperConfigLoader.getLitesniperRestrictedMaterials(),
                voxelSniperConfigLoader.getBrushSizeWarningThreshold(),
                voxelSniperConfigLoader.getDefaultVoxelHeight(),
                voxelSniperConfigLoader.getDefaultCylinderCenter()
        );
    }

    private BrushRegistry loadBrushRegistry() {
        BrushRegistry brushRegistry = new BrushRegistry();
        File dataFolder = getDataFolder();
        BrushRegistrar brushRegistrar = new BrushRegistrar(brushRegistry, dataFolder);
        brushRegistrar.registerBrushes();
        return brushRegistry;
    }

    private PerformerRegistry loadPerformerRegistry() {
        PerformerRegistry performerRegistry = new PerformerRegistry();
        PerformerRegistrar performerRegistrar = new PerformerRegistrar(performerRegistry);
        performerRegistrar.registerPerformers();
        return performerRegistry;
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

    public void reload() {
        this.reloadConfig();
        this.voxelSniperConfig = loadConfig();
    }

    public VoxelSniperConfig getVoxelSniperConfig() {
        return this.voxelSniperConfig;
    }

    public BrushRegistry getBrushRegistry() {
        return this.brushRegistry;
    }

    public PerformerRegistry getPerformerRegistry() {
        return this.performerRegistry;
    }

    public SniperRegistry getSniperRegistry() {
        return this.sniperRegistry;
    }

}
