package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b flat_ocean|flatocean|fo")
@CommandPermission("voxelsniper.brush.flatocean")
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.flat-ocean.info"));
    }

    @CommandMethod("yo <water-level>")
    public void onBrushYo(
            final @NotNull Snipe snipe,
            final @Argument("water-level") int waterLevel
    ) {
        this.waterLevel = waterLevel < this.floorLevel ? this.floorLevel + 1 : waterLevel;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.flat-ocean.set-water-level",
                this.waterLevel
        ));
    }

    @CommandMethod("yl <floor-level>")
    public void onBrushYl(
            final @NotNull Snipe snipe,
            final @Argument("floor-level") int floorLevel
    ) {
        int newFloorLevel = floorLevel;
        if (newFloorLevel > this.waterLevel) {
            EditSession editSession = this.getEditSession();
            newFloorLevel = this.waterLevel - 1;

            if (newFloorLevel <= editSession.getMinY()) {
                newFloorLevel = editSession.getMinY() + 1;
                this.waterLevel = editSession.getMinY() + 2;
            }
        }
        this.floorLevel = newFloorLevel;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.flat-ocean.set-ocean-level",
                this.floorLevel
        ));
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
        int blockX = targetBlock.x();
        int blockZ = targetBlock.z();
        flatOcean((blockX + additionalX) >> 4, (blockZ + additionalZ) >> 4);
    }

    private void flatOceanAtTarget() {
        BlockVector3 targetBlock = getTargetBlock();
        flatOcean(targetBlock.x() >> 4, targetBlock.z() >> 4);
    }

    private void flatOcean(int chunkX, int chunkZ) {
        EditSession editSession = getEditSession();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = editSession.getMinY(); y <= editSession.getMaxY(); y++) {
                    if (y <= this.floorLevel) {
                        setBlock(blockX + x, y, blockZ + z, BlockTypes.DIRT);
                    } else if (y <= this.waterLevel) {
                        setBlock(blockX + x, y, blockZ + z, BlockTypes.WATER);
                    } else {
                        setBlock(blockX + x, y, blockZ + z, BlockTypes.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.brush.flat-ocean.set-water-level",
                        this.waterLevel
                ))
                .message(Caption.of(
                        "voxelsniper.brush.flat-ocean.set-ocean-level",
                        this.floorLevel
                ))
                .send();
    }

}
