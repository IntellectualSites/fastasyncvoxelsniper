package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b ellipse|el")
@CommandPermission("voxelsniper.brush.ellipse")
public class EllipseBrush extends AbstractPerformerBrush {

    private static final double TWO_PI = (2 * Math.PI);

    private static final int SCL_MIN = 1;
    private static final int SCL_MAX = 9999;
    private static final int STEPS_MIN = 1;
    private static final int STEPS_MAX = 2000;

    private static final int DEFAULT_SCL = 10;
    private static final int DEFAULT_STEPS = 200;

    private boolean fill;
    private double stepSize;

    private int sclMin;
    private int sclMax;
    private int stepsMin;
    private int stepsMax;

    private int xscl;
    private int yscl;
    private int steps;

    @Override
    public void loadProperties() {
        this.sclMin = getIntegerProperty("scl-min", SCL_MIN);
        this.sclMax = getIntegerProperty("scl-max", SCL_MAX);
        this.stepsMin = getIntegerProperty("steps-min", STEPS_MIN);
        this.stepsMax = getIntegerProperty("steps-max", STEPS_MAX);

        this.xscl = getIntegerProperty("default-x-scl", DEFAULT_SCL);
        this.yscl = getIntegerProperty("default-y-scl", DEFAULT_SCL);
        this.steps = getIntegerProperty("default-steps", DEFAULT_STEPS);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.ellipse.info"));
    }

