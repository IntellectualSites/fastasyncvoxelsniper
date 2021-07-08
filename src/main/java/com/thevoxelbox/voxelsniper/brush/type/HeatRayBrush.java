package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.util.Random;

public class HeatRayBrush extends AbstractBrush {

	private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
	private static final double REQUIRED_COBBLE_DENSITY = 0.5;
	private static final double REQUIRED_FIRE_DENSITY = -0.25;
	private static final double REQUIRED_AIR_DENSITY = 0;
	private static final MaterialSet FLAMEABLE_BLOCKS = MaterialSet.builder()
		.with(Tag.LOGS)
		.with(Tag.SAPLINGS)
		.with(Tag.PLANKS)
		.with(Tag.LEAVES)
		.with(Tag.WOOL)
		.with(Tag.WOODEN_SLABS)
		.with(Tag.WOODEN_STAIRS)
		.with(Tag.WOODEN_DOORS)
		.with(Tag.WOODEN_TRAPDOORS)
		.with(Tag.WOODEN_PRESSURE_PLATES)
		.with(Tag.ICE)
		.with(MaterialSets.SIGNS)
		.with(MaterialSets.WOODEN_FENCES)
		.with(MaterialSets.FENCE_GATES)
		.with(MaterialSets.SNOWS)
		.with(MaterialSets.TORCHES)
		.with(MaterialSets.FLORA)
		.add(Material.SPONGE)
		.add(Material.COBWEB)
		.add(Material.FIRE)
		.add(Material.LADDER)
		.build();

	private int octaves = 5;
	private double frequency = 1;
	private double amplitude = 0.3;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String s : parameters) {
			String parameter = s.toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Heat Ray brush Parameters:");
				messenger.sendMessage(ChatColor.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
				messenger.sendMessage(ChatColor.AQUA + "/b hr amp[float] -- Amplitude parameter for the noise generator.");
				messenger.sendMessage(ChatColor.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
			}
			if (parameter.startsWith("oct")) {
				this.octaves = Integer.parseInt(parameter.replace("oct", ""));
				messenger.sendMessage(ChatColor.GREEN + "Octaves: " + this.octaves);
			} else if (parameter.startsWith("amp")) {
				this.amplitude = Double.parseDouble(parameter.replace("amp", ""));
				messenger.sendMessage(ChatColor.GREEN + "Amplitude: " + this.amplitude);
			} else if (parameter.startsWith("freq")) {
				this.frequency = Double.parseDouble(parameter.replace("freq", ""));
				messenger.sendMessage(ChatColor.GREEN + "Frequency: " + this.frequency);
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		heatRay(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		heatRay(snipe);
	}

	/**
	 * Heat Ray executer.
	 */
	public void heatRay(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		PerlinNoiseGenerator generator = new PerlinNoiseGenerator(new Random());
		BlockVector3 targetBlock = getTargetBlock();
		Vector targetBlockVector = Vectors.toBukkit(targetBlock);
		Vector currentLocation = new Vector();
		Undo undo = new Undo();
		int brushSize = toolkitProperties.getBrushSize();
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				for (int y = brushSize; y >= -brushSize; y--) {
					currentLocation.setX(targetBlock.getX() + x);
					currentLocation.setY(targetBlock.getY() + y);
					currentLocation.setZ(targetBlock.getZ() + z);
					Vector currentLocationVector = currentLocation.clone();
					if (currentLocationVector.isInSphere(targetBlockVector, brushSize)) {
						BlockState currentBlock = getBlock(currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ());
						Material currentBlockType = BukkitAdapter.adapt(currentBlock.getBlockType());
						if (currentBlockType == Material.CHEST) {
							continue;
						}
						if (Materials.isLiquid(currentBlockType)) {
							undo.put(currentBlock);
							setBlockType(currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ(), Material.AIR);
							continue;
						}
						if (FLAMEABLE_BLOCKS.contains(currentBlockType)) {
							undo.put(currentBlock);
							setBlockType(currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ(), Material.FIRE);
							continue;
						}
						if (!currentBlockType.isAir()) {
							double airDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double fireDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double cobbleDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double obsidianDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							if (obsidianDensity >= REQUIRED_OBSIDIAN_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.OBSIDIAN) {
									setBlockType(currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ(), Material.OBSIDIAN);
								}
							} else if (cobbleDensity >= REQUIRED_COBBLE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.COBBLESTONE) {
									setBlockType(currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ(), Material.COBBLESTONE);
								}
							} else if (fireDensity >= REQUIRED_FIRE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.FIRE) {
									setBlockType(currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ(), Material.FIRE);
								}
							} else if (airDensity >= REQUIRED_AIR_DENSITY) {
								undo.put(currentBlock);
								setBlockType(currentLocation.getBlockX(), currentLocation.getBlockY(), currentLocation.getBlockZ(), Material.AIR);
							}
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(ChatColor.GREEN + "Octaves: " + this.octaves);
		messenger.sendMessage(ChatColor.GREEN + "Amplitude: " + this.amplitude);
		messenger.sendMessage(ChatColor.GREEN + "Frequency: " + this.frequency);
		messenger.sendBrushSizeMessage();
	}
}
