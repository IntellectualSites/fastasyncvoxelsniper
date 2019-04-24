package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Disc_Brush
 *
 * @author Voxel
 */
public class DiscBrush extends AbstractPerformerBrush {

	private double trueCircle;

	/**
	 * Default Constructor.
	 */
	public DiscBrush() {
		super("Disc");
	}

	/**
	 * Disc executor.
	 */
	private void disc(ToolkitProperties toolkitProperties, Block targetBlock) {
		double radiusSquared = (toolkitProperties.getBrushSize() + this.trueCircle) * (toolkitProperties.getBrushSize() + this.trueCircle);
		Vector centerPoint = targetBlock.getLocation()
			.toVector();
		Vector currentPoint = new Vector().copy(centerPoint);
		for (int x = -toolkitProperties.getBrushSize(); x <= toolkitProperties.getBrushSize(); x++) {
			currentPoint.setX(centerPoint.getX() + x);
			for (int z = -toolkitProperties.getBrushSize(); z <= toolkitProperties.getBrushSize(); z++) {
				currentPoint.setZ(centerPoint.getZ() + z);
				if (centerPoint.distanceSquared(currentPoint) <= radiusSquared) {
					this.performer.perform(this.clampY(currentPoint.getBlockX(), currentPoint.getBlockY(), currentPoint.getBlockZ()));
				}
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.disc(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.disc(toolkitProperties, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i].toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				toolkitProperties.sendMessage(ChatColor.GOLD + "Disc Brush Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b d true|false" + " -- toggles useing the true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
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
		return "voxelsniper.brush.disc";
	}
}
