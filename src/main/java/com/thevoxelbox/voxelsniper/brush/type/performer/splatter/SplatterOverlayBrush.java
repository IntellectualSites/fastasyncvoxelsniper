package com.thevoxelbox.voxelsniper.brush.type.performer.splatter;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class SplatterOverlayBrush extends AbstractPerformerBrush {

    private static final int GROW_PERCENT_MIN = 1;
    private static final int GROW_PERCENT_DEFAULT = 1000;
    private static final int GROW_PERCENT_MAX = 9999;
    private static final int SEED_PERCENT_MIN = 1;
    private static final int SEED_PERCENT_DEFAULT = 1000;
    private static final int SEED_PERCENT_MAX = 9999;
    private static final int SPLATTER_RECURSIONS_PERCENT_MIN = 1;
    private static final int SPLATTER_RECURSIONS_PERCENT_DEFAULT = 3;
    private static final int SPLATTER_RECURSIONS_PERCENT_MAX = 10;
    private final Random generator = new Random();
    private int seedPercent; // Chance block on first pass is made active
    private int growPercent; // chance block on recursion pass is made active
    private int splatterRecursions; // How many times you grow the seeds
    private int yOffset;
    private boolean randomizeHeight;
    private int depth = 3;
    private boolean allBlocks;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Splatter Overlay Brush Parameters:");
            messenger.sendMessage(ChatColor.BLUE + "/b sover all -- Sets the brush to overlay over ALL materials, not just " +
                    "natural surface ones (will no longer ignore trees and buildings).");
            messenger.sendMessage(ChatColor.BLUE + "/b sover some -- Sets the brush to overlay over natural surface " +
                    "materials.");
            messenger.sendMessage(ChatColor.AQUA + "/b sover randh -- Sets whether height should be randomized or not.");
            messenger.sendMessage(ChatColor.AQUA + "/b sover d [n] -- Sets how many blocks deep you want to replace " +
                    "from the surface to n.");
            messenger.sendMessage(ChatColor.AQUA + "/b sover s [n] -- Sets a seed percentage to n (1-9999). 100 = 1% " +
                    "Default is 1000.");
            messenger.sendMessage(ChatColor.AQUA + "/b sover g [n] -- Sets a growth percentage to n (1-9999). Default " +
                    "is 1000.");
            messenger.sendMessage(ChatColor.AQUA + "/b sover r [n] -- Sets a recursion to n (1-10). Default is 3.");
            messenger.sendMessage(ChatColor.AQUA + "/b sover yoff [n] -- Sets y offset to n.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("all")) {
                    this.allBlocks = true;
                    messenger.sendMessage(ChatColor.BLUE + "Will overlay over any block: " + this.depth);
                } else if (firstParameter.equalsIgnoreCase("some")) {
                    this.allBlocks = false;
                    messenger.sendMessage(ChatColor.BLUE + "Will overlay only natural block types: " + this.depth);
                } else if (firstParameter.equalsIgnoreCase("randh")) {
                    this.randomizeHeight = !this.randomizeHeight;
                    messenger.sendMessage(ChatColor.RED + "RandomizeHeight set to: " + this.randomizeHeight);
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("d")) {
                    Integer depth = NumericParser.parseInteger(parameters[1]);
                    if (depth != null) {
                        this.depth = depth < 1 ? 1 : depth;
                        messenger.sendMessage(ChatColor.AQUA + "Depth set to: " + this.depth);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else if (firstParameter.equalsIgnoreCase("s")) {
                    Integer seedPercent = NumericParser.parseInteger(parameters[1]);
                    if (seedPercent != null && seedPercent >= SEED_PERCENT_MIN && seedPercent <= SEED_PERCENT_MAX) {
                        this.seedPercent = seedPercent;
                        messenger.sendMessage(ChatColor.AQUA + "Seed percent set to: " + this.seedPercent / 100 + "%");
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
                    }
                } else if (firstParameter.equalsIgnoreCase("g")) {
                    Integer growPercent = NumericParser.parseInteger(parameters[1]);
                    if (growPercent != null && growPercent >= GROW_PERCENT_MIN && growPercent <= GROW_PERCENT_MAX) {
                        this.growPercent = growPercent;
                        messenger.sendMessage(ChatColor.AQUA + "Growth percent set to: " + this.growPercent / 100 + "%");
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
                    }
                } else if (firstParameter.equalsIgnoreCase("r")) {
                    Integer splatterRecursions = NumericParser.parseInteger(parameters[1]);
                    if (splatterRecursions != null && splatterRecursions >= SPLATTER_RECURSIONS_PERCENT_MIN
                            && splatterRecursions <= SPLATTER_RECURSIONS_PERCENT_MAX) {
                        this.splatterRecursions = splatterRecursions;
                        messenger.sendMessage(ChatColor.AQUA + "Recursions set to: " + this.splatterRecursions);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
                    }
                } else if (firstParameter.equalsIgnoreCase("yoff")) {
                    Integer yOffset = NumericParser.parseInteger(parameters[1]);
                    if (yOffset != null) {
                        this.yOffset = yOffset;
                        messenger.sendMessage(ChatColor.AQUA + "Y-Offset set to: " + this.yOffset);
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
            return super.sortCompletions(Stream.of("all", "some", "d", "s", "g", "r", "randh", "yoff"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        splatterOverlay(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        splatterOverlayTwo(snipe);
    }

    private void splatterOverlay(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        // Splatter Time
        int brushSize = toolkitProperties.getBrushSize();
        int[][] splat = new int[2 * brushSize + 1][2 * brushSize + 1];
        // Seed the array
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                if (this.generator.nextInt(SEED_PERCENT_MAX + 1) <= this.seedPercent) {
                    splat[x][y] = 1;
                }
            }
        }
        // Grow the seeds
        int gref = this.growPercent;
        int[][] tempSplat = new int[2 * brushSize + 1][2 * brushSize + 1];
        for (int r = 0; r < this.splatterRecursions; r++) {
            this.growPercent = gref - ((gref / this.splatterRecursions) * (r));
            for (int x = 2 * brushSize; x >= 0; x--) {
                for (int y = 2 * brushSize; y >= 0; y--) {
                    tempSplat[x][y] = splat[x][y]; // prime tempsplat
                    int growcheck = 0;
                    if (splat[x][y] == 0) {
                        if (x != 0 && splat[x - 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 0 && splat[x][y - 1] == 1) {
                            growcheck++;
                        }
                        if (x != 2 * brushSize && splat[x + 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 2 * brushSize && splat[x][y + 1] == 1) {
                            growcheck++;
                        }
                    }
                    if (growcheck >= 1 && this.generator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
                        tempSplat[x][y] = 1; // prevent bleed into splat
                    }
                }
            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * brushSize; x >= 0; x--) {
                if (2 * brushSize + 1 >= 0) {
                    System.arraycopy(tempSplat[x], 0, splat[x], 0, 2 * brushSize + 1);
                }
            }
        }
        this.growPercent = gref;
        int[][] memory = new int[2 * brushSize + 1][2 * brushSize + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockZ = targetBlock.getZ();
                for (int y = targetBlock.getY(); y > 0; y--) {
                    // start scanning from the height you clicked at
                    if (memory[x + brushSize][z + brushSize] != 1) {
                        // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared && splat[x + brushSize][z + brushSize] == 1) {
                            // if inside of the column && if to be splattered
                            BlockType check = this.getBlockType(blockX + x, y + 1, blockZ + z);
                            if (Materials.isEmpty(check) || check == BlockTypes.WATER) {
                                // must start at surface... this prevents it filling stuff in if you click in a wall
                                // and it starts out below surface.
                                if (this.allBlocks) {
                                    int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
                                    for (int i = this.depth - 1; ((this.depth - i) <= depth); i--) {
                                        if (!this.clampY(blockX + x, y - i, blockZ + z).isAir()) {
                                            // fills down as many layers as you specify in parameters
                                            this.performer.perform(
                                                    getEditSession(),
                                                    blockX + x,
                                                    clampY(y - i + this.yOffset),
                                                    blockZ + z,
                                                    clampY(blockX + x, y - i + this.yOffset, blockZ + z)
                                            );
                                            // stop it from checking any other blocks in this vertical 1x1 column.
                                            memory[x + brushSize][z + brushSize] = 1;
                                        }
                                    }
                                } else {
                                    // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                    if (MaterialSets.OVERRIDEABLE.contains(getBlockType(blockX + x, y, blockZ + z))) {
                                        int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
                                        for (int d = this.depth - 1; ((this.depth - d) <= depth); d--) {
                                            if (!this.clampY(blockX + x, y - d, blockZ + z).isAir()) {
                                                // fills down as many layers as you specify in parameters
                                                this.performer.perform(
                                                        getEditSession(),
                                                        blockX + x,
                                                        clampY(y - d + this.yOffset),
                                                        blockZ + z,
                                                        clampY(blockX + x, y - d + this.yOffset, blockZ + z)
                                                );
                                                // stop it from checking any other blocks in this vertical 1x1 column.
                                                memory[x + brushSize][z + brushSize] = 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void splatterOverlayTwo(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        // Splatter Time
        int brushSize = toolkitProperties.getBrushSize();
        int[][] splat = new int[2 * brushSize + 1][2 * brushSize + 1];
        // Seed the array
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                if (this.generator.nextInt(SEED_PERCENT_MAX + 1) <= this.seedPercent) {
                    splat[x][y] = 1;
                }
            }
        }
        // Grow the seeds
        int gref = this.growPercent;
        int[][] tempsplat = new int[2 * brushSize + 1][2 * brushSize + 1];
        for (int r = 0; r < this.splatterRecursions; r++) {
            this.growPercent = gref - ((gref / this.splatterRecursions) * (r));
            for (int x = 2 * brushSize; x >= 0; x--) {
                for (int y = 2 * brushSize; y >= 0; y--) {
                    tempsplat[x][y] = splat[x][y]; // prime tempsplat
                    int growcheck = 0;
                    if (splat[x][y] == 0) {
                        if (x != 0 && splat[x - 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 0 && splat[x][y - 1] == 1) {
                            growcheck++;
                        }
                        if (x != 2 * brushSize && splat[x + 1][y] == 1) {
                            growcheck++;
                        }
                        if (y != 2 * brushSize && splat[x][y + 1] == 1) {
                            growcheck++;
                        }
                    }
                    if (growcheck >= 1 && this.generator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
                        tempsplat[x][y] = 1; // prevent bleed into splat
                    }
                }
            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * brushSize; x >= 0; x--) {
                if (2 * brushSize + 1 >= 0) {
                    System.arraycopy(tempsplat[x], 0, splat[x], 0, 2 * brushSize + 1);
                }
            }
        }
        this.growPercent = gref;
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockZ = targetBlock.getZ();
                for (int y = targetBlock.getY(); y > 0; y--) { // start scanning from the height you clicked at
                    if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(
                                z,
                                2
                        )) <= brushSizeSquared && splat[x + brushSize][z + brushSize] == 1) { // if inside of the column...&& if to be splattered
                            if (!Materials.isEmpty(getBlockType(
                                    blockX + x,
                                    y - 1,
                                    blockZ + z
                            ))) { // if not a floating block (like one of Notch'world pools)
                                if (Materials.isEmpty(getBlockType(
                                        blockX + x,
                                        y + 1,
                                        targetBlock.getZ() + z
                                ))) { // must start at surface... this prevents it filling stuff in if
                                    // you click in a wall and it starts out below surface.
                                    if (this.allBlocks) {
                                        int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
                                        for (int i = 1; (i < depth + 1); i++) {
                                            this.performer.perform(
                                                    getEditSession(),
                                                    blockX + x,
                                                    clampY(y + i + this.yOffset),
                                                    blockZ + z,
                                                    clampY(blockX + x, y + i + this.yOffset, blockZ + z)
                                            ); // fills down as many layers as you specify in
                                            // parameters
                                            memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    } else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                        if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(getBlockType(
                                                blockX + x,
                                                y,
                                                blockZ + z
                                        ))) {
                                            int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
                                            for (int i = 1; (i < depth + 1); i++) {
                                                this.performer.perform(
                                                        getEditSession(),
                                                        blockX + x,
                                                        clampY(y + i + this.yOffset),
                                                        blockZ + z,
                                                        clampY(blockX + x, y + i + this.yOffset, blockZ + z)
                                                ); // fills down as many layers as you specify
                                                // in parameters
                                                memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        if (this.seedPercent < SEED_PERCENT_MIN || this.seedPercent > SEED_PERCENT_MAX) {
            this.seedPercent = SEED_PERCENT_DEFAULT;
        }
        if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX) {
            this.growPercent = GROW_PERCENT_DEFAULT;
        }
        if (this.splatterRecursions < SPLATTER_RECURSIONS_PERCENT_MIN || this.splatterRecursions > SPLATTER_RECURSIONS_PERCENT_MAX) {
            this.splatterRecursions = SPLATTER_RECURSIONS_PERCENT_DEFAULT;
        }
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(ChatColor.BLUE + "Seed percent set to: " + this.seedPercent / 100 + "%")
                .message(ChatColor.BLUE + "Growth percent set to: " + this.growPercent / 100 + "%")
                .message(ChatColor.BLUE + "Recursions set to: " + this.splatterRecursions)
                .message(ChatColor.BLUE + "Y-Offset set to: " + this.yOffset)
                .send();
    }

}
