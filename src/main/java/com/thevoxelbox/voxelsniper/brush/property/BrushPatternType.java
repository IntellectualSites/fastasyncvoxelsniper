package com.thevoxelbox.voxelsniper.brush.property;

/**
 * Main user BrushPattern types for internal usage.
 * {@code BrushPatternType.PATTERN} might not support all brushes such as
 * {@link com.thevoxelbox.voxelsniper.brush.type.SpiralStaircaseBrush}.
 *
 * @since TODO
 */
public enum BrushPatternType {

    ANY, // Ignores pattern input.
    PATTERN, // Any pattern (single blocks included).
    SINGLE_BLOCK // Only single blocks.

}
