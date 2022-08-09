package com.thevoxelbox.voxelsniper.config;

import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Configuration storage defining global configurations for VoxelSniper.
 */
public class VoxelSniperConfigLoader {

    protected static final String BRUSH_PROPERTIES = "brush-properties";
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static final String CONFIG_VERSION = "config-version";
    private static final String UPDATE_NOTIFICATIONS_ENABLED = "update-notifications";
    private static final String MESSAGE_ON_LOGIN_ENABLED = "message-on-login-enabled";
    private static final String PERSIST_SESSIONS_ON_LOGOUT = "persist-sessions-on-logout";
    private static final String DEFAULT_BLOCK_MATERIAL = "default-block-material";
    private static final String DEFAULT_REPLACE_BLOCK_MATERIAL = "default-replace-block-material";
    private static final String DEFAULT_BRUSH_SIZE = "default-brush-size";
    private static final String LITESNIPER_MAX_BRUSH_SIZE = "litesniper-max-brush-size";
    private static final String LITESNIPER_RESTRICTED_MATERIALS = "litesniper-restricted-materials";
    private static final String BRUSH_SIZE_WARNING_THRESHOLD = "brush-size-warning-threshold";
    private static final String DEFAULT_VOXEL_HEIGHT = "default-voxel-height";
    private static final String DEFAULT_CYLINDER_CENTER = "default-cylinder-center";
    private static final int CONFIG_VERSION_VALUE = 5;
    private static final boolean DEFAULT_UPDATE_NOTIFICATIONS_ENABLED = true;
    private static final boolean DEFAULT_MESSAGE_ON_LOGIN_ENABLED = true;
    private static final boolean DEFAULT_PERSIST_SESSIONS_ON_LOGOUT = true;
    private static final BlockType DEFAULT_BLOCK_MATERIAL_VALUE = BlockTypes.AIR;
    private static final BlockType DEFAULT_REPLACE_BLOCK_MATERIAL_VALUE = BlockTypes.AIR;
    private static final int DEFAULT_BRUSH_SIZE_VALUE = 3;
    private static final int DEFAULT_LITESNIPER_MAX_BRUSH_SIZE = 5;
    private static final List<String> DEFAULT_LITESNIPER_RESTRICTED_MATERIALS = Stream.of(
                    BlockTypes.BARRIER,
                    BlockTypes.BEDROCK
            ).filter(Objects::nonNull)
            .map(BlockType::getResource)
            .toList();
    private static final int DEFAULT_BRUSH_SIZE_WARNING_THRESHOLD = 100;
    private static final int DEFAULT_VOXEL_HEIGHT_VALUE = 1;
    private static final int DEFAULT_CYLINDER_CENTER_VALUE = 0;
    private static final Map<String, Map<String, Object>> DEFAULT_BRUSH_PROPERTIES = null;

    private final VoxelSniperPlugin plugin;
    private final FileConfiguration config;

    /**
     * Create a new cached voxel configuration loader.
     *
     * @param plugin the plugin instance
     * @param config the configuration that is going to be used.
     */
    public VoxelSniperConfigLoader(VoxelSniperPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        updateConfig();
    }

