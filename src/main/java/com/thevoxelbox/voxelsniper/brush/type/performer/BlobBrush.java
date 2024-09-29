package com.thevoxelbox.voxelsniper.brush.type.performer;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b blob|splatblob")
@Permission("voxelsniper.brush.blob")
public class BlobBrush extends AbstractPerformerBrush {

    @Override
    public void loadProperties() {
        this.growthPercentMin = getIntegerProperty("growth-percent-min", GROWTH_PERCENT_MIN);
        this.growthPercentMax = getIntegerProperty("growth-percent-max", GROWTH_PERCENT_MAX);

        this.growthPercent = getIntegerProperty("default-growth-percent", DEFAULT_GROWTH_PERCENT);
    }

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Command("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.blob.info",
                this.growthPercentMin, this.growthPercentMax, DEFAULT_GROWTH_PERCENT
        ));
    }

    @Command("g <growth-percent>")
    public void onBrushG(
            final @NotNull Snipe snipe,
            final @Argument("growth-percent") @DynamicRange(min = "growthPercentMin", max = "growthPercentMax") int growthPercent
    ) {
        this.growthPercent = growthPercent;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.blob.set-growth-percent",
                DECIMAL_FORMAT.format(this.growthPercent / 100)
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        growBlob(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        digBlob(snipe);
    }

    private void digBlob(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        if (checkValidgrowthPercent()) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of(
                    "voxelsniper.performer-brush.blob.set-growth-percent",
                    DECIMAL_FORMAT.format(this.growthPercent / 100)
            ));
        }
        // Seed the array
        int brushSizeDoubled = 2 * brushSize;
        int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        for (int x = brushSizeDoubled; x >= 0; x--) {
            for (int y = brushSizeDoubled; y >= 0; y--) {
                for (int z = brushSizeDoubled; z >= 0; z--) {
                    splat[x][y][z] =
                            (x == 0 || y == 0 | z == 0 || x == brushSizeDoubled || y == brushSizeDoubled || z == brushSizeDoubled) && super.generator
                                    .nextInt(super.growthPercentMax + 1) <= this.growthPercent ? 0 : 1;
                }
            }
        }
        // Grow the seed
        int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        for (int r = 0; r < brushSize; r++) {
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z];
                        double growCheck = 0;
                        if (splat[x][y][z] == 1) {
                            if (x != 0 && splat[x - 1][y][z] == 0) {
                                growCheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 0) {
                                growCheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 0) {
                                growCheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 0) {
                                growCheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 0) {
                                growCheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 0) {
                                growCheck++;
                            }
                        }
                        if (growCheck >= 1 && super.generator.nextInt(super.growthPercentMax + 1) <= this.growthPercent) {
                            tempSplat[x][y][z] = 0; // prevent bleed into splat
                        }
                    }
                }
            }
            // shouldn't this just be splat = tempsplat;? -Gavjenks
            // integrate tempsplat back into splat at end of iteration
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    System.arraycopy(tempSplat[x][y], 0, splat[x][y], 0, brushSizeDoubled + 1);
                }
            }
        }
        double rSquared = Math.pow(brushSize + 1, 2);
        // Make the changes
        for (int x = brushSizeDoubled; x >= 0; x--) {
            double xSquared = Math.pow(x - brushSize - 1, 2);
            for (int y = brushSizeDoubled; y >= 0; y--) {
                double ySquared = Math.pow(y - brushSize - 1, 2);
                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
                        BlockVector3 targetBlock = this.getTargetBlock();
                        this.performer.perform(
                                getEditSession(),
                                targetBlock.getX() - brushSize + x,
                                clampY(targetBlock.getY() - brushSize + z),
                                targetBlock.getZ() - brushSize + y,
                                this.clampY(
                                        targetBlock.getX() - brushSize + x,
                                        targetBlock.getY() - brushSize + z,
                                        targetBlock.getZ() - brushSize + y
                                )
                        );
                    }
                }
            }
        }
    }

    private void growBlob(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        if (checkValidgrowthPercent()) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of(
                    "voxelsniper.performer-brush.blob.set-growth-percent",
                    DECIMAL_FORMAT.format(this.growthPercent / 100)
            ));
        }
        // Seed the array
        int brushSizeDoubled = 2 * brushSize;
        int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        splat[brushSize][brushSize][brushSize] = 1;
        // Grow the seed
        int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        for (int r = 0; r < brushSize; r++) {
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    for (int z = brushSizeDoubled; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z];
                        int growCheck = 0;
                        if (splat[x][y][z] == 0) {
                            if (x != 0 && splat[x - 1][y][z] == 1) {
                                growCheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 1) {
                                growCheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 1) {
                                growCheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 1) {
                                growCheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 1) {
                                growCheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 1) {
                                growCheck++;
                            }
                        }
                        if (growCheck >= 1 && super.generator.nextInt(super.growthPercentMax + 1) <= this.growthPercent) {
                            // prevent bleed into splat
                            tempSplat[x][y][z] = 1;
                        }
                    }
                }
            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = brushSizeDoubled; x >= 0; x--) {
                for (int y = brushSizeDoubled; y >= 0; y--) {
                    System.arraycopy(tempSplat[x][y], 0, splat[x][y], 0, brushSizeDoubled + 1);
                }
            }
        }
        double rSquared = Math.pow(brushSize + 1, 2);
        // Make the changes
        for (int x = brushSizeDoubled; x >= 0; x--) {
            double xSquared = Math.pow(x - brushSize - 1, 2);
            for (int y = brushSizeDoubled; y >= 0; y--) {
                double ySquared = Math.pow(y - brushSize - 1, 2);
                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
                        BlockVector3 targetBlock = this.getTargetBlock();
                        this.performer.perform(
                                getEditSession(),
                                targetBlock.getX() - brushSize + x,
                                clampY(targetBlock.getY() - brushSize + z),
                                targetBlock.getZ() - brushSize + y,
                                this.clampY(
                                        targetBlock.getX() - brushSize + x,
                                        targetBlock.getY() - brushSize + z,
                                        targetBlock.getZ() - brushSize + y
                                )
                        );
                    }
                }
            }
        }
    }

    private boolean checkValidgrowthPercent() {
        if (this.growthPercent < super.growthPercentMin || this.growthPercent > super.growthPercentMax) {
            this.growthPercent = getIntegerProperty("default-grow-percent", DEFAULT_GROWTH_PERCENT);
            return true;
        }
        return false;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        checkValidgrowthPercent();
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.blob.set-growth-percent",
                        DECIMAL_FORMAT.format(this.growthPercent / 100)
                ))
                .send();
    }

}
