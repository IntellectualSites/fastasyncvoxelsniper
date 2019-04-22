package com.thevoxelbox.voxelsniper.brush.type;

import java.util.EnumSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author MikeMatrix
 */
public class BlockResetBrush extends AbstractBrush {

	private static final Set<Material> DENIED_UPDATES = EnumSet.of(Material.LEGACY_SIGN, Material.LEGACY_SIGN_POST, Material.LEGACY_WALL_SIGN, Material.LEGACY_CHEST, Material.LEGACY_FURNACE, Material.LEGACY_BURNING_FURNACE, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_REDSTONE_TORCH_ON, Material.LEGACY_REDSTONE_WIRE, Material.LEGACY_DIODE_BLOCK_OFF, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_WOOD_DOOR, Material.LEGACY_IRON_DOOR, Material.LEGACY_IRON_DOOR_BLOCK, Material.LEGACY_FENCE_GATE);

	public BlockResetBrush() {
		super("Block Reset Brush");
	}

	private void applyBrush(ToolkitProperties v) {
		for (int z = -v.getBrushSize(); z <= v.getBrushSize(); z++) {
			for (int x = -v.getBrushSize(); x <= v.getBrushSize(); x++) {
				for (int y = -v.getBrushSize(); y <= v.getBrushSize(); y++) {
					World world = this.getWorld();
					Block targetBlock = this.getTargetBlock();
					Block block = world.getBlockAt(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + z);
					Material blockType = block.getType();
					if (DENIED_UPDATES.contains(blockType)) {
						continue;
					}
					block.setBlockData(blockType.createBlockData(), true);
				}
			}
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		applyBrush(toolkitProperties);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		applyBrush(toolkitProperties);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.blockreset";
	}
}
