package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Stream;

public class CometBrush extends AbstractBrush {

    private boolean useBigBalls;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.RED + "Warning: This brush is not undo-able! You can use //ex to extinguish the " +
                    "fire it creates.");
            messenger.sendMessage(ChatColor.GOLD + "Comet Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b com balls [big|small] -- Sets your ball size.");
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("balls")) {
                    String newBallSize = parameters[1];
                    if (newBallSize.equalsIgnoreCase("big")) {
                        this.useBigBalls = true;
                        messenger.sendMessage(ChatColor.AQUA + "Your balls are " + ChatColor.DARK_RED + "big");
                    } else if (newBallSize.equalsIgnoreCase("small")) {
                        this.useBigBalls = false;
                        messenger.sendMessage(ChatColor.AQUA + "Your balls are " + ChatColor.DARK_RED + "small");
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Unknown ball size.");
                    }
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter " +
                            "info.");
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display parameter " +
                        "info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("balls"), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (firstParameter.equalsIgnoreCase("balls")) {
                String parameter = parameters[1];
                return super.sortCompletions(Stream.of("big", "small"), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        doFireball(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        doFireball(snipe);
    }

    private void doFireball(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        int x = targetBlock.getX();
        int y = targetBlock.getY();
        int z = targetBlock.getZ();
        Vector targetCoordinates = new Vector(x + 0.5 * x / Math.abs(x), y + 0.5, z + 0.5 * z / Math.abs(z));
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        TaskManager.taskManager().sync(() -> {
            Location playerLocation = player.getEyeLocation();
            Vector slope = targetCoordinates.subtract(playerLocation.toVector());
            Vector normalizedSlope = slope.normalize();
            if (this.useBigBalls) {
                LargeFireball fireball = player.launchProjectile(LargeFireball.class);
                fireball.setVelocity(normalizedSlope);
            } else {
                SmallFireball fireball = player.launchProjectile(SmallFireball.class);
                fireball.setVelocity(normalizedSlope);
            }
            return null;
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .blockTypeMessage()
                .message("Your balls are " + ChatColor.DARK_RED + (this.useBigBalls ? "BIG" : "small"))
                .send();
    }

}
