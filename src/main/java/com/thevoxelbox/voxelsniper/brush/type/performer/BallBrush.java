package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * A brush that creates a solid ball.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ball_Brush
 *
 * @author Piotr
 */
public class BallBrush extends AbstractPerformerBrush {

	public static final double TRUE_CIRCLE_ON_VALUE = 0.5;
	public static final int TRUE_CIRCLE_OFF_VALUE = 0;

	private double trueCircle;

	public BallBrush() {
		super("Ball");
	}

	private void ball(ToolkitProperties toolkitProperties, Block targetBlock) {
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
		Sniper owner = toolkitProperties.getOwner();
		owner.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.ball(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.ball(toolkitProperties, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				toolkitProperties.sendMessage(ChatColor.GOLD + "Ball Brush Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b b true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = TRUE_CIRCLE_ON_VALUE;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = TRUE_CIRCLE_OFF_VALUE;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else {
				toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ball";
	}
}
