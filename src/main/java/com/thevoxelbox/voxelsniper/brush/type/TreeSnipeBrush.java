package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeSnipeBrush extends AbstractBrush {

    private TreeGenerator.TreeType treeType = TreeGenerator.TreeType.TREE;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.isEmpty()) {
                continue;
            }
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
                messenger.sendMessage(ChatColor.AQUA + "/b t treetype");
                printTreeType(messenger);
                return;
            }
            try {
                this.treeType = TreeGenerator.TreeType.valueOf(parameter.toUpperCase());
                printTreeType(messenger);
            } catch (IllegalArgumentException exception) {
                messenger.sendMessage(ChatColor.LIGHT_PURPLE + "No such tree type.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock().add(0, getYOffset(), 0);
        single(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        single(snipe, getTargetBlock());
    }

    private void single(Snipe snipe, BlockVector3 targetBlock) {
        BlockState currentBlockData = getBlock(targetBlock.getX(), targetBlock.getY() - 1, targetBlock.getZ());
        setBlockType(targetBlock.getX(), targetBlock.getY() - 1, targetBlock.getZ(), BlockTypes.GRASS_BLOCK);
        SnipeMessenger messenger = snipe.createMessenger();
        if (!generateTree(this.treeType, targetBlock)) {
            messenger.sendMessage(ChatColor.RED + "Failed to generate a tree!");
        }
        setBlockData(targetBlock.getX(), targetBlock.getY() - 1, targetBlock.getZ(), currentBlockData);
    }

    private int getYOffset() {
        BlockVector3 targetBlock = getTargetBlock();
        EditSession editSession = getEditSession();
        return IntStream.range(1, (editSession.getMaxY() - targetBlock.getY()))
                .filter(i -> Materials.isEmpty(getBlockType(targetBlock.add(0, i + 1, 0))))
                .findFirst()
                .orElse(0);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        printTreeType(messenger);
        // TODO Remove once fixed
        messenger.sendMessage(ChatColor.GOLD + "Warning: This brush is currently not undo-able due to a server bug!");
    }

    private void printTreeType(SnipeMessenger messenger) {
        String printout = Arrays.stream(TreeGenerator.TreeType.values())
                .map(treeType -> ((treeType == this.treeType) ? ChatColor.GRAY + treeType.name()
                        .toLowerCase() : ChatColor.DARK_GRAY + treeType.name()
                        .toLowerCase()) + ChatColor.WHITE)
                .collect(Collectors.joining(", "));
        messenger.sendMessage(printout);
    }

}
