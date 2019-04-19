package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Disc_Brush
 *
 * @author Voxel
 */
public class VoxelDiscBrush extends PerformBrush {

	/**
	 *
	 */
	public VoxelDiscBrush() {
		this.setName("Voxel Disc");
	}

	private void disc(SnipeData v, Block targetBlock) {
		for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
			for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
				this.current.perform(targetBlock.getRelative(x, 0, z));
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.disc(v, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData v) {
		this.disc(v, this.getLastBlock());
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
