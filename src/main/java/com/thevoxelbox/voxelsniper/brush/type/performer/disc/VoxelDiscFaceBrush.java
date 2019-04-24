package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Voxel_Disc_Face_Brush
 *
 * @author Voxel
 */
public class VoxelDiscFaceBrush extends AbstractPerformerBrush {

	public VoxelDiscFaceBrush() {
		super("Voxel Disc Face");
	}

	private void disc(ToolkitProperties toolkitProperties, Block targetBlock) {
		for (int x = toolkitProperties.getBrushSize(); x >= -toolkitProperties.getBrushSize(); x--) {
			for (int y = toolkitProperties.getBrushSize(); y >= -toolkitProperties.getBrushSize(); y--) {
				this.performer.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY(), targetBlock.getZ() + y));
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void discNorthSouth(ToolkitProperties toolkitProperties, Block targetBlock) {
		for (int x = toolkitProperties.getBrushSize(); x >= -toolkitProperties.getBrushSize(); x--) {
			for (int y = toolkitProperties.getBrushSize(); y >= -toolkitProperties.getBrushSize(); y--) {
				this.performer.perform(this.clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ()));
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void discEastWest(ToolkitProperties toolkitProperties, Block targetBlock) {
		for (int x = toolkitProperties.getBrushSize(); x >= -toolkitProperties.getBrushSize(); x--) {
			for (int y = toolkitProperties.getBrushSize(); y >= -toolkitProperties.getBrushSize(); y--) {
				this.performer.perform(this.clampY(targetBlock.getX(), targetBlock.getY() + x, targetBlock.getZ() + y));
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void pre(ToolkitProperties toolkitProperties, BlockFace blockFace, Block targetBlock) {
		if (blockFace == null) {
			return;
		}
		switch (blockFace) {
			case NORTH:
			case SOUTH:
				this.discNorthSouth(toolkitProperties, targetBlock);
				break;
			case EAST:
			case WEST:
				this.discEastWest(toolkitProperties, targetBlock);
				break;
			case UP:
			case DOWN:
				this.disc(toolkitProperties, targetBlock);
				break;
			default:
				break;
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Block targetBlock = this.getTargetBlock();
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		this.pre(toolkitProperties, face, targetBlock);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Block targetBlock = this.getTargetBlock();
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		this.pre(toolkitProperties, face, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voxeldiscface";
	}
}
