package com.thevoxelbox.voxelsniper.config;

import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;

import java.util.List;
import java.util.Map;

public class VoxelSniperConfig {

    private static final VoxelSniperPlugin plugin = VoxelSniperPlugin.plugin;

    private final boolean messageOnLoginEnabled;
    private final boolean disableSnipingOnLogin;
    private final BlockType defaultBlockMaterial;
    private final BlockType defaultReplaceBlockMaterial;
    private final int defaultBrushSize;
    private final int litesniperMaxBrushSize;
    private final List<BlockType> litesniperRestrictedMaterials;
    private final int brushSizeWarningThreshold;
    private final int defaultVoxelHeight;
    private final int defaultCylinderCenter;
    private final Map<String, Map<String, Object>> brushProperties;


    /**
     * Create a new cached voxel configuration, used runtime.
     *
     * @param messageOnLoginEnabled         if message on login is enabled
     * @param disableSnipingOnLogin         if to disable sniping on login
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
            boolean messageOnLoginEnabled,
            boolean disableSnipingOnLogin,
            BlockType defaultBlockMaterial,
            BlockType defaultReplaceBlockMaterial,
            int defaultBrushSize,
            int litesniperMaxBrushSize,
            List<BlockType> litesniperRestrictedMaterials,
            int brushSizeWarningThreshold,
            int defaultVoxelHeight,
            int defaultCylinderCenter,
            Map<String, Map<String, Object>> brushProperties
    ) {
        this.messageOnLoginEnabled = messageOnLoginEnabled;
        this.disableSnipingOnLogin = disableSnipingOnLogin;
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
     * Return if the login message is enabled.
     *
     * @return {@code true} if message on login is enabled, {@code false} otherwise.
     * @see VoxelSniperConfigLoader#isMessageOnLoginEnabled()
     */
    public boolean isMessageOnLoginEnabled() {
        return this.messageOnLoginEnabled;
    }

    /**
     * Return when sniping shall be disabled on login.
     *
     * @return {@code true} if sniping shall be disabled on login, {@code false} otherwise.
     * @see VoxelSniperConfigLoader#isDisableSnipingOnLoginEnabled()
     */
    public boolean isDisableSnipingOnLoginEnabled() {
        return this.disableSnipingOnLogin;
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
    public List<BlockType> getLitesniperRestrictedMaterials() {
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
     * @param value       alue
     */
    public void saveBrushPropertyToConfig(String brush, String propertyKey, Object value) {
        plugin.getConfig().set(VoxelSniperConfigLoader.BRUSH_PROPERTIES + "." + brush + "." + propertyKey, value);
        plugin.saveConfig();
    }

}
