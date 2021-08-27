package com.thevoxelbox.voxelsniper.config;

import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;

import java.util.List;
import java.util.Map;

public class VoxelSniperConfig {

    private static final VoxelSniperPlugin plugin = VoxelSniperPlugin.plugin;

    private final boolean messageOnLoginEnabled;
    private final BlockType defaultBlockMaterial;
    private final BlockType defaultReplaceBlockMaterial;
    private final int defaultBrushSize;
    private final int litesniperMaxBrushSize;
    private final List<BlockType> litesniperRestrictedMaterials;
    private final int brushSizeWarningThreshold;
    private final int defaultVoxelHeight;
    private final int defaultCylinderCenter;
    private final Map<String, Map<String, Object>> brushProperties;

    public VoxelSniperConfig(
            boolean messageOnLoginEnabled,
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

    public boolean isMessageOnLoginEnabled() {
        return this.messageOnLoginEnabled;
    }

    public BlockType getDefaultBlockMaterial() {
        return defaultBlockMaterial;
    }

    public BlockType getDefaultReplaceBlockMaterial() {
        return defaultReplaceBlockMaterial;
    }

    public int getDefaultBrushSize() {
        return defaultBrushSize;
    }

    public int getLitesniperMaxBrushSize() {
        return this.litesniperMaxBrushSize;
    }

    public List<BlockType> getLitesniperRestrictedMaterials() {
        return this.litesniperRestrictedMaterials;
    }

    public int getBrushSizeWarningThreshold() {
        return brushSizeWarningThreshold;
    }

    public int getDefaultVoxelHeight() {
        return defaultVoxelHeight;
    }

    public int getDefaultCylinderCenter() {
        return defaultCylinderCenter;
    }

    public Map<String, Map<String, Object>> getBrushProperties() {
        return brushProperties;
    }

    public void saveBrushPropertyToConfig(String brush, String propertyKey, Object value) {
        plugin.getConfig().set(VoxelSniperConfigLoader.BRUSH_PROPERTIES + "." + brush + "." + propertyKey, value);
        plugin.saveConfig();
    }

}
