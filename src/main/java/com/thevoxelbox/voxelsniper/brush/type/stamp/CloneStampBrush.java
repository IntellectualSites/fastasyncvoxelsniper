package com.thevoxelbox.voxelsniper.brush.type.stamp;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

/**
 * The CloneStamp class is used to create a collection of blocks in a cylinder shape according to the selection the player has set.
 */
public class CloneStampBrush extends AbstractStampBrush {

    private static final StampType DEFAULT_STAMP_TYPE = StampType.DEFAULT;

    @Override
    public void loadProperties() {
        this.setStamp((StampType) getEnumProperty("default-stamp-type", StampType.class, DEFAULT_STAMP_TYPE));
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Clone / Stamp Cylinder Brush Parameters:");
            messenger.sendMessage(ChatColor.GREEN + "/b cs f -- Activates Fill mode.");
            messenger.sendMessage(ChatColor.GREEN + "/b cs a -- Activates No-Air mode.");
            messenger.sendMessage(ChatColor.GREEN + "/b cs d -- Activates Default mode.");
            messenger.sendMessage(ChatColor.GREEN + "/b cs c [n] -- Sets center to n.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("a")) {
                    setStamp(StampType.NO_AIR);
                    reSort();
                    messenger.sendMessage(ChatColor.AQUA + "No-Air stamp brush activated.");
                } else if (firstParameter.equalsIgnoreCase("f")) {
                    setStamp(StampType.FILL);
                    reSort();
                    messenger.sendMessage(ChatColor.AQUA + "Fill stamp brush activated.");
                } else if (firstParameter.equalsIgnoreCase("d")) {
                    setStamp(StampType.DEFAULT);
                    reSort();
                    messenger.sendMessage(ChatColor.AQUA + "Default stamp brush activated.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("c")) {
                    Integer cylinderCenter = NumericParser.parseInteger(parameters[1]);
                    if (cylinderCenter != null) {
                        toolkitProperties.setCylinderCenter(cylinderCenter);
                        messenger.sendMessage(ChatColor.BLUE + "Center set to: " + toolkitProperties.getCylinderCenter());
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
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
            return super.sortCompletions(Stream.of("f", "a", "d", "c"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        clone(snipe);
    }

    /**
     * The clone method is used to grab a snapshot of the selected area dictated blockPositionY targetBlock.x y z v.brushSize v.voxelHeight and v.cCen.
     * x y z -- initial center of the selection v.brushSize -- the radius of the cylinder v.voxelHeight -- the height of the cylinder c.cCen -- the offset on
     * the Y axis of the selection ( bottom of the cylinder ) as blockPositionY: Bottom_Y = targetBlock.y + v.cCen;
     */
    private void clone(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        this.clone.clear();
        this.fall.clear();
        this.drop.clear();
        this.solid.clear();
        this.sorted = false;
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockY = targetBlock.getY();
        int yStartingPoint = targetBlockY + toolkitProperties.getCylinderCenter();
        int yEndPoint = targetBlockY + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
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
        double bSquared = Math.pow(brushSize, 2);
        int targetBlockX = targetBlock.getX();
        int targetBlockZ = targetBlock.getZ();
        for (int z = yStartingPoint; z < yEndPoint; z++) {
            this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ), 0, z - yStartingPoint, 0));
            for (int y = 1; y <= brushSize; y++) {
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ + y), 0, z - yStartingPoint, y));
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ - y), 0, z - yStartingPoint, -y));
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX + y, z, targetBlockZ), y, z - yStartingPoint, 0));
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX - y, z, targetBlockZ), -y, z - yStartingPoint, 0));
            }
            for (int x = 1; x <= brushSize; x++) {
                double xSquared = Math.pow(x, 2);
                for (int y = 1; y <= brushSize; y++) {
                    if ((xSquared + Math.pow(y, 2)) <= bSquared) {
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX + x, z, targetBlockZ + y),
                                x,
                                z - yStartingPoint,
                                y
                        ));
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX + x, z, targetBlockZ - y),
                                x,
                                z - yStartingPoint,
                                -y
                        ));
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX - x, z, targetBlockZ + y),
                                -x,
                                z - yStartingPoint,
                                y
                        ));
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX - x, z, targetBlockZ - y),
                                -x,
                                z - yStartingPoint,
                                -y
                        ));
                    }
                }
            }
        }
        messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.clone.size()) + ChatColor.AQUA + " blocks copied successfully.");
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .voxelHeightMessage()
                .cylinderCenterMessage()
                .message(switch (this.stamp) {
                    case DEFAULT -> ChatColor.LIGHT_PURPLE + "Default Stamp";
                    case NO_AIR -> ChatColor.LIGHT_PURPLE + "No-Air Stamp";
                    case FILL -> ChatColor.LIGHT_PURPLE + "Fill Stamp";
                })
                .send();
    }

}
