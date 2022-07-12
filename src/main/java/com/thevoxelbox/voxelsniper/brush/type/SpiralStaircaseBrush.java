package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class SpiralStaircaseBrush extends AbstractBrush {

    private static final String DEFAULT_SDIRECT = "c";
    private static final String DEFAULT_SOPEN = "n";

    private String sdirect; // "c" clockwise (default), "cc" counter-clockwise
    private String sopen; // "n" north (default), "e" east, "s" south, "w" west

    @Override
    public void loadProperties() {
        this.sdirect = getStringProperty("default-sdirect", DEFAULT_SDIRECT);
        this.sopen = getStringProperty("default-sopen", DEFAULT_SOPEN);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Spiral Staircase Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/vh [n] -- Sets height to n.");
            messenger.sendMessage(ChatColor.AQUA + "/v [b] -- Sets block/step/stair to b.");
            messenger.sendMessage(ChatColor.AQUA + "/b sstair [c|cc] -- Sets the turning direction of staircase.");
            messenger.sendMessage(ChatColor.AQUA + "/b sstair [n|e|s|w] -- Sets the opening direction of staircase.");
        } else {
            if (parameters.length == 1) {
                if (Stream.of("c", "cc")
                        .anyMatch(firstParameter::equalsIgnoreCase)) {
                    this.sdirect = firstParameter;
                    messenger.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
                } else if (Stream.of("n", "e", "s", "w")
                        .anyMatch(firstParameter::equalsIgnoreCase)) {
                    this.sopen = firstParameter;
                    messenger.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
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
            return super.sortCompletions(Stream.of(
                            "c", "cc",
                            "n", "e", "s", "w"
                    ), parameter, 0
            );
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        buildSpiralStaircase(snipe, targetBlock, getStairType(snipe), false); // make stairwell below target
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        buildSpiralStaircase(snipe, lastBlock, getStairType(snipe), true); // make stairwell above target
    }

    private void buildSpiralStaircase(Snipe snipe, BlockVector3 targetBlock, StairType stairType, boolean up) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        int voxelHeight = toolkitProperties.getVoxelHeight();
        if (voxelHeight < 1) {
            toolkitProperties.setVoxelHeight(1);
            messenger.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Now set to 1.");
        }
        // Initialize array.
        int brushSize = toolkitProperties.getBrushSize();
        // Locate first block in staircase.
        int startX;
        int startZ;
        if (this.sdirect.equalsIgnoreCase("cc")) {
            if (this.sopen.equalsIgnoreCase("n")) {
                startX = 0;
                startZ = 2 * brushSize;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startX = 0;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startX = 2 * brushSize;
                startZ = 0;
            } else {
                startX = 2 * brushSize;
                startZ = 2 * brushSize;
            }
        } else {
            if (this.sopen.equalsIgnoreCase("n")) {
                startX = 0;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startX = 2 * brushSize;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startX = 2 * brushSize;
                startZ = 2 * brushSize;
            } else {
                startX = 0;
                startZ = 2 * brushSize;
            }
        }
        int toggle = 0;
        int xOffset = 0;
        int zOffset = 0;
        int y = 0;
        int[][][] spiral = new int[2 * brushSize + 1][voxelHeight][2 * brushSize + 1];

        while (y < voxelHeight) {
            if (stairType == StairType.BLOCK) {
                // 1x1x1 voxel material steps
                spiral[startX + xOffset][y][startZ + zOffset] = 1;
                y++;
            } else if (stairType == StairType.STEP) {
                // alternating step-doublestep, uses data value to determine type
                switch (toggle) {
                    case 0, 1 -> {
                        toggle = 2;
                        spiral[startX + xOffset][y][startZ + zOffset] = up ? 1 : 2;
                    }
                    case 2 -> {
                        toggle = 1;
                        spiral[startX + xOffset][y][startZ + zOffset] = up ? 2 : 1;
                        y++;
                    }
                    default -> {
                    }
                }
            }
            // Adjust horizontal position and do stair-option array stuff
            if (startX + xOffset == 0) { // All North
                if (startZ + zOffset == 0) { // NORTHEAST
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xOffset++;
                    } else {
                        zOffset++;
                    }
                } else if (startZ + zOffset == 2 * brushSize) { // NORTHWEST
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zOffset--;
                    } else {
                        xOffset++;
                    }
                } else { // JUST PLAIN NORTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (stairType == StairType.STAIR) {
                            spiral[startX + xOffset][y][startZ + zOffset] = up ? 5 : 4;
                            y++;
                        }
                        zOffset--;
                    } else {
                        if (stairType == StairType.STAIR) {
                            spiral[startX + xOffset][y][startZ + zOffset] = up ? 4 : 5;
                            y++;
                        }
                        zOffset++;
                    }
                }
            } else if (startX + xOffset == 2 * brushSize) { // ALL SOUTH
                if (startZ + zOffset == 0) { // SOUTHEAST
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zOffset++;
                    } else {
                        xOffset--;
                    }
                } else if (startZ + zOffset == 2 * brushSize) { // SOUTHWEST
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xOffset--;
                    } else {
                        zOffset--;
                    }
                } else { // JUST PLAIN SOUTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (stairType == StairType.STAIR) {
                            spiral[startX + xOffset][y][startZ + zOffset] = up ? 4 : 5;
                            y++;
                        }
                        zOffset++;
                    } else {
                        if (stairType == StairType.STAIR) {
                            spiral[startX + xOffset][y][startZ + zOffset] = up ? 5 : 4;
                            y++;
                        }
                        zOffset--;
                    }
                }
            } else if (startZ + zOffset == 0) { // JUST PLAIN EAST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = up ? 2 : 3;
                        y++;
                    }
                    xOffset++;
                } else {
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = up ? 3 : 2;
                        y++;
                    }
                    xOffset--;
                }
            } else { // JUST PLAIN WEST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = up ? 3 : 2;
                        y++;
                    }
                    xOffset--;
                } else {
                    if (stairType == StairType.STAIR) {
                        spiral[startX + xOffset][y][startZ + zOffset] = up ? 2 : 3;
                        y++;
                    }
                    xOffset++;
                }
            }
        }

        // Make the changes.
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int i = voxelHeight - 1; i >= 0; i--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    int blockPositionX = targetBlock.getX();
                    int blockPositionY = targetBlock.getY();
                    int blockPositionZ = targetBlock.getZ();

                    BlockVector3 position = BlockVector3.at(
                            blockPositionX - brushSize + x,
                            up ? blockPositionY + i : blockPositionY - i,
                            blockPositionZ - brushSize + z
                    );
                    if (spiral[x][i][z] == 0) {
                        if (up) {
                            if (i == voxelHeight - 1) {
                                setBlockType(position, BlockTypes.AIR);
                            } else {
                                if (!((stairType == StairType.STAIR) && spiral[x][i + 1][z] == 1)) {
                                    setBlockType(position, BlockTypes.AIR);
                                }
                            }
                        } else {
                            setBlockType(position, BlockTypes.AIR);
                        }
                    } else if (spiral[x][i][z] == 1) {
                        if (stairType == StairType.BLOCK) {
                            setBlockType(position, toolkitProperties.getBlockType());
                        } else if (stairType == StairType.STEP) {
                            setBlockType(position, toolkitProperties.getBlockType());
                        } else if (stairType == StairType.STAIR) {
                            BlockType newState = BlockTypes.get(toolkitProperties.getBlockType().getId()
                                    .replace("stairs", "slab"));
                            setBlockData(
                                    up ? BlockVector3.at(
                                            blockPositionX - brushSize + x,
                                            blockPositionY + i - 1,
                                            blockPositionZ - brushSize + z
                                    ) : position,
                                    newState == null ? toolkitProperties.getBlockType().getDefaultState() :
                                            newState.getDefaultState().with(PropertyKey.TYPE, "top")
                            );
                        }
                    } else if (spiral[x][i][z] == 2) {
                        if (stairType == StairType.STEP) {
                            setBlockData(
                                    position,
                                    toolkitProperties.getBlockType().getDefaultState().with(PropertyKey.TYPE, "top")
                            );
                        } else if (stairType == StairType.STAIR) {
                            setBlockData(
                                    position,
                                    toolkitProperties.getBlockType().getDefaultState().with(PropertyKey.FACING, Direction.EAST)
                            );
                        }
                    } else if (stairType == StairType.STAIR) {
                        setBlockData(
                                position,
                                toolkitProperties
                                        .getBlockType()
                                        .getDefaultState()
                                        .with(PropertyKey.FACING, dataToDirection(spiral[x][i][z] - 2))
                        );
                    }
                }
            }
        }
    }

    private static Direction dataToDirection(int data) {
        return switch (data) {
            case 3 -> Direction.NORTH;
            case 2 -> Direction.SOUTH;
            case 1 -> Direction.WEST;
            default -> Direction.EAST;
        };
    }

    private static StairType getStairType(Snipe snipe) {
        BlockType blockType = snipe.getToolkitProperties().getBlockType();
        if (Materials.isSlab(blockType)) {
            return StairType.STEP;
        }
        if (Materials.isStair(blockType)) {
            return StairType.STAIR;
        }
        return StairType.BLOCK;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .blockTypeMessage()
                .blockDataMessage()
                .voxelHeightMessage()
                .message(ChatColor.BLUE + "Staircase type: " + getStairType(snipe).getName())
                .message(ChatColor.BLUE + "Staircase turns: " + this.sdirect)
                .message(ChatColor.BLUE + "Staircase opens: " + this.sopen)
                .send();
    }

    private enum StairType {
        BLOCK("Block"),
        STEP("Step"),
        STAIR("Stair");

        private final String name;

        StairType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
