package com.thevoxelbox.voxelsniper.brush.property;

import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import org.jetbrains.annotations.Nullable;

/**
 * A BrushPattern based on a WE pattern and a name.
 *
 * @since TODO
 */
public class BrushPattern {

    private final Pattern pattern;
    private final String name;

    /**
     * Create a new brush pattern.
     *
     * @param pattern the pattern
     * @param name    the name
     */
    public BrushPattern(Pattern pattern, String name) {
        this.pattern = pattern;
        this.name = name;
    }

    /**
     * Create a new brush pattern from a block type.
     *
     * @param blockType the block type
     */
    public BrushPattern(BlockType blockType) {
        this(blockType, blockType.getId());
    }

    /**
     * Create a new brush pattern from a block state.
     *
     * @param blockState the block state
     */
    public BrushPattern(BlockState blockState) {
        this(blockState, blockState.getAsString());
    }

    /**
     * Gets pattern.
     *
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Return the name:
     * - command argument for complex patterns
     * - block id for block types
     * - block data for block states
     *
     * @return the pattern name
     */
    public String getName() {
        return name;
    }

    /**
     * Try to parse the pattern as a block type.
     *
     * @return the potential corresponding block type
     */
    public @Nullable BlockType asBlockType() {
        if (this.pattern instanceof BaseBlock baseBlock) {
            return baseBlock.getBlockType();
        }
        if (this.pattern instanceof BlockType blockType) {
            return blockType;
        }
        if (this.pattern instanceof BlockState blockState) {
            return blockState.getBlockType();
        }
        return null;
    }

    /**
     * Try to parse the pattern as a block state.
     *
     * @return the potential corresponding block state
     */
    public @Nullable BlockState asBlockState() {
        if (this.pattern instanceof BaseBlock baseBlock) {
            return baseBlock.toBlockState();
        }
        if (this.pattern instanceof BlockState blockState) {
            return blockState;
        }
        if (this.pattern instanceof BlockType blockType) {
            return blockType.getDefaultState();
        }
        return null;
    }

}
