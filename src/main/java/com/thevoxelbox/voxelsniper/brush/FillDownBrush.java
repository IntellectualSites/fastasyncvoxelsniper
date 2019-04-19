package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class FillDownBrush extends PerformBrush {

	private double trueCircle;
	private boolean fillLiquid = true;
	private boolean fromExisting;

	/**
	 *
	 */
	public FillDownBrush() {
		this.setName("Fill Down");
	}

	private void fillDown(SnipeData v, Block b) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Block targetBlock = this.getTargetBlock();
		for (int x = -brushSize; x <= brushSize; x++) {
			double currentXSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if (currentXSquared + Math.pow(z, 2) <= brushSizeSquared) {
					int y = 0;
					if (this.fromExisting) {
						boolean found = false;
						for (y = -v.getVoxelHeight(); y < v.getVoxelHeight(); y++) {
							Block currentBlock = this.getWorld()
								.getBlockAt(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
							if (!currentBlock.isEmpty()) {
								found = true;
								break;
							}
						}
						if (!found) {
							continue;
						}
						y--;
					}
					for (; y >= -targetBlock.getY(); --y) {
						Block currentBlock = this.getWorld()
							.getBlockAt(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
						if (currentBlock.isEmpty() || (this.fillLiquid && currentBlock.isLiquid())) {
							this.current.perform(currentBlock);
						} else {
							break;
						}
					}
				}
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.fillDown(v, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData v) {
		this.fillDown(v, this.getLastBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			if (parameters[i].equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Fill Down Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd true -- will use a true circle algorithm.");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd false -- will switch back. (Default)");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd some -- Fills only into air.");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd all -- Fills into liquids as well. (Default)");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd -e -- Fills into only existing blocks. (Toggle)");
				return;
			} else if (parameters[i].equalsIgnoreCase("true")) {
				this.trueCircle = 0.5;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameters[i].equalsIgnoreCase("false")) {
				this.trueCircle = 0;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (parameters[i].equalsIgnoreCase("all")) {
				this.fillLiquid = true;
				snipeData.sendMessage(ChatColor.AQUA + "Now filling liquids as well as air.");
			} else if (parameters[i].equalsIgnoreCase("some")) {
				this.fillLiquid = false;
				snipeData.setReplaceId(0);
				snipeData.sendMessage(ChatColor.AQUA + "Now only filling air.");
			} else if (parameters[i].equalsIgnoreCase("-e")) {
				this.fromExisting = !this.fromExisting;
				snipeData.sendMessage(ChatColor.AQUA + "Now filling down from " + ((this.fromExisting) ? "existing" : "all") + " blocks.");
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.filldown";
	}
}
