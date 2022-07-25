package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class FillDownBrush extends AbstractPerformerBrush {

    private double trueCircle;
    private boolean fillLiquid = true;
    private boolean fromExisting;
    private int minY;

    @Override
    public void loadProperties() {
        if (getEditSession() != null) {
            this.minY = getEditSession().getMinY();
        }
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.fill-down.info", getEditSession().getMinY()));
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
                } else if (firstParameter.equalsIgnoreCase("all")) {
                    this.fillLiquid = true;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.fill-down.set-fill-all",
                            VoxelSniperText.getStatus(true)
                    ));
                } else if (firstParameter.equalsIgnoreCase("some")) {
                    this.fillLiquid = false;
                    ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
                    toolkitProperties.resetReplacePattern();
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.fill-down.set-fill-some",
                            VoxelSniperText.getStatus(true)
                    ));
                } else if (firstParameter.equalsIgnoreCase("e")) {
                    this.fromExisting = !this.fromExisting;
                    if (this.fromExisting) {
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.fill-down.set-fill-down-existing",
                                VoxelSniperText.getStatus(true)
                        ));
                    } else {
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.fill-down.set-fill-down-all",
                                VoxelSniperText.getStatus(true)
                        ));
                    }
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("y")) {
                    Integer minY = NumericParser.parseInteger(parameters[1]);
                    if (minY != null) {
                        int minYMin = getEditSession().getMinY();
                        int minYMax = getEditSession().getMaxY();
                        this.minY = minY < minYMin ? minYMin : Math.min(minY, minYMax);
                        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.fill-down.set-min-y", this.minY));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
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
            return super.sortCompletions(Stream.of("true", "false", "some", "all", "e"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        fillDown(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        fillDown(snipe);
    }

    private void fillDown(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        BlockVector3 targetBlock = this.getTargetBlock();
        for (int x = -brushSize; x <= brushSize; x++) {
            double currentXSquared = Math.pow(x, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if (currentXSquared + Math.pow(z, 2) <= brushSizeSquared) {
                    int y = 0;
                    if (this.fromExisting) {
                        boolean found = false;
                        for (y = -toolkitProperties.getVoxelHeight(); y < toolkitProperties.getVoxelHeight(); y++) {
                            BlockType currentBlockType = getBlockType(
                                    targetBlock.getX() + x,
                                    targetBlock.getY() + y,
                                    targetBlock.getZ() + z
                            );
                            if (!Materials.isEmpty(currentBlockType)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            continue;
                        }
                        y--;
                    }
                    for (; y >= -(targetBlock.getY() - minY); --y) {
                        BlockState currentBlockState = getBlock(
                                targetBlock.getX() + x,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + z
                        );
                        if (Materials.isEmpty(currentBlockState.getBlockType())
                                || (this.fillLiquid && Materials.isLiquid(currentBlockState.getBlockType()))) {
                            this.performer.perform(
                                    getEditSession(),
                                    targetBlock.getX() + x,
                                    targetBlock.getY() + y,
                                    targetBlock.getZ() + z,
                                    currentBlockState
                            );
                        } else {
                            break;
                        }
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
                .message(Caption.of("voxelsniper.brush.parameter.true-circle", VoxelSniperText.getStatus(this.trueCircle == 0.5)))
                .message(Caption.of("voxelsniper.performer-brush.fill-down.set-fill-all", VoxelSniperText.getStatus(fillLiquid)))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-fill-some",
                        VoxelSniperText.getStatus(!fillLiquid)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-fill-down-existing",
                        VoxelSniperText.getStatus(fromExisting)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-fill-down-all",
                        VoxelSniperText.getStatus(!fromExisting)
                ))
                .message(Caption.of("voxelsniper.performer-brush.fill-down.set-min-y", this.minY))
                .send();
    }

}
