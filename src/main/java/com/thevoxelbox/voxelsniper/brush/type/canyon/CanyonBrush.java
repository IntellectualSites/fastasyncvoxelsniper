package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class CanyonBrush extends AbstractBrush {

    private static final int SHIFT_LEVEL_MIN = -54;
    private static final int SHIFT_LEVEL_MAX = 60;

    private static final int DEFAULT_Y_LEVEL = -54;

    private int shiftLevelMin;
    private int shiftLevelMax;

    private int yLevel;

    @Override
    public void loadProperties() {
        this.shiftLevelMin = getIntegerProperty("shift-level-min", SHIFT_LEVEL_MIN);
        this.shiftLevelMax = getIntegerProperty("shift-level-max", SHIFT_LEVEL_MAX);

        this.yLevel = getIntegerProperty("default-y-level", DEFAULT_Y_LEVEL);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Canyon Brush Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b ca y [n] -- Sets the y-level to which the land will be shifted down to n" +
                    ".");
        } else {
            if (parameters.length == 2) {

                if (firstParameter.equalsIgnoreCase("y")) {
                    Integer yLevel = NumericParser.parseInteger(parameters[1]);
                    if (yLevel != null) {
                        if (yLevel < this.shiftLevelMin) {
                            yLevel = this.shiftLevelMin;
                            messenger.sendMessage(ChatColor.RED + "Shift Level must be an integer greater than or equal to" +
                                    this.shiftLevelMax + ".");
                        } else if (yLevel > this.shiftLevelMax) {
                            yLevel = this.shiftLevelMax;
                            messenger.sendMessage(ChatColor.RED + "Shift Level must be an integer less than or equal to " +
                                    this.shiftLevelMin + ".");
                        }
                        this.yLevel = yLevel;
                        messenger.sendMessage(ChatColor.GREEN + "Shift Level set to: " + this.yLevel);
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
            return super.sortCompletions(Stream.of("y"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        canyon(targetBlock.getX() >> 4, targetBlock.getZ() >> 4);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkX + 1; z++) {
                canyon(x, z);
            }
        }
    }

    protected void canyon(int chunkX, int chunkZ) {
        EditSession editSession = getEditSession();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int currentYLevel = this.yLevel;
                for (int y = 63; y <= editSession.getMaxY(); y++) {
                    BlockType blockType = getBlockType(blockX + x, y, blockZ + z);
                    setBlock(blockX + x, currentYLevel, blockZ + z, blockType);
                    setBlock(blockX + x, y, blockZ + z, BlockTypes.AIR);
                    currentYLevel++;
                }
                setBlock(blockX + x, editSession.getMinY(), blockZ + z, BlockTypes.BEDROCK);
                for (int y = editSession.getMinY() + 1; y < this.shiftLevelMin; y++) {
                    setBlock(blockX + x, y, blockZ + z, BlockTypes.STONE);
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GREEN + "Shift Level set to: " + this.yLevel)
                .send();
    }

    public int getYLevel() {
        return this.yLevel;
    }

}
