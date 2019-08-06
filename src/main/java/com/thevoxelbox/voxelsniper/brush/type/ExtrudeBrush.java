package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

public class ExtrudeBrush extends AbstractBrush {

	private double trueCircle;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			try {
				if (parameter.equalsIgnoreCase("info")) {
					messenger.sendMessage(ChatColor.GOLD + "Extrude brush Parameters:");
					messenger.sendMessage(ChatColor.AQUA + "/b ex true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ex false will switch back. (false is default)");
					return;
				} else if (parameter.startsWith("true")) {
					this.trueCircle = 0.5;
					messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
				} else if (parameter.startsWith("false")) {
					this.trueCircle = 0;
					messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
				} else {
					messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
					return;
				}
			} catch (RuntimeException exception) {
				messenger.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
			}
		}
	}

	private void extrudeUpOrDown(Snipe snipe, boolean isUp) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
					int direction = (isUp ? 1 : -1);
					for (int y = 0; y < Math.abs(toolkitProperties.getVoxelHeight()); y++) {
						int tempY = y * direction;
						Block targetBlock = getTargetBlock();
						int targetBlockX = targetBlock.getX();
						int targetBlockY = targetBlock.getY();
						int targetBlockZ = targetBlock.getZ();
						perform(clampY(targetBlockX + x, targetBlockY + tempY, targetBlockZ + z), clampY(targetBlockX + x, targetBlockY + tempY + direction, targetBlockZ + z), toolkitProperties, undo);
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private void extrudeNorthOrSouth(Snipe snipe, boolean isSouth) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int y = -brushSize; y <= brushSize; y++) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					int direction = (isSouth) ? 1 : -1;
					for (int z = 0; z < Math.abs(toolkitProperties.getVoxelHeight()); z++) {
						int tempZ = z * direction;
						Block targetBlock = this.getTargetBlock();
						perform(clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + tempZ), this.clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + tempZ + direction), toolkitProperties, undo);
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private void extrudeEastOrWest(Snipe snipe, boolean isEast) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		for (int y = -brushSize; y <= brushSize; y++) {
			double ySquared = Math.pow(y, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if ((ySquared + Math.pow(z, 2)) <= brushSizeSquared) {
					int direction = (isEast) ? 1 : -1;
					for (int x = 0; x < Math.abs(toolkitProperties.getVoxelHeight()); x++) {
						int tempX = x * direction;
						Block targetBlock = this.getTargetBlock();
						perform(this.clampY(targetBlock.getX() + tempX, targetBlock.getY() + y, targetBlock.getZ() + z), this.clampY(targetBlock.getX() + tempX + direction, targetBlock.getY() + y, targetBlock.getZ() + z), toolkitProperties, undo);
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private void perform(Block block1, Block block2, ToolkitProperties toolkitProperties, Undo undo) {
		if (toolkitProperties.isVoxelListContains(getBlockData(block1.getX(), block1.getY(), block1.getZ()))) {
			undo.put(block2);
			setBlockType(block2.getX(), block2.getY(), block2.getZ(), getBlockType(block1.getX(), block1.getY(), block1.getZ()));
			clampY(block2.getX(), block2.getY(), block2.getZ()).setBlockData(clampY(block1.getX(), block1.getY(), block1.getZ()).getBlockData());
		}
	}

	private void selectExtrudeMethod(Snipe snipe, @Nullable BlockFace blockFace, boolean towardsUser) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		if (blockFace == null || toolkitProperties.getVoxelHeight() == 0) {
			return;
		}
		switch (blockFace) {
			case UP:
				extrudeUpOrDown(snipe, towardsUser);
				break;
			case SOUTH:
				extrudeNorthOrSouth(snipe, towardsUser);
				break;
			case EAST:
				extrudeEastOrWest(snipe, towardsUser);
				break;
			default:
				break;
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		Block lastBlock = getLastBlock();
		selectExtrudeMethod(snipe, targetBlock.getFace(lastBlock), false);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		Block lastBlock = getLastBlock();
		selectExtrudeMethod(snipe, targetBlock.getFace(lastBlock), true);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendVoxelHeightMessage();
		messenger.sendVoxelListMessage();
		messenger.sendMessage(ChatColor.AQUA + (Double.compare(this.trueCircle, 0.5) == 0 ? "True circle mode ON" : "True circle mode OFF"));
	}
}
