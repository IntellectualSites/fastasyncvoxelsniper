package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Brush
 *
 * @author Piotr
 */
public class VoxelBrush extends AbstractPerformerBrush {

	public VoxelBrush() {
		super("Voxel");
	}

	@Override
	public void arrow(SnipeData snipeData) {
		voxel(snipeData);
	}

	@Override
	public void powder(SnipeData snipeData) {
		voxel(snipeData);
	}

	private void voxel(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				for (int y = brushSize; y >= -brushSize; y--) {
					Block targetBlock = this.getTargetBlock();
					this.performer.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY() + z, targetBlock.getZ() + y));
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(this.performer.getUndo());
	}

	@Override
	public void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voxel";
	}
}
