package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class RulerBrush extends AbstractBrush {

    private static final int DEFAULT_X_OFFSET = 0;
    private static final int DEFAULT_Y_OFFSET = 0;
    private static final int DEFAULT_Z_OFFSET = 0;

    private boolean first = true;
    private BlockVector3 coordinates = BlockVector3.ZERO;

    private int xOffset = DEFAULT_X_OFFSET;
    private int yOffset = DEFAULT_Y_OFFSET;
    private int zOffset = DEFAULT_Z_OFFSET;

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
                    this.zOffset = 0;
                    this.yOffset = 0;
                    this.xOffset = 0;
                    messenger.sendMessage(ChatColor.BLUE + "Ruler mode.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else if (parameters.length == 3) {
                Integer xOffset = NumericParser.parseInteger(parameters[0]);
                Integer yOffset = NumericParser.parseInteger(parameters[1]);
                Integer zOffset = NumericParser.parseInteger(parameters[2]);
                this.xOffset = xOffset == null ? 0 : xOffset;
                this.yOffset = yOffset == null ? 0 : yOffset;
                this.zOffset = zOffset == null ? 0 : zOffset;
                messenger.sendMessage(ChatColor.AQUA + "X-Offset set to: " + this.xOffset);
                messenger.sendMessage(ChatColor.AQUA + "Y-Offset set to: " + this.yOffset);
                messenger.sendMessage(ChatColor.AQUA + "Z-Offset set to: " + this.zOffset);
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
        BlockVector3 targetBlock = getTargetBlock();
        this.coordinates = targetBlock;
        if (this.xOffset == 0 && this.yOffset == 0 && this.zOffset == 0) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
            this.first = !this.first;
        } else {
            int x = targetBlock.getX();
            int y = targetBlock.getY();
            int z = targetBlock.getZ();
            setBlock(x + this.xOffset, y + this.yOffset, z + this.zOffset, toolkitProperties.getPattern().getPattern());
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
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .patternMessage()
                .send();
    }

}
