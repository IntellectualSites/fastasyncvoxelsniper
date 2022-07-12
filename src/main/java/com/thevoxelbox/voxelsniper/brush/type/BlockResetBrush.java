package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;

public class BlockResetBrush extends AbstractBrush {

    private static final MaterialSet DENIED_UPDATES = MaterialSet.builder()
            .with(BlockCategories.DOORS)
            .with(BlockCategories.TRAPDOORS)
            .with(BlockCategories.SIGNS)
            .with(MaterialSets.CHESTS)
            .with(BlockCategories.FENCE_GATES)
            .add(BlockTypes.FURNACE)
            .add(BlockTypes.REDSTONE_TORCH)
            .add(BlockTypes.REDSTONE_WALL_TORCH)
            .add(BlockTypes.REDSTONE_WIRE)
            .add(BlockTypes.REPEATER)
            .add(BlockTypes.COMPARATOR)
            .build();

    @Override
    public void handleArrowAction(Snipe snipe) {
        applyBrush(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        applyBrush(snipe);
    }

    private void applyBrush(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        for (int z = -brushSize; z <= brushSize; z++) {
            for (int x = -brushSize; x <= brushSize; x++) {
                for (int y = -brushSize; y <= brushSize; y++) {
                    BlockVector3 targetBlock = getTargetBlock();
                    BlockType blockType = getBlockType(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
                    if (!DENIED_UPDATES.contains(blockType)) {
                        setBlockData(
                                targetBlock.getX() + x,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + z,
                                blockType.getDefaultState()
                        );
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .send();
    }

}
