package com.thevoxelbox.voxelsniper.brush.property;

/**
 * Main user BrushPattern types for internal usage.
 * {@code BrushPatternType.PATTERN} might not support all brushes such as
 * {@link com.thevoxelbox.voxelsniper.brush.type.SpiralStaircaseBrush}.
 *
 * @since TODO
 */
public enum BrushPatternType {

    ANY,
    PATTERN,
    SINGLE_BLOCK

}
