package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
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
		super("Shell Set");
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
			int x1 = this.block.getX();
			int x2 = block.getX();
			int y1 = this.block.getY();
			int y2 = block.getY();
			int z1 = this.block.getZ();
			int z2 = block.getZ();
			int lowX = (x1 <= x2) ? x1 : x2;
			int lowY = (y1 <= y2) ? y1 : y2;
			int lowZ = (z1 <= z2) ? z1 : z2;
			int highX = (x1 >= x2) ? x1 : x2;
			int highY = (y1 >= y2) ? y1 : y2;
			int highZ = (z1 >= z2) ? z1 : z2;
			if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > MAX_SIZE) {
				snipeData.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
			} else {
				List<Block> blocks = new ArrayList<>(((Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY)) / 2));
				for (int y = lowY; y <= highY; y++) {
					for (int x = lowX; x <= highX; x++) {
						for (int z = lowZ; z <= highZ; z++) {
							World world = getWorld();
							Material replaceBlockDataType = snipeData.getReplaceBlockDataType();
							if (isBlockTypeNotEqual(world, y, x, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x + 1, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x - 1, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x, z + 1, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x, z - 1, replaceBlockDataType) && isBlockTypeNotEqual(world, y + 1, x, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y - 1, x, z, replaceBlockDataType)) {
								blocks.add(world.getBlockAt(x, y, z));
							}
						}
					}
				}
				Undo undo = new Undo();
				for (Block currentBlock : blocks) {
					if (currentBlock.getType() != snipeData.getBlockDataType()) {
						undo.put(currentBlock);
						currentBlock.setType(snipeData.getBlockDataType());
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

	private boolean isBlockTypeNotEqual(World world, int y, int x, int z, Material replaceBlockDataType) {
		Block block = world.getBlockAt(x, y, z);
		return block.getType() != replaceBlockDataType;
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		if (this.set(this.getTargetBlock(), snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		if (this.set(lastBlock, snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.blockDataType();
		message.replaceBlockDataType();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.shellset";
	}
}
