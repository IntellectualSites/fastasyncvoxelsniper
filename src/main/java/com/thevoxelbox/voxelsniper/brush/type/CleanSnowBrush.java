package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b clean_snow|cleansnow|cls")
@CommandPermission("voxelsniper.brush.cleansnow")
public class CleanSnowBrush extends AbstractBrush {

    private boolean trueCircle;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.clean-snow.info"));
    }

    @CommandMethod("<true-circle>")
    public void onBrushTruecircle(
            final @NotNull Snipe snipe,
            final @Argument("true-circle") @Liberal boolean trueCircle
    ) {
        this.trueCircle = trueCircle;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.parameter.true-circle",
                VoxelSniperText.getStatus(this.trueCircle)
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        cleanSnow(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        cleanSnow(snipe);
    }

    private void cleanSnow(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        for (int y = (brushSize + 1) * 2; y >= 0; y--) {
            double ySquared = MathHelper.square(y - brushSize);
            for (int x = (brushSize + 1) * 2; x >= 0; x--) {
                double xSquared = MathHelper.square(x - brushSize);
                for (int z = (brushSize + 1) * 2; z >= 0; z--) {
                    if (xSquared + MathHelper.square(z - brushSize) + ySquared <= brushSizeSquared) {
                        BlockVector3 targetBlock = getTargetBlock();
                        int targetBlockX = targetBlock.x();
                        int targetBlockY = targetBlock.y();
                        int targetBlockZ = targetBlock.z();
                        if (clampY(
                                targetBlockX + x - brushSize,
                                targetBlockY + z - brushSize,
                                targetBlockZ + y - brushSize
                        ).getBlockType() == BlockTypes.SNOW && (clampY(
                                targetBlockX + x - brushSize,
                                targetBlockY + z - brushSize - 1,
                                targetBlockZ + y - brushSize
                        ).getBlockType() == BlockTypes.SNOW || clampY(
                                targetBlockX + x - brushSize,
                                targetBlockY + z - brushSize - 1,
                                targetBlockZ + y - brushSize
                        ).isAir())) {
                            setBlockData(
                                    targetBlockZ + y - brushSize,
                                    targetBlockX + x - brushSize,
                                    targetBlockY + z - brushSize,
                                    BlockTypes.AIR.getDefaultState()
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(Caption.of(
                        "voxelsniper.brush.parameter.true-circle",
                        VoxelSniperText.getStatus(this.trueCircle)
                ))
                .send();
    }

}
