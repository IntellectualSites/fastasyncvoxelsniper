package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author Kavutop
 */
public class CylinderBrush extends PerformBrush {

	private double trueCircle;

	public CylinderBrush() {
		super("Cylinder");
	}

	private void cylinder(SnipeData snipeData, Block targetBlock) {
		int brushSize = snipeData.getBrushSize();
		int yStartingPoint = targetBlock.getY() + snipeData.getCylinderCenter();
		int yEndPoint = targetBlock.getY() + snipeData.getVoxelHeight() + snipeData.getCylinderCenter();
		if (yEndPoint < yStartingPoint) {
			yEndPoint = yStartingPoint;
		}
		World world = this.getWorld();
		if (yStartingPoint < 0) {
			yStartingPoint = 0;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
		} else if (yStartingPoint > world.getMaxHeight() - 1) {
			yStartingPoint = world.getMaxHeight() - 1;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
		}
		if (yEndPoint < 0) {
			yEndPoint = 0;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
		} else if (yEndPoint > world.getMaxHeight() - 1) {
			yEndPoint = world.getMaxHeight() - 1;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
		}
		double bSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int y = yEndPoint; y >= yStartingPoint; y--) {
			for (int x = brushSize; x >= 0; x--) {
				double xSquared = Math.pow(x, 2);
				for (int z = brushSize; z >= 0; z--) {
					if ((xSquared + Math.pow(z, 2)) <= bSquared) {
						this.current.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() + z));
						this.current.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() - z));
						this.current.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() + z));
						this.current.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() - z));
					}
				}
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.cylinder(snipeData, this.getTargetBlock());
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.cylinder(snipeData, lastBlock);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.height();
		message.center();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Cylinder Brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b c h[number] -- set the cylinder v.voxelHeight.  Default is 1.");
				snipeData.sendMessage(ChatColor.DARK_AQUA + "/b c true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				snipeData.sendMessage(ChatColor.DARK_BLUE + "/b c c[number] -- set the origin of the cylinder compared to the target block. Positive numbers will move the cylinder upward, negative will move it downward.");
				return;
			}
			if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'h') {
				snipeData.setVoxelHeight((int) Double.parseDouble(parameter.replace("h", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Cylinder v.voxelHeight set to: " + snipeData.getVoxelHeight());
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'c') {
				snipeData.setCylinderCenter((int) Double.parseDouble(parameter.replace("c", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Cylinder origin set to: " + snipeData.getCylinderCenter());
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.cylinder";
	}
}
