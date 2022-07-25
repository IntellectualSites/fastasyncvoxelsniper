package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.painter.Painters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlendVoxelBrush extends AbstractBlendBrush {

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.blend-voxel.info"));
            return;
        }
        super.handleCommand(parameters, snipe);
    }

    @Override
    public void blend(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int cubeEdge = 2 * brushSize + 1;
        BlockVector3 targetBlock = getTargetBlock();
        int smallCubeVolume = MathHelper.cube(cubeEdge);
        Set<BlockVector3> smallCube = new HashSet<>(smallCubeVolume);
        Map<BlockVector3, BlockType> smallCubeBlockTypes = new HashMap<>(smallCubeVolume);
        Painters.cube()
                .center(targetBlock)
                .radius(brushSize)
                .blockSetter(position -> {
                    BlockType type = getBlockType(position);
                    smallCube.add(position);
                    smallCubeBlockTypes.put(position, type);
                })
                .paint();
        for (BlockVector3 smallCubeBlock : smallCube) {
            Map<BlockType, Integer> blockTypesFrequencies = new HashMap<>();
            Painters.cube()
                    .center(smallCubeBlock)
                    .radius(1)
                    .blockSetter(position -> {
                        if (position.equals(smallCubeBlock)) {
                            return;
                        }
                        BlockType type = getBlockType(position);
                        blockTypesFrequencies.merge(type, 1, Integer::sum);
                    })
                    .paint();
            CommonMaterial commonMaterial = findCommonMaterial(blockTypesFrequencies);
            BlockType type = commonMaterial.getBlockType();
            if (type != null) {
                smallCubeBlockTypes.put(smallCubeBlock, type);
            }
        }
        setBlocks(smallCubeBlockTypes);
    }

}
