package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class OceanBrush extends AbstractBrush {

    private static final int WATER_LEVEL_MIN = 12;
    private static final int LOW_CUT_LEVEL = 12;

    private static final int DEFAULT_WATER_LEVEL = 63;

    private static final MaterialSet EXCLUDED_MATERIALS = MaterialSet.builder()
            .with(BlockCategories.SAPLINGS)
            .with(BlockCategories.LOGS)
            .with(BlockCategories.LEAVES)
            .with(BlockCategories.ICE)
            .with(MaterialSets.AIRS)
            .with(MaterialSets.LIQUIDS)
            .with(BlockCategories.SNOW)
            .with(MaterialSets.STEMS)
            .with(MaterialSets.MUSHROOMS)
            .with(BlockCategories.FLOWERS)
            .add(BlockTypes.MELON)
            .add(BlockTypes.PUMPKIN)
            .add(BlockTypes.COCOA)
            .add(BlockTypes.SUGAR_CANE)
            .add(BlockTypes.TALL_GRASS)
            .build();

    private int waterLevelMin;
    private int lowCutLevel;

    private int waterLevel;
    private boolean coverFloor;

    @Override
    public void loadProperties() {
        this.waterLevelMin = getIntegerProperty("water-level-min", WATER_LEVEL_MIN);
        this.lowCutLevel = getIntegerProperty("low-cut-level", LOW_CUT_LEVEL);

        this.waterLevel = getIntegerProperty("default-water-lever", DEFAULT_WATER_LEVEL);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.BLUE + "Parameters:");
            messenger.sendMessage(ChatColor.GREEN + "/b o wlevel [n] " + ChatColor.BLUE + "-- Sets the water level to n.");
            messenger.sendMessage(ChatColor.GREEN + "/b o cfloor [true|false] " + ChatColor.BLUE + "-- Enables or disables " +
                    "sea floor cover. (e.g. /b o cfloor true -> Cover material will be your voxel material.)");
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("wlevel")) {
                    Integer waterLevel = NumericParser.parseInteger(parameters[1]);
                    if (waterLevel != null && waterLevel > this.waterLevelMin) {
                        this.waterLevel = waterLevel;
                        messenger.sendMessage(ChatColor.BLUE + "Water level set to: " + ChatColor.GREEN + this.waterLevel);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number, must be an integer greater than" +
                                this.waterLevelMin + ".");
                    }
                } else if (firstParameter.equalsIgnoreCase("cfloor")) {
                    this.coverFloor = Boolean.parseBoolean(parameters[1]);
                    messenger.sendMessage(ChatColor.BLUE + "Floor cover " + ChatColor.GREEN + (this.coverFloor ? "enabled" :
                            "disabled") + ChatColor.BLUE + ".");
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
            return super.sortCompletions(Stream.of("wlevel", "cfloor"), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (firstParameter.equalsIgnoreCase("cfloor")) {
                String parameter = parameters[1];
                return super.sortCompletions(Stream.of("true", "false"), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        oceanator(toolkitProperties);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        handleArrowAction(snipe);
    }

    private void oceanator(ToolkitProperties toolkitProperties) {
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockX = targetBlock.getX();
        int targetBlockZ = targetBlock.getZ();
        int brushSize = toolkitProperties.getBrushSize();
        int minX = (int) Math.floor(targetBlockX - brushSize);
        int minZ = (int) Math.floor(targetBlockZ - brushSize);
        int maxX = (int) Math.floor(targetBlockX + brushSize);
        int maxZ = (int) Math.floor(targetBlockZ + brushSize);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int currentHeight = getHeight(x, z);
                int wLevelDiff = currentHeight - this.waterLevel;
                int newSeaFloorLevel = Math.max((this.waterLevel - wLevelDiff), this.lowCutLevel);
                int highestY = getHighestTerrainBlock(x, z, editSession.getMinY(), editSession.getMaxY());
                // go down from highest Y block down to new sea floor
                for (int y = highestY; y > newSeaFloorLevel; y--) {
                    BlockState block = getBlock(x, y, z);
                    if (!block.isAir()) {
                        setBlock(x, y, z, BlockTypes.AIR);
                    }
                }
                // go down from water level to new sea level
                for (int y = this.waterLevel; y >= newSeaFloorLevel; y--) {
                    BlockState block = getBlock(x, y, z);
                    BlockType blockType = block.getBlockType();
                    if (blockType != BlockTypes.WATER) {
                        setBlock(x, y, z, BlockTypes.WATER);
                    }
                }
                // cover the sea floor of required
                if (this.coverFloor && (newSeaFloorLevel <= this.waterLevel)) {
                    setBlock(x, newSeaFloorLevel, z, toolkitProperties.getPattern().getPattern());
                }
            }
        }
    }

    private int getHeight(int bx, int bz) {
        EditSession editSession = getEditSession();
        for (int y = getHighestTerrainBlock(
                bx,
                bz,
                editSession.getMinY(),
                editSession.getMaxY()
        ); y > editSession.getMinY(); y--) {
            BlockState clamp = this.clampY(bx, y, bz);
            if (!EXCLUDED_MATERIALS.contains(clamp)) {
                return y;
            }
        }
        return 0;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.BLUE + "Water level set to: " + ChatColor.GREEN + this.waterLevel)
                .message(ChatColor.BLUE + String.format(
                        "Floor cover %s.",
                        ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")
                ))
                .send();
    }

}
