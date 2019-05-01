package com.thevoxelbox.voxelsniper.brush.type;

import java.util.HashSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Dome_Brush
 *
 * @author Gavjenks
 * @author MikeMatrix
 */
public class DomeBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		generateDome(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		generateDome(snipe, lastBlock);
	}

	private void generateDome(Snipe snipe, Block block) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int voxelHeight = toolkitProperties.getVoxelHeight();
		if (voxelHeight == 0) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage("VoxelHeight must not be 0.");
			return;
		}
		int absoluteHeight = Math.abs(voxelHeight);
		boolean negative = voxelHeight < 0;
		Set<Vector> changeablePositions = new HashSet<>();
		Undo undo = new Undo();
		int brushSize = toolkitProperties.getBrushSize();
		int brushSizeTimesVoxelHeight = brushSize * absoluteHeight;
		double stepScale = (brushSize * brushSize + brushSizeTimesVoxelHeight + brushSizeTimesVoxelHeight) / 5.0;
		double stepSize = 1.0 / stepScale;
		for (double u = 0; u <= Math.PI / 2; u += stepSize) {
			double y = absoluteHeight * Math.sin(u);
			for (double stepV = -Math.PI; stepV <= -(Math.PI / 2); stepV += stepSize) {
				double x = brushSize * Math.cos(u) * Math.cos(stepV);
				double z = brushSize * Math.cos(u) * Math.sin(stepV);
				double targetBlockX = block.getX() + 0.5;
				double targetBlockZ = block.getZ() + 0.5;
				int targetY = NumberConversions.floor(block.getY() + (negative ? -y : y));
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
		World world = getWorld();
		for (Vector vector : changeablePositions) {
			Location location = vector.toLocation(world);
			Block currentTargetBlock = location.getBlock();
			BlockData currentTargetBlockBlockData = currentTargetBlock.getBlockData();
			BlockData snipeBlockData = toolkitProperties.getBlockData();
			if (!currentTargetBlockBlockData.equals(snipeBlockData)) {
				undo.put(currentTargetBlock);
				currentTargetBlock.setBlockData(snipeBlockData);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendBlockTypeMessage();
		messenger.sendVoxelHeightMessage();
	}
}
