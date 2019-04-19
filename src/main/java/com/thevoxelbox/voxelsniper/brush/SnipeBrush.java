package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Snipe_Brush
 *
 * @author Voxel
 */
public class SnipeBrush extends PerformBrush {

	/**
	 *
	 */
	public SnipeBrush() {
		super("Snipe");
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.current.perform(this.getTargetBlock());
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.current.perform(this.getLastBlock());
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
