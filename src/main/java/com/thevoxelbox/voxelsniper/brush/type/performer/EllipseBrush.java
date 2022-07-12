package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

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

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Ellipse Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b el fill -- Toggles fill mode. Default is false.");
            messenger.sendMessage(ChatColor.AQUA + "/b el x [n] -- Sets X size modifier to n.");
            messenger.sendMessage(ChatColor.AQUA + "/b el y [n] -- Sets Y size modifier to n.");
            messenger.sendMessage(ChatColor.AQUA + "/b el t [n] -- Sets the amount of time steps.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("fill")) {
                    if (this.fill) {
                        this.fill = false;
                        messenger.sendMessage(ChatColor.AQUA + "Fill mode is disabled");
                    } else {
                        this.fill = true;
                        messenger.sendMessage(ChatColor.AQUA + "Fill mode is enabled");
                    }
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("x")) {
                    Integer xscl = NumericParser.parseInteger(parameters[1]);
                    if (xscl != null && xscl >= this.sclMin && xscl <= this.sclMax) {
                        this.xscl = xscl;
                        messenger.sendMessage(ChatColor.AQUA + "X-scale modifier set to: " + this.xscl);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("y")) {
                    Integer yscl = NumericParser.parseInteger(parameters[1]);
                    if (yscl != null && yscl >= this.sclMin && yscl <= this.sclMax) {
                        this.yscl = yscl;
                        messenger.sendMessage(ChatColor.AQUA + "Y-scale modifier set to: " + this.yscl);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("t")) {
                    Integer steps = NumericParser.parseInteger(parameters[1]);
                    if (steps != null && steps >= this.stepsMin && steps <= this.stepsMax) {
                        this.steps = steps;
                        messenger.sendMessage(ChatColor.AQUA + "Render step number set to: " + this.steps);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number : " + parameters[1]);
                    }

                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display " +
                        "parameter info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("fill", "x", "y", "t"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
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
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
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
        } catch (RuntimeException exception) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.RED + "Invalid target.");
        }
    }

    private void ellipseFill(Snipe snipe, BlockVector3 targetBlock) {
        EditSession editSession = getEditSession();
        int ix = this.xscl;
        int iy = this.yscl;
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
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
        } catch (RuntimeException exception) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.RED + "Invalid target.");
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
                .message(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xscl)
                .message(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yscl)
                .message(ChatColor.AQUA + "Render step number set to: " + ChatColor.DARK_AQUA + this.steps)
                .message(ChatColor.AQUA + "Fill mode is " + (this.fill ? "enabled" : "disabled"))
                .send();
    }

}