    /**
     * Update config settings the current config version and apply changes.
     */
    @SuppressWarnings("unchecked")
    private void updateConfig() {
        int currentConfigVersion = getConfigVersion();
        if (currentConfigVersion != CONFIG_VERSION_VALUE) {
            LOGGER.warn("Invalid config file found! Trying to apply required changes...");
            setConfigVersion(CONFIG_VERSION_VALUE);

            if (currentConfigVersion < 1) {
                Map<String, Object> oldEntries = new HashMap<>();
                Stream.of(MESSAGE_ON_LOGIN_ENABLED, LITESNIPER_MAX_BRUSH_SIZE, LITESNIPER_RESTRICTED_MATERIALS)
                        .forEach(key -> {
                            if (key.equals(LITESNIPER_RESTRICTED_MATERIALS)) {
                                oldEntries.put(key, getLitesniperRestrictedMaterials());
                            } else {
                                oldEntries.put(key, this.config.get(key));
                            }
                            this.config.set(key, null);
                        });

                setMessageOnLoginEnabled((boolean) oldEntries.getOrDefault(
                        MESSAGE_ON_LOGIN_ENABLED,
                        DEFAULT_MESSAGE_ON_LOGIN_ENABLED
                ));
                setDefaultBlockMaterial(getDefaultReplaceBlockMaterial());
                setDefaultReplaceBlockMaterial(getDefaultReplaceBlockMaterial());
                setDefaultBrushSize(getDefaultBrushSize());
                setLitesniperMaxBrushSize((int) oldEntries.getOrDefault(
                        LITESNIPER_MAX_BRUSH_SIZE,
                        DEFAULT_LITESNIPER_MAX_BRUSH_SIZE
                ));
                setLitesniperRestrictedMaterials((List<String>) oldEntries.getOrDefault(
                        LITESNIPER_RESTRICTED_MATERIALS,
                        DEFAULT_LITESNIPER_RESTRICTED_MATERIALS
                ));
                setBrushSizeWarningThreshold(getDefaultBrushSize());
                setDefaultVoxelHeight(getDefaultVoxelHeight());
                setDefaultCylinderCenter(getDefaultCylinderCenter());
                setBrushProperties(getBrushProperties());
            }
            if (currentConfigVersion < 2) {
                setPersistentSessions(arePersistentSessionsEnabled());
            }
            if (currentConfigVersion < 3) {
                enableUpdateNotifications(areUpdateNotificationsEnabled());
            }
            if (currentConfigVersion < 4) {
                Map<String, Map<String, Object>> brushProperties = getBrushProperties();
                Map<String, Object> jaggedLineBrush = brushProperties.get("Jagged Line");
                if (jaggedLineBrush == null) {
                    return;
                }
                Object oldDefaultRecursion = jaggedLineBrush.remove("defaut-recursion");
                Object oldDefaultSpread = jaggedLineBrush.remove("defaut-spread");
                if (oldDefaultRecursion != null) {
                    this.removeBrushProperty("Jagged Line", "defaut-recursion");
                    jaggedLineBrush.put("default-recursion", oldDefaultRecursion);
                }
                if (oldDefaultSpread != null) {
                    this.removeBrushProperty("Jagged Line", "defaut-spread");
                    jaggedLineBrush.put("default-spread", oldDefaultSpread);
                }
                setBrushProperties(brushProperties);
            }
            if (currentConfigVersion < 5) {
                setBrushProperty("Stencil", "default-max-area-volume", 5000000);
                setBrushProperty("Stencil", "default-max-save-volume", 50000);
            }

            plugin.saveConfig();
            LOGGER.info("Your config file is now up-to-date! (v{})", CONFIG_VERSION_VALUE);
        }
    }

    /**
     * Return current config version.
     *
     * @return current version
     */
    protected int getConfigVersion() {
        return this.config.getInt(CONFIG_VERSION, 0);
    }

    /**
     * Set current config version.
     *
     * @param version new version
     */
    protected void setConfigVersion(int version) {
        this.config.set(CONFIG_VERSION, version);
    }

    /**
     * Return if update notifications are enabled.
     *
     * @return {@code true} if notifications for updates are enabled, {@code false} otherwise.
     * @since 2.3.0
     */
    public boolean areUpdateNotificationsEnabled() {
        return this.config.getBoolean(UPDATE_NOTIFICATIONS_ENABLED, DEFAULT_UPDATE_NOTIFICATIONS_ENABLED);
    }

    /**
     * Set the update notifier be enabled or disabled.
     *
     * @param enabled Update notifications enabled
     * @since 2.3.0
     */
    protected void enableUpdateNotifications(boolean enabled) {
        this.config.set(UPDATE_NOTIFICATIONS_ENABLED, enabled);
    }

    /**
     * Return if the login message is enabled.
     *
     * @return {@code true} if message on login is enabled, {@code false} otherwise.
     */
    public boolean isMessageOnLoginEnabled() {
        return this.config.getBoolean(MESSAGE_ON_LOGIN_ENABLED, DEFAULT_MESSAGE_ON_LOGIN_ENABLED);
    }

