package com.thevoxelbox.voxelsniper.brush.type.blend;

import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.painter.Painters;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequireToolkit
@Command(value = "brush|b blend_voxel_disc|blendvoxeldisc|bvd")
@Permission("voxelsniper.brush.blendvoxeldisc")
public class BlendVoxelDiscBrush extends AbstractBlendBrush {

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Command("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.blend-voxel-disc.info"));
    }

    @Command("water")
    public void onBrushWater(
            final @NotNull Snipe snipe
    ) {
        super.onBrushWaterCommand(snipe);
    }

    @Override
    public void blend(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int squareEdge = 2 * brushSize + 1;
        BlockVector3 targetBlock = getTargetBlock();
        int smallSquareArea = MathHelper.square(squareEdge);
        Set<BlockVector3> smallSquare = new HashSet<>(smallSquareArea);
        Map<BlockVector3, BlockType> smallSquareBlockTypes = new HashMap<>(smallSquareArea);
        Painters.square()
                .center(targetBlock)
                .radius(brushSize)
                .blockSetter(position -> {
                    BlockType type = getBlockType(position);
                    smallSquare.add(position);
                    smallSquareBlockTypes.put(position, type);
                })
                .paint();
        for (BlockVector3 smallSquareBlock : smallSquare) {
            Map<BlockType, Integer> blockTypesFrequencies = new HashMap<>();
            Painters.square()
                    .center(smallSquareBlock)
                    .radius(1)
                    .blockSetter(position -> {
                        if (position.equals(smallSquareBlock)) {
                            return;
                        }
                        BlockType type = getBlockType(position);
                        blockTypesFrequencies.merge(type, 1, Integer::sum);
                    })
                    .paint();
            CommonMaterial commonMaterial = findCommonMaterial(blockTypesFrequencies);
            BlockType type = commonMaterial.getBlockType();
            if (type != null) {
                smallSquareBlockTypes.put(smallSquareBlock, type);
            }
        }
        setBlocks(smallSquareBlockTypes);
    }

}
