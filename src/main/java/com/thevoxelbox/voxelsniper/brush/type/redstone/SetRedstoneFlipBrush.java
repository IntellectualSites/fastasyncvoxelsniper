package com.thevoxelbox.voxelsniper.brush.type.redstone;

import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class SetRedstoneFlipBrush extends AbstractBrush {

    @Nullable
    private BlockVector3 block;
    private boolean northSouth = true;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.set-redstone-flip.info"));
        } else {
            if (parameters.length == 1) {
                if (Stream.of("n", "s", "ns")
                        .anyMatch(firstParameter::startsWith)) {
                    this.northSouth = true;
                    messenger.sendMessage(Caption.of("voxelsniper.brush.set-redstone-flip.north-south"));
                } else if (Stream.of("e", "w", "ew")
                        .anyMatch(firstParameter::startsWith)) {
                    this.northSouth = false;
                    messenger.sendMessage(Caption.of("voxelsniper.brush.set-redstone-flip.east-west"));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
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
            return super.sortCompletions(Stream.of("n", "s", "ns", "e", "w", "ew"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
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
            int x1 = this.block.getX();
            int x2 = block.getX();
            int y1 = this.block.getY();
            int y2 = block.getY();
            int z1 = this.block.getZ();
            int z2 = block.getZ();
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
