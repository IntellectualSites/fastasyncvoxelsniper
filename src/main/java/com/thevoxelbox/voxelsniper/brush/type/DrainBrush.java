package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Drain_Brush
 *
 * @author Gavjenks
 * @author psanker
 */
public class DrainBrush extends AbstractBrush {

	private double trueCircle;
	private boolean disc;

	public DrainBrush() {
		super("Drain");
	}

	private void drain(ToolkitProperties toolkitProperties) {
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockY = targetBlock.getY();
		int targetBlockZ = targetBlock.getZ();
		if (this.disc) {
			for (int x = brushSize; x >= 0; x--) {
				double xSquared = Math.pow(x, 2);
				for (int y = brushSize; y >= 0; y--) {
					if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
						Material typePlusPlus = getBlockType(targetBlockX + x, targetBlockY, targetBlockZ + y);
						if (typePlusPlus == Material.WATER || typePlusPlus == Material.LAVA) {
							undo.put(clampY(targetBlockX + x, targetBlockY, targetBlockZ + y));
							setBlockType(targetBlockZ + y, targetBlockX + x, targetBlockY, Material.AIR);
						}
						Material typePlusMinus = getBlockType(targetBlockX + x, targetBlockY, targetBlockZ - y);
						if (typePlusMinus == Material.WATER || typePlusMinus == Material.LAVA) {
							undo.put(clampY(targetBlockX + x, targetBlockY, targetBlockZ - y));
							setBlockType(targetBlockZ - y, targetBlockX + x, targetBlockY, Material.AIR);
						}
						Material typeMinusPlus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ + y);
						if (typeMinusPlus == Material.WATER || typeMinusPlus == Material.LAVA) {
							undo.put(clampY(targetBlockX - x, targetBlockY, targetBlockZ + y));
							setBlockType(targetBlockZ + y, targetBlockX - x, targetBlockY, Material.AIR);
						}
						Material typeMinusMinus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ - y);
						if (typeMinusMinus == Material.WATER || typeMinusMinus == Material.LAVA) {
							undo.put(clampY(targetBlockX - x, targetBlockY, targetBlockZ - y));
							setBlockType(targetBlockZ - y, targetBlockX - x, targetBlockY, Material.AIR);
						}
					}
				}
			}
		} else {
			for (int y = (brushSize + 1) * 2; y >= 0; y--) {
				double ySquared = Math.pow(y - brushSize, 2);
				for (int x = (brushSize + 1) * 2; x >= 0; x--) {
					double xSquared = Math.pow(x - brushSize, 2);
					for (int z = (brushSize + 1) * 2; z >= 0; z--) {
						if ((xSquared + Math.pow(z - brushSize, 2) + ySquared) <= brushSizeSquared) {
							Material type = getBlockType(targetBlockX + x - brushSize, targetBlockY + z - brushSize, targetBlockZ + y - brushSize);
							if (type == Material.WATER || type == Material.LAVA) {
								undo.put(clampY(targetBlockX + x, targetBlockY + z, targetBlockZ + y));
								setBlockType(targetBlockZ + y - brushSize, targetBlockX + x - brushSize, targetBlockY + z - brushSize, Material.AIR);
							}
						}
					}
				}
			}
		}
		Sniper owner = toolkitProperties.getOwner();
		owner.storeUndo(undo);
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		drain(toolkitProperties);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		drain(toolkitProperties);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
		messages.custom(ChatColor.AQUA + (Double.compare(this.trueCircle, 0.5) == 0 ? "True circle mode ON" : "True circle mode OFF"));
		messages.custom(ChatColor.AQUA + (this.disc ? "Disc drain mode ON" : "Disc drain mode OFF"));
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				toolkitProperties.sendMessage(ChatColor.GOLD + "Drain Brush Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (parameter.equalsIgnoreCase("d")) {
				if (this.disc) {
					this.disc = false;
					toolkitProperties.sendMessage(ChatColor.AQUA + "Disc drain mode OFF");
				} else {
					this.disc = true;
					toolkitProperties.sendMessage(ChatColor.AQUA + "Disc drain mode ON");
				}
			} else {
				toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.drain";
	}
}
