package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Clean_Snow_Brush
 *
 * @author psanker
 */
public class CleanSnowBrush extends AbstractBrush {

	private double trueCircle;

	public CleanSnowBrush() {
		super("Clean Snow");
	}

	private void cleanSnow(ToolkitProperties toolkitProperties) {
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		for (int y = (brushSize + 1) * 2; y >= 0; y--) {
			double ySquared = Math.pow(y - brushSize, 2);
			for (int x = (brushSize + 1) * 2; x >= 0; x--) {
				double xSquared = Math.pow(x - brushSize, 2);
				for (int z = (brushSize + 1) * 2; z >= 0; z--) {
					if ((xSquared + Math.pow(z - brushSize, 2) + ySquared) <= brushSizeSquared) {
						Block targetBlock = getTargetBlock();
						int targetBlockX = targetBlock.getX();
						int targetBlockY = targetBlock.getY();
						int targetBlockZ = targetBlock.getZ();
						if ((clampY(targetBlockX + x - brushSize, targetBlockY + z - brushSize, targetBlockZ + y - brushSize).getType() == Material.SNOW) && ((clampY(targetBlockX + x - brushSize, targetBlockY + z - brushSize - 1, targetBlockZ + y - brushSize).getType() == Material.SNOW) || (clampY(targetBlockX + x - brushSize, targetBlockY + z - brushSize - 1, targetBlockZ + y - brushSize).getType() == Material.AIR))) {
							undo.put(clampY(targetBlockX + x, targetBlockY + z, targetBlockZ + y));
							setBlockData(targetBlockZ + y - brushSize, targetBlockX + x - brushSize, targetBlockY + z - brushSize, Material.AIR.createBlockData());
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
		this.cleanSnow(toolkitProperties);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.cleanSnow(toolkitProperties);
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
				toolkitProperties.sendMessage(ChatColor.GOLD + "Clean Snow Brush Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b cls true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b cls false will switch back. (false is default)");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else {
				toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.cleansnow";
	}
}
