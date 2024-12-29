package com.thevoxelbox.voxelsniper.brush.type.redstone;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequireToolkit
@CommandMethod(value = "brush|b set_redstone_flip|setredstoneflip|setrf")
@CommandPermission("voxelsniper.brush.setredstoneflip")
public class SetRedstoneFlipBrush extends AbstractBrush {

    @Nullable
    private BlockVector3 block;
    private boolean northSouth = true;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.set-redstone-flip.info"));
    }

    @CommandMethod("ns|n|s")
    public void onBrushNs(
            final @NotNull Snipe snipe
    ) {
        this.northSouth = true;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.set-redstone-flip.north-south"));
    }

    @CommandMethod("ew|e|w")
    public void onBrushEw(
            final @NotNull Snipe snipe
    ) {
        this.northSouth = false;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.set-redstone-flip.east-west"));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        SnipeMessenger messenger = snipe.createMessenger();
        if (set(targetBlock)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        SnipeMessenger messenger = snipe.createMessenger();
        if (set(lastBlock)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
        }
    }

    protected boolean set(BlockVector3 block) {
        if (this.block == null) {
            this.block = block;
            return true;
        } else {
            int x1 = this.block.x();
            int x2 = block.x();
            int y1 = this.block.y();
            int y2 = block.y();
            int z1 = this.block.z();
            int z2 = block.z();
            int lowX = Math.min(x1, x2);
            int lowY = Math.min(y1, y2);
            int lowZ = Math.min(z1, z2);
            int highX = Math.max(x1, x2);
            int highY = Math.max(y1, y2);
            int highZ = Math.max(z1, z2);
            for (int y = lowY; y <= highY; y++) {
                for (int x = lowX; x <= highX; x++) {
                    for (int z = lowZ; z <= highZ; z++) {
                        this.perform(x, clampY(y), z, this.clampY(x, y, z));
                    }
                }
            }
            this.block = null;
            return false;
        }
    }

    private void perform(int x, int y, int z, BlockState block) {
        BlockType type = block.getBlockType();
        if (type == BlockTypes.REPEATER) {
            Property<Integer> delayProperty = type.getProperty(PropertyKey.DELAY);
            if (delayProperty == null) {
                return;
            }
            int delay = block.getState(delayProperty);
            if (this.northSouth) {
                if ((delay % 4) == 1) {
                    block = block.with(delayProperty, delay + 2);
                } else if ((delay % 4) == 3) {
                    block = block.with(delayProperty, delay - 2);
                }
            } else {
                if ((delay % 4) == 2) {
                    block = block.with(delayProperty, delay - 2);
                } else if ((delay % 4) == 0) {
                    block = block.with(delayProperty, delay + 2);
                }
            }
            setBlockData(x, y, z, block);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        this.block = null;
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(northSouth
                        ? "voxelsniper.brush.set-redstone-flip.north-south"
                        : "voxelsniper.brush.set-redstone-flip.east-west"))
                .send();
    }

}
