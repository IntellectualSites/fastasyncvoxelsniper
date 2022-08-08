package com.thevoxelbox.voxelsniper.config;

import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;

import java.util.List;
import java.util.Map;

public class VoxelSniperConfig {

    private static final VoxelSniperPlugin plugin = VoxelSniperPlugin.plugin;

    private final boolean updateNotificationsEnabled;
    private final boolean messageOnLoginEnabled;
    private final boolean persistSessionsOnLogout;
    private final BlockType defaultBlockMaterial;
    private final BlockType defaultReplaceBlockMaterial;
    private final int defaultBrushSize;
    private final int litesniperMaxBrushSize;
    private final List<String> litesniperRestrictedMaterials;
    private final int brushSizeWarningThreshold;
    private final int defaultVoxelHeight;
    private final int defaultCylinderCenter;
    private final Map<String, Map<String, Object>> brushProperties;


    /**
     * Create a new cached voxel configuration, used runtime.
     *
     * @param updateNotificationsEnabled    notify whether updates are available or not
     * @param messageOnLoginEnabled         if message on login is enabled
     * @param persistSessionsOnLogout       if snipers shall be removed on logout
     * @param defaultBlockMaterial          default block material
     * @param defaultReplaceBlockMaterial   default replace block material
     * @param defaultBrushSize              default brush size
     * @param litesniperMaxBrushSize        litesniper max brush size
     * @param litesniperRestrictedMaterials litesniper restricted materials
     * @param brushSizeWarningThreshold     brush size warning threshold
     * @param defaultVoxelHeight            default voxel height
     * @param defaultCylinderCenter         default cylinder center
     * @param brushProperties               brush properties
     */
    public VoxelSniperConfig(
            boolean updateNotificationsEnabled,
            boolean messageOnLoginEnabled,
            boolean persistSessionsOnLogout,
            BlockType defaultBlockMaterial,
            BlockType defaultReplaceBlockMaterial,
            int defaultBrushSize,
            int litesniperMaxBrushSize,
            List<String> litesniperRestrictedMaterials,
            int brushSizeWarningThreshold,
            int defaultVoxelHeight,
            int defaultCylinderCenter,
            Map<String, Map<String, Object>> brushProperties
    ) {
        this.updateNotificationsEnabled = updateNotificationsEnabled;
        this.messageOnLoginEnabled = messageOnLoginEnabled;
        this.persistSessionsOnLogout = persistSessionsOnLogout;
        this.defaultBlockMaterial = defaultBlockMaterial;
        this.defaultReplaceBlockMaterial = defaultReplaceBlockMaterial;
        this.defaultBrushSize = defaultBrushSize;
        this.litesniperMaxBrushSize = litesniperMaxBrushSize;
        this.litesniperRestrictedMaterials = litesniperRestrictedMaterials;
        this.brushSizeWarningThreshold = brushSizeWarningThreshold;
        this.defaultVoxelHeight = defaultVoxelHeight;
        this.defaultCylinderCenter = defaultCylinderCenter;
        this.brushProperties = brushProperties;
    }

    /**
     * Return if update notifications are enabled.
     *
     * @return {@code true} if notifications for updates are enabled, {@code false} otherwise.
     * @see VoxelSniperConfigLoader#areUpdateNotificationsEnabled()
     * @since 2.3.0
     */
    public boolean areUpdateNotificationsEnabled() {
        return this.updateNotificationsEnabled;
    }

    /**
     * Return if the login message is enabled.
     *
     * @return {@code true} if message on login is enabled, {@code false} otherwise.
     * @see VoxelSniperConfigLoader#isMessageOnLoginEnabled()
     */
    public boolean isMessageOnLoginEnabled() {
        return this.messageOnLoginEnabled;
    }

    /**
     * Return if persistent sessions are enabled.
     *
     * @return {@code true} if persistent session are enabled, {@code false} otherwise.
     * @see VoxelSniperConfigLoader#arePersistentSessionsEnabled()
     */
    public boolean arePersistentSessionsEnabled() {
        return this.persistSessionsOnLogout;
    }

    /**
     * Return default block type
     *
     * @return default type
     * @see VoxelSniperConfigLoader#getDefaultBlockMaterial()
     */
    public BlockType getDefaultBlockMaterial() {
        return defaultBlockMaterial;
    }

    /**
     * Return default replace block type.
     *
     * @return default type
     * @see VoxelSniperConfigLoader#getDefaultReplaceBlockMaterial()
     */
    public BlockType getDefaultReplaceBlockMaterial() {
        return defaultReplaceBlockMaterial;
    }

    /**
     * Return default brush size.
     *
     * @return default size
     * @see VoxelSniperConfigLoader#getDefaultBrushSize()
     */
    public int getDefaultBrushSize() {
        return defaultBrushSize;
    }

    /**
     * Return maximum size of brushes that LiteSnipers can use.
     *
     * @return maximum size
     * @see VoxelSniperConfigLoader#getLitesniperMaxBrushSize()
     */
    public int getLitesniperMaxBrushSize() {
        return this.litesniperMaxBrushSize;
    }

    /**
     * Return List of restricted Litesniper materials.
     *
     * @return List of restricted Litesniper materials
     * @see VoxelSniperConfigLoader#getLitesniperRestrictedMaterials()
     */
    public List<String> getLitesniperRestrictedMaterials() {
        return this.litesniperRestrictedMaterials;
    }

    /**
     * Gets brush size warning threshold.
     *
     * @return the brush size warning threshold
     * @see VoxelSniperConfigLoader#getBrushSizeWarningThreshold()
     */
    public int getBrushSizeWarningThreshold() {
        return brushSizeWarningThreshold;
    }

    /**
     * Return default voxel height.
     *
     * @return default height
     * @see VoxelSniperConfigLoader#getDefaultVoxelHeight()
     */
    public int getDefaultVoxelHeight() {
        return defaultVoxelHeight;
    }

    /**
     * Return default cylinder center.
     *
     * @return default center
     * @see VoxelSniperConfigLoader#getDefaultCylinderCenter()
     */
    public int getDefaultCylinderCenter() {
        return defaultCylinderCenter;
    }

    /**
     * Return brush properties.
     * This Map stores another Map (associating Property -> Value) per brush.
     *
     * @return brush properties
     * @see VoxelSniperConfigLoader#getBrushProperties()
     */
    public Map<String, Map<String, Object>> getBrushProperties() {
        return brushProperties;
    }

    /**
     * Force saving a brush property and its value to config.
     * Used to register missing or fix wrong values.
     *
     * @param brush       brush
     * @param propertyKey property key
     * @param value       value
     */
    public void saveBrushPropertyToConfig(String brush, String propertyKey, Object value) {
        plugin.getConfig().set(VoxelSniperConfigLoader.BRUSH_PROPERTIES + "." + brush + "." + propertyKey, value);
        plugin.saveConfig();
    }

}
