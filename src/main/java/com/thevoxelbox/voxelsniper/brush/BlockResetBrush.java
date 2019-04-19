package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author MikeMatrix
 */
public class BlockResetBrush extends AbstractBrush {

	private static final List<Material> DENIED_UPDATES = new ArrayList<>();

	static {
		DENIED_UPDATES.add(Material.LEGACY_SIGN);
		DENIED_UPDATES.add(Material.LEGACY_SIGN_POST);
		DENIED_UPDATES.add(Material.LEGACY_WALL_SIGN);
		DENIED_UPDATES.add(Material.LEGACY_CHEST);
		DENIED_UPDATES.add(Material.LEGACY_FURNACE);
		DENIED_UPDATES.add(Material.LEGACY_BURNING_FURNACE);
		DENIED_UPDATES.add(Material.LEGACY_REDSTONE_TORCH_OFF);
		DENIED_UPDATES.add(Material.LEGACY_REDSTONE_TORCH_ON);
		DENIED_UPDATES.add(Material.LEGACY_REDSTONE_WIRE);
		DENIED_UPDATES.add(Material.LEGACY_DIODE_BLOCK_OFF);
		DENIED_UPDATES.add(Material.LEGACY_DIODE_BLOCK_ON);
		DENIED_UPDATES.add(Material.LEGACY_WOODEN_DOOR);
		DENIED_UPDATES.add(Material.LEGACY_WOOD_DOOR);
		DENIED_UPDATES.add(Material.LEGACY_IRON_DOOR);
		DENIED_UPDATES.add(Material.LEGACY_IRON_DOOR_BLOCK);
		DENIED_UPDATES.add(Material.LEGACY_FENCE_GATE);
	}

	/**
	 *
	 */
	public BlockResetBrush() {
		this.setName("Block Reset Brush");
	}

	@SuppressWarnings("deprecation")
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
