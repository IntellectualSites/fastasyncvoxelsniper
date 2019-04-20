package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Snipe_Brush
 *
 * @author Voxel
 */
public class SnipeBrush extends PerformBrush {

	public SnipeBrush() {
		super("Snipe");
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.current.perform(this.getTargetBlock());
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.current.perform(lastBlock);
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.snipe";
	}
}