    /**
     * Set the message on login to be enabled or disabled.
     *
     * @param enabled Messages on Login enabled
     */
    protected void setMessageOnLoginEnabled(boolean enabled) {
        this.config.set(MESSAGE_ON_LOGIN_ENABLED, enabled);
    }

    /**
     * Return if persistent sessions are enabled.
     *
     * @return {@code true} if persistent session are enabled, {@code false} otherwise.
     */
    public boolean arePersistentSessionsEnabled() {
        return this.config.getBoolean(PERSIST_SESSIONS_ON_LOGOUT, DEFAULT_PERSIST_SESSIONS_ON_LOGOUT);
    }

    /**
     * Set option for sniping sessions to be persisted on logout or not.
     *
     * @param enabled Save sniping session upon logout
     */
    protected void setPersistentSessions(boolean enabled) {
        this.config.set(PERSIST_SESSIONS_ON_LOGOUT, enabled);
    }

    /**
     * Return default block type.
     *
     * @return default type
     */
    public BlockType getDefaultBlockMaterial() {
        String type = this.config.getString(DEFAULT_BLOCK_MATERIAL);
        if (type != null) {
            BlockType blockType = BlockTypes.get(type.toLowerCase(Locale.ROOT));
            return blockType == null ? DEFAULT_BLOCK_MATERIAL_VALUE : blockType;
        } else {
            return DEFAULT_BLOCK_MATERIAL_VALUE;
        }
    }

    /**
     * Set default block type.
     *
     * @param blockType default type
     */
    protected void setDefaultBlockMaterial(BlockType blockType) {
        this.config.set(DEFAULT_BLOCK_MATERIAL, blockType.getId());
    }

    /**
     * Return default replace block type.
     *
     * @return default type
     */
    public BlockType getDefaultReplaceBlockMaterial() {
        String type = this.config.getString(DEFAULT_REPLACE_BLOCK_MATERIAL);
        if (type != null) {
            BlockType blockType = BlockTypes.get(type.toLowerCase(Locale.ROOT));
            return blockType == null ? DEFAULT_REPLACE_BLOCK_MATERIAL_VALUE : blockType;
        } else {
            return DEFAULT_REPLACE_BLOCK_MATERIAL_VALUE;
        }
    }

    /**
     * Set default replace block type.
     *
     * @param blockType default type
     */
    protected void setDefaultReplaceBlockMaterial(BlockType blockType) {
        this.config.set(DEFAULT_REPLACE_BLOCK_MATERIAL, blockType.getId());
    }

    /**
     * Return default brush size.
     *
     * @return default size
     */
    public int getDefaultBrushSize() {
        return this.config.getInt(DEFAULT_BRUSH_SIZE, DEFAULT_BRUSH_SIZE_VALUE);
    }

    /**
     * Set default brush size.
     *
     * @param size default size
     */
    protected void setDefaultBrushSize(int size) {
        this.config.set(DEFAULT_BRUSH_SIZE, size);
    }

    /**
     * Return maximum size of brushes that LiteSnipers can use.
     *
     * @return maximum size
     */
    public int getLitesniperMaxBrushSize() {
        return this.config.getInt(LITESNIPER_MAX_BRUSH_SIZE, DEFAULT_LITESNIPER_MAX_BRUSH_SIZE);
    }

    /**
     * Set maximum size of brushes that LiteSnipers can use.
     *
     * @param size maximum size
     */
    protected void setLitesniperMaxBrushSize(int size) {
        this.config.set(LITESNIPER_MAX_BRUSH_SIZE, size);
    }

