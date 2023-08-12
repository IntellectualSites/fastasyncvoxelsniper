package com.thevoxelbox.voxelsniper.brush.type.performer.splatter;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b splatter_voxel|splattervoxel|sv")
@CommandPermission("voxelsniper.brush.splattervoxel")
public class SplatterVoxelBrush extends AbstractPerformerBrush {

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.splatter-voxel.info"));
    }

    @CommandMethod("s <seed-percent>")
    public void onBrushS(
            final @NotNull Snipe snipe,
            final @Argument("seed-percent") @DynamicRange(min = "seedPercentMin", max = "seedPercentMax") int seedPercent
    ) {
        this.seedPercent = seedPercent;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-voxel.set-seed-percent",
                DECIMAL_FORMAT.format(this.seedPercent / 100)
        ));
    }

    @CommandMethod("g <growth-percent>")
    public void onBrushG(
            final @NotNull Snipe snipe,
            final @Argument("growth-percent") @DynamicRange(min = "growthPercentMin", max = "growthPercentMax") int growthPercent
    ) {
        this.growthPercent = growthPercent;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-voxel.set-growth-percent",
                DECIMAL_FORMAT.format(this.growthPercent / 100)
        ));
    }

    @CommandMethod("r <splatter-recursions>")
    public void onBrushR(
            final @NotNull Snipe snipe,
            final @Argument("splatter-recursions") @DynamicRange(min = "splatterRecursionsMin", max = "splatterRecursionsMax") int splatterRecursions
    ) {
        this.splatterRecursions = splatterRecursions;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-voxel.set-splatter-recursions",
                this.splatterRecursions
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        voxelSplatterBall(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        voxelSplatterBall(snipe, lastBlock);
    }

    private void voxelSplatterBall(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.seedPercent < super.seedPercentMin || this.seedPercent > super.seedPercentMax) {
            this.seedPercent = getIntegerProperty("default-seed-percent", DEFAULT_SEED_PERCENT);
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.splatter-voxel.set-seed-percent",
                    DECIMAL_FORMAT.format(this.seedPercent / 100)
            ));
        }
        if (this.growthPercent < super.growthPercentMin || this.growthPercent > super.growthPercentMax) {
            this.growthPercent = getIntegerProperty("default-grow-percent", DEFAULT_GROWTH_PERCENT);
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.splatter-voxel.set-growth-percent",
                    DECIMAL_FORMAT.format(this.growthPercent / 100)
            ));
        }
        if (this.splatterRecursions < super.splatterRecursionsMin || this.splatterRecursions > super.splatterRecursionsMax) {
            this.splatterRecursions = getIntegerProperty("default-splatter-recursions", DEFAULT_SPLATTER_RECURSIONS);
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.splatter-voxel.set-splatter-recursions",
                    this.splatterRecursions
            ));
        }
        int brushSize = toolkitProperties.getBrushSize();
        int[][][] splat = new int[2 * brushSize + 1][2 * brushSize + 1][2 * brushSize + 1];
        // Seed the array
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    if (super.generator.nextInt(super.seedPercentMax + 1) <= this.seedPercent) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        // Grow the seeds
        int gref = this.growthPercent;
        int[][][] tempSplat = new int[2 * brushSize + 1][2 * brushSize + 1][2 * brushSize + 1];
        for (int r = 0; r < this.splatterRecursions; r++) {
            this.growthPercent = gref - ((gref / this.splatterRecursions) * r);
            for (int x = 2 * brushSize; x >= 0; x--) {
                for (int y = 2 * brushSize; y >= 0; y--) {
                    for (int z = 2 * brushSize; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z]; // prime tempsplat
                        int growcheck = 0;
                        if (splat[x][y][z] == 0) {
                            if (x != 0 && splat[x - 1][y][z] == 1) {
                                growcheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 1) {
                                growcheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 1) {
                                growcheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 1) {
                                growcheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 1) {
                                growcheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 1) {
                                growcheck++;
                            }
                        }
                        if (growcheck >= 1 && super.generator.nextInt(super.growthPercentMax + 1) <= this.growthPercent) {
                            tempSplat[x][y][z] = 1; // prevent bleed into splat
                        }
                    }
                }
            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * brushSize; x >= 0; x--) {
                for (int y = 2 * brushSize; y >= 0; y--) {
                    if (2 * brushSize + 1 >= 0) {
                        System.arraycopy(tempSplat[x][y], 0, splat[x][y], 0, 2 * brushSize + 1);
                    }
                }
            }
        }
        this.growthPercent = gref;
        // Fill 1x1x1 holes
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    if (splat[Math.max(x - 1, 0)][y][z] == 1 && splat[Math.min(
                            x + 1,
                            2 * brushSize
                    )][y][z] == 1 && splat[x][Math.max(0, y - 1)][z] == 1 && splat[x][Math.min(2 * brushSize, y + 1)][z] == 1) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        // Make the changes
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    if (splat[x][y][z] == 1) {
                        this.performer.perform(
                                getEditSession(),
                                blockX - brushSize + x,
                                blockY - brushSize + z,
                                blockZ - brushSize + y,
                                getBlock(blockX - brushSize + x, blockY - brushSize + z, blockZ - brushSize + y)
                        );
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        if (this.seedPercent < super.seedPercentMin || this.seedPercent > super.seedPercentMax) {
            this.seedPercent = getIntegerProperty("default-seed-percent", DEFAULT_SEED_PERCENT);
        }
        if (this.growthPercent < super.growthPercentMin || this.growthPercent > super.growthPercentMax) {
            this.growthPercent = getIntegerProperty("default-grow-percent", DEFAULT_GROWTH_PERCENT);
        }
        if (this.splatterRecursions < super.splatterRecursionsMin || this.splatterRecursions > super.splatterRecursionsMax) {
            this.splatterRecursions = getIntegerProperty("default-splatter-recursions", DEFAULT_SPLATTER_RECURSIONS);
        }
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(Caption.of(
                        "voxelsniper.brush.splatter-voxel.set-seed-percent",
                        DECIMAL_FORMAT.format(this.seedPercent / 100)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-voxel.set-growth-percent",
                        DECIMAL_FORMAT.format(this.growthPercent / 100)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-voxel.set-splatter-recursions",
                        this.splatterRecursions
                ))
                .send();
    }

}
