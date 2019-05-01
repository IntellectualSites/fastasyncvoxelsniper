package com.thevoxelbox.voxelsniper.brush.type;

import java.util.EnumSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author MikeMatrix
 */
public class BlockResetBrush extends AbstractBrush {

	private static final Set<Material> DENIED_UPDATES = EnumSet.of(Material.LEGACY_SIGN, Material.LEGACY_SIGN_POST, Material.LEGACY_WALL_SIGN, Material.LEGACY_CHEST, Material.LEGACY_FURNACE, Material.LEGACY_BURNING_FURNACE, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_REDSTONE_TORCH_ON, Material.LEGACY_REDSTONE_WIRE, Material.LEGACY_DIODE_BLOCK_OFF, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_WOOD_DOOR, Material.LEGACY_IRON_DOOR, Material.LEGACY_IRON_DOOR_BLOCK, Material.LEGACY_FENCE_GATE);

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
					World world = getWorld();
					Block targetBlock = getTargetBlock();
					Block block = world.getBlockAt(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
					Material blockType = block.getType();
					if (!DENIED_UPDATES.contains(blockType)) {
						block.setBlockData(blockType.createBlockData(), true);
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
