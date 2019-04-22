package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Disc_Face_Brush
 *
 * @author Voxel
 */
public class DiscFaceBrush extends AbstractPerformerBrush {

	private double trueCircle;

	public DiscFaceBrush() {
		super("Disc Face");
	}

	private void discUpDown(ToolkitProperties toolkitProperties, Block targetBlock) {
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int z = brushSize; z >= 0; z--) {
				if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
					this.performer.perform(targetBlock.getRelative(x, 0, z));
					this.performer.perform(targetBlock.getRelative(x, 0, -z));
					this.performer.perform(targetBlock.getRelative(-x, 0, z));
					this.performer.perform(targetBlock.getRelative(-x, 0, -z));
				}
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void discNorthSouth(ToolkitProperties toolkitProperties, Block targetBlock) {
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int y = brushSize; y >= 0; y--) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					this.performer.perform(targetBlock.getRelative(x, y, 0));
					this.performer.perform(targetBlock.getRelative(x, -y, 0));
					this.performer.perform(targetBlock.getRelative(-x, y, 0));
					this.performer.perform(targetBlock.getRelative(-x, -y, 0));
				}
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void discEastWest(ToolkitProperties toolkitProperties, Block targetBlock) {
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int y = brushSize; y >= 0; y--) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					this.performer.perform(targetBlock.getRelative(0, x, y));
					this.performer.perform(targetBlock.getRelative(0, x, -y));
					this.performer.perform(targetBlock.getRelative(0, -x, y));
					this.performer.perform(targetBlock.getRelative(0, -x, -y));
				}
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void pre(ToolkitProperties toolkitProperties, Block targetBlock) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		BlockFace blockFace = getTargetBlock().getFace(lastBlock);
		if (blockFace == null) {
			return;
		}
		switch (blockFace) {
			case NORTH:
			case SOUTH:
				this.discNorthSouth(toolkitProperties, targetBlock);
				break;
			case EAST:
			case WEST:
				this.discEastWest(toolkitProperties, targetBlock);
				break;
			case UP:
			case DOWN:
				this.discUpDown(toolkitProperties, targetBlock);
				break;
			default:
				break;
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.pre(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.pre(toolkitProperties, lastBlock);
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
				toolkitProperties.sendMessage(ChatColor.GOLD + "Disc Face brush Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b df true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
				return;
			}
			if (parameter.startsWith("true")) {
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
		return "voxelsniper.brush.discface";
	}
}
