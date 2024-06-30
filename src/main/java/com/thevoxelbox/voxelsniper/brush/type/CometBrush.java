package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.Location;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b comet|meteor|com|met")
@Permission("voxelsniper.brush.comet")
public class CometBrush extends AbstractBrush {

    private boolean useBigBalls;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.comet.info"));
    }

    @Command("<use-big-balls>")
    public void onBrushBigballs(
            final @NotNull Snipe snipe,
            final @Argument("use-big-balls") @Liberal boolean useBigBalls
    ) {
        this.useBigBalls = useBigBalls;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.comet.set-size",
                this.getStatus(this.useBigBalls)
        ));
    }

    @Command("balls big")
    public void onBrushBallsBig(
            final @NotNull Snipe snipe
    ) {
        this.onBrushBigballs(snipe, true);
    }

    @Command("balls small")
    public void onBrushBallsSmall(
            final @NotNull Snipe snipe
    ) {
        this.onBrushBigballs(snipe, false);
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
                .patternMessage()
                .message(Caption.of(
                        "voxelsniper.brush.comet.set-size",
                        this.getStatus(this.useBigBalls)
                ))
                .send();
    }

    private Component getStatus(boolean status) {
        return Caption.of(status ? "voxelsniper.sniper.big" : "voxelsniper.sniper.small");
    }

}
