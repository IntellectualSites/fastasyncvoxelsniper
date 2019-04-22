package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
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

	private void disc(SnipeData snipeData, Block targetBlock) {
		for (int x = snipeData.getBrushSize(); x >= -snipeData.getBrushSize(); x--) {
			for (int z = snipeData.getBrushSize(); z >= -snipeData.getBrushSize(); z--) {
				this.current.perform(targetBlock.getRelative(x, 0, z));
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.disc(snipeData, this.getTargetBlock());
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.disc(snipeData, lastBlock);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voxeldisc";
	}
}
