package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class RulerBrush extends AbstractBrush {

    private static final int DEFAULT_X_OFFSET = 0;
    private static final int DEFAULT_Y_OFFSET = 0;
    private static final int DEFAULT_Z_OFFSET = 0;

    private boolean first = true;
    private BlockVector3 coordinates = BlockVector3.ZERO;

    private int xOffset = DEFAULT_X_OFFSET;
    private int yOffset = DEFAULT_Y_OFFSET;
    private int zOffset = DEFAULT_Z_OFFSET;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("ruler")) {
                    this.zOffset = 0;
                    this.yOffset = 0;
                    this.xOffset = 0;
                    messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.ruler-mode"));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else if (parameters.length == 3) {
                Integer xOffset = NumericParser.parseInteger(parameters[0]);
                Integer yOffset = NumericParser.parseInteger(parameters[1]);
                Integer zOffset = NumericParser.parseInteger(parameters[2]);
                this.xOffset = xOffset == null ? 0 : xOffset;
                this.yOffset = yOffset == null ? 0 : yOffset;
                this.zOffset = zOffset == null ? 0 : zOffset;
                messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.set-x-offset", this.xOffset));
                messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.set-y-offset", this.yOffset));
                messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.set-z-offset", this.zOffset));
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("ruler"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockVector3 targetBlock = getTargetBlock();
        this.coordinates = targetBlock;
        if (this.xOffset == 0 && this.yOffset == 0 && this.zOffset == 0) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
            this.first = !this.first;
        } else {
            int x = targetBlock.getX();
            int y = targetBlock.getY();
            int z = targetBlock.getZ();
            setBlock(x + this.xOffset, y + this.yOffset, z + this.zOffset, toolkitProperties.getPattern().getPattern());
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.coordinates == null || this.coordinates.lengthSq() == 0) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.warning"));
            return;
        }
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.format"));
        BlockVector3 targetBlock = getTargetBlock();

        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.x-change", (targetBlock.getX() - this.coordinates.getX())));
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.y-change", (targetBlock.getY() - this.coordinates.getY())));
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.z-change", (targetBlock.getZ() - this.coordinates.getZ())));
        double distance = Math.round(targetBlock
                .subtract(this.coordinates)
                .length() * 100) / 100.0;
        double blockDistance = Math.round((Math.abs(Math.max(Math.max(
                Math.abs(targetBlock.getX() - this.coordinates.getX()),
                Math.abs(targetBlock.getY() - this.coordinates.getY())
        ), Math.abs(targetBlock.getZ() - this.coordinates.getZ()))) + 1) * 100) / 100.0;
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.euclidean-distance", distance));
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.block-distance", blockDistance));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .patternMessage()
                .message(Caption.of("voxelsniper.brush.ruler.set-x-offset", this.xOffset))
                .message(Caption.of("voxelsniper.brush.ruler.set-y-offset", this.yOffset))
                .message(Caption.of("voxelsniper.brush.ruler.set-z-offset", this.zOffset))
                .send();
    }

}
