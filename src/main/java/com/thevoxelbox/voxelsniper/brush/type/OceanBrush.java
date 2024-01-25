package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b ocean|o")
@Permission("voxelsniper.brush.ocean")
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.ocean.info"));
    }

    @Command("wlevel <water-level>")
    public void onBrushWlevel(
            final @NotNull Snipe snipe,
            final @Argument("water-level") @DynamicRange(min = "waterLevelMin") int waterLevel
    ) {
        this.waterLevel = waterLevel;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.ocean.set-water-level",
                this.waterLevel
        ));
    }

    @Command("cfloor <cover-floor>")
    public void onBrushCfloor(
            final @NotNull Snipe snipe,
            final @Argument("cover-floor") @Liberal boolean coverFloor
    ) {
        this.coverFloor = coverFloor;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.ocean.set-floor-cover",
                VoxelSniperText.getStatus(this.coverFloor)
        ));
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
        int minX = targetBlockX - brushSize;
        int minZ = targetBlockZ - brushSize;
        int maxX = targetBlockX + brushSize;
        int maxZ = targetBlockZ + brushSize;
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
                .message(Caption.of(
                        "voxelsniper.brush.ocean.set-water-level",
                        this.waterLevel
                ))
                .message(Caption.of(
                        "voxelsniper.brush.ocean.set-floor-cover",
                        VoxelSniperText.getStatus(this.coverFloor)
                ))
                .send();
    }

}
