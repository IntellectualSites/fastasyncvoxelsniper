package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.util.ArtHelper;
import org.bukkit.Art;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandMethod(value = "paint")
@CommandDescription("Change the selected painting to another painting.")
@CommandPermission("voxelsniper.sniper")
public class PaintExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public PaintExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("")
    public void onPaint(
            final @NotNull Sniper sniper
    ) {
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, false);
    }

    @CommandMethod("back")
    public void onPaintBack(
            final @NotNull Sniper sniper
    ) {
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, true);
    }

    @CommandMethod("<art>")
    public void onPaintArt(
            final @NotNull Sniper sniper,
            final @Nullable @Argument("art") Art art
    ) {
        Player player = sniper.getPlayer();
        ArtHelper.paint(player, art);
    }

}
