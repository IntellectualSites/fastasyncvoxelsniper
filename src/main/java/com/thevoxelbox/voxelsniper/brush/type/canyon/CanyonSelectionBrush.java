package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
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

	public CanyonSelectionBrush() {
		super("Canyon Selection");
	}

	private void execute(ToolkitProperties toolkitProperties) {
		Chunk chunk = getTargetBlock().getChunk();
		if (this.first) {
			this.fx = chunk.getX();
			this.fz = chunk.getZ();
			toolkitProperties.sendMessage(ChatColor.YELLOW + "First point selected!");
		} else {
			toolkitProperties.sendMessage(ChatColor.YELLOW + "Second point selected!");
			selection(Math.min(this.fx, chunk.getX()), Math.min(this.fz, chunk.getZ()), Math.max(this.fx, chunk.getX()), Math.max(this.fz, chunk.getZ()), toolkitProperties);
		}
		this.first = !this.first;
	}

	private void selection(int lowX, int lowZ, int highX, int highZ, ToolkitProperties v) {
		Undo undo = new Undo();
		for (int x = lowX; x <= highX; x++) {
			for (int z = lowZ; z <= highZ; z++) {
				canyon(getWorld().getChunkAt(x, z), undo);
			}
		}
		v.getOwner()
			.storeUndo(undo);
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		execute(toolkitProperties);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		execute(toolkitProperties);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.custom(ChatColor.GREEN + "Shift Level set to " + this.getYLevel());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.canyonselection";
	}
}
