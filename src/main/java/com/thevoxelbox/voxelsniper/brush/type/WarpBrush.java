package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b warp|world")
@CommandPermission("voxelsniper.brush.warp")
public class WarpBrush extends AbstractBrush {

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        BlockVector3 lastBlock = this.getLastBlock();
        if (lastBlock == null) {
            return;
        }
        Fawe.instance().getQueueHandler().sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            Location location = BukkitAdapter.adapt(world, lastBlock);
            Location playerLocation = player.getLocation();
            location.setPitch(playerLocation.getPitch());
            location.setYaw(playerLocation.getYaw());
            player.teleport(location);
        });
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        BlockVector3 lastBlock = this.getLastBlock();
        if (lastBlock == null) {
            return;
        }
        Fawe.instance().getQueueHandler().sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            Location location = BukkitAdapter.adapt(world, lastBlock);
            Location playerLocation = player.getLocation();
            location.setPitch(playerLocation.getPitch());
            location.setYaw(playerLocation.getYaw());
            world.strikeLightning(location);
            player.teleport(location);
            world.strikeLightning(location);
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .send();
    }

}
