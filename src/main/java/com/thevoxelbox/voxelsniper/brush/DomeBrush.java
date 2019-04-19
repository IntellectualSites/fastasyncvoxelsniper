package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Dome_Brush
 *
 * @author Gavjenks
 * @author MikeMatrix
 */
public class DomeBrush extends AbstractBrush {

	/**
	 *
	 */
	public DomeBrush() {
		this.setName("Dome");
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.voxel();
		message.height();
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	private void generateDome(SnipeData v, Block targetBlock) {
		if (v.getVoxelHeight() == 0) {
			v.sendMessage("VoxelHeight must not be 0.");
			return;
		}
		int absoluteHeight = Math.abs(v.getVoxelHeight());
		boolean negative = v.getVoxelHeight() < 0;
		Set<Vector> changeablePositions = new HashSet<>();
		Undo undo = new Undo();
		int brushSizeTimesVoxelHeight = v.getBrushSize() * absoluteHeight;
		double stepScale = ((v.getBrushSize() * v.getBrushSize()) + brushSizeTimesVoxelHeight + brushSizeTimesVoxelHeight) / 5;
		double stepSize = 1 / stepScale;
		for (double u = 0; u <= Math.PI / 2; u += stepSize) {
			double y = absoluteHeight * Math.sin(u);
			for (double stepV = -Math.PI; stepV <= -(Math.PI / 2); stepV += stepSize) {
				double x = v.getBrushSize() * Math.cos(u) * Math.cos(stepV);
				double z = v.getBrushSize() * Math.cos(u) * Math.sin(stepV);
				double targetBlockX = targetBlock.getX() + 0.5;
				double targetBlockZ = targetBlock.getZ() + 0.5;
				int targetY = NumberConversions.floor(targetBlock.getY() + (negative ? -y : y));
				int currentBlockXAdd = NumberConversions.floor(targetBlockX + x);
				int currentBlockZAdd = NumberConversions.floor(targetBlockZ + z);
				int currentBlockXSubtract = NumberConversions.floor(targetBlockX - x);
				int currentBlockZSubtract = NumberConversions.floor(targetBlockZ - z);
				changeablePositions.add(new Vector(currentBlockXAdd, targetY, currentBlockZAdd));
				changeablePositions.add(new Vector(currentBlockXSubtract, targetY, currentBlockZAdd));
				changeablePositions.add(new Vector(currentBlockXAdd, targetY, currentBlockZSubtract));
				changeablePositions.add(new Vector(currentBlockXSubtract, targetY, currentBlockZSubtract));
			}
		}
		for (Vector vector : changeablePositions) {
			Block currentTargetBlock = vector.toLocation(this.getTargetBlock()
				.getWorld())
				.getBlock();
			if (currentTargetBlock.getTypeId() != v.getVoxelId() || currentTargetBlock.getData() != v.getData()) {
				undo.put(currentTargetBlock);
				currentTargetBlock.setTypeIdAndData(v.getVoxelId(), v.getData(), true);
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.generateDome(v, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData v) {
		this.generateDome(v, this.getLastBlock());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.dome";
	}
}
