package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * @author Gavjenks
 */
public class LightningBrush extends AbstractBrush {

	/**
	 *
	 */
	public LightningBrush() {
		this.setName("Lightning");
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.brushMessage("Lightning Brush!  Please use in moderation.");
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.getWorld()
			.strikeLightning(this.getTargetBlock()
				.getLocation());
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.getWorld()
			.strikeLightning(this.getTargetBlock()
				.getLocation());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.lightning";
	}
}
