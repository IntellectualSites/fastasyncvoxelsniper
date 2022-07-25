package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;

import java.util.List;
import java.util.stream.Stream;

public class CleanSnowBrush extends AbstractBrush {

    private double trueCircle;

    @Override
    public final void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.clean-snow.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("true")) {
                    this.trueCircle = 0.5;
                    messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.true-circle", VoxelSniperText.getStatus(true)));
                } else if (firstParameter.equalsIgnoreCase("false")) {
                    this.trueCircle = 0;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.parameter.true-circle",
                            VoxelSniperText.getStatus(false)
                    ));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("true", "false"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
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
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        for (int y = (brushSize + 1) * 2; y >= 0; y--) {
            double ySquared = MathHelper.square(y - brushSize);
            for (int x = (brushSize + 1) * 2; x >= 0; x--) {
                double xSquared = MathHelper.square(x - brushSize);
                for (int z = (brushSize + 1) * 2; z >= 0; z--) {
                    if (xSquared + MathHelper.square(z - brushSize) + ySquared <= brushSizeSquared) {
                        BlockVector3 targetBlock = getTargetBlock();
                        int targetBlockX = targetBlock.getX();
                        int targetBlockY = targetBlock.getY();
                        int targetBlockZ = targetBlock.getZ();
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
                .message(Caption.of("voxelsniper.brush.parameter.true-circle", VoxelSniperText.getStatus(this.trueCircle == 0.5)))
                .send();
    }

}
