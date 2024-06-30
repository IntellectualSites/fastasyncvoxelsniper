package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b lightning|light")
@Permission("voxelsniper.brush.lightning")
public class LightningBrush extends AbstractBrush {

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        spawnLighting(targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        spawnLighting(targetBlock);
    }

    private void spawnLighting(BlockVector3 targetBlock) {
        TaskManager.taskManager().sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            world.strikeLightning(BukkitAdapter.adapt(world, targetBlock));
            return null;
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of("voxelsniper.brush.lightning.warning"))
                .send();
    }

}
