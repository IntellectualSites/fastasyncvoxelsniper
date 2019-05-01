package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BiomeBrush extends AbstractBrush {

	private Biome selectedBiome = Biome.PLAINS;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		String firstParameter = parameters[1];
		if (firstParameter.equalsIgnoreCase("info")) {
			player.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
			StringBuilder availableBiomes = new StringBuilder();
			for (Biome biome : Biome.values()) {
				if (availableBiomes.length() == 0) {
					availableBiomes = new StringBuilder(ChatColor.DARK_GREEN + biome.name());
					continue;
				}
				availableBiomes.append(ChatColor.RED + ", " + ChatColor.DARK_GREEN)
					.append(biome.name());
			}
			player.sendMessage(ChatColor.DARK_BLUE + "Available biomes: " + availableBiomes);
		} else {
			// allows biome names with spaces in their name
			String biomeName = IntStream.range(2, parameters.length)
				.mapToObj(index -> " " + parameters[index])
				.collect(Collectors.joining("", firstParameter, ""));
			this.selectedBiome = Arrays.stream(Biome.values())
				.filter(biome -> biomeName.equalsIgnoreCase(biome.name()))
				.findFirst()
				.orElse(this.selectedBiome);
			player.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		biome(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		biome(snipe);
	}

	private void biome(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize, 2);
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockZ = targetBlock.getZ();
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if (xSquared + Math.pow(z, 2) <= brushSizeSquared) {
					world.setBiome(targetBlockX + x, targetBlockZ + z, this.selectedBiome);
				}
			}
		}
		Block block1 = world.getBlockAt(targetBlockX - brushSize, 0, targetBlockZ - brushSize);
		Block block2 = world.getBlockAt(targetBlockX + brushSize, 0, targetBlockZ + brushSize);
		Chunk chunk1 = block1.getChunk();
		Chunk chunk2 = block2.getChunk();
		int block1X = block1.getX();
		int block2X = block2.getX();
		int chunk1X = chunk1.getX();
		int chunk2X = chunk2.getX();
		int block1Z = block1.getZ();
		int block2Z = block2.getZ();
		int chunk1Z = chunk1.getZ();
		int chunk2Z = chunk2.getZ();
		int lowChunkX = block1X <= block2X ? chunk1X : chunk2X;
		int lowChunkZ = block1Z <= block2Z ? chunk1Z : chunk2Z;
		int highChunkX = block1X >= block2X ? chunk1X : chunk2X;
		int highChunkZ = block1Z >= block2Z ? chunk1Z : chunk2Z;
		for (int x = lowChunkX; x <= highChunkX; x++) {
			for (int z = lowChunkZ; z <= highChunkZ; z++) {
				refreshChunk(world, x, z);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void refreshChunk(World world, int x, int z) {
		world.refreshChunk(x, z);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
	}
}
