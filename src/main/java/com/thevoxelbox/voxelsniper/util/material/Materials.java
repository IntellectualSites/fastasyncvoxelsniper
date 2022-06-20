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
     * @since 2.4.1
     */
    public static boolean isSlab(BlockType blockType) {
        return BlockCategories.SLABS.contains(blockType);
    }

    /**
     * @since 2.4.1
     */
    public static boolean isStair(BlockType blockType) {
        return BlockCategories.STAIRS.contains(blockType);
    }

}
