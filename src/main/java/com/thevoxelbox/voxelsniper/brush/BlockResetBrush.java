package com.thevoxelbox.voxelsniper.brush;

import java.util.EnumSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author MikeMatrix
 */
public class BlockResetBrush extends AbstractBrush {

	private static final Set<Material> DENIED_UPDATES = EnumSet.of(Material.LEGACY_SIGN, Material.LEGACY_SIGN_POST, Material.LEGACY_WALL_SIGN, Material.LEGACY_CHEST, Material.LEGACY_FURNACE, Material.LEGACY_BURNING_FURNACE, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_REDSTONE_TORCH_ON, Material.LEGACY_REDSTONE_WIRE, Material.LEGACY_DIODE_BLOCK_OFF, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_WOOD_DOOR, Material.LEGACY_IRON_DOOR, Material.LEGACY_IRON_DOOR_BLOCK, Material.LEGACY_FENCE_GATE);

	public BlockResetBrush() {
		super("Block Reset Brush");
	}

	private void applyBrush(SnipeData v) {
		for (int z = -v.getBrushSize(); z <= v.getBrushSize(); z++) {
			for (int x = -v.getBrushSize(); x <= v.getBrushSize(); x++) {
				for (int y = -v.getBrushSize(); y <= v.getBrushSize(); y++) {
					Block block = this.getWorld()
						.getBlockAt(this.getTargetBlock()
							.getX() + x, this.getTargetBlock()
							.getY() + y, this.getTargetBlock()
							.getZ() + z);
					if (DENIED_UPDATES.contains(block.getType())) {
						continue;
					}
					block.setBlockData(block.getType()
						.createBlockData(), true);
				}
			}
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		applyBrush(snipeData);
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		applyBrush(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.blockreset";
	}
}
