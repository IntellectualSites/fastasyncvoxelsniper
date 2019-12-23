package com.thevoxelbox.voxelsniper.brush.type;

import com.boydti.fawe.Fawe;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WarpBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Location location = lastBlock.getLocation();
		Location playerLocation = player.getLocation();
		location.setPitch(playerLocation.getPitch());
		location.setYaw(playerLocation.getYaw());
		Fawe.get().getQueueHandler().sync(() -> {//FAWE Add
		player.teleport(location);
		});
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Location location = lastBlock.getLocation();
		Location playerLocation = player.getLocation();
		location.setPitch(playerLocation.getPitch());
		location.setYaw(playerLocation.getYaw());
		Fawe.get().getQueueHandler().sync(() -> {//FAWE add
		getWorld().strikeLightning(location);
		player.teleport(location);
		getWorld().strikeLightning(location);
		});
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
