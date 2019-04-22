package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
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
	public final void info(Messages messages) {
		messages.brushName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.snipe";
	}
}
