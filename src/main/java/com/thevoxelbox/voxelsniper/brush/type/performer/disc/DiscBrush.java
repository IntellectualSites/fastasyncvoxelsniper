package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.math.MutableBlockVector3;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;

import java.util.List;
import java.util.stream.Stream;

public class DiscBrush extends AbstractPerformerBrush {

    private double trueCircle;

    @Override
    public void loadProperties() {
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.disc.info"));
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
        BlockVector3 targetBlock = getTargetBlock();
        disc(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        disc(snipe, lastBlock);
    }

    private void disc(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double radiusSquared = (brushSize + this.trueCircle) * (brushSize + this.trueCircle);
        MutableBlockVector3 currentPoint = new MutableBlockVector3(targetBlock);
        for (int x = -brushSize; x <= brushSize; x++) {
            currentPoint.mutX(targetBlock.getX() + x);
            for (int z = -brushSize; z <= brushSize; z++) {
                currentPoint.mutZ(targetBlock.getZ() + z);
                if (targetBlock.distanceSq(currentPoint) <= radiusSquared) {
                    this.performer.perform(
                            getEditSession(),
                            currentPoint.getBlockX(),
                            clampY(currentPoint.getBlockY()),
                            currentPoint.getBlockZ(),
                            clampY(currentPoint.getBlockX(), currentPoint.getBlockY(), currentPoint.getBlockZ())
                    );
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