    @CommandMethod("fill")
    public void onBrushFill(
            final @NotNull Snipe snipe
    ) {
        this.fill = !this.fill;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipse.set-fill-mode",
                VoxelSniperText.getStatus(this.fill)
        ));
    }

    @CommandMethod("x <x-scl>")
    public void onBrushX(
            final @NotNull Snipe snipe,
            final @Argument("x-scl") @DynamicRange(min = "sclMin", max = "sclMax") int xscl
    ) {
        this.xscl = xscl;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipse.set-x-scale",
                this.xscl
        ));
    }

    @CommandMethod("y <y-scl>")
    public void onBrushY(
            final @NotNull Snipe snipe,
            final @Argument("y-scl") @DynamicRange(min = "sclMin", max = "sclMax") int yscl
    ) {
        this.yscl = yscl;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipse.set-y-scale",
                this.yscl
        ));
    }

    @CommandMethod("t <steps>")
    public void onBrushT(
            final @NotNull Snipe snipe,
            final @Argument("steps") @DynamicRange(min = "stepsMin", max = "stepsMax") int steps
    ) {
        this.steps = steps;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipse.set-steps",
                this.steps
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        execute(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        execute(snipe, lastBlock);
    }

    private void execute(Snipe snipe, BlockVector3 targetBlock) {
        this.stepSize = TWO_PI / this.steps;
        if (this.fill) {
            ellipseFill(snipe, targetBlock);
        } else {
            ellipse(snipe, targetBlock);
        }
    }

    private void ellipse(Snipe snipe, BlockVector3 targetBlock) {
        int blockX = targetBlock.x();
        int blockY = targetBlock.y();
        int blockZ = targetBlock.z();
        try {
            for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                int x = (int) Math.round(this.xscl * Math.cos(steps));
                int y = (int) Math.round(this.yscl * Math.sin(steps));
                BlockVector3 lastBlock = getLastBlock();
                Direction face = getDirection(getTargetBlock(), lastBlock);
                if (face != null) {
                    switch (face) {
                        case NORTH, SOUTH -> this.performer.perform(
                                getEditSession(),
                                blockX,
                                blockY + x,
                                blockZ + y,
                                getBlock(blockX, blockY + x, blockZ + y)
                        );
                        case EAST, WEST -> this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                blockY + y,
                                blockZ,
                                getBlock(blockX + x, blockY + y, blockZ)
                        );
                        case UP, DOWN -> this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                blockY,
                                blockZ + y,
                                getBlock(blockX + x, blockY, blockZ + y)
                        );
                        default -> {
                        }
                    }
                }
                if (steps >= TWO_PI) {
                    break;
                }
            }
        } catch (RuntimeException e) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.command.invalid-target-block"));
        }
    }

    private void ellipseFill(Snipe snipe, BlockVector3 targetBlock) {
        EditSession editSession = getEditSession();
        int ix = this.xscl;
        int iy = this.yscl;
        int blockX = targetBlock.x();
        int blockY = targetBlock.y();
        int blockZ = targetBlock.z();
        this.performer.perform(editSession, blockX, blockY, blockZ, getBlock(blockX, blockY, blockZ));
        try {
            if (ix >= iy) { // Need this unless you want weird holes
                for (iy = this.yscl; iy >= editSession.getMinY(); iy--) {
                    for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                        int x = (int) Math.round(ix * Math.cos(steps));
                        int y = (int) Math.round(iy * Math.sin(steps));
                        BlockVector3 lastBlock = getLastBlock();
                        Direction face = getDirection(getTargetBlock(), lastBlock);
                        if (face != null) {
                            switch (face) {
                                case NORTH, SOUTH -> this.performer.perform(
                                        getEditSession(),
                                        blockX,
                                        blockY + x,
                                        blockZ + y,
                                        getBlock(blockX, blockY + x, blockZ + y)
                                );
                                case EAST, WEST -> this.performer.perform(
                                        getEditSession(),
                                        blockX + x,
                                        blockY + y,
                                        blockZ,
                                        getBlock(blockX + x, blockY + y, blockZ)
                                );
                                case UP, DOWN -> this.performer.perform(
                                        getEditSession(),
                                        blockX + x,
                                        blockY,
                                        blockZ + y,
                                        getBlock(blockX + x, blockY, blockZ + y)
                                );
                                default -> {
                                }
                            }
                        }
                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    ix--;
                }
            } else {
                for (ix = this.xscl; ix >= editSession.getMinY(); ix--) {
                    for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                        int x = (int) Math.round(ix * Math.cos(steps));
                        int y = (int) Math.round(iy * Math.sin(steps));
                        BlockVector3 lastBlock = getLastBlock();
                        Direction face = getDirection(getTargetBlock(), lastBlock);
                        if (face != null) {
                            switch (face) {
                                case NORTH, SOUTH -> this.performer.perform(
                                        getEditSession(),
                                        blockX,
                                        blockY + x,
                                        blockZ + y,
                                        getBlock(blockX, blockY + x, blockZ + y)
                                );
                                case EAST, WEST -> this.performer.perform(
                                        getEditSession(),
                                        blockX + x,
                                        blockY + y,
                                        blockZ,
                                        getBlock(blockX + x, blockY + y, blockZ)
                                );
                                case UP, DOWN -> this.performer.perform(
                                        getEditSession(),
                                        blockX + x,
                                        blockY,
                                        blockZ + y,
                                        getBlock(blockX + x, blockY, blockZ + y)
                                );
                                default -> {
                                }
                            }
                        }
                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    iy--;
                }
            }
        } catch (RuntimeException e) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.command.invalid-target-block"));
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        if (this.xscl < this.sclMin || this.xscl > this.sclMax) {
            this.xscl = getIntegerProperty("scl-default", DEFAULT_SCL);
        }
        if (this.yscl < this.sclMin || this.yscl > this.sclMax) {
            this.yscl = getIntegerProperty("scl-default", DEFAULT_SCL);
        }
        if (this.steps < this.stepsMin || this.steps > this.stepsMax) {
            this.steps = getIntegerProperty("default-steps", DEFAULT_STEPS);
        }
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipse.set-fill-mode",
                        VoxelSniperText.getStatus(fill)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipse.set-x-scale",
                        this.xscl
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipse.set-y-scale",
                        this.yscl
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipse.set-steps",
                        this.steps
                ))
                .send();
    }

}
