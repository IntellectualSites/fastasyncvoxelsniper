package com.thevoxelbox.voxelsniper.brush.type.performer;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b ellipsoid|elo")
@Permission("voxelsniper.brush.ellipsoid")
public class EllipsoidBrush extends AbstractPerformerBrush {

    private static final int DEFAULT_X_RAD = 0;
    private static final int DEFAULT_Y_RAD = 0;
    private static final int DEFAULT_Z_RAD = 0;

    private boolean offset;

    private double xRad = DEFAULT_X_RAD;
    private double yRad = DEFAULT_Y_RAD;
    private double zRad = DEFAULT_Z_RAD;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.ellipsoid.info"));
    }

    @Command("<offset>")
    public void onBrushOffset(
            final @NotNull Snipe snipe,
            final @Argument("offset") @Liberal boolean offset
    ) {
        this.offset = offset;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipsoid.set-offset",
                VoxelSniperText.getStatus(this.offset)
        ));
    }

    @Command("x <x-rad>")
    public void onBrushX(
            final @NotNull Snipe snipe,
            final @Argument("x-rad") int xRad
    ) {
        this.xRad = xRad;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipsoid.set-x-radius",
                this.xRad
        ));
    }

    @Command("y <y-rad>")
    public void onBrushY(
            final @NotNull Snipe snipe,
            final @Argument("y-rad") int yRad
    ) {
        this.yRad = yRad;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipsoid.set-y-radius",
                this.yRad
        ));
    }

    @Command("z <z-rad>")
    public void onBrushZ(
            final @NotNull Snipe snipe,
            final @Argument("z-rad") int zRad
    ) {
        this.zRad = zRad;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ellipsoid.set-z-radius",
                this.zRad
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        execute(targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        execute(lastBlock);
    }

    private void execute(BlockVector3 targetBlock) {
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        this.performer.perform(getEditSession(), blockX, blockY, blockZ, getBlock(blockX, blockY, blockZ));
        double trueOffset = this.offset ? 0.5 : 0;
        for (double x = 0; x <= this.xRad; x++) {
            double xSquared = (x / (this.xRad + trueOffset)) * (x / (this.xRad + trueOffset));
            for (double z = 0; z <= this.zRad; z++) {
                double zSquared = (z / (this.zRad + trueOffset)) * (z / (this.zRad + trueOffset));
                for (double y = 0; y <= this.yRad; y++) {
                    double ySquared = (y / (this.yRad + trueOffset)) * (y / (this.yRad + trueOffset));
                    if (xSquared + ySquared + zSquared <= 1) {
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX + x), (int) (blockY + y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX + x), (int) (blockY + y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX + x), (int) (blockY - y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX + x), (int) (blockY - y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX - x), (int) (blockY + y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX - x), (int) (blockY + y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX - x), (int) (blockY - y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX - x), (int) (blockY - y), (int) (blockZ - z))
                        );
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
                        "voxelsniper.performer-brush.ellipsoid.set-offset",
                        VoxelSniperText.getStatus(offset)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipsoid.set-x-radius",
                        this.xRad
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipsoid.set-y-radius",
                        this.yRad
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.ellipsoid.set-z-radius",
                        this.zRad
                ))
                .send();
    }

}
