package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequireToolkit
@CommandMethod(value = "brush|b set")
@CommandPermission("voxelsniper.brush.set")
public class SetBrush extends AbstractPerformerBrush {

    private static final int SELECTION_SIZE_MAX = 5000000;

    @Nullable
    private BlockVector3 block;
    private World world;

    private int selectionSizeMax;

    @Override
    public void loadProperties() {
        this.selectionSizeMax = getIntegerProperty("selection-size-max", SELECTION_SIZE_MAX);
    }

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        SnipeMessenger messenger = snipe.createMessenger();
        if (set(targetBlock, getEditSession().getWorld(), snipe)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        SnipeMessenger messenger = snipe.createMessenger();
        if (set(lastBlock, getEditSession().getWorld(), snipe)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
        }
    }

    private boolean set(BlockVector3 block, World world, Snipe snipe) {
        if (this.block == null) {
            this.block = block;
            this.world = world;
            return true;
        }
        SnipeMessenger messenger = snipe.createMessenger();
        String name = this.world.getName();
        String parameterBlockWorldName = world.getName();
        if (!name.equals(parameterBlockWorldName)) {
            messenger.sendMessage(Caption.of("voxelsniper.error.brush.different-world"));
            this.block = null;
            return true;
        }
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
        if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > this.selectionSizeMax) {
            messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-selection", this.selectionSizeMax));
        } else {
            for (int y = lowY; y <= highY; y++) {
                for (int x = lowX; x <= highX; x++) {
                    for (int z = lowZ; z <= highZ; z++) {
                        this.performer.perform(getEditSession(), x, clampY(y), z, clampY(x, y, z));
                    }
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.set.completed"));
        }
        this.block = null;
        return false;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        this.block = null;
        snipe.createMessageSender()
                .brushNameMessage()
                .send();
    }

}
