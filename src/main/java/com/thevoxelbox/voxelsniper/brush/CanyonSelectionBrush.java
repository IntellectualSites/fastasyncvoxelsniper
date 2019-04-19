package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Canyon_Selection_Brush
 *
 * @author Voxel
 */
public class CanyonSelectionBrush extends CanyonBrush {

	private boolean first = true;
	private int fx;
	private int fz;

	/**
	 *
	 */
	public CanyonSelectionBrush() {
		this.setName("Canyon Selection");
	}

	private void execute(SnipeData v) {
		Chunk chunk = getTargetBlock().getChunk();
		if (this.first) {
			this.fx = chunk.getX();
			this.fz = chunk.getZ();
			v.sendMessage(ChatColor.YELLOW + "First point selected!");
		} else {
			v.sendMessage(ChatColor.YELLOW + "Second point selected!");
			selection(Math.min(this.fx, chunk.getX()), Math.min(this.fz, chunk.getZ()), Math.max(this.fx, chunk.getX()), Math.max(this.fz, chunk.getZ()), v);
		}
		this.first = !this.first;
	}

	private void selection(int lowX, int lowZ, int highX, int highZ, SnipeData v) {
		Undo undo = new Undo();
		for (int x = lowX; x <= highX; x++) {
			for (int z = lowZ; z <= highZ; z++) {
				canyon(getWorld().getChunkAt(x, z), undo);
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@Override
	protected final void arrow(SnipeData v) {
		execute(v);
	}

	@Override
	protected final void powder(SnipeData v) {
		execute(v);
	}

	@Override
	public final void info(Message vm) {
		vm.brushName(this.getName());
		vm.custom(ChatColor.GREEN + "Shift Level set to " + this.getYLevel());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.canyonselection";
	}
}
