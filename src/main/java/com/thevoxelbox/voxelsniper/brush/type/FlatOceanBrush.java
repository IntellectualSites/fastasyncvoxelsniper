package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author GavJenks
 */
public class FlatOceanBrush extends AbstractBrush {

	private static final int DEFAULT_WATER_LEVEL = 29;
	private static final int DEFAULT_FLOOR_LEVEL = 8;

	private int waterLevel = DEFAULT_WATER_LEVEL;
	private int floorLevel = DEFAULT_FLOOR_LEVEL;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GREEN + "yo[number] to set the Level to which the water will rise.");
				messenger.sendMessage(ChatColor.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
			}
			if (parameter.startsWith("yo")) {
				int newWaterLevel = Integer.parseInt(parameter.replace("yo", ""));
				if (newWaterLevel < this.floorLevel) {
					newWaterLevel = this.floorLevel + 1;
				}
				this.waterLevel = newWaterLevel;
				messenger.sendMessage(ChatColor.GREEN + "Water Level set to " + this.waterLevel);
			} else if (parameter.startsWith("yl")) {
				int newFloorLevel = Integer.parseInt(parameter.replace("yl", ""));
				if (newFloorLevel > this.waterLevel) {
					newFloorLevel = this.waterLevel - 1;
					if (newFloorLevel == 0) {
						newFloorLevel = 1;
						this.waterLevel = 2;
					}
				}
				this.floorLevel = newFloorLevel;
				messenger.sendMessage(ChatColor.GREEN + "Ocean floor Level set to " + this.floorLevel);
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		flatOceanAtTarget();
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		flatOceanAtTarget();
		flatOceanAtTarget(CHUNK_SIZE, 0);
		flatOceanAtTarget(CHUNK_SIZE, CHUNK_SIZE);
		flatOceanAtTarget(0, CHUNK_SIZE);
		flatOceanAtTarget(-CHUNK_SIZE, CHUNK_SIZE);
		flatOceanAtTarget(-CHUNK_SIZE, 0);
		flatOceanAtTarget(-CHUNK_SIZE, -CHUNK_SIZE);
		flatOceanAtTarget(0, -CHUNK_SIZE);
		flatOceanAtTarget(CHUNK_SIZE, -CHUNK_SIZE);
	}

	private void flatOceanAtTarget(int additionalX, int additionalZ) {
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		int blockX = targetBlock.getX();
		int blockZ = targetBlock.getZ();
		Block block = clampY(blockX + additionalX, 1, blockZ + additionalZ);
		Chunk chunk = world.getChunkAt(block);
		flatOcean(chunk);
	}

	private void flatOceanAtTarget() {
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		Chunk chunk = world.getChunkAt(targetBlock);
		flatOcean(chunk);
	}

	private void flatOcean(Chunk chunk) {
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				World world = chunk.getWorld();
				for (int y = 0; y < world.getMaxHeight(); y++) {
					Block block = chunk.getBlock(x, y, z);
					if (y <= this.floorLevel) {
						block.setType(Material.DIRT);
					} else if (y <= this.waterLevel) {
						block.setType(Material.WATER, false);
					} else {
						block.setType(Material.AIR, false);
					}
				}
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(ChatColor.RED + "THIS BRUSH DOES NOT UNDO");
		messenger.sendMessage(ChatColor.GREEN + "Water level set to " + this.waterLevel);
		messenger.sendMessage(ChatColor.GREEN + "Ocean floor level set to " + this.floorLevel);
	}
}
