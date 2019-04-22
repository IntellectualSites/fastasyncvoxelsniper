package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * A brush that creates a solid ball.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ball_Brush
 *
 * @author Piotr
 */
public class BallBrush extends PerformBrush {

	public static final double TRUE_CIRCLE_ON_VALUE = 0.5;
	public static final int TRUE_CIRCLE_OFF_VALUE = 0;

	private double trueCircle;

	public BallBrush() {
		super("Ball");
	}

	private void ball(SnipeData snipeData, Block targetBlock) {
		int brushSize = snipeData.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		this.current.perform(targetBlock);
		for (int z = 1; z <= brushSize; z++) {
			double zSquared = Math.pow(z, 2);
			this.current.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ));
			this.current.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ));
			this.current.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ));
			this.current.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ));
			this.current.perform(this.clampY(blockPositionX, blockPositionY, blockPositionZ + z));
			this.current.perform(this.clampY(blockPositionX, blockPositionY, blockPositionZ - z));
			for (int x = 1; x <= brushSize; x++) {
				double xSquared = Math.pow(x, 2);
				if (zSquared + xSquared <= brushSizeSquared) {
					this.current.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ + x));
					this.current.perform(this.clampY(blockPositionX + z, blockPositionY, blockPositionZ - x));
					this.current.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ + x));
					this.current.perform(this.clampY(blockPositionX - z, blockPositionY, blockPositionZ - x));
					this.current.perform(this.clampY(blockPositionX + z, blockPositionY + x, blockPositionZ));
					this.current.perform(this.clampY(blockPositionX + z, blockPositionY - x, blockPositionZ));
					this.current.perform(this.clampY(blockPositionX - z, blockPositionY + x, blockPositionZ));
					this.current.perform(this.clampY(blockPositionX - z, blockPositionY - x, blockPositionZ));
					this.current.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ + x));
					this.current.perform(this.clampY(blockPositionX, blockPositionY + z, blockPositionZ - x));
					this.current.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ + x));
					this.current.perform(this.clampY(blockPositionX, blockPositionY - z, blockPositionZ - x));
				}
				for (int y = 1; y <= brushSize; y++) {
					if ((xSquared + Math.pow(y, 2) + zSquared) <= brushSizeSquared) {
						this.current.perform(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ + z));
						this.current.perform(this.clampY(blockPositionX + x, blockPositionY + y, blockPositionZ - z));
						this.current.perform(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ + z));
						this.current.perform(this.clampY(blockPositionX - x, blockPositionY + y, blockPositionZ - z));
						this.current.perform(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ + z));
						this.current.perform(this.clampY(blockPositionX + x, blockPositionY - y, blockPositionZ - z));
						this.current.perform(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ + z));
						this.current.perform(this.clampY(blockPositionX - x, blockPositionY - y, blockPositionZ - z));
					}
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.ball(snipeData, this.getTargetBlock());
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.ball(snipeData, lastBlock);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Ball Brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b b true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = TRUE_CIRCLE_ON_VALUE;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = TRUE_CIRCLE_OFF_VALUE;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ball";
	}
}
