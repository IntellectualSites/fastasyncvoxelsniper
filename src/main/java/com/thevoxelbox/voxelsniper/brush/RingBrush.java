package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Ring_Brush
 *
 * @author Voxel
 */
public class RingBrush extends PerformBrush {

	private double trueCircle;
	private double innerSize;

	/**
	 *
	 */
	public RingBrush() {
		super("Ring");
	}

	private void ring(SnipeData v, Block targetBlock) {
		int brushSize = v.getBrushSize();
		double outerSquared = Math.pow(brushSize + this.trueCircle, 2);
		double innerSquared = Math.pow(this.innerSize, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int z = brushSize; z >= 0; z--) {
				double ySquared = Math.pow(z, 2);
				if ((xSquared + ySquared) <= outerSquared && (xSquared + ySquared) >= innerSquared) {
					this.current.perform(targetBlock.getRelative(x, 0, z));
					this.current.perform(targetBlock.getRelative(x, 0, -z));
					this.current.perform(targetBlock.getRelative(-x, 0, z));
					this.current.perform(targetBlock.getRelative(-x, 0, -z));
				}
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.ring(snipeData, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.ring(snipeData, this.getLastBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.custom(ChatColor.AQUA + "The inner radius is " + ChatColor.RED + this.innerSize);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			if (parameters[i].equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Ring Brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b ri true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ri false will switch back. (false is default)");
				snipeData.sendMessage(ChatColor.AQUA + "/b ri ir2.5 -- will set the inner radius to 2.5 units");
				return;
			} else if (parameters[i].startsWith("true")) {
				this.trueCircle = 0.5;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameters[i].startsWith("false")) {
				this.trueCircle = 0;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (parameters[i].startsWith("ir")) {
				try {
					this.innerSize = Double.parseDouble(parameters[i].replace("ir", ""));
					snipeData.sendMessage(ChatColor.AQUA + "The inner radius has been set to " + ChatColor.RED + this.innerSize);
				} catch (NumberFormatException exception) {
					snipeData.sendMessage(ChatColor.RED + "The parameters included are invalid.");
				}
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ring";
	}
}
