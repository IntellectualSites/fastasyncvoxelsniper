package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * A brush that creates a solid ball.
 */
public class BallBrush extends AbstractPerformerBrush {

	private static final double TRUE_CIRCLE_ON_VALUE = 0.5;
	private static final int TRUE_CIRCLE_OFF_VALUE = 0;

	private double trueCircle;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int index = 1; index < parameters.length; index++) {
			String parameter = parameters[index];
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Ball Brush Parameters:");
				messenger.sendMessage(ChatColor.AQUA + "/b b true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = TRUE_CIRCLE_ON_VALUE;
				messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = TRUE_CIRCLE_OFF_VALUE;
				messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else {
				messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		ball(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		ball(snipe, lastBlock);
	}

	private void ball(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		this.performer.perform(targetBlock);
		for (int z = 1; z <= brushSize; z++) {
			double zSquared = Math.pow(z, 2);
			this.performer.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ));
			this.performer.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ));
			this.performer.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ));
			this.performer.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ));
			this.performer.perform(this.clampY(blockPositionX, blockPositionY, blockPositionZ + z));
			this.performer.perform(this.clampY(blockPositionX, blockPositionY, blockPositionZ - z));
			for (int x = 1; x <= brushSize; x++) {
				double xSquared = Math.pow(x, 2);
				if (zSquared + xSquared <= brushSizeSquared) {
					this.performer.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ + x));
					this.performer.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ - x));
					this.performer.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ + x));
					this.performer.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ - x));
					this.performer.perform(this.clampY(blockPositionX + z, blockPositionY + x, blockPositionZ));
					this.performer.perform(this.clampY(blockPositionX + z, blockPositionY - x, blockPositionZ));
					this.performer.perform(this.clampY(blockPositionX - z, blockPositionY + x, blockPositionZ));
					this.performer.perform(this.clampY(blockPositionX - z, blockPositionY - x, blockPositionZ));
					this.performer.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ + x));
					this.performer.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ - x));
					this.performer.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ + x));
					this.performer.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ - x));
				}
				for (int y = 1; y <= brushSize; y++) {
					if ((xSquared + Math.pow(y, 2) + zSquared) <= brushSizeSquared) {
						this.performer.perform(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ + z));
						this.performer.perform(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ - z));
						this.performer.perform(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ + z));
						this.performer.perform(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ - z));
						this.performer.perform(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ + z));
						this.performer.perform(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ - z));
						this.performer.perform(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ + z));
						this.performer.perform(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ - z));
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.send();
	}
}
