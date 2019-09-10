package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Random;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

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
		Block targetBlock = getTargetBlock();
		Location targetBlockLocation = targetBlock.getLocation();
		Vector targetBlockVector = targetBlockLocation.toVector();
		Location currentLocation = new Location(targetBlock.getWorld(), 0, 0, 0);
		Undo undo = new Undo();
		int brushSize = toolkitProperties.getBrushSize();
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				for (int y = brushSize; y >= -brushSize; y--) {
					currentLocation.setX(targetBlock.getX() + x);
					currentLocation.setY(targetBlock.getY() + y);
					currentLocation.setZ(targetBlock.getZ() + z);
					Vector currentLocationVector = currentLocation.toVector();
					if (currentLocationVector.isInSphere(targetBlockVector, brushSize)) {
						Block currentBlock = currentLocation.getBlock();
						Material currentBlockType = currentBlock.getType();
						if (currentBlockType == Material.CHEST) {
							continue;
						}
						if (currentBlock.isLiquid()) {
							undo.put(currentBlock);
							currentBlock.setType(Material.AIR);
							continue;
						}
						if (FLAMEABLE_BLOCKS.contains(currentBlockType)) {
							undo.put(currentBlock);
							currentBlock.setType(Material.FIRE);
							continue;
						}
						if (currentBlockType != Material.AIR) {
							double airDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double fireDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double cobbleDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double obsidianDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							if (obsidianDensity >= REQUIRED_OBSIDIAN_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.OBSIDIAN) {
									currentBlock.setType(Material.OBSIDIAN);
								}
							} else if (cobbleDensity >= REQUIRED_COBBLE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.COBBLESTONE) {
									currentBlock.setType(Material.COBBLESTONE);
								}
							} else if (fireDensity >= REQUIRED_FIRE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.FIRE) {
									currentBlock.setType(Material.FIRE);
								}
							} else if (airDensity >= REQUIRED_AIR_DENSITY) {
								undo.put(currentBlock);
								currentBlock.setType(Material.AIR);
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
