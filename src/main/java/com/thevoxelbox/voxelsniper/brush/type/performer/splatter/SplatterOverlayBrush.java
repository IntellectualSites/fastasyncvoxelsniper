package com.thevoxelbox.voxelsniper.brush.type.performer.splatter;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b splatter_overlay|splatteroverlay|sover")
@CommandPermission("voxelsniper.brush.splatteroverlay")
public class SplatterOverlayBrush extends AbstractPerformerBrush {

    private static final int DEFAULT_DEPTH = 3;
    private static final int DEFAULT_Y_OFFSET = 0;

    private boolean randomizeHeight;
    private boolean allBlocks;

    private int depth;
    private int yOffset;

    @Override
    public void loadProperties() {
        super.loadProperties();
        this.depth = getIntegerProperty("default-depth", DEFAULT_DEPTH);
        this.yOffset = getIntegerProperty("default-y-offset", DEFAULT_Y_OFFSET);
    }

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.splatter-overlay.info"));
    }

    @CommandMethod("all")
    public void onBrushAll(
            final @NotNull Snipe snipe
    ) {
        this.allBlocks = true;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-overlay.set-overlay-all",
                VoxelSniperText.getStatus(true)
        ));
    }

    @CommandMethod("some")
    public void onBrushSome(
            final @NotNull Snipe snipe
    ) {
        this.allBlocks = false;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-overlay.set-overlay-natural",
                VoxelSniperText.getStatus(true)
        ));
    }

    @CommandMethod("randh")
    public void onBrushRandh(
            final @NotNull Snipe snipe
    ) {
        this.randomizeHeight = !this.randomizeHeight;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-overlay.set-randomize-height",
                VoxelSniperText.getStatus(this.randomizeHeight)
        ));
    }

    @CommandMethod("d <depth>")
    public void onBrushD(
            final @NotNull Snipe snipe,
            final @Argument("depth") @Range(min = "1") int depth
    ) {
        this.depth = depth;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-overlay.set-depth",
                this.depth
        ));
    }

    @CommandMethod("s <seed-percent>")
    public void onBrushS(
            final @NotNull Snipe snipe,
            final @Argument("seed-percent") @DynamicRange(min = "seedPercentMin", max = "seedPercentMax") int seedPercent
    ) {
        this.seedPercent = seedPercent;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-overlay.set-seed-percent",
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
                "voxelsniper.brush.splatter-overlay.set-growth-percent",
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
                "voxelsniper.brush.splatter-overlay.set-splatter-recursions",
                this.splatterRecursions
        ));
    }

    @CommandMethod("yoff <y-offset>")
    public void onBrushY(
            final @NotNull Snipe snipe,
            final @Argument("y-offset") int yOffset
    ) {
        this.yOffset = yOffset;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.splatter-overlay.set-y-offset",
                this.yOffset
        ));
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
        EditSession editSession = getEditSession();
        // Splatter Time
        int brushSize = toolkitProperties.getBrushSize();
        int[][] splat = new int[2 * brushSize + 1][2 * brushSize + 1];
        // Seed the array
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                if (super.generator.nextInt(super.seedPercentMax + 1) <= this.seedPercent) {
                    splat[x][y] = 1;
                }
            }
        }
        // Grow the seeds
        int gref = this.growthPercent;
        int[][] tempSplat = new int[2 * brushSize + 1][2 * brushSize + 1];
        for (int r = 0; r < this.splatterRecursions; r++) {
            this.growthPercent = gref - ((gref / this.splatterRecursions) * r);
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
                    if (growcheck >= 1 && super.generator.nextInt(super.growthPercentMax + 1) <= this.growthPercent) {
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
        this.growthPercent = gref;
        boolean[][] memory = new boolean[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockZ = targetBlock.getZ();
                for (int y = targetBlock.getY(); y >= editSession.getMinY(); y--) {
                    // start scanning from the height you clicked at
                    boolean noSurfaceFound = !memory[x + brushSize][z + brushSize];
                    if (noSurfaceFound) {
                        // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared && splat[x + brushSize][z + brushSize] == 1) {
                            // if inside of the column && if to be splattered
                            BlockType check = this.getBlockType(blockX + x, y + 1, blockZ + z);
                            if (Materials.isEmpty(check) || check == BlockTypes.WATER) {
                                // must start at surface... this prevents it filling stuff in if you click in a wall
                                // and it starts out below surface.
                                if (this.allBlocks) {
                                    int depth = this.randomizeHeight ? super.generator.nextInt(this.depth) : this.depth;
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
                                            memory[x + brushSize][z + brushSize] = true;
                                        }
                                    }
                                } else {
                                    // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                    if (MaterialSets.OVERRIDEABLE.contains(getBlockType(blockX + x, y, blockZ + z))) {
                                        int depth = this.randomizeHeight ? super.generator.nextInt(this.depth) : this.depth;
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
                                                memory[x + brushSize][z + brushSize] = true;
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
        EditSession editSession = getEditSession();
        // Splatter Time
        int brushSize = toolkitProperties.getBrushSize();
        int[][] splat = new int[2 * brushSize + 1][2 * brushSize + 1];
        // Seed the array
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                if (super.generator.nextInt(super.seedPercentMax + 1) <= this.seedPercent) {
                    splat[x][y] = 1;
                }
            }
        }
        // Grow the seeds
        int gref = this.growthPercent;
        int[][] tempsplat = new int[2 * brushSize + 1][2 * brushSize + 1];
        for (int r = 0; r < this.splatterRecursions; r++) {
            this.growthPercent = gref - ((gref / this.splatterRecursions) * r);
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
                    if (growcheck >= 1 && super.generator.nextInt(super.growthPercentMax + 1) <= this.growthPercent) {
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
        this.growthPercent = gref;
        boolean[][] memory = new boolean[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockZ = targetBlock.getZ();
                for (int y = targetBlock.getY(); y >= editSession.getMinY(); y--) { // start scanning from the height you clicked at
                    boolean noSurfaceFound = !memory[x + brushSize][z + brushSize];
                    if (noSurfaceFound) {
                        // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared
                                && splat[x + brushSize][z + brushSize] == 1) {
                            // if inside of the column...&& if to be splattered
                            boolean nonEmptyMaterialBelow = !Materials.isEmpty(getBlockType(
                                    blockX + x,
                                    y - 1,
                                    blockZ + z
                            ));
                            if (nonEmptyMaterialBelow) {
                                // if not a floating block (like one of Notch'world pools)
                                if (Materials.isEmpty(getBlockType(
                                        blockX + x,
                                        y + 1,
                                        targetBlock.getZ() + z
                                ))) {
                                    // must start at surface... this prevents it filling stuff in if
                                    // you click in a wall and it starts out below surface.
                                    BlockType type = getBlockType(blockX + x, y, blockZ + z);
                                    if (this.allBlocks) {
                                        boolean nonEmptyMaterial = !Materials.isEmpty(type);
                                        if (nonEmptyMaterial) {
                                            int depth = this.randomizeHeight ? super.generator.nextInt(this.depth) : this.depth;
                                            for (int i = 1; (i < depth + 1); i++) {
                                                // fills down as many layers as you specify in parameters
                                                this.performer.perform(
                                                        getEditSession(),
                                                        blockX + x,
                                                        clampY(y + i + this.yOffset),
                                                        blockZ + z,
                                                        clampY(blockX + x, y + i + this.yOffset, blockZ + z)
                                                );
                                                // stop it from checking any other blocks in this vertical 1x1 column.
                                                memory[x + brushSize][z + brushSize] = true;
                                            }
                                        }
                                    } else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                        if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(type)) {
                                            int depth = this.randomizeHeight ? super.generator.nextInt(this.depth) : this.depth;
                                            for (int i = 1; (i < depth + 1); i++) {
                                                // fills down as many layers as you specify in parameters
                                                this.performer.perform(
                                                        getEditSession(),
                                                        blockX + x,
                                                        clampY(y + i + this.yOffset),
                                                        blockZ + z,
                                                        clampY(blockX + x, y + i + this.yOffset, blockZ + z)
                                                );
                                                // stop it from checking any other blocks in this vertical 1x1 column.
                                                memory[x + brushSize][z + brushSize] = true;
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
                        "voxelsniper.brush.splatter-overlay.set-overlay-all",
                        VoxelSniperText.getStatus(this.allBlocks)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-overlay.set-overlay-natural",
                        VoxelSniperText.getStatus(!this.allBlocks)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-overlay.set-randomize-height",
                        VoxelSniperText.getStatus(this.randomizeHeight)
                ))
                .message(Caption.of("voxelsniper.brush.splatter-overlay.set-depth", this.depth))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-overlay.set-seed-percent",
                        DECIMAL_FORMAT.format(this.seedPercent / 100)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-overlay.set-growth-percent",
                        DECIMAL_FORMAT.format(this.growthPercent / 100)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-overlay.set-splatter-recursions",
                        this.splatterRecursions
                ))
                .message(Caption.of(
                        "voxelsniper.brush.splatter-overlay.set-y-offset",
                        this.yOffset
                ))
                .send();
    }

}
