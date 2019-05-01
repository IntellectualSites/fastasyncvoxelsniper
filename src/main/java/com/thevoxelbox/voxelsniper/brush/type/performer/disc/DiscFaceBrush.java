package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class DiscFaceBrush extends AbstractPerformerBrush {

	private double trueCircle;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int index = 1; index < parameters.length; index++) {
			String parameter = parameters[index];
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
				messenger.sendMessage(ChatColor.AQUA + "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				return;
			}
			if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else {
				messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		pre(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		pre(snipe, lastBlock);
	}

	private void discUpDown(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int z = brushSize; z >= 0; z--) {
				if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
					this.performer.perform(targetBlock.getRelative(x, 0, z));
					this.performer.perform(targetBlock.getRelative(x, 0, -z));
					this.performer.perform(targetBlock.getRelative(-x, 0, z));
					this.performer.perform(targetBlock.getRelative(-x, 0, -z));
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	private void discNorthSouth(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int y = brushSize; y >= 0; y--) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					this.performer.perform(targetBlock.getRelative(x, y, 0));
					this.performer.perform(targetBlock.getRelative(x, -y, 0));
					this.performer.perform(targetBlock.getRelative(-x, y, 0));
					this.performer.perform(targetBlock.getRelative(-x, -y, 0));
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	private void discEastWest(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int y = brushSize; y >= 0; y--) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					this.performer.perform(targetBlock.getRelative(0, x, y));
					this.performer.perform(targetBlock.getRelative(0, x, -y));
					this.performer.perform(targetBlock.getRelative(0, -x, y));
					this.performer.perform(targetBlock.getRelative(0, -x, -y));
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	private void pre(Snipe snipe, Block targetBlock) {
		Block lastBlock = getLastBlock();
		BlockFace blockFace = getTargetBlock().getFace(lastBlock);
		if (blockFace == null) {
			return;
		}
		switch (blockFace) {
			case NORTH:
			case SOUTH:
				discNorthSouth(snipe, targetBlock);
				break;
			case EAST:
			case WEST:
				discEastWest(snipe, targetBlock);
				break;
			case UP:
			case DOWN:
				discUpDown(snipe, targetBlock);
				break;
			default:
				break;
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.send();
	}
}
