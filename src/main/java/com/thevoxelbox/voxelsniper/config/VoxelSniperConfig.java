package com.thevoxelbox.voxelsniper.config;

import com.sk89q.worldedit.world.block.BlockType;

import java.util.List;

public class VoxelSniperConfig {

    private final boolean messageOnLoginEnabled;
    private final int litesniperMaxBrushSize;
    private final List<BlockType> litesniperRestrictedMaterials;

    public VoxelSniperConfig(
            boolean messageOnLoginEnabled,
            int litesniperMaxBrushSize,
            List<BlockType> litesniperRestrictedMaterials
    ) {
        this.messageOnLoginEnabled = messageOnLoginEnabled;
        this.litesniperMaxBrushSize = litesniperMaxBrushSize;
        this.litesniperRestrictedMaterials = litesniperRestrictedMaterials;
    }

    public boolean isMessageOnLoginEnabled() {
        return this.messageOnLoginEnabled;
    }

    public int getLitesniperMaxBrushSize() {
        return this.litesniperMaxBrushSize;
    }

    public List<BlockType> getLitesniperRestrictedMaterials() {
        return this.litesniperRestrictedMaterials;
    }

}
