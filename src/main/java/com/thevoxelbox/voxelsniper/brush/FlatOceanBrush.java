package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
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

	public FlatOceanBrush() {
		super("FlatOcean");
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		flatOceanAtTarget();
	}

	@Override
	public final void powder(SnipeData snipeData) {
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
	public final void info(Message message) {
		message.brushName(getName());
		message.custom(ChatColor.RED + "THIS BRUSH DOES NOT UNDO");
		message.custom(ChatColor.GREEN + "Water level set to " + this.waterLevel);
		message.custom(ChatColor.GREEN + "Ocean floor level set to " + this.floorLevel);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GREEN + "yo[number] to set the Level to which the water will rise.");
				snipeData.sendMessage(ChatColor.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
			}
			if (parameter.startsWith("yo")) {
				int newWaterLevel = Integer.parseInt(parameter.replace("yo", ""));
				if (newWaterLevel < this.floorLevel) {
					newWaterLevel = this.floorLevel + 1;
				}
				this.waterLevel = newWaterLevel;
				snipeData.sendMessage(ChatColor.GREEN + "Water Level set to " + this.waterLevel);
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
				snipeData.sendMessage(ChatColor.GREEN + "Ocean floor Level set to " + this.floorLevel);
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.flatocean";
	}
}
