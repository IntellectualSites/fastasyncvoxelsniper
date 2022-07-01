package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class ScannerBrush extends AbstractBrush {

    private static final int DEPTH_MIN = 1;
    private static final int DEPTH_MAX = 64;

    private static final int DEFAULT_DEPTH = 24;

    private int depthMin;
    private int depthMax;

    private int depth;
    private BlockType checkFor;

    @Override
    public void loadProperties() {
        this.depthMin = getIntegerProperty("depth-min", DEPTH_MIN);
        this.depthMax = getIntegerProperty("depth-max", DEPTH_MAX);

        this.depth = getIntegerProperty("default-depth", DEFAULT_DEPTH);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Scanner Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b sc d [d] -- Sets the search depth to d. Clamps to 1 - 64.");
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("d")) {
                    Integer depth = NumericParser.parseInteger(parameters[1]);
                    if (depth != null) {
                        this.depth = depth < this.depthMin ? this.depthMin : Math.min(depth, this.depthMax);
                        messenger.sendMessage(ChatColor.AQUA + "Scanner depth set to: " + this.depth);
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
            return super.sortCompletions(Stream.of("d"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        scan(snipe, face);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        scan(snipe, face);
    }

    private void scan(Snipe snipe, Direction blockFace) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.checkFor = toolkitProperties.getPattern().asBlockType();
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (blockFace == Direction.NORTH) { // Scan south
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ() + i) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + this.checkFor.getId() + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "No matching block found.");
        } else if (blockFace == Direction.SOUTH) { // Scan north
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ() - i) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + this.checkFor.getId() + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "No matching block found.");
        } else if (blockFace == Direction.EAST) { // Scan west
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() - i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + this.checkFor.getId() + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "No matching block found.");
        } else if (blockFace == Direction.WEST) { // Scan east
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() + i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + this.checkFor.getId() + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "No matching block found.");
        } else if (blockFace == Direction.UP) { // Scan down
            for (int i = 1; i < this.depth + 1; i++) {
                if ((targetBlock.getY() - i) <= getEditSession().getMinY()) {
                    break;
                }
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() - i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + this.checkFor.getId() + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "No matching block found.");
        } else if (blockFace == Direction.DOWN) { // Scan up
            for (int i = 1; i < this.depth + 1; i++) {
                EditSession editSession = getEditSession();
                if ((targetBlock.getY() + i) >= editSession.getMaxY()) {
                    break;
                }
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() + i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + this.checkFor.getId() + " found after " + i + " " + "blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "No matching block found.");
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GREEN + "Scanner depth set to: " + this.depth)
                .message(ChatColor.GREEN + "Scanner scans for " + this.checkFor.getId() + " (change with /v #)")
                .send();
    }

}
