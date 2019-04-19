package com.thevoxelbox.voxelsniper.brush;

import java.util.EnumSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Eraser_Brush
 *
 * @author Voxel
 */
public class EraserBrush extends AbstractBrush {

	private static final Set<Material> EXCLUSIVE_MATERIALS = EnumSet.of(Material.AIR, Material.STONE, Material.GRASS, Material.DIRT, Material.SAND, Material.GRAVEL, Material.SANDSTONE);
	private static final Set<Material> EXCLUSIVE_LIQUIDS = EnumSet.of(Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);

	/**
	 *
	 */
	public EraserBrush() {
		this.setName("Eraser");
	}

	private void doErase(SnipeData v, boolean keepWater) {
		int brushSize = v.getBrushSize();
		int brushSizeDoubled = 2 * brushSize;
		World world = this.getTargetBlock()
			.getWorld();
		Undo undo = new Undo();
		for (int x = brushSizeDoubled; x >= 0; x--) {
			int currentX = this.getTargetBlock()
				.getX() - brushSize + x;
			for (int y = 0; y <= brushSizeDoubled; y++) {
				int currentY = this.getTargetBlock()
					.getY() - brushSize + y;
				for (int z = brushSizeDoubled; z >= 0; z--) {
					int currentZ = this.getTargetBlock()
						.getZ() - brushSize + z;
					Block currentBlock = world.getBlockAt(currentX, currentY, currentZ);
					if (EXCLUSIVE_MATERIALS.contains(currentBlock.getType()) || (keepWater && EXCLUSIVE_LIQUIDS.contains(currentBlock.getType()))) {
						continue;
					}
					undo.put(currentBlock);
					currentBlock.setType(Material.AIR);
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.doErase(v, false);
	}

	@Override
	protected final void powder(SnipeData v) {
		this.doErase(v, true);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.eraser";
	}
}
