package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TreeSnipeBrush extends AbstractBrush {

    private static final TreeGenerator.TreeType DEFAULT_TREE_TYPE = TreeGenerator.TreeType.TREE;

    private static final List<String> TREES = new ArrayList<>(TreeGenerator.TreeType.getPrimaryAliases());

    private TreeGenerator.TreeType treeType;

    @Override
    public void loadProperties() {
        this.treeType = (TreeGenerator.TreeType) getEnumProperty("default-tree-type", TreeGenerator.TreeType.class,
                DEFAULT_TREE_TYPE
        );
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.tree-sniper.inf"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                            List.of(TreeGenerator.TreeType.values()),
                            (type, type2) -> type.lookupKeys.get(0).compareTo(type2.lookupKeys.get(0)),
                            type -> TextComponent.of(type.lookupKeys.get(0)),
                            type -> type,
                            this.treeType,
                            "voxelsniper.brush.tree-sniper"
                    ));
                } else {
                    TreeGenerator.TreeType treeType = TreeGenerator.TreeType.lookup(firstParameter);

                    if (treeType != null) {
                        this.treeType = treeType;
                        messenger.sendMessage(Caption.of("voxelsniper.brush.tree-sniper.set-tree", this.treeType.getName()));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.brush.tree-sniper.invalid-tree", firstParameter));
                    }
                }
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.concat(
                    TREES.stream(),
                    Stream.of("list")
            ), parameter, 0);
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
        setBlock(targetBlock.getX(), targetBlock.getY() - 1, targetBlock.getZ(), BlockTypes.GRASS_BLOCK);
        if (!generateTree(targetBlock, this.treeType)) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.brush.tree-sniper.generate-failed"));
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
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of("voxelsniper.brush.tree-sniper.set-tree", this.treeType.getName()))
                .send();
    }

}
