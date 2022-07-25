package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class CylinderBrush extends AbstractPerformerBrush {

    private double trueCircle;

    @Override
    public void loadProperties() {
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.cylindern.info"));
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
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("h")) {
                    Integer height = NumericParser.parseInteger(parameters[1]);
                    if (height != null) {
                        toolkitProperties.setVoxelHeight(height);
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.cylinder.set-voxel-height",
                                toolkitProperties.getVoxelHeight()
                        ));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                } else if (firstParameter.equalsIgnoreCase("c")) {
                    Integer center = NumericParser.parseInteger(parameters[1]);
                    if (center != null) {
                        toolkitProperties.setCylinderCenter(center);
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.cylinder.set-origin",
                                toolkitProperties.getCylinderCenter()
                        ));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
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
            return super.sortCompletions(Stream.of("h", "c", "true", "false"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        cylinder(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        cylinder(snipe, lastBlock);
    }

    private void cylinder(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        int brushSize = toolkitProperties.getBrushSize();
        int yStartingPoint = targetBlock.getY() + toolkitProperties.getCylinderCenter();
        int yEndPoint = targetBlock.getY() + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
        if (yEndPoint < yStartingPoint) {
            yEndPoint = yStartingPoint;
        }
        EditSession editSession = getEditSession();
        int minHeight = editSession.getMinY();
        if (yStartingPoint < minHeight) {
            yStartingPoint = minHeight;
            messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-start"));
        } else {
            int maxHeight = editSession.getMaxY();
            if (yStartingPoint > maxHeight) {
                yStartingPoint = maxHeight;
                messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-start"));
            }
        }
        if (yEndPoint < minHeight) {
            yEndPoint = minHeight;
            messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-end"));
        } else {
            int maxHeight = editSession.getMaxY();
            if (yEndPoint > maxHeight) {
                yEndPoint = maxHeight;
                messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-end"));
            }
        }
        int blockX = targetBlock.getX();
        int blockZ = targetBlock.getZ();
        double bSquared = Math.pow(brushSize + this.trueCircle, 2);
        for (int y = yEndPoint; y >= yStartingPoint; y--) {
            for (int x = brushSize; x >= 0; x--) {
                double xSquared = Math.pow(x, 2);
                for (int z = brushSize; z >= 0; z--) {
                    if ((xSquared + Math.pow(z, 2)) <= bSquared) {
                        this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                clampY(y),
                                blockZ + z,
                                this.clampY(blockX + x, y, blockZ + z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                clampY(y),
                                blockZ - z,
                                this.clampY(blockX + x, y, blockZ - z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX - x,
                                clampY(y),
                                blockZ + z,
                                this.clampY(blockX - x, y, blockZ + z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX - x,
                                clampY(y),
                                blockZ - z,
                                this.clampY(blockX - x, y, blockZ - z)
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
                .brushSizeMessage()
                .voxelHeightMessage()
                .cylinderCenterMessage()
                .message(Caption.of("voxelsniper.brush.parameter.true-circle", VoxelSniperText.getStatus(this.trueCircle == 0.5)))
                .send();
    }

}
