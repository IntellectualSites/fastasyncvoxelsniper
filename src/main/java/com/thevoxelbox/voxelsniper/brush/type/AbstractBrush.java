package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public abstract class AbstractBrush implements Brush {

	protected static final int CHUNK_SIZE = 16;

	private Block targetBlock;
	private Block lastBlock;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		player.sendMessage(ChatColor.RED + "This brush does not accept additional parameters.");
	}

	@Override
	public void perform(Snipe snipe, ToolAction action, Block targetBlock, Block lastBlock) {
		this.targetBlock = targetBlock;
		this.lastBlock = lastBlock;
		if (action == ToolAction.ARROW) {
			handleArrowAction(snipe);
		} else if (action == ToolAction.GUNPOWDER) {
			handleGunpowderAction(snipe);
		}
	}

	public Block clampY(int x, int y, int z) {
		int clampedY = y;
		World world = this.targetBlock.getWorld();
		if (clampedY < 0) {
			clampedY = 0;
		} else {
			int maxHeight = world.getMaxHeight();
			if (clampedY > maxHeight) {
				clampedY = maxHeight;
			}
		}
		return world.getBlockAt(x, clampedY, z);
	}

	public Material getBlockType(int x, int y, int z) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		return block.getType();
	}

	public BlockData getBlockData(int x, int y, int z) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		return block.getBlockData();
	}

	public void setBlockType(int x, int y, int z, Material type) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		block.setType(type);
	}

	public void setBlockData(int x, int y, int z, BlockData blockData) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		block.setBlockData(blockData);
	}

	public World getWorld() {
		return this.targetBlock.getWorld();
	}

	public Block getTargetBlock() {
		return this.targetBlock;
	}

	/**
	 * @return Block before target Block.
	 */
	public Block getLastBlock() {
		return this.lastBlock;
	}
}
