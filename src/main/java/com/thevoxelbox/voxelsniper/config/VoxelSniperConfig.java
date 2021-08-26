package com.thevoxelbox.voxelsniper.config;

import com.sk89q.worldedit.world.block.BlockType;

import java.util.List;

public class VoxelSniperConfig {

    private final boolean messageOnLoginEnabled;
    private final BlockType defaultBlockMaterial;
    private final BlockType defaultReplaceBlockMaterial;
    private final int defaultBrushSize;
    private final int litesniperMaxBrushSize;
    private final List<BlockType> litesniperRestrictedMaterials;
    private final int brushSizeWarningThreshold;
    private final int defaultVoxelHeight;
    private final int defaultCylinderCenter;

    public VoxelSniperConfig(
            boolean messageOnLoginEnabled,
            BlockType defaultBlockMaterial,
            BlockType defaultReplaceBlockMaterial,
            int defaultBrushSize,
            int litesniperMaxBrushSize,
            List<BlockType> litesniperRestrictedMaterials,
            int brushSizeWarningThreshold,
            int defaultVoxelHeight,
            int defaultCylinderCenter
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

}
