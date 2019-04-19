package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Disc_Brush
 *
 * @author Voxel
 */
public class DiscBrush extends PerformBrush {

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
	private void disc(SnipeData v, Block targetBlock) {
		double radiusSquared = (v.getBrushSize() + this.trueCircle) * (v.getBrushSize() + this.trueCircle);
		Vector centerPoint = targetBlock.getLocation()
			.toVector();
		Vector currentPoint = centerPoint.clone();
		for (int x = -v.getBrushSize(); x <= v.getBrushSize(); x++) {
			currentPoint.setX(centerPoint.getX() + x);
			for (int z = -v.getBrushSize(); z <= v.getBrushSize(); z++) {
				currentPoint.setZ(centerPoint.getZ() + z);
				if (centerPoint.distanceSquared(currentPoint) <= radiusSquared) {
					this.current.perform(this.clampY(currentPoint.getBlockX(), currentPoint.getBlockY(), currentPoint.getBlockZ()));
				}
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.disc(snipeData, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.disc(snipeData, this.getLastBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i].toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Disc Brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b d true|false" + " -- toggles useing the true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.disc";
	}
}
