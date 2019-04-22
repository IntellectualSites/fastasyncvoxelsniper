package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author Kavutop
 */
public class CylinderBrush extends AbstractPerformerBrush {

	private double trueCircle;

	public CylinderBrush() {
		super("Cylinder");
	}

	private void cylinder(ToolkitProperties toolkitProperties, Block targetBlock) {
		int brushSize = toolkitProperties.getBrushSize();
		int yStartingPoint = targetBlock.getY() + toolkitProperties.getCylinderCenter();
		int yEndPoint = targetBlock.getY() + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
		if (yEndPoint < yStartingPoint) {
			yEndPoint = yStartingPoint;
		}
		World world = this.getWorld();
		if (yStartingPoint < 0) {
			yStartingPoint = 0;
			toolkitProperties.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
		} else if (yStartingPoint > world.getMaxHeight() - 1) {
			yStartingPoint = world.getMaxHeight() - 1;
			toolkitProperties.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
		}
		if (yEndPoint < 0) {
			yEndPoint = 0;
			toolkitProperties.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
		} else if (yEndPoint > world.getMaxHeight() - 1) {
			yEndPoint = world.getMaxHeight() - 1;
			toolkitProperties.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
		}
		double bSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int y = yEndPoint; y >= yStartingPoint; y--) {
			for (int x = brushSize; x >= 0; x--) {
				double xSquared = Math.pow(x, 2);
				for (int z = brushSize; z >= 0; z--) {
					if ((xSquared + Math.pow(z, 2)) <= bSquared) {
						this.performer.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() + z));
						this.performer.perform(this.clampY(targetBlock.getX() + x, y, targetBlock.getZ() - z));
						this.performer.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() + z));
						this.performer.perform(this.clampY(targetBlock.getX() - x, y, targetBlock.getZ() - z));
					}
				}
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.cylinder(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.cylinder(toolkitProperties, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
		messages.height();
		messages.center();
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				toolkitProperties.sendMessage(ChatColor.GOLD + "Cylinder Brush Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b c h[number] -- set the cylinder v.voxelHeight.  Default is 1.");
				toolkitProperties.sendMessage(ChatColor.DARK_AQUA + "/b c true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				toolkitProperties.sendMessage(ChatColor.DARK_BLUE + "/b c c[number] -- set the origin of the cylinder compared to the target block. Positive numbers will move the cylinder upward, negative will move it downward.");
				return;
			}
			if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				toolkitProperties.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'h') {
				toolkitProperties.setVoxelHeight((int) Double.parseDouble(parameter.replace("h", "")));
				toolkitProperties.sendMessage(ChatColor.AQUA + "Cylinder v.voxelHeight set to: " + toolkitProperties.getVoxelHeight());
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'c') {
				toolkitProperties.setCylinderCenter((int) Double.parseDouble(parameter.replace("c", "")));
				toolkitProperties.sendMessage(ChatColor.AQUA + "Cylinder origin set to: " + toolkitProperties.getCylinderCenter());
			} else {
				toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.cylinder";
	}
}
