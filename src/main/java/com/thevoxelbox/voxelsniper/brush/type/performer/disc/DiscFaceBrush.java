package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;

import java.util.List;
import java.util.stream.Stream;

public class DiscFaceBrush extends AbstractPerformerBrush {

    private double trueCircle;

    @Override
    public void loadProperties() {
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.disc-face.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("true")) {
                    this.trueCircle = 0.5;
                    messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.true-circle", VoxelSniperText.getStatus(true)));
                } else if (firstParameter.equalsIgnoreCase("false")) {
                    this.trueCircle = 0;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.parameter.true-circle",
                            VoxelSniperText.getStatus(false)
                    ));
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
            return super.sortCompletions(Stream.of("true", "false"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        pre(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        pre(snipe, lastBlock);
    }

    private void discUpDown(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int z = brushSize; z >= 0; z--) {
                if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY,
                            blockZ + z,
                            getBlock(blockX + x, blockY, blockZ + z)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY,
                            blockZ - z,
                            getBlock(blockX + x, blockY, blockZ - z)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY,
                            blockZ + z,
                            getBlock(blockX - x, blockY, blockZ + z)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY,
                            blockZ - z,
                            getBlock(blockX - x, blockY, blockZ - z)
                    );
                }
            }
        }
    }

    private void discNorthSouth(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int y = brushSize; y >= 0; y--) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY + y,
                            blockZ,
                            getBlock(blockX + x, blockY + y, blockZ)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY - y,
                            blockZ,
                            getBlock(blockX + x, blockY - y, blockZ)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY + y,
                            blockZ,
                            getBlock(blockX - x, blockY + y, blockZ)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY - y,
                            blockZ,
                            getBlock(blockX - x, blockY - y, blockZ)
                    );
                }
            }
        }
    }

    private void discEastWest(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int y = brushSize; y >= 0; y--) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY + x,
                            blockZ + y,
                            getBlock(blockX, blockY + x, blockZ + y)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY + x,
                            blockZ - y,
                            getBlock(blockX, blockY + x, blockZ - y)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY - x,
                            blockZ + y,
                            getBlock(blockX, blockY - x, blockZ + y)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY - x,
                            blockZ - y,
                            getBlock(blockX, blockY - x, blockZ - y)
                    );
                }
            }
        }
    }

    private void pre(Snipe snipe, BlockVector3 targetBlock) {
        BlockVector3 lastBlock = getLastBlock();
        Direction blockFace = getDirection(getTargetBlock(), lastBlock);
        if (blockFace == null) {
            return;
        }
        switch (blockFace) {
            case NORTH, SOUTH -> discNorthSouth(snipe, targetBlock);
            case EAST, WEST -> discEastWest(snipe, targetBlock);
            case UP, DOWN -> discUpDown(snipe, targetBlock);
            default -> {
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(Caption.of("voxelsniper.brush.parameter.true-circle", VoxelSniperText.getStatus(this.trueCircle == 0.5)))
                .send();
    }

}
