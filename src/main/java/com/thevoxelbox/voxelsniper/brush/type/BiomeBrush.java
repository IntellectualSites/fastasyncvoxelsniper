package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class BiomeBrush extends AbstractBrush {

	private Biome selectedBiome = Biome.PLAINS;

	public BiomeBrush() {
		super("Biome (/b biome [Biome Name])");
	}

	private void biome(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize, 2);
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
					world.setBiome(targetBlock.getX() + x, targetBlock.getZ() + z, this.selectedBiome);
				}
			}
		}
		Block block1 = world.getBlockAt(targetBlock.getX() - brushSize, 0, targetBlock.getZ() - brushSize);
		Block block2 = world.getBlockAt(targetBlock.getX() + brushSize, 0, targetBlock.getZ() + brushSize);
		Chunk chunk1 = block1.getChunk();
		Chunk chunk2 = block2.getChunk();
		int lowChunkX = (block1.getX() <= block2.getX()) ? chunk1.getX() : chunk2.getX();
		int lowChunkZ = (block1.getZ() <= block2.getZ()) ? chunk1.getZ() : chunk2.getZ();
		int highChunkX = (block1.getX() >= block2.getX()) ? chunk1.getX() : chunk2.getX();
		int highChunkZ = (block1.getZ() >= block2.getZ()) ? chunk1.getZ() : chunk2.getZ();
		for (int x = lowChunkX; x <= highChunkX; x++) {
			for (int z = lowChunkZ; z <= highChunkZ; z++) {
				world.refreshChunk(x, z);
			}
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.biome(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.biome(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.custom(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		if (parameters[1].equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
			StringBuilder availableBiomes = new StringBuilder();
			for (Biome biome : Biome.values()) {
				if (availableBiomes.length() == 0) {
					availableBiomes = new StringBuilder(ChatColor.DARK_GREEN + biome.name());
					continue;
				}
				availableBiomes.append(ChatColor.RED + ", " + ChatColor.DARK_GREEN)
					.append(biome.name());
			}
			snipeData.sendMessage(ChatColor.DARK_BLUE + "Available biomes: " + availableBiomes);
		} else {
			// allows biome names with spaces in their name
			String biomeName = IntStream.range(2, parameters.length)
				.mapToObj(i -> " " + parameters[i])
				.collect(Collectors.joining("", parameters[1], ""));
			this.selectedBiome = Arrays.stream(Biome.values())
				.filter(biome -> biome.name()
					.equalsIgnoreCase(biomeName))
				.findFirst()
				.orElse(this.selectedBiome);
			snipeData.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.biome";
	}
}
