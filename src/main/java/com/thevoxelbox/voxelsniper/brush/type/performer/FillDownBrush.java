package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class FillDownBrush extends AbstractPerformerBrush {

    private double trueCircle;
    private boolean fillLiquid = true;
    private boolean fromExisting;

    @Override
    public void loadProperties() {
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Fill Down Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b fd [true|false] -- Uses a true circle algorithm. Default is false.");
            messenger.sendMessage(ChatColor.AQUA + "/b fd all -- Fills into liquids as well. (Default)");
            messenger.sendMessage(ChatColor.AQUA + "/b fd some -- Fills only into air.");
            messenger.sendMessage(ChatColor.AQUA + "/b fd e -- Fills into only existing blocks. (Toggle)");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("true")) {
                    this.trueCircle = 0.5;
                    messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                } else if (firstParameter.equalsIgnoreCase("false")) {
                    this.trueCircle = 0;
                    messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                } else if (firstParameter.equalsIgnoreCase("all")) {
                    this.fillLiquid = true;
                    messenger.sendMessage(ChatColor.AQUA + "Now filling liquids as well as air.");
                } else if (firstParameter.equalsIgnoreCase("some")) {
                    this.fillLiquid = false;
                    ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
                    toolkitProperties.resetReplaceBlockData();
                    messenger.sendMessage(ChatColor.AQUA + "Now only filling air.");
                } else if (firstParameter.equalsIgnoreCase("e")) {
                    this.fromExisting = !this.fromExisting;
                    messenger.sendMessage(ChatColor.AQUA + "Now filling down from " + (this.fromExisting
                            ? "existing"
                            : "all") + " blocks.");
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
                    for (; y >= -targetBlock.getY(); --y) {
                        BlockType currentBlockType = getBlockType(
                                targetBlock.getX() + x,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + z
                        );
                        if (Materials.isEmpty(currentBlockType) || (this.fillLiquid && Materials.isLiquid(currentBlockType))) {
                            this.performer.perform(
                                    getEditSession(),
                                    targetBlock.getX() + x,
                                    targetBlock.getY() + y,
                                    targetBlock.getZ() + z,
                                    getBlock(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z)
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
                .send();
    }

}
