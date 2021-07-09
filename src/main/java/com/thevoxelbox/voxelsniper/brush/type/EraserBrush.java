package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.bukkit.Material;
import org.bukkit.Tag;

public class EraserBrush extends AbstractBrush {

	private static final MaterialSet EXCLUSIVE_MATERIALS = MaterialSet.builder()
		.with(Tag.SAND)
		.with(MaterialSets.SANDSTONES)
		.with(MaterialSets.RED_SANDSTONES)
		.with(MaterialSets.AIRS)
		.with(MaterialSets.STONES)
		.with(MaterialSets.GRASSES)
		.with(MaterialSets.DIRT)
		.add(Material.GRAVEL)
		.build();

	private static final MaterialSet EXCLUSIVE_LIQUIDS = MaterialSet.builder()
		.with(MaterialSets.LIQUIDS)
		.build();

	@Override
	public void handleArrowAction(Snipe snipe) {
		doErase(snipe, false);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		doErase(snipe, true);
	}

	private void doErase(Snipe snipe, boolean keepWater) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		int brushSizeDoubled = 2 * brushSize;
		BlockVector3 targetBlock = getTargetBlock();
		Undo undo = new Undo();
		for (int x = brushSizeDoubled; x >= 0; x--) {
			int currentX = targetBlock.getX() - brushSize + x;
			for (int y = 0; y <= brushSizeDoubled; y++) {
				int currentY = targetBlock.getY() - brushSize + y;
				for (int z = brushSizeDoubled; z >= 0; z--) {
					int currentZ = targetBlock.getZ() - brushSize + z;
					BlockState currentBlock = getBlock(currentX, currentY, currentZ);
					if (!EXCLUSIVE_MATERIALS.contains(currentBlock) && (!keepWater || !EXCLUSIVE_LIQUIDS.contains(currentBlock))) {
						undo.put(currentBlock);
						setBlockType(currentX, currentY, currentZ, Material.AIR);
					}
				}
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
	}
}
