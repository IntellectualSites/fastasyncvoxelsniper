package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;

public class VoxelBrush extends AbstractPerformerBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		voxel(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		voxel(snipe);
	}

	private void voxel(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				for (int y = brushSize; y >= -brushSize; y--) {
					Block targetBlock = getTargetBlock();
					this.performer.perform(clampY(targetBlock.getX() + x, targetBlock.getY() + z, targetBlock.getZ() + y));
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.send();
	}
}
