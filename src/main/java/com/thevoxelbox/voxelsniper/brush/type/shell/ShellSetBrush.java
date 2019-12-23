package com.thevoxelbox.voxelsniper.brush.type.shell;

import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShellSetBrush extends AbstractBrush {

	private static final int MAX_SIZE = 5000000;

	@Nullable
	private Block block;

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (set(targetBlock, snipe)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(ChatColor.GRAY + "Point one");
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		if (set(lastBlock, snipe)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(ChatColor.GRAY + "Point one");
		}
	}

	private boolean set(Block block, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		if (this.block == null) {
			this.block = block;
			return true;
		} else {
			if (!this.block.getWorld()
				.getName()
				.equals(block.getWorld()
					.getName())) {
				messenger.sendMessage(ChatColor.RED + "You selected points in different worlds!");
				this.block = null;
				return true;
			}
			int x1 = this.block.getX();
			int x2 = block.getX();
			int y1 = this.block.getY();
			int y2 = block.getY();
			int z1 = this.block.getZ();
			int z2 = block.getZ();
			int lowX = Math.min(x1, x2);
			int lowY = Math.min(y1, y2);
			int lowZ = Math.min(z1, z2);
			int highX = Math.max(x1, x2);
			int highY = Math.max(y1, y2);
			int highZ = Math.max(z1, z2);
			int size = Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY);
			if (size > MAX_SIZE) {
				messenger.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
			} else {
				List<Block> blocks = new ArrayList<>(size / 2);
				for (int y = lowY; y <= highY; y++) {
					for (int x = lowX; x <= highX; x++) {
						for (int z = lowZ; z <= highZ; z++) {
							World world = getWorld();
							Material replaceBlockDataType = toolkitProperties.getReplaceBlockType();
							if (isBlockTypeNotEqual(world, y, x, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x + 1, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x - 1, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x, z + 1, replaceBlockDataType) && isBlockTypeNotEqual(world, y, x, z - 1, replaceBlockDataType) && isBlockTypeNotEqual(world, y + 1, x, z, replaceBlockDataType) && isBlockTypeNotEqual(world, y - 1, x, z, replaceBlockDataType)) {
								blocks.add(world.getBlockAt(x, y, z));
							}
						}
					}
				}
				Undo undo = new Undo();
				for (Block currentBlock : blocks) {
					Material blockType = toolkitProperties.getBlockType();
					if (currentBlock.getType() != blockType) {
						undo.put(currentBlock);
						currentBlock.setType(blockType);
					}
				}
				Sniper sniper = snipe.getSniper();
				sniper.storeUndo(undo);
				messenger.sendMessage(ChatColor.AQUA + "Shell complete.");
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
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.blockTypeMessage()
			.replaceBlockTypeMessage()
			.send();
	}
}
