package com.thevoxelbox.voxelsniper.util.material;

import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;

public final class Materials {

    private Materials() {
        throw new UnsupportedOperationException("Cannot create instance of this class");
    }

    public static boolean isEmpty(BlockType blockType) {
        return MaterialSets.AIRS.contains(blockType);
    }

    public static boolean isLiquid(BlockType blockType) {
        return MaterialSets.LIQUIDS.contains(blockType);
    }

    /**
     * Return if the given block type belongs to slabs.
     *
     * @param blockType the block type to check
     * @return {@code true} if block type is a slab, {@code false} otherwise.
     * @since 2.5.0
     */
    public static boolean isSlab(BlockType blockType) {
        return BlockCategories.SLABS.contains(blockType);
    }

    /**
     * Return if the given block type belongs to stairs.
     *
     * @param blockType the block type to check
     * @return {@code true} if block type is a stair, {@code false} otherwise.
     * @since 2.5.0
     */
    public static boolean isStair(BlockType blockType) {
        return BlockCategories.STAIRS.contains(blockType);
    }

}
