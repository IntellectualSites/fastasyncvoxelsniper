package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequireToolkit
@CommandMethod(value = "brush|b dome|do")
@CommandPermission("voxelsniper.brush.dome")
public class DomeBrush extends AbstractBrush {

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        generateDome(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        generateDome(snipe, lastBlock);
    }

    private void generateDome(Snipe snipe, BlockVector3 block) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int voxelHeight = toolkitProperties.getVoxelHeight();
        if (voxelHeight == 0) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-voxel-height", voxelHeight));
            return;
        }
        int absoluteHeight = Math.abs(voxelHeight);
        boolean negative = voxelHeight < 0;
        List<Vector> changeablePositions = new ArrayList<>();
        int brushSize = toolkitProperties.getBrushSize();
        int brushSizeTimesVoxelHeight = brushSize * absoluteHeight;
        double stepScale = (brushSize * brushSize + brushSizeTimesVoxelHeight + brushSizeTimesVoxelHeight) / 5.0;
        double stepSize = 1.0 / stepScale;
        for (double u = 0; u <= Math.PI / 2; u += stepSize) {
            double y = absoluteHeight * Math.sin(u);
            for (double stepV = -Math.PI; stepV <= -(Math.PI / 2); stepV += stepSize) {
                double x = brushSize * Math.cos(u) * Math.cos(stepV);
                double z = brushSize * Math.cos(u) * Math.sin(stepV);
                double targetBlockX = block.x() + 0.5;
                double targetBlockZ = block.z() + 0.5;
                int targetY = NumberConversions.floor(block.y() + (negative ? -y : y));
                int currentBlockXAdd = NumberConversions.floor(targetBlockX + x);
                int currentBlockZAdd = NumberConversions.floor(targetBlockZ + z);
                int currentBlockXSubtract = NumberConversions.floor(targetBlockX - x);
                int currentBlockZSubtract = NumberConversions.floor(targetBlockZ - z);
                changeablePositions.add(new Vector(currentBlockXAdd, targetY, currentBlockZAdd));
                changeablePositions.add(new Vector(currentBlockXSubtract, targetY, currentBlockZAdd));
                changeablePositions.add(new Vector(currentBlockXAdd, targetY, currentBlockZSubtract));
                changeablePositions.add(new Vector(currentBlockXSubtract, targetY, currentBlockZSubtract));
            }
        }
        for (Vector vector : changeablePositions) {
            Pattern pattern = toolkitProperties.getPattern().getPattern();
            setBlock(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), pattern);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .patternMessage()
                .voxelHeightMessage()
                .send();
    }

}
