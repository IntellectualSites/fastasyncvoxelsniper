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
		v.owner()
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
	public final void info(Message vm) {
		vm.brushName(this.getName());
		vm.size();
	}

	@Override
	public final void parameters(String[] par, SnipeData v) {
		for (int i = 1; i < par.length; i++) {
			if (par[i].equalsIgnoreCase("info")) {
				v.sendMessage(ChatColor.GOLD + "Fill Down Parameters:");
				v.sendMessage(ChatColor.AQUA + "/b fd true -- will use a true circle algorithm.");
				v.sendMessage(ChatColor.AQUA + "/b fd false -- will switch back. (Default)");
				v.sendMessage(ChatColor.AQUA + "/b fd some -- Fills only into air.");
				v.sendMessage(ChatColor.AQUA + "/b fd all -- Fills into liquids as well. (Default)");
				v.sendMessage(ChatColor.AQUA + "/b fd -e -- Fills into only existing blocks. (Toggle)");
				return;
			} else if (par[i].equalsIgnoreCase("true")) {
				this.trueCircle = 0.5;
				v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
			} else if (par[i].equalsIgnoreCase("false")) {
				this.trueCircle = 0;
				v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
			} else if (par[i].equalsIgnoreCase("all")) {
				this.fillLiquid = true;
				v.sendMessage(ChatColor.AQUA + "Now filling liquids as well as air.");
			} else if (par[i].equalsIgnoreCase("some")) {
				this.fillLiquid = false;
				v.setReplaceId(0);
				v.sendMessage(ChatColor.AQUA + "Now only filling air.");
			} else if (par[i].equalsIgnoreCase("-e")) {
				this.fromExisting = !this.fromExisting;
				v.sendMessage(ChatColor.AQUA + "Now filling down from " + ((this.fromExisting) ? "existing" : "all") + " blocks.");
			} else {
				v.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.filldown";
	}
}
