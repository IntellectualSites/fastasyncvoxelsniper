package com.thevoxelbox.voxelsniper.sniper.toolkit;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class BlockTracer {

	private Block targetBlock;
	private Block lastBlock;

	BlockTracer(Player player, int distance) {
		Location eyeLocation = player.getEyeLocation();
		Block block = eyeLocation.getBlock();
		this.targetBlock = block;
		this.lastBlock = block;
		Iterator<Block> iterator = new BlockIterator(player, distance);
		iterate(iterator);
	}

	private void iterate(Iterator<? extends Block> iterator) {
		while (iterator.hasNext()) {
			Block block = iterator.next();
			this.lastBlock = this.targetBlock;
			this.targetBlock = block;
			if (!block.isEmpty()) {
				return;
			}
		}
	}

	public Block getTargetBlock() {
		return this.targetBlock;
	}

	public Block getLastBlock() {
		return this.lastBlock;
	}
}
