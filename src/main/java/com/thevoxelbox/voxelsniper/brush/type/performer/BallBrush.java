package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.math.Vector3i;
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
		double brushSizeSquared = MathHelper.square(brushSize + this.trueCircle);
		Vector3i blockPosition = new Vector3i(targetBlock);
		this.performer.perform(targetBlock);
		for (int z = 1; z <= brushSize; z++) {
			performClamped(blockPosition.addX(z));
			performClamped(blockPosition.addX(-z));
			performClamped(blockPosition.addY(z));
			performClamped(blockPosition.addY(-z));
			performClamped(blockPosition.addZ(z));
			performClamped(blockPosition.addZ(-z));
			double zSquared = MathHelper.square(z);
			for (int x = 1; x <= brushSize; x++) {
				double xSquared = MathHelper.square(x);
				if (zSquared + xSquared <= brushSizeSquared) {
					performClamped(blockPosition.add(z, 0, x));
					performClamped(blockPosition.add(z, 0, -x));
					performClamped(blockPosition.add(-z, 0, x));
					performClamped(blockPosition.add(-z, 0, -x));
					performClamped(blockPosition.add(z, x, 0));
					performClamped(blockPosition.add(z, -x, 0));
					performClamped(blockPosition.add(-z, x, 0));
					performClamped(blockPosition.add(-z, -x, 0));
					performClamped(blockPosition.add(0, z, x));
					performClamped(blockPosition.add(0, z, -x));
					performClamped(blockPosition.add(0, -z, x));
					performClamped(blockPosition.add(0, -z, -x));
				}
				for (int y = 1; y <= brushSize; y++) {
					int ySquared = MathHelper.square(y);
					if (xSquared + ySquared + zSquared <= brushSizeSquared) {
						performClamped(blockPosition.add(x, y, z));
						performClamped(blockPosition.add(x, y, -z));
						performClamped(blockPosition.add(-x, y, z));
						performClamped(blockPosition.add(-x, y, -z));
						performClamped(blockPosition.add(x, -y, z));
						performClamped(blockPosition.add(x, -y, -z));
						performClamped(blockPosition.add(-x, -y, z));
						performClamped(blockPosition.add(-x, -y, -z));
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		Undo undo = this.performer.getUndo();
		sniper.storeUndo(undo);
	}

	private void performClamped(Vector3i position) {
		Block block = clampY(position);
		this.performer.perform(block);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.send();
	}
}
