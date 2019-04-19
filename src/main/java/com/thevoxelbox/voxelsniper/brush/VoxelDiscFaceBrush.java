package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Disc_Face_Brush
 *
 * @author Voxel
 */
public class VoxelDiscFaceBrush extends PerformBrush {

	/**
	 *
	 */
	public VoxelDiscFaceBrush() {
		this.setName("Voxel Disc Face");
	}

	private void disc(SnipeData snipeData, Block targetBlock) {
		for (int x = snipeData.getBrushSize(); x >= -snipeData.getBrushSize(); x--) {
			for (int y = snipeData.getBrushSize(); y >= -snipeData.getBrushSize(); y--) {
				this.current.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY(), targetBlock.getZ() + y));
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	private void discNorthSouth(SnipeData snipeData, Block targetBlock) {
		for (int x = snipeData.getBrushSize(); x >= -snipeData.getBrushSize(); x--) {
			for (int y = snipeData.getBrushSize(); y >= -snipeData.getBrushSize(); y--) {
				this.current.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ()));
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	private void discEastWest(SnipeData snipeData, Block targetBlock) {
		for (int x = snipeData.getBrushSize(); x >= -snipeData.getBrushSize(); x--) {
			for (int y = snipeData.getBrushSize(); y >= -snipeData.getBrushSize(); y--) {
				this.current.perform(this.clampY(targetBlock.getX(), targetBlock.getY() + x, targetBlock.getZ() + y));
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	private void pre(SnipeData snipeData, BlockFace blockFace, Block targetBlock) {
		if (blockFace == null) {
			return;
		}
		switch (blockFace) {
			case NORTH:
			case SOUTH:
				this.discNorthSouth(snipeData, targetBlock);
				break;
			case EAST:
			case WEST:
				this.discEastWest(snipeData, targetBlock);
				break;
			case UP:
			case DOWN:
				this.disc(snipeData, targetBlock);
				break;
			default:
				break;
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.pre(snipeData, this.getTargetBlock()
			.getFace(this.getLastBlock()), this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.pre(snipeData, this.getTargetBlock()
			.getFace(this.getLastBlock()), this.getLastBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voxeldiscface";
	}
}
