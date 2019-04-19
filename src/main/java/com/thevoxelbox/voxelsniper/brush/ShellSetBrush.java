package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Shell_Brushes
 *
 * @author Piotr
 */
public class ShellSetBrush extends AbstractBrush {

	private static final int MAX_SIZE = 5000000;

	@Nullable
	private Block block;

	public ShellSetBrush() {
		this.setName("Shell Set");
	}

	private boolean set(Block block, SnipeData snipeData) {
		if (this.block == null) {
			this.block = block;
			return true;
		} else {
			if (!this.block.getWorld()
				.getName()
				.equals(block.getWorld()
					.getName())) {
				snipeData.sendMessage(ChatColor.RED + "You selected points in different worlds!");
				this.block = null;
				return true;
			}
			int lowX = (this.block.getX() <= block.getX()) ? this.block.getX() : block.getX();
			int lowY = (this.block.getY() <= block.getY()) ? this.block.getY() : block.getY();
			int lowZ = (this.block.getZ() <= block.getZ()) ? this.block.getZ() : block.getZ();
			int highX = (this.block.getX() >= block.getX()) ? this.block.getX() : block.getX();
			int highY = (this.block.getY() >= block.getY()) ? this.block.getY() : block.getY();
			int highZ = (this.block.getZ() >= block.getZ()) ? this.block.getZ() : block.getZ();
			if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > MAX_SIZE) {
				snipeData.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
			} else {
				List<Block> blocks = new ArrayList<>(((Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY)) / 2));
				for (int y = lowY; y <= highY; y++) {
					for (int x = lowX; x <= highX; x++) {
						for (int z = lowZ; z <= highZ; z++) {
							if (this.getWorld()
								.getBlockTypeIdAt(x, y, z) == snipeData.getReplaceId()) {
							} else if (this.getWorld()
								.getBlockTypeIdAt(x + 1, y, z) == snipeData.getReplaceId()) {
							} else if (this.getWorld()
								.getBlockTypeIdAt(x - 1, y, z) == snipeData.getReplaceId()) {
							} else if (this.getWorld()
								.getBlockTypeIdAt(x, y, z + 1) == snipeData.getReplaceId()) {
							} else if (this.getWorld()
								.getBlockTypeIdAt(x, y, z - 1) == snipeData.getReplaceId()) {
							} else if (this.getWorld()
								.getBlockTypeIdAt(x, y + 1, z) == snipeData.getReplaceId()) {
							} else if (this.getWorld()
								.getBlockTypeIdAt(x, y - 1, z) == snipeData.getReplaceId()) {
							} else {
								blocks.add(this.getWorld()
									.getBlockAt(x, y, z));
							}
						}
					}
				}
				Undo undo = new Undo();
				for (Block currentBlock : blocks) {
					if (currentBlock.getTypeId() != snipeData.getVoxelId()) {
						undo.put(currentBlock);
						currentBlock.setTypeId(snipeData.getVoxelId());
					}
				}
				Sniper owner = snipeData.getOwner();
				owner.storeUndo(undo);
				snipeData.sendMessage(ChatColor.AQUA + "Shell complete.");
			}
			this.block = null;
			return false;
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		if (this.set(this.getTargetBlock(), snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		}
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		if (this.set(this.getLastBlock(), snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.voxel();
		message.replace();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.shellset";
	}
}
