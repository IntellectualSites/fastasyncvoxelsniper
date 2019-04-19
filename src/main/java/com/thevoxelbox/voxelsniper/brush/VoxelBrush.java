package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Brush
 *
 * @author Piotr
 */
public class VoxelBrush extends PerformBrush {

	/**
	 *
	 */
	public VoxelBrush() {
		this.setName("Voxel");
	}

	private void voxel(SnipeData v) {
		for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
			for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
				for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
					this.current.perform(this.clampY(this.getTargetBlock()
						.getX() + x, this.getTargetBlock()
						.getY() + z, this.getTargetBlock()
						.getZ() + y));
				}
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.voxel(v);
	}

	@Override
	protected final void powder(SnipeData v) {
		this.voxel(v);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voxel";
	}
}
