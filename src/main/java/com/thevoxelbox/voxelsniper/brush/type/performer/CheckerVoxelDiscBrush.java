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
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b checker_voxel_disc|checkervoxeldisc|cvd")
@Permission("voxelsniper.brush.checkervoxeldisc")
public class CheckerVoxelDiscBrush extends AbstractPerformerBrush {

    private boolean useWorldCoordinates = true;

    @Override
    public void loadProperties() {
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.checker-voxel-disc.info"));
    }

    @Command("<use-world-coordinates>")
    public void onBrushUseworldcoordinates(
            final @NotNull Snipe snipe,
            final @Argument("use-world-coordinates") @Liberal boolean useWorldCoordinates
    ) {
        this.useWorldCoordinates = useWorldCoordinates;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.checker-voxel-disc.set-using-world-coordinates",
                VoxelSniperText.getStatus(this.useWorldCoordinates)
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        applyBrush(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        applyBrush(snipe, lastBlock);
    }

    private void applyBrush(Snipe snipe, BlockVector3 target) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        for (int x = brushSize; x >= -brushSize; x--) {
            for (int y = brushSize; y >= -brushSize; y--) {
                int sum = this.useWorldCoordinates ? target.getX() + x + target.getZ() + y : x + y;
                if (sum % 2 != 0) {
                    this.performer.perform(
                            getEditSession(),
                            target.getX() + x,
                            clampY(target.getY()),
                            target.getZ() + y,
                            this.clampY(target.getX() + x, target.getY(), target.getZ() + y)
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
                .message(Caption.of(
                        "voxelsniper.performer-brush.checker-voxel-disc.set-using-world-coordinates",
                        VoxelSniperText.getStatus(this.useWorldCoordinates)
                ))
                .send();
    }

}
