package com.thevoxelbox.voxelsniper.brush.type.performer.splatter;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class SplatterVoxelDiscBrush extends AbstractPerformerBrush {

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

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Splatter Voxel Disc Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b svd s [n] -- Sets a seed percentage to n (1-9999). 100 = 1% Default is " +
                    "1000.");
            messenger.sendMessage(ChatColor.AQUA + "/b svd g [n] -- Sets a growth percentage to n (1-9999). Default is 1000.");
            messenger.sendMessage(ChatColor.AQUA + "/b svd r [n] -- Sets a recursion i (1-10). Default is 3.");
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("s")) {
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
            return super.sortCompletions(Stream.of("s", "g", "r"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        vSplatterDisc(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        vSplatterDisc(snipe, lastBlock);
    }

    private void vSplatterDisc(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.seedPercent < SEED_PERCENT_MIN || this.seedPercent > SEED_PERCENT_MAX) {
            messenger.sendMessage(ChatColor.BLUE + "Seed percent set to: 10%");
            this.seedPercent = SEED_PERCENT_DEFAULT;
        }
        if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX) {
            messenger.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
            this.growPercent = GROW_PERCENT_DEFAULT;
        }
        if (this.splatterRecursions < SPLATTER_RECURSIONS_PERCENT_MIN || this.splatterRecursions > SPLATTER_RECURSIONS_PERCENT_MAX) {
            messenger.sendMessage(ChatColor.BLUE + "Recursions set to: 3");
            this.splatterRecursions = SPLATTER_RECURSIONS_PERCENT_DEFAULT;
        }
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
        // Fill 1x1 holes
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                if (splat[Math.max(x - 1, 0)][y] == 1 && splat[Math.min(x + 1, 2 * brushSize)][y] == 1 && splat[x][Math.max(
                        0,
                        y - 1
                )] == 1 && splat[x][Math.min(2 * brushSize, y + 1)] == 1) {
                    splat[x][y] = 1;
                }
            }
        }
        // Make the changes
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                if (splat[x][y] == 1) {
                    this.performer.perform(
                            getEditSession(),
                            blockX - brushSize + x,
                            clampY(blockY),
                            blockZ - brushSize + y,
                            clampY(blockX - brushSize + x, blockY, blockZ - brushSize + y)
                    );
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
                .send();
    }

}
