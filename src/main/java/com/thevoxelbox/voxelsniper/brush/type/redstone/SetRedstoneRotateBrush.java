package com.thevoxelbox.voxelsniper.brush.type.redstone;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Repeater;
import org.jetbrains.annotations.Nullable;

public class SetRedstoneRotateBrush extends AbstractBrush {

	@Nullable
	private BlockVector3 block;
	private Undo undo;

	@Override
	public void handleArrowAction(Snipe snipe) {
		BlockVector3 targetBlock = getTargetBlock();
		if (set(targetBlock)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(this.undo);
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		BlockVector3 lastBlock = getLastBlock();
		if (set(lastBlock)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(this.undo);
		}
	}

	private boolean set(BlockVector3 block) {
		if (this.block == null) {
			this.block = block;
			return true;
		} else {
			this.undo = new Undo();
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
			for (int y = lowY; y <= highY; y++) {
				for (int x = lowX; x <= highX; x++) {
					for (int z = lowZ; z <= highZ; z++) {
						perform(x, clampY(y), z, clampY(x, y, z));
					}
				}
			}
			this.block = null;
			return false;
		}
	}

	private void perform(int x, int y, int z, BlockState block) {
		if (BukkitAdapter.adapt(block.getBlockType()) == Material.REPEATER) {
			this.undo.put(block);
			BlockData blockData = getBlockData(x, y, z);
			Repeater repeater = (Repeater) blockData;
			int delay = repeater.getDelay();
			repeater.setDelay(delay % 4 + 1 < 5 ? (byte) (delay + 1) : (byte) (delay - 4));
			setBlockData(x, y, z, blockData);
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		this.block = null;
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
