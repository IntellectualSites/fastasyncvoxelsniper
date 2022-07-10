package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class FlatOceanBrush extends AbstractBrush {

    private static final int DEFAULT_WATER_LEVEL = 29;
    private static final int DEFAULT_FLOOR_LEVEL = 8;

    private int waterLevel;
    private int floorLevel;

    @Override
    public void loadProperties() {
        this.waterLevel = getIntegerProperty("default-water-level", DEFAULT_WATER_LEVEL);
        this.floorLevel = getIntegerProperty("default-floor-level", DEFAULT_FLOOR_LEVEL);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GREEN + "/b fo yo [n] -- Sets the level to which the water will rise to n.");
            messenger.sendMessage(ChatColor.GREEN + "/b fo yl [n] -- Sets the level to which the ocean floor will rise to n.");
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("yo")) {
                    Integer newWaterLevel = NumericParser.parseInteger(parameters[1]);
                    if (newWaterLevel != null) {
                        if (newWaterLevel < this.floorLevel) {
                            newWaterLevel = this.floorLevel + 1;
                        }
                        this.waterLevel = newWaterLevel;
                        messenger.sendMessage(ChatColor.GREEN + "Water level set to: " + this.waterLevel);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else if (firstParameter.equalsIgnoreCase("yl")) {
                    EditSession editSession = getEditSession();
                    Integer newFloorLevel = NumericParser.parseInteger(parameters[1]);
                    if (newFloorLevel != null) {
                        if (newFloorLevel > this.waterLevel) {
                            newFloorLevel = this.waterLevel - 1;
                            if (newFloorLevel <= editSession.getMinY()) {
                                newFloorLevel = editSession.getMinY() + 1;
                                this.waterLevel = editSession.getMinY() + 2;
                            }
                        }
                        this.floorLevel = newFloorLevel;
                        messenger.sendMessage(ChatColor.GREEN + "Ocean floor level set to: " + this.floorLevel);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display parameter " +
                        "info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("yo", "yl"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        flatOceanAtTarget();
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        flatOceanAtTarget();
        flatOceanAtTarget(CHUNK_SIZE, 0);
        flatOceanAtTarget(CHUNK_SIZE, CHUNK_SIZE);
        flatOceanAtTarget(0, CHUNK_SIZE);
        flatOceanAtTarget(-CHUNK_SIZE, CHUNK_SIZE);
        flatOceanAtTarget(-CHUNK_SIZE, 0);
        flatOceanAtTarget(-CHUNK_SIZE, -CHUNK_SIZE);
        flatOceanAtTarget(0, -CHUNK_SIZE);
        flatOceanAtTarget(CHUNK_SIZE, -CHUNK_SIZE);
    }

    private void flatOceanAtTarget(int additionalX, int additionalZ) {
        BlockVector3 targetBlock = getTargetBlock();
        int blockX = targetBlock.getX();
        int blockZ = targetBlock.getZ();
        flatOcean((blockX + additionalX) >> 4, (blockZ + additionalZ) >> 4);
    }

    private void flatOceanAtTarget() {
        BlockVector3 targetBlock = getTargetBlock();
        flatOcean(targetBlock.getX() >> 4, targetBlock.getZ() >> 4);
    }

    private void flatOcean(int chunkX, int chunkZ) {
        EditSession editSession = getEditSession();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = editSession.getMinY(); y <= editSession.getMaxY(); y++) {
                    if (y <= this.floorLevel) {
                        setBlockType(blockX + x, y, blockZ + z, BlockTypes.DIRT);
                    } else if (y <= this.waterLevel) {
                        setBlockType(blockX + x, y, blockZ + z, BlockTypes.WATER);
                    } else {
                        setBlockType(blockX + x, y, blockZ + z, BlockTypes.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GREEN + "Water level set to: " + this.waterLevel)
                .message(ChatColor.GREEN + "Ocean floor level set to: " + this.floorLevel)
                .send();
    }

}