    /**
     * Return List of restricted Litesniper materials.
     *
     * @return List of restricted Litesniper materials
     */
    public List<String> getLitesniperRestrictedMaterials() {
        return this.config.getStringList(LITESNIPER_RESTRICTED_MATERIALS).stream()
                .map(key -> key.toLowerCase(Locale.ROOT))
                .map(key -> key.startsWith(Identifiers.MINECRAFT_IDENTIFIER)
                        ? key.substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)
                        : key)
                .toList();
    }

    /**
     * Set new list of restricted Litesniper materials.
     *
     * @param restrictedMaterials List of restricted Litesniper materials
     */
    protected void setLitesniperRestrictedMaterials(List<String> restrictedMaterials) {
        this.config.set(LITESNIPER_RESTRICTED_MATERIALS, new ArrayList<>(restrictedMaterials));
    }

    /**
     * Return maximum reasonable brush size before sending a warning.
     *
     * @return maximum size
     */
    public int getBrushSizeWarningThreshold() {
        return this.config.getInt(BRUSH_SIZE_WARNING_THRESHOLD, DEFAULT_BRUSH_SIZE_WARNING_THRESHOLD);
    }

    /**
     * Set maximum reasonable brush size before sending a warning.
     *
     * @param size maximum size
     */
    protected void setBrushSizeWarningThreshold(int size) {
        this.config.set(BRUSH_SIZE_WARNING_THRESHOLD, size);
    }

    /**
     * Return default voxel height.
     *
     * @return default height
     */
    public int getDefaultVoxelHeight() {
        return this.config.getInt(DEFAULT_VOXEL_HEIGHT, DEFAULT_VOXEL_HEIGHT_VALUE);
    }

    /**
     * Set default voxel height.
     *
     * @param height default height
     */
    protected void setDefaultVoxelHeight(int height) {
        this.config.set(DEFAULT_VOXEL_HEIGHT, height);
    }

    /**
     * Return default cylinder center.
     *
     * @return default center
     */
    public int getDefaultCylinderCenter() {
        return this.config.getInt(DEFAULT_CYLINDER_CENTER, DEFAULT_CYLINDER_CENTER_VALUE);
    }

    /**
     * Set default cylinder center.
     *
     * @param center default center
     */
    protected void setDefaultCylinderCenter(int center) {
        this.config.set(DEFAULT_CYLINDER_CENTER, center);
    }

    /**
     * Return brush properties.
     * This Map stores another Map (associating Property -> Value) per brush.
     *
     * @return brush properties
     */
    public Map<String, Map<String, Object>> getBrushProperties() {
        Map<String, Map<String, Object>> brushesMap = new HashMap<>();

        ConfigurationSection brushesSection = config.getConfigurationSection(BRUSH_PROPERTIES);
        if (brushesSection == null) {
            return DEFAULT_BRUSH_PROPERTIES;
        }

        for (String brush : brushesSection.getKeys(false)) {
            Map<String, Object> propertiesMap = new HashMap<>();

            ConfigurationSection propertiesSection = config.getConfigurationSection(BRUSH_PROPERTIES + "." + brush);
            if (propertiesSection == null) {
                continue;
            }

            for (String property : propertiesSection.getKeys(false)) {
                propertiesMap.put(property, this.config.get(BRUSH_PROPERTIES + "." + brush + "." + property));
            }

            brushesMap.put(brush, propertiesMap);
        }

        return brushesMap;
    }

    /**
     * Set brush properties.
     *
     * @param brushProperties brush properties
     */
    protected void setBrushProperties(Map<String, Map<String, Object>> brushProperties) {
        for (Map.Entry<String, Map<String, Object>> brushesEntry : brushProperties.entrySet()) {
            String brush = brushesEntry.getKey();

            for (Map.Entry<String, Object> propertiesEntry : brushesEntry.getValue().entrySet()) {
                String propertyKey = propertiesEntry.getKey();
                Object propertyValue = propertiesEntry.getValue();

                this.config.set(BRUSH_PROPERTIES + "." + brush + "." + propertyKey, propertyValue);
            }
        }
    }

    /**
     * Set a brush property from config.
     *
     * @param brush       brush
     * @param value       value
     * @param propertyKey propertyKey
     */
    protected void setBrushProperty(String brush, String propertyKey, Object value) {
        this.config.set(BRUSH_PROPERTIES + "." + brush + "." + propertyKey, value);
    }

    /**
     * Remove a brush property from config.
     *
     * @param brush       brush
     * @param propertyKey propertyKey
     */
    protected void removeBrushProperty(String brush, String propertyKey) {
        this.config.set(BRUSH_PROPERTIES + "." + brush + "." + propertyKey, null);
    }

}
