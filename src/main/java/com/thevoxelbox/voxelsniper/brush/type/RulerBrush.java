package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b ruler|r")
@Permission("voxelsniper.brush.ruler")
public class RulerBrush extends AbstractBrush {

    private static final int DEFAULT_X_OFFSET = 0;
    private static final int DEFAULT_Y_OFFSET = 0;
    private static final int DEFAULT_Z_OFFSET = 0;

    private boolean first = true;
    private BlockVector3 coordinates = BlockVector3.ZERO;

    private int xOffset = DEFAULT_X_OFFSET;
    private int yOffset = DEFAULT_Y_OFFSET;
    private int zOffset = DEFAULT_Z_OFFSET;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.ruler.info"));
    }

    @Command("ruler")
    public void onBrushRuler(
            final @NotNull Snipe snipe
    ) {
        this.xOffset = 0;
        this.yOffset = 0;
        this.zOffset = 0;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.ruler-mode"));
    }

    @Command("<x-offset> <y-offset> <z-offset>")
    public void onBrushOffsets(
            final @NotNull Snipe snipe,
            final @Argument("x-offset") int xOffset,
            final @Argument("y-offset") int yOffset,
            final @Argument("z-offset") int zOffset
    ) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.ruler.set-x-offset",
                this.xOffset
        ));
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.ruler.set-y-offset",
                this.yOffset
        ));
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.ruler.set-z-offset",
                this.zOffset
        ));
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
                Math.abs((double) targetBlock.getX() - this.coordinates.getX()),
                Math.abs((double) targetBlock.getY() - this.coordinates.getY())
        ), Math.abs((double) targetBlock.getZ() - this.coordinates.getZ()))) + 1) * 100) / 100.0;
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.euclidean-distance", distance));
        messenger.sendMessage(Caption.of("voxelsniper.brush.ruler.block-distance", blockDistance));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .patternMessage()
                .message(Caption.of(
                        "voxelsniper.brush.ruler.set-x-offset",
                        this.xOffset
                ))
                .message(Caption.of(
                        "voxelsniper.brush.ruler.set-y-offset",
                        this.yOffset
                ))
                .message(Caption.of(
                        "voxelsniper.brush.ruler.set-z-offset",
                        this.zOffset
                ))
                .send();
    }

}
