package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b block_reset|blockreset|br")
@CommandPermission("voxelsniper.brush.blockreset")
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

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

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
                    BlockType blockType = getBlockType(targetBlock.x() + x, targetBlock.y() + y, targetBlock.z() + z);
                    if (!DENIED_UPDATES.contains(blockType)) {
                        setBlockData(
                                targetBlock.x() + x,
                                targetBlock.y() + y,
                                targetBlock.z() + z,
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
