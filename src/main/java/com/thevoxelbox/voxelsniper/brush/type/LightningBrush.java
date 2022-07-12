package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import org.bukkit.ChatColor;
import org.bukkit.World;

public class LightningBrush extends AbstractBrush {

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
                .message(ChatColor.LIGHT_PURPLE + "Lightning Brush! Please use in moderation.")
                .send();
    }

}
