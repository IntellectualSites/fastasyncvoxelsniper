package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Disc_Brush
 *
 * @author Voxel
 */
public class VoxelDiscBrush extends AbstractPerformerBrush {

	public VoxelDiscBrush() {
		super("Voxel Disc");
	}

	private void disc(ToolkitProperties toolkitProperties, Block targetBlock) {
		for (int x = toolkitProperties.getBrushSize(); x >= -toolkitProperties.getBrushSize(); x--) {
			for (int z = toolkitProperties.getBrushSize(); z >= -toolkitProperties.getBrushSize(); z--) {
				this.performer.perform(targetBlock.getRelative(x, 0, z));
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.disc(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.disc(toolkitProperties, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voxeldisc";
	}
}
