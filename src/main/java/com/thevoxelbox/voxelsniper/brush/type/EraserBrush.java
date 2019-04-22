package com.thevoxelbox.voxelsniper.brush.type;

import java.util.EnumSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Eraser_Brush
 *
 * @author Voxel
 */
public class EraserBrush extends AbstractBrush {

	private static final Set<Material> EXCLUSIVE_MATERIALS = EnumSet.of(Material.LEGACY_AIR, Material.LEGACY_STONE, Material.LEGACY_GRASS, Material.LEGACY_DIRT, Material.LEGACY_SAND, Material.LEGACY_GRAVEL, Material.LEGACY_SANDSTONE);
	private static final Set<Material> EXCLUSIVE_LIQUIDS = EnumSet.of(Material.LEGACY_WATER, Material.LEGACY_STATIONARY_WATER, Material.LEGACY_LAVA, Material.LEGACY_STATIONARY_LAVA);

	public EraserBrush() {
		super("Eraser");
	}

	private void doErase(ToolkitProperties toolkitProperties, boolean keepWater) {
		int brushSize = toolkitProperties.getBrushSize();
		int brushSizeDoubled = 2 * brushSize;
		Block targetBlock = this.getTargetBlock();
		World world = targetBlock.getWorld();
		Undo undo = new Undo();
		for (int x = brushSizeDoubled; x >= 0; x--) {
			int currentX = targetBlock.getX() - brushSize + x;
			for (int y = 0; y <= brushSizeDoubled; y++) {
				int currentY = targetBlock.getY() - brushSize + y;
				for (int z = brushSizeDoubled; z >= 0; z--) {
					int currentZ = targetBlock.getZ() - brushSize + z;
					Block currentBlock = world.getBlockAt(currentX, currentY, currentZ);
					if (EXCLUSIVE_MATERIALS.contains(currentBlock.getType()) || (keepWater && EXCLUSIVE_LIQUIDS.contains(currentBlock.getType()))) {
						continue;
					}
					undo.put(currentBlock);
					currentBlock.setType(Material.AIR);
				}
			}
		}
		toolkitProperties.getOwner()
			.storeUndo(undo);
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.doErase(toolkitProperties, false);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.doErase(toolkitProperties, true);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.eraser";
	}
}
