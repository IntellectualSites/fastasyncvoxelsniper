package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Moves a selection blockPositionY a certain amount.
 */
public class MoveBrush extends AbstractBrush {

    private static final int MAX_BLOCK_COUNT = 5000000;

    /**
     * Saved direction.
     */
    private final int[] moveDirections = {0, 0, 0};
    /**
     * Saved selection.
     */
    @Nullable
    private Selection selection;

    private int maxBlockCount;

    @Override
    public void loadProperties() {
        this.maxBlockCount = getIntegerProperty("max-block-count", MAX_BLOCK_COUNT);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.DARK_AQUA + "Use arrow and gunpowder to define two points.");
            messenger.sendMessage(ChatColor.GOLD + "Move Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b mv x [n] -- Sets the x direction to n. (positive => east)");
            messenger.sendMessage(ChatColor.AQUA + "/b mv y [n] -- Sets the y direction to n. (positive => up)");
            messenger.sendMessage(ChatColor.AQUA + "/b mv z [n] -- Sets the z direction to n. (positive => south)");
            messenger.sendMessage(ChatColor.AQUA + "/b mv reset -- Resets the brush. (x:0 y:0 z:0)");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("reset")) {
                    this.moveDirections[0] = 0;
                    this.moveDirections[1] = 0;
                    this.moveDirections[2] = 0;
                    messenger.sendMessage(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
                    messenger.sendMessage(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
                    messenger.sendMessage(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("x")) {
                    Integer moveDirection = NumericParser.parseInteger(parameters[1]);
                    if (moveDirection != null) {
                        this.moveDirections[0] = moveDirection;
                        messenger.sendMessage(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else if (firstParameter.equalsIgnoreCase("y")) {
                    Integer moveDirection = NumericParser.parseInteger(parameters[1]);
                    if (moveDirection != null) {
                        this.moveDirections[1] = moveDirection;
                        messenger.sendMessage(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[0]);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else if (firstParameter.equalsIgnoreCase("z")) {
                    Integer moveDirection = NumericParser.parseInteger(parameters[1]);
                    if (moveDirection != null) {
                        this.moveDirections[2] = moveDirection;
                        messenger.sendMessage(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[0]);
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
            return super.sortCompletions(Stream.of("x", "y", "z", "reset"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.selection == null) {
            this.selection = new Selection(this.maxBlockCount);
        }
        this.selection.setLocation1(this.getTargetBlock(), getEditSession().getWorld());
        messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Point 1 set.");
        try {
            if (this.selection.calculateRegion()) {
                moveSelection(snipe, this.selection, this.moveDirections);
                this.selection = null;
            }
        } catch (RuntimeException exception) {
            messenger.sendMessage(exception.getMessage());
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.selection == null) {
            this.selection = new Selection(this.maxBlockCount);
        }
        this.selection.setLocation2(this.getTargetBlock(), getEditSession().getWorld());
        messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Point 2 set.");
        try {
            if (this.selection.calculateRegion()) {
                this.moveSelection(snipe, this.selection, this.moveDirections);
                this.selection = null;
            }
        } catch (RuntimeException exception) {
            messenger.sendMessage(exception.getMessage());
        }
    }

    /**
     * Moves the given selection blockPositionY the amount given in direction.
     */
    private void moveSelection(Snipe snipe, Selection selection, int[] direction) {
        SnipeMessenger messenger = snipe.createMessenger();
        List<BlockVector3> locations = selection.getLocations();
        if (!locations.isEmpty()) {
            Selection newSelection = new Selection(this.maxBlockCount);
            BlockVector3 movedLocation1 = selection.getLocation1();
            movedLocation1.add(direction[0], direction[1], direction[2]);
            BlockVector3 movedLocation2 = selection.getLocation2();
            movedLocation2.add(direction[0], direction[1], direction[2]);
            newSelection.setLocation1(movedLocation1, selection.getWorld1());
            newSelection.setLocation2(movedLocation2, selection.getWorld2());
            try {
                newSelection.calculateRegion();
            } catch (RuntimeException exception) {
                messenger.sendMessage(ChatColor.LIGHT_PURPLE + "The new Selection has more blocks than the original selection. This should never happen!");
            }
            locations.forEach(block -> setBlockType(block.getX(), block.getY(), block.getZ(), BlockTypes.AIR));
            for (BlockVector3 block : locations) {
                setBlockData(
                        block.getX() + direction[0],
                        block.getY() + direction[1],
                        block.getZ() + direction[2],
                        getBlock(block.getX(), block.getY(), block.getZ())
                );
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.BLUE + "Move selection blockPositionY " + ChatColor.GOLD + "x:" + this.moveDirections[0] + " y:" + this.moveDirections[1] + " z:" + this.moveDirections[2])
                .send();
    }

    private static class Selection {

        private final int maxBlockCount;

        /**
         * Calculated Locations of the selection.
         */
        private final List<BlockVector3> locations = new ArrayList<>();
        private BlockVector3 location1;
        private World world1;
        private BlockVector3 location2;
        private World world2;

        /**
         * Create a new limited selection.
         *
         * @param maxBlockCount maximum number of blocks allowed inside
         */
        public Selection(int maxBlockCount) {
            this.maxBlockCount = maxBlockCount;
        }

        /**
         * Calculates region, then saves all Blocks as Locations.
         *
         * @return boolean success.
         * @throws RuntimeException Messages to be sent to the player.
         */
        public boolean calculateRegion() {
            if (this.location1 != null && this.location2 != null) {
                if (world1.getName().equals(world2.getName())) {
                    int x1 = this.location1.getBlockX();
                    int x2 = this.location2.getBlockX();
                    int y1 = this.location1.getBlockY();
                    int y2 = this.location2.getBlockY();
                    int z1 = this.location1.getBlockZ();
                    int z2 = this.location2.getBlockZ();
                    int lowX = Math.min(x1, x2);
                    int lowY = Math.min(y1, y2);
                    int lowZ = Math.min(z1, z2);
                    int highX = Math.max(x1, x2);
                    int highY = Math.max(y1, y2);
                    int highZ = Math.max(z1, z2);
                    if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > this.maxBlockCount) {
                        throw new RuntimeException(ChatColor.RED + "Selection size above " + this.maxBlockCount + " limit, " +
                                "please use a smaller selection.");
                    }
                    for (int y = lowY; y <= highY; y++) {
                        for (int x = lowX; x <= highX; x++) {
                            for (int z = lowZ; z <= highZ; z++) {
                                this.locations.add(BlockVector3.at(x, y, z));
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the calculated locations of defined region.
         */
        public List<BlockVector3> getLocations() {
            return this.locations;
        }

        public BlockVector3 getLocation1() {
            return this.location1;
        }

        public World getWorld1() {
            return world1;
        }

        public void setLocation1(BlockVector3 location1, World world1) {
            this.location1 = location1;
            this.world1 = world1;
        }

        public BlockVector3 getLocation2() {
            return this.location2;
        }

        public World getWorld2() {
            return world2;
        }

        public void setLocation2(BlockVector3 location2, World world2) {
            this.location2 = location2;
            this.world2 = world2;
        }

    }

}
