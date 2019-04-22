package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Snipe_Brush
 *
 * @author Voxel
 */
public class SnipeBrush extends AbstractPerformerBrush {

	public SnipeBrush() {
		super("Snipe");
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.performer.perform(this.getTargetBlock());
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.performer.perform(lastBlock);
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.snipe";
	}
}
