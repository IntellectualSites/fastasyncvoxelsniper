package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class EllipsoidBrush extends AbstractPerformerBrush {

    private static final int DEFAULT_X_RAD = 0;
    private static final int DEFAULT_Y_RAD = 0;
    private static final int DEFAULT_Z_RAD = 0;

    private boolean offset;

    private double xRad = DEFAULT_X_RAD;
    private double yRad = DEFAULT_Y_RAD;
    private double zRad = DEFAULT_Z_RAD;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.ellipsoid.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("true")) {
                    this.offset = true;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.ellipsoid.set-offset",
                            VoxelSniperText.getStatus(true)
                    ));
                } else if (firstParameter.equalsIgnoreCase("false")) {
                    this.offset = false;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.ellipsoid.set-offset",
                            VoxelSniperText.getStatus(false)
                    ));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("x")) {
                    Integer xRad = NumericParser.parseInteger(parameters[1]);
                    if (xRad != null) {
                        this.xRad = xRad;
                        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.ellipsoid.set-x-radius", this.xRad));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                } else if (firstParameter.equalsIgnoreCase("y")) {
                    Integer yRad = NumericParser.parseInteger(parameters[1]);
                    if (yRad != null) {
                        this.yRad = yRad;
                        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.ellipsoid.set-y-radius", this.yRad));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                } else if (firstParameter.equalsIgnoreCase("z")) {
                    Integer zRad = NumericParser.parseInteger(parameters[1]);
                    if (zRad != null) {
                        this.zRad = zRad;
                        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.ellipsoid.set-z-radius", this.zRad));
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
            return super.sortCompletions(Stream.of("true", "false", "x", "y", "z"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        execute(targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        execute(lastBlock);
    }

    private void execute(BlockVector3 targetBlock) {
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        this.performer.perform(getEditSession(), blockX, blockY, blockZ, getBlock(blockX, blockY, blockZ));
        double trueOffset = this.offset ? 0.5 : 0;
        for (double x = 0; x <= this.xRad; x++) {
            double xSquared = (x / (this.xRad + trueOffset)) * (x / (this.xRad + trueOffset));
            for (double z = 0; z <= this.zRad; z++) {
                double zSquared = (z / (this.zRad + trueOffset)) * (z / (this.zRad + trueOffset));
                for (double y = 0; y <= this.yRad; y++) {
                    double ySquared = (y / (this.yRad + trueOffset)) * (y / (this.yRad + trueOffset));
                    if (xSquared + ySquared + zSquared <= 1) {
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX + x), (int) (blockY + y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX + x), (int) (blockY + y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX + x), (int) (blockY - y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX + x), (int) (blockY - y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX - x), (int) (blockY + y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX - x), (int) (blockY + y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX - x), (int) (blockY - y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX - x), (int) (blockY - y), (int) (blockZ - z))
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
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipsoid.set-offset",
                        VoxelSniperText.getStatus(offset)
                ))
                .message(Caption.of("voxelsniper.performer-brush.ellipsoid.set-x-radius", this.xRad))
                .message(Caption.of("voxelsniper.performer-brush.ellipsoid.set-y-radius", this.yRad))
                .message(Caption.of("voxelsniper.performer-brush.ellipsoid.set-z-radius", this.zRad))
                .send();
    }

}
