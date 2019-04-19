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
		this.setName("Snipe");
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.current.perform(this.getTargetBlock());
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void powder(SnipeData v) {
		this.current.perform(this.getLastBlock());
		v.getOwner()
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
