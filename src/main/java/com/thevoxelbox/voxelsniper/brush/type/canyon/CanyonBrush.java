package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CanyonBrush extends AbstractBrush {

	private static final int SHIFT_LEVEL_MIN = 10;
	private static final int SHIFT_LEVEL_MAX = 60;
	private int yLevel = 10;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		String firstParameter = parameters[0];
		if (firstParameter.equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.GREEN + "y[number] to set the Level to which the land will be shifted down");
		}
		if (!firstParameter.isEmpty() && firstParameter.charAt(0) == 'y') {
			int y = Integer.parseInt(firstParameter.replace("y", ""));
			if (y < SHIFT_LEVEL_MIN) {
				y = SHIFT_LEVEL_MIN;
			} else if (y > SHIFT_LEVEL_MAX) {
				y = SHIFT_LEVEL_MAX;
			}
			this.yLevel = y;
			messenger.sendMessage(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Undo undo = new Undo();
		BlockVector3 targetBlock = getTargetBlock();
		canyon(targetBlock.getX() >> 4, targetBlock.getZ() >> 4, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Undo undo = new Undo();
		BlockVector3 targetBlock = getTargetBlock();
		int chunkX = targetBlock.getX() >> 4;
		int chunkZ = targetBlock.getZ() >> 4;
		for (int x = chunkX - 1; x <= chunkX + 1; x++) {
			for (int z = chunkZ - 1; z <= chunkX + 1; z++) {
				canyon(x, z, undo);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	protected void canyon(int chunkX, int chunkZ, Undo undo) {
		EditSession editSession = getEditSession();
		int blockX = chunkX << 4;
		int blockZ = chunkZ << 4;
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				int currentYLevel = this.yLevel;
				for (int y = 63; y < editSession.getMaxY() + 1; y++) {
					BlockState block = getBlock(blockX + x, y, blockZ + z);
					Material blockType = getBlockType(blockX + x, y, blockZ + z);
					BlockState currentYLevelBlock = getBlock(blockX + x, currentYLevel, blockZ + z);
					undo.put(block);
					undo.put(currentYLevelBlock);
					setBlockType(blockX + x, currentYLevel, blockZ + z, blockType);
					setBlockType(blockX + x, y, blockZ + z, Material.AIR);
					currentYLevel++;
				}
				BlockState block = getBlock(blockX + x, 0, blockZ + z);
				undo.put(block);
				setBlockType(blockX + x, 0, blockZ + z, Material.BEDROCK);
				for (int y = 1; y < SHIFT_LEVEL_MIN; y++) {
					BlockState currentBlock = getBlock(blockX + x, y, blockZ + z);
					undo.put(currentBlock);
					setBlockType(blockX + x, y, blockZ + z, Material.STONE);
				}
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.message(ChatColor.GREEN + "Shift Level set to " + this.yLevel)
			.send();
	}

	public int getYLevel() {
		return this.yLevel;
	}

	public void setYLevel(int yLevel) {
		this.yLevel = yLevel;
	}
}
