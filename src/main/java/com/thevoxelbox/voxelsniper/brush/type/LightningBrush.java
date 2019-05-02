package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LightningBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		world.strikeLightning(targetBlock.getLocation());
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		world.strikeLightning(targetBlock.getLocation());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Lightning Brush! Please use in moderation.");
	}
}
