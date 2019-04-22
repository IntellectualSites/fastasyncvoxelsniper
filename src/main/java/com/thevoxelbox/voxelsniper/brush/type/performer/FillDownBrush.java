package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class FillDownBrush extends AbstractPerformerBrush {

	private double trueCircle;
	private boolean fillLiquid = true;
	private boolean fromExisting;

	public FillDownBrush() {
		super("Fill Down");
	}

	private void fillDown(SnipeData snipeData, Block block) {
		int brushSize = snipeData.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Block targetBlock = this.getTargetBlock();
		for (int x = -brushSize; x <= brushSize; x++) {
			double currentXSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if (currentXSquared + Math.pow(z, 2) <= brushSizeSquared) {
					int y = 0;
					if (this.fromExisting) {
						boolean found = false;
						for (y = -snipeData.getVoxelHeight(); y < snipeData.getVoxelHeight(); y++) {
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
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.fillDown(snipeData, this.getTargetBlock());
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.fillDown(snipeData, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Fill Down Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd true -- will use a true circle algorithm.");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd false -- will switch back. (Default)");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd some -- Fills only into air.");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd all -- Fills into liquids as well. (Default)");
				snipeData.sendMessage(ChatColor.AQUA + "/b fd -e -- Fills into only existing blocks. (Toggle)");
				return;
			} else if (parameter.equalsIgnoreCase("true")) {
				this.trueCircle = 0.5;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (parameter.equalsIgnoreCase("false")) {
				this.trueCircle = 0;
				snipeData.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (parameter.equalsIgnoreCase("all")) {
				this.fillLiquid = true;
				snipeData.sendMessage(ChatColor.AQUA + "Now filling liquids as well as air.");
			} else if (parameter.equalsIgnoreCase("some")) {
				this.fillLiquid = false;
				snipeData.resetReplaceBlockData();
				snipeData.sendMessage(ChatColor.AQUA + "Now only filling air.");
			} else if (parameter.equalsIgnoreCase("-e")) {
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
