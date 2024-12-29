package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

@RequireToolkit
@CommandMethod(value = "brush|b tree_snipe|treesnipe|tree|t")
@CommandPermission("voxelsniper.brush.treesnipe")
public class TreeSnipeBrush extends AbstractBrush {

    private static final TreeGenerator.TreeType DEFAULT_TREE_TYPE = TreeGenerator.TreeType.TREE;

    private TreeGenerator.TreeType treeType;

    @Override
    public void loadProperties() {
        this.treeType = (TreeGenerator.TreeType) getEnumProperty("default-tree-type", TreeGenerator.TreeType.class,
                DEFAULT_TREE_TYPE
        );
    }

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.tree-snipe.info"));
    }

    @CommandMethod("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                List.of(TreeGenerator.TreeType.values()),
                (type, type2) -> type.lookupKeys.get(0).compareTo(type2.lookupKeys.get(0)),
                type -> TextComponent.of(type.lookupKeys.get(0)),
                type -> type,
                this.treeType,
                "voxelsniper.brush.tree-snipe"
        ));
    }

    @CommandMethod("<tree-type>")
    public void onBrushTreetype(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("tree-type") TreeGenerator.TreeType treeType
    ) {
        this.treeType = treeType;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.tree-snipe.set-tree",
                this.treeType.getName()
        ));
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
        BlockState currentBlockData = getBlock(targetBlock.x(), targetBlock.y() - 1, targetBlock.z());
        setBlock(targetBlock.x(), targetBlock.y() - 1, targetBlock.z(), BlockTypes.GRASS_BLOCK);
        if (!generateTree(targetBlock, this.treeType)) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.brush.tree-snipe.generate-failed"));
        }
        setBlockData(targetBlock.x(), targetBlock.y() - 1, targetBlock.z(), currentBlockData);
    }

    private int getYOffset() {
        BlockVector3 targetBlock = getTargetBlock();
        EditSession editSession = getEditSession();
        return IntStream.range(1, (editSession.getMaxY() - targetBlock.y()))
                .filter(i -> Materials.isEmpty(getBlockType(targetBlock.add(0, i + 1, 0))))
                .findFirst()
                .orElse(0);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.brush.tree-snipe.set-tree",
                        this.treeType.getName()
                ))
                .send();
    }

}
