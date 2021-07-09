package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.bukkit.Material;
import org.bukkit.Tag;

public class BlockResetBrush extends AbstractBrush {

	private static final MaterialSet DENIED_UPDATES = MaterialSet.builder()
		.with(Tag.DOORS)
		.with(MaterialSets.SIGNS)
		.with(MaterialSets.CHESTS)
		.with(MaterialSets.REDSTONE_TORCHES)
		.with(MaterialSets.FENCE_GATES)
		.add(Material.FURNACE)
		.add(Material.REDSTONE_WIRE)
		.add(Material.REPEATER)
		.build();

	@Override
	public void handleArrowAction(Snipe snipe) {
		applyBrush(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		applyBrush(snipe);
	}

	private void applyBrush(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		for (int z = -brushSize; z <= brushSize; z++) {
			for (int x = -brushSize; x <= brushSize; x++) {
				for (int y = -brushSize; y <= brushSize; y++) {
					BlockVector3 targetBlock = getTargetBlock();
					Material blockType = getBlockType(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
					if (!DENIED_UPDATES.contains(blockType)) {
						setBlockData(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z, blockType.createBlockData());
					}
				}
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
