package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Brush
 *
 * @author Piotr
 */
public class VoxelBrush extends PerformBrush {

	public VoxelBrush() {
		this.setName("Voxel");
	}

	@Override
	protected void arrow(SnipeData snipeData) {
		voxel(snipeData);
	}

	@Override
	protected void powder(SnipeData snipeData) {
		voxel(snipeData);
	}

	private void voxel(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				for (int y = brushSize; y >= -brushSize; y--) {
					this.current.perform(this.clampY(this.getTargetBlock()
						.getX() + x, this.getTargetBlock()
						.getY() + z, this.getTargetBlock()
						.getZ() + y));
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(this.current.getUndo());
	}

	@Override
	public void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voxel";
	}
}
