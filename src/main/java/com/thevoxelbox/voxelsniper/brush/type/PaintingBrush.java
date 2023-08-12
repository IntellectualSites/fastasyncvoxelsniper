package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.util.ArtHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Painting scrolling Brush.
 */
@RequireToolkit
@CommandMethod(value = "brush|b painting|paint")
@CommandPermission("voxelsniper.brush.painting")
public class PaintingBrush extends AbstractBrush {

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    /**
     * Scroll painting forward.
     *
     * @param snipe Sniper caller
     */
    @Override
    public void handleArrowAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, false);
    }

    /**
     * Scroll painting backwards.
     *
     * @param snipe Sniper caller
     */
    @Override
    public void handleGunpowderAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, true);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .send();
    }

}
