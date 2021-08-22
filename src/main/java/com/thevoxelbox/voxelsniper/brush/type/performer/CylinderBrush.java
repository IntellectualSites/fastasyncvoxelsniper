package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class CylinderBrush extends AbstractPerformerBrush {

    private double trueCircle;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Cylinder Brush Parameters:");
            messenger.sendMessage(ChatColor.DARK_AQUA + "/b c [true|false] -- Uses a true circle algorithm instead of the " +
                    "skinnier version with classic sniper nubs. (false is default)");
            messenger.sendMessage(ChatColor.AQUA + "/b c h [n] -- Sets the cylinder v.voxelHeight to n. Default is 1.");
            messenger.sendMessage(ChatColor.BLUE + "/b c c [n] -- Sets the origin of the cylinder compared to the target block " +
                    "to n. Positive numbers will move the cylinder upward, negative will move it downward.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("true")) {
                    this.trueCircle = 0.5;
                    messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                } else if (firstParameter.equalsIgnoreCase("false")) {
                    this.trueCircle = 0;
                    messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("h")) {
                    Integer height = NumericParser.parseInteger(parameters[1]);
                    if (height != null) {
                        toolkitProperties.setVoxelHeight(height);
                        messenger.sendMessage(ChatColor.AQUA + "Cylinder v.voxelHeight set to: " + toolkitProperties.getVoxelHeight());
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else if (firstParameter.equalsIgnoreCase("c")) {
                    Integer center = NumericParser.parseInteger(parameters[1]);
                    if (center != null) {
                        toolkitProperties.setCylinderCenter(center);
                        messenger.sendMessage(ChatColor.AQUA + "Cylinder origin set to: " + toolkitProperties.getCylinderCenter());
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
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
            messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        } else {
            int maxHeight = editSession.getMaxY();
            if (yStartingPoint > maxHeight) {
                yStartingPoint = maxHeight;
                messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
            }
        }
        if (yEndPoint < minHeight) {
            yEndPoint = minHeight;
            messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        } else {
            int maxHeight = editSession.getMaxY();
            if (yEndPoint > maxHeight) {
                yEndPoint = maxHeight;
                messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
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
                .send();
    }

}
