package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

/**
 *
 */
public class BiomeBrush extends AbstractBrush {

	private Biome selectedBiome = Biome.PLAINS;

	/**
	 *
	 */
	public BiomeBrush() {
		super("Biome (/b biome [Biome Name])");
	}

	private void biome(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize, 2);
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
					this.getWorld()
						.setBiome(this.getTargetBlock()
							.getX() + x, this.getTargetBlock()
							.getZ() + z, this.selectedBiome);
				}
			}
		}
		Block block1 = this.getWorld()
			.getBlockAt(this.getTargetBlock()
				.getX() - brushSize, 0, this.getTargetBlock()
				.getZ() - brushSize);
		Block block2 = this.getWorld()
			.getBlockAt(this.getTargetBlock()
				.getX() + brushSize, 0, this.getTargetBlock()
				.getZ() + brushSize);
		int lowChunkX = (block1.getX() <= block2.getX()) ? block1.getChunk()
			.getX() : block2.getChunk()
			.getX();
		int lowChunkZ = (block1.getZ() <= block2.getZ()) ? block1.getChunk()
			.getZ() : block2.getChunk()
			.getZ();
		int highChunkX = (block1.getX() >= block2.getX()) ? block1.getChunk()
			.getX() : block2.getChunk()
			.getX();
		int highChunkZ = (block1.getZ() >= block2.getZ()) ? block1.getChunk()
			.getZ() : block2.getChunk()
			.getZ();
		for (int x = lowChunkX; x <= highChunkX; x++) {
			for (int z = lowChunkZ; z <= highChunkZ; z++) {
				this.getWorld()
					.refreshChunk(x, z);
			}
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.biome(snipeData);
	}

	@Override
	protected final void powder(SnipeData snipeData) {
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
