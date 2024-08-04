package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.math.MutableBlockVector3;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b disc|d")
@Permission("voxelsniper.brush.disc")
public class DiscBrush extends AbstractPerformerBrush {

    private boolean trueCircle;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.disc.info"));
    }

    @Command("<true-circle>")
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
        double radiusSquared = (brushSize + (this.trueCircle ? 0.5 : 0)) * (brushSize + (this.trueCircle ? 0.5 : 0));
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
                .message(Caption.of(
                        "voxelsniper.brush.parameter.true-circle",
                        VoxelSniperText.getStatus(this.trueCircle)
                ))
                .send();
    }

}
