package com.thevoxelbox.voxelsniper;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.command.PatternParser;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfigLoader;
import com.thevoxelbox.voxelsniper.listener.PlayerInteractListener;
import com.thevoxelbox.voxelsniper.listener.PlayerJoinListener;
import com.thevoxelbox.voxelsniper.listener.PlayerQuitListener;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.util.io.VoxelSniperResourceLoader;
import io.papermc.lib.PaperLib;

import java.net.URI;

import org.apache.logging.log4j.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.serverlib.ServerLib;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;

public class VoxelSniperPlugin extends JavaPlugin {

    private static final long HOURS_TO_TICKS = 72000L;

    private static final Logger LOGGER = LogManagerCompat.getLogger();
    public static VoxelSniperPlugin plugin;
    public static String newVersionTitle = "";
    public static boolean hasUpdate;
    public static boolean updateCheckFailed;
    private static String currentVersionTitle = "";
    private VoxelSniperConfig voxelSniperConfig;
    private final LazyReference<TranslationManager> translationManager =
            LazyReference.from(() -> new TranslationManager(
                    new VoxelSniperResourceLoader(this))
            );
    private BrushRegistry brushRegistry;
    private PerformerRegistry performerRegistry;
    private SniperRegistry sniperRegistry;
    private PatternParser patternParser;
    private double newVersion = 0;
    private double currentVersion = 0;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @SuppressWarnings({"unused", "deprecation"}) // Paper deprecation
    @Override
    public void onEnable() {
        plugin = this;
        this.voxelSniperConfig = loadConfig();
        this.brushRegistry = loadBrushRegistry();
        this.performerRegistry = loadPerformerRegistry();
        this.sniperRegistry = new SniperRegistry();
        this.patternParser = new PatternParser(WorldEdit.getInstance());
        testRegistries();
        loadCommands();
        loadListeners();
        new FastAsyncVoxelSniper(this);
        // Enable metrics
        Metrics metrics = new Metrics(this, 6405);
        // Check if we are in a safe environment
        ServerLib.checkUnsafeForks();
        ServerLib.isJavaSixteen();
        PaperLib.suggestPaper(this);
        currentVersionTitle = getDescription().getVersion().split("-")[0];
        currentVersion = Double.parseDouble(currentVersionTitle.replaceFirst("\\.", ""));
        // Don't register task if update check is unwanted
        if (!voxelSniperConfig.isUpdateCheckerEnabled()) {
            return;
        }
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                newVersion = updateCheck(currentVersion);
                if (Double.isNaN(newVersion)) {
                    updateCheckFailed = true;
                } else if (newVersion > currentVersion) {
                    hasUpdate = true;
                    LOGGER.info("An update for FastAsyncVoxelSniper is available.");
                    LOGGER.info("You are running version {}, the latest version is {}", currentVersionTitle, newVersionTitle);
                    LOGGER.info("Update at https://dev.bukkit.org/projects/favs/");
                } else if (currentVersion == newVersion) {
                    LOGGER.info("Your version is up to date.");
                } else if (currentVersion > newVersion) {
                    LOGGER.info(
                            "You are using a snapshot release or a custom version. This is not a stable release found on https://dev.bukkit.org/projects/favs/.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, voxelSniperConfig.getUpdateCheckerInterval() * HOURS_TO_TICKS);
    }

    private VoxelSniperConfig loadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        VoxelSniperConfigLoader voxelSniperConfigLoader = new VoxelSniperConfigLoader(this, config);

        return new VoxelSniperConfig(
                voxelSniperConfigLoader.isUpdateCheckerEnabled(),
                voxelSniperConfigLoader.getUpdateCheckerInterval(),
                voxelSniperConfigLoader.isMessageOnLoginEnabled(),
                voxelSniperConfigLoader.arePersistentSessionsEnabled(),
                voxelSniperConfigLoader.getDefaultBlockMaterial(),
                voxelSniperConfigLoader.getDefaultReplaceBlockMaterial(),
                voxelSniperConfigLoader.getDefaultBrushSize(),
                voxelSniperConfigLoader.getLitesniperMaxBrushSize(),
                voxelSniperConfigLoader.getLitesniperRestrictedMaterials(),
                voxelSniperConfigLoader.getBrushSizeWarningThreshold(),
                voxelSniperConfigLoader.getDefaultVoxelHeight(),
                voxelSniperConfigLoader.getDefaultCylinderCenter(),
                voxelSniperConfigLoader.getBrushProperties()
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
        pluginManager.registerEvents(new PlayerQuitListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
    }

    public void reload() {
        this.reloadConfig();
        this.voxelSniperConfig = loadConfig();
        testRegistries();
    }

    private void testRegistries() {
        // Load brushes and performers to ensure their configuration is up-to-date.
        this.brushRegistry.getBrushesProperties().keySet().stream().sorted().forEachOrdered(key -> {
            BrushProperties properties = this.brushRegistry.getBrushProperties(key);
            Brush brush = properties.getCreator().create();
            brush.setProperties(properties);
            brush.loadProperties();
        });
        this.performerRegistry.getPerformerProperties().keySet().stream().sorted().forEachOrdered(key -> {
            PerformerProperties properties = this.performerRegistry.getPerformerProperties(key);
            Performer performer = properties.getCreator().create();
            performer.setProperties(properties);
            performer.loadProperties();
        });
    }

    /**
     * Return the voxel sniper config.
     *
     * @return the voxel sniper config
     */
    public VoxelSniperConfig getVoxelSniperConfig() {
        return this.voxelSniperConfig;
    }

    /**
     * Return the translation manager.
     *
     * @return the translation manager
     * @since 2.7.0
     */
    public TranslationManager getTranslationManager() {
        return translationManager.getValue();
    }

    /**
     * Return the brush registry.
     *
     * @return the brush registry
     */
    public BrushRegistry getBrushRegistry() {
        return this.brushRegistry;
    }

    /**
     * Return performer registry.
     *
     * @return the performer registry
     */
    public PerformerRegistry getPerformerRegistry() {
        return this.performerRegistry;
    }

    /**
     * Return the sniper registry.
     *
     * @return the sniper registry
     */
    public SniperRegistry getSniperRegistry() {
        return this.sniperRegistry;
    }

    /**
     * Return the pattern parser.
     *
     * @return the pattern parser
     * @since 2.6.0
     */
    public PatternParser getPatternParser() {
        return patternParser;
    }

    // Borrowed from Vault
    private double updateCheck(double currentVersion) {
        try {

            URI uri = URI.create("https://api.curseforge.com/servermods/files?projectids=454430");
            URLConnection conn = uri.toURL().openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "FastAsyncVoxelSniper Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0) {
                LOGGER.warn("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            newVersionTitle =
                    ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).replace("FastAsyncVoxelSniper", "").trim();
            return Double.parseDouble(newVersionTitle.replaceFirst("\\.", "").trim());
        } catch (IOException ignored) {
            LOGGER.error("Unable to connect to api.curseforge.com to check for updates. Improper firewall configuration?");
        }
        return Double.NaN;
    }

}
