package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

/**
 * @author Gavjenks
 */
public class LightningBrush extends AbstractBrush {

	public LightningBrush() {
		super("Lightning");
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.brushMessage("Lightning Brush!  Please use in moderation.");
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.getWorld()
			.strikeLightning(this.getTargetBlock()
				.getLocation());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.getWorld()
			.strikeLightning(this.getTargetBlock()
				.getLocation());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.lightning";
	}
}
