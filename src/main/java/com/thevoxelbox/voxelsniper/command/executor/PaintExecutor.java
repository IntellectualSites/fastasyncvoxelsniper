package com.thevoxelbox.voxelsniper.command.executor;

import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.util.ArtHelper;
import org.bukkit.Art;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Command(value = "paint")
@CommandDescription("Change the selected painting to another painting.")
@Permission("voxelsniper.sniper")
public class PaintExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public PaintExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("")
    public void onPaint(
            final @NotNull Sniper sniper
    ) {
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, false);
    }

    @Command("back")
    public void onPaintBack(
            final @NotNull Sniper sniper
    ) {
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, true);
    }

    @Command("<art>")
    public void onPaintArt(
            final @NotNull Sniper sniper,
            final @Nullable Art art
    ) {
        Player player = sniper.getPlayer();
        ArtHelper.paint(player, art);
    }

}
