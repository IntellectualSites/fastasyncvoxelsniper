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

	private void disc(SnipeData v, Block targetBlock) {
		for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
			for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
				this.current.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY(), targetBlock.getZ() + y));
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	private void discNS(SnipeData v, Block targetBlock) {
		for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
			for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
				this.current.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ()));
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	private void discEW(SnipeData v, Block targetBlock) {
		for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
			for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
				this.current.perform(this.clampY(targetBlock.getX(), targetBlock.getY() + x, targetBlock.getZ() + y));
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	private void pre(SnipeData v, BlockFace bf, Block targetBlock) {
		if (bf == null) {
			return;
		}
		switch (bf) {
			case NORTH:
			case SOUTH:
				this.discNS(v, targetBlock);
				break;
			case EAST:
			case WEST:
				this.discEW(v, targetBlock);
				break;
			case UP:
			case DOWN:
				this.disc(v, targetBlock);
				break;
			default:
				break;
		}
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.pre(v, this.getTargetBlock()
			.getFace(this.getLastBlock()), this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData v) {
		this.pre(v, this.getTargetBlock()
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
