package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Disc_Face_Brush
 *
 * @author Voxel
 */
public class DiscFaceBrush extends PerformBrush {

	private double trueCircle;

	/**
	 *
	 */
	public DiscFaceBrush() {
		this.setName("Disc Face");
	}

	private void discUD(SnipeData v, Block targetBlock) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int z = brushSize; z >= 0; z--) {
				if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
					this.current.perform(targetBlock.getRelative(x, 0, z));
					this.current.perform(targetBlock.getRelative(x, 0, -z));
					this.current.perform(targetBlock.getRelative(-x, 0, z));
					this.current.perform(targetBlock.getRelative(-x, 0, -z));
				}
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	private void discNS(SnipeData v, Block targetBlock) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int y = brushSize; y >= 0; y--) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					this.current.perform(targetBlock.getRelative(x, y, 0));
					this.current.perform(targetBlock.getRelative(x, -y, 0));
					this.current.perform(targetBlock.getRelative(-x, y, 0));
					this.current.perform(targetBlock.getRelative(-x, -y, 0));
				}
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	private void discEW(SnipeData v, Block targetBlock) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int y = brushSize; y >= 0; y--) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					this.current.perform(targetBlock.getRelative(0, x, y));
					this.current.perform(targetBlock.getRelative(0, x, -y));
					this.current.perform(targetBlock.getRelative(0, -x, y));
					this.current.perform(targetBlock.getRelative(0, -x, -y));
				}
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	private void pre(SnipeData v, Block targetBlock) {
		BlockFace blockFace = getTargetBlock().getFace(this.getLastBlock());
		if (blockFace == null) {
			return;
		}
		switch (blockFace) {
			case NORTH:
			case SOUTH:
				this.discNS(v, targetBlock);
				break;
			case EAST:
			case WEST:
				this.discEW(v, targetBlock);
				break;
			case UP:
			case DOWN:
				this.discUD(v, targetBlock);
				break;
			default:
				break;
		}
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.pre(v, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData v) {
		this.pre(v, this.getLastBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				return;
			}
			if (parameter.startsWith("true")) {
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
		return "voxelsniper.brush.discface";
	}
}
