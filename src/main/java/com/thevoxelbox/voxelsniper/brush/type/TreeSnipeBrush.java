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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TreeSnipeBrush extends AbstractBrush {

    private static final List<String> TREES = new ArrayList<>(TreeGenerator.TreeType.getPrimaryAliases());

    private TreeGenerator.TreeType treeType = TreeGenerator.TreeType.TREE;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];
        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
            messenger.sendMessage(ChatColor.AQUA + "/b t tree_type");
            messenger.sendMessage(ChatColor.GOLD + "Currently selected tree type: " + ChatColor.DARK_GREEN + this.treeType.getName());
        } else {
            String treeName = parameters[0];
            TreeGenerator.TreeType treeType = TreeGenerator.TreeType.lookup(treeName);
            if (this.treeType == null) {
                this.treeType = treeType;
                messenger.sendMessage(ChatColor.GOLD + "Currently selected tree type: " + ChatColor.DARK_GREEN + this.treeType.getName());
            } else {
                messenger.sendMessage(ChatColor.LIGHT_PURPLE + "No such tree type.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("treetype"), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (firstParameter.equalsIgnoreCase("treetype")) {
                String parameter = parameters[1];
                return super.sortCompletions(TREES.stream(), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
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
        if (!generateTree(targetBlock, this.treeType)) {
            SnipeMessenger messenger = snipe.createMessenger();
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
        messenger.sendMessage(ChatColor.GOLD + "Currently selected tree type: " + ChatColor.DARK_GREEN + this.treeType.getName());
    }

}
