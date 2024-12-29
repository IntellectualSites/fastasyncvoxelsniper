package com.thevoxelbox.voxelsniper.brush.type.canyon;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b canyon|ca")
@CommandPermission("voxelsniper.brush.canyon")
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.canyon.info"));
    }

    @CommandMethod("y <y-level>")
    public void onBrushY(
            final @NotNull Snipe snipe,
            final @Argument("y-level") @DynamicRange(min = "shiftLevelMin", max = "shiftLevelMax") int yLevel
    ) {
        this.yLevel = yLevel;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.canyon.set-shift-level",
                this.yLevel
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        canyon(targetBlock.x() >> 4, targetBlock.z() >> 4);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.x() >> 4;
        int chunkZ = targetBlock.z() >> 4;
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
                .message(Caption.of(
                        "voxelsniper.brush.canyon.set-shift-level",
                        this.yLevel
                ))
                .send();
    }

    public int getYLevel() {
        return this.yLevel;
    }

}
