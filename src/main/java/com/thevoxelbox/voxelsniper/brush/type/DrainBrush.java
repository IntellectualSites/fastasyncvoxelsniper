package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import net.mcparkour.common.math.MathHelper;
import net.mcparkour.common.math.vector.Vector3i;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class DrainBrush extends AbstractBrush {

	private double trueCircle;
	private boolean disc;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Drain Brush Parameters:");
				messenger.sendMessage(ChatColor.AQUA + "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
				messenger.sendMessage(ChatColor.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (parameter.equalsIgnoreCase("d")) {
				if (this.disc) {
					this.disc = false;
					messenger.sendMessage(ChatColor.AQUA + "Disc drain mode OFF");
				} else {
					this.disc = true;
					messenger.sendMessage(ChatColor.AQUA + "Disc drain mode ON");
				}
			} else {
				messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		drain(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		drain(snipe);
	}

	private void drain(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockY = targetBlock.getY();
		int targetBlockZ = targetBlock.getZ();
		Vector3i position = Vectors.of(targetBlock);
		if (this.disc) {
			for (int x = brushSize; x >= 0; x--) {
				double xSquared = MathHelper.square(x);
				for (int y = brushSize; y >= 0; y--) {
					double ySquared = MathHelper.square(y);
					if (xSquared + ySquared <= brushSizeSquared) {
						Material typePlusPlus = getBlockType(position.plus(x, 0, y));
						if (typePlusPlus == Material.WATER || typePlusPlus == Material.LAVA) {
							undo.put(clampY(position.plus(x, 0, y)));
							setBlockType(position.plus(x, 0, y), Material.AIR);
						}
						Material typePlusMinus = getBlockType(targetBlockX + x, targetBlockY, targetBlockZ - y);
						if (typePlusMinus == Material.WATER || typePlusMinus == Material.LAVA) {
							undo.put(clampY(targetBlockX + x, targetBlockY, targetBlockZ - y));
							setBlockType(targetBlockX + x, targetBlockY, targetBlockZ - y, Material.AIR);
						}
						Material typeMinusPlus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ + y);
						if (typeMinusPlus == Material.WATER || typeMinusPlus == Material.LAVA) {
							undo.put(clampY(targetBlockX - x, targetBlockY, targetBlockZ + y));
							setBlockType(targetBlockX - x, targetBlockY, targetBlockZ + y, Material.AIR);
						}
						Material typeMinusMinus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ - y);
						if (typeMinusMinus == Material.WATER || typeMinusMinus == Material.LAVA) {
							undo.put(clampY(targetBlockX - x, targetBlockY, targetBlockZ - y));
							setBlockType(targetBlockX - x, targetBlockY, targetBlockZ - y, Material.AIR);
						}
					}
				}
			}
		} else {
			for (int y = (brushSize + 1) * 2; y >= 0; y--) {
				double ySquared = MathHelper.square(y - brushSize);
				for (int x = (brushSize + 1) * 2; x >= 0; x--) {
					double xSquared = MathHelper.square(x - brushSize);
					for (int z = (brushSize + 1) * 2; z >= 0; z--) {
						if ((xSquared + MathHelper.square(z - brushSize) + ySquared) <= brushSizeSquared) {
							Material type = getBlockType(targetBlockX + x - brushSize, targetBlockY + z - brushSize, targetBlockZ + y - brushSize);
							if (type == Material.WATER || type == Material.LAVA) {
								undo.put(clampY(targetBlockX + x, targetBlockY + z, targetBlockZ + y));
								setBlockType(targetBlockX + x - brushSize, targetBlockY + z - brushSize, targetBlockZ + y - brushSize, Material.AIR);
							}
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendMessage(ChatColor.AQUA + (Double.compare(this.trueCircle, 0.5) == 0 ? "True circle mode ON" : "True circle mode OFF"));
		messenger.sendMessage(ChatColor.AQUA + (this.disc ? "Disc drain mode ON" : "Disc drain mode OFF"));
	}
}
