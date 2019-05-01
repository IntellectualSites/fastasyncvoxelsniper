package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class VoxelDiscFaceBrush extends AbstractPerformerBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		Block targetBlock = getTargetBlock();
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		pre(snipe, face, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		Block targetBlock = getTargetBlock();
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		pre(snipe, face, lastBlock);
	}

	private void pre(Snipe snipe, BlockFace blockFace, Block targetBlock) {
		switch (blockFace) {
			case NORTH:
			case SOUTH:
				discNorthSouth(snipe, targetBlock);
				break;
			case EAST:
			case WEST:
				discEastWest(snipe, targetBlock);
				break;
			case UP:
			case DOWN:
				disc(snipe, targetBlock);
				break;
			default:
				break;
		}
	}

	private void discNorthSouth(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		for (int x = brushSize; x >= -brushSize; x--) {
			for (int y = brushSize; y >= -brushSize; y--) {
				this.performer.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ()));
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	private void discEastWest(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		for (int x = brushSize; x >= -brushSize; x--) {
			for (int y = brushSize; y >= -brushSize; y--) {
				this.performer.perform(this.clampY(targetBlock.getX(), targetBlock.getY() + x, targetBlock.getZ() + y));
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	private void disc(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		for (int x = brushSize; x >= -brushSize; x--) {
			for (int y = brushSize; y >= -brushSize; y--) {
				this.performer.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY(), targetBlock.getZ() + y));
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
