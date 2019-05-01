package com.thevoxelbox.voxelsniper.brush.type;

import java.util.EnumSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * This brush only looks for solid blocks, and then changes those plus any air blocks touching them. If it works, this brush should be faster than the original
 * blockPositionY an amount proportional to the volume of a snipe selection area / the number of blocks touching air in the selection. This is because every solid block
 * surrounded blockPositionY others should take equally long to check and not change as it would take MC to change them and then check and find no lighting to update. For
 * air blocks surrounded blockPositionY other air blocks, this brush saves about 80-100 checks blockPositionY not updating them or their lighting. And for air blocks touching solids,
 * this brush is slower, because it replaces the air once per solid block it is touching. I assume on average this is about 2 blocks. So every air block
 * touching a solid negates one air block floating in air. Thus, for selections that have more air blocks surrounded blockPositionY air than air blocks touching solids,
 * this brush will be faster, which is almost always the case, especially for undeveloped terrain and for larger brush sizes (unlike the original brush, this
 * should only slow down blockPositionY the square of the brush size, not the cube of the brush size). For typical terrain, blockPositionY my calculations, overall speed increase is
 * about a factor of 5-6 for a size 20 brush. For a complicated city or ship, etc., this may be only a factor of about 2. In a hypothetical worst case scenario
 * of a 3d checkerboard of stone and air every other block, this brush should only be about 1.5x slower than the original brush. Savings increase for larger
 * brushes.
 *
 * @author GavJenks
 */
public class BlockResetSurfaceBrush extends AbstractBrush {

	private static final Set<Material> DENIED_UPDATES = EnumSet.of(Material.LEGACY_SIGN, Material.LEGACY_SIGN_POST, Material.LEGACY_WALL_SIGN, Material.LEGACY_CHEST, Material.LEGACY_FURNACE, Material.LEGACY_BURNING_FURNACE, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_REDSTONE_TORCH_ON, Material.LEGACY_REDSTONE_WIRE, Material.LEGACY_DIODE_BLOCK_OFF, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_WOOD_DOOR, Material.LEGACY_IRON_DOOR, Material.LEGACY_IRON_DOOR_BLOCK, Material.LEGACY_FENCE_GATE, Material.LEGACY_AIR);

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
		int size = toolkitProperties.getBrushSize();
		for (int x = -size; x <= size; x++) {
			for (int y = -size; y <= size; y++) {
				for (int z = -size; z <= size; z++) {
					Block block = getBlockAtRelativeToTarget(x, y, z);
					Material type = block.getType();
					if (!DENIED_UPDATES.contains(type) && isAirAround(x, y, z)) {
						resetBlock(block);
					}
				}
			}
		}
	}

	private boolean isAirAround(int x, int y, int z) {
		return findAir(x + 1, y, z) || findAir(x - 1, y, z) || findAir(x, y + 1, z) || findAir(x, y - 1, z) || findAir(x, y, z + 1) || findAir(x, y, z - 1);
	}

	private boolean findAir(int x, int y, int z) {
		Block block = getBlockAtRelativeToTarget(x, y, z);
		if (!block.isEmpty()) {
			return false;
		}
		resetBlock(block);
		return true;
	}

	private Block getBlockAtRelativeToTarget(int x, int y, int z) {
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockY = targetBlock.getY();
		int targetBlockZ = targetBlock.getZ();
		return world.getBlockAt(targetBlockX + x, targetBlockY + y, targetBlockZ + z);
	}

	private void resetBlock(Block block) {
		BlockData oldData = block.getBlockData();
		Material type = block.getType();
		BlockData defaultData = type.createBlockData();
		block.setBlockData(defaultData);
		block.setBlockData(oldData);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
