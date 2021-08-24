package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class RulerBrush extends AbstractBrush {

    private boolean first = true;
    private BlockVector3 coordinates = BlockVector3.ZERO;
    private int offsetX;
    private int offsetY;
    private int offsetZ;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Ruler Brush instructions: Right click first point with the arrow. " +
                    "Right click with gunpowder for distances from that block (can repeat without getting a new first " +
                    "block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
            messenger.sendMessage(ChatColor.LIGHT_PURPLE + "/b r [x] [y] [z] -- Places blocks one at a time of the type you " +
                    "have set with /v at the location you click + this many units away. If you don't include a value, " +
                    "it will be zero. Don't include ANY values, and the brush will just measure distance.");
            messenger.sendMessage(ChatColor.BLUE + "/b r ruler -- Resets the tool to just measure distances, not " +
                    "layout blocks.");

        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("ruler")) {
                    this.offsetZ = 0;
                    this.offsetY = 0;
                    this.offsetX = 0;
                    messenger.sendMessage(ChatColor.BLUE + "Ruler mode.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else if (parameters.length == 3) {
                Integer offsetX = NumericParser.parseInteger(parameters[0]);
                Integer offsetY = NumericParser.parseInteger(parameters[1]);
                Integer offsetZ = NumericParser.parseInteger(parameters[2]);
                this.offsetX = offsetX == null ? 0 : offsetX;
                this.offsetY = offsetY == null ? 0 : offsetY;
                this.offsetZ = offsetZ == null ? 0 : offsetZ;
                messenger.sendMessage(ChatColor.AQUA + "X offset set to: " + this.offsetX);
                messenger.sendMessage(ChatColor.AQUA + "Y offset set to: " + this.offsetY);
                messenger.sendMessage(ChatColor.AQUA + "Z offset set to: " + this.offsetZ);
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
            return super.sortCompletions(Stream.of("ruler"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockType blockDataType = toolkitProperties.getBlockType();
        BlockVector3 targetBlock = getTargetBlock();
        this.coordinates = targetBlock;
        if (this.offsetX == 0 && this.offsetY == 0 && this.offsetZ == 0) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
            this.first = !this.first;
        } else {
            int x = targetBlock.getX();
            int y = targetBlock.getY();
            int z = targetBlock.getZ();
            setBlockType(x + this.offsetX, y + this.offsetY, z + this.offsetZ, blockDataType);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.coordinates == null || this.coordinates.lengthSq() == 0) {
            messenger.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow. Comparing to point 0,0,0 instead.");
            return;
        }
        messenger.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
        BlockVector3 targetBlock = getTargetBlock();
        messenger.sendMessage(ChatColor.AQUA + "X change: " + (targetBlock.getX() - this.coordinates.getX()));
        messenger.sendMessage(ChatColor.AQUA + "Y change: " + (targetBlock.getY() - this.coordinates.getY()));
        messenger.sendMessage(ChatColor.AQUA + "Z change: " + (targetBlock.getZ() - this.coordinates.getZ()));
        double distance = Math.round(targetBlock
                .subtract(this.coordinates)
                .length() * 100) / 100.0;
        double blockDistance = Math.round((Math.abs(Math.max(Math.max(
                Math.abs(targetBlock.getX() - this.coordinates.getX()),
                Math.abs(targetBlock.getY() - this.coordinates.getY())
        ), Math.abs(targetBlock.getZ() - this.coordinates.getZ()))) + 1) * 100) / 100.0;
        messenger.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance);
        messenger.sendMessage(ChatColor.AQUA + "Block distance = " + blockDistance);
    }

    @Override
    public final void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendBlockTypeMessage();
    }

}
