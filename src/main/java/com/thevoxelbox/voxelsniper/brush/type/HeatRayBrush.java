package com.thevoxelbox.voxelsniper.brush.type;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Heat_Ray
 *
 * @author Gavjenks
 */
public class HeatRayBrush extends AbstractBrush {

	private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
	private static final double REQUIRED_COBBLE_DENSITY = 0.5;
	private static final double REQUIRED_FIRE_DENSITY = -0.25;
	private static final double REQUIRED_AIR_DENSITY = 0;
	private static final Set<Material> FLAMEABLE_BLOCKS = EnumSet.of(Material.LEGACY_WOOD, Material.LEGACY_SAPLING, Material.LEGACY_LOG, Material.LEGACY_LEAVES, Material.LEGACY_SPONGE, Material.LEGACY_WEB, Material.LEGACY_LONG_GRASS, Material.LEGACY_DEAD_BUSH, Material.LEGACY_WOOL, Material.LEGACY_YELLOW_FLOWER, Material.LEGACY_RED_ROSE, Material.LEGACY_TORCH, Material.LEGACY_FIRE, Material.LEGACY_WOOD_STAIRS, Material.LEGACY_CROPS, Material.LEGACY_SIGN_POST, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_LADDER, Material.LEGACY_WALL_SIGN, Material.LEGACY_WOOD_PLATE, Material.LEGACY_SNOW, Material.LEGACY_ICE, Material.LEGACY_SUGAR_CANE_BLOCK, Material.LEGACY_FENCE, Material.LEGACY_TRAP_DOOR, Material.LEGACY_VINE, Material.LEGACY_FENCE_GATE, Material.LEGACY_WATER_LILY);

	private int octaves = 5;
	private double frequency = 1;
	private double amplitude = 0.3;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int index = 1; index < parameters.length; index++) {
			String parameter = parameters[index].toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Heat Ray brush Parameters:");
				messenger.sendMessage(ChatColor.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
				messenger.sendMessage(ChatColor.AQUA + "/b hr amp[float] -- Amplitude parameter for the noise generator.");
				messenger.sendMessage(ChatColor.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
			}
			if (parameter.startsWith("oct")) {
				this.octaves = Integer.valueOf(parameter.replace("oct", ""));
				messenger.sendMessage(ChatColor.GREEN + "Octaves: " + this.octaves);
			} else if (parameter.startsWith("amp")) {
				this.amplitude = Double.valueOf(parameter.replace("amp", ""));
				messenger.sendMessage(ChatColor.GREEN + "Amplitude: " + this.amplitude);
			} else if (parameter.startsWith("freq")) {
				this.frequency = Double.valueOf(parameter.replace("freq", ""));
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
	public final void heatRay(Snipe snipe) {
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
						if (currentBlockType == Material.LEGACY_CHEST) {
							continue;
						}
						if (currentBlock.isLiquid()) {
							undo.put(currentBlock);
							currentBlock.setType(Material.LEGACY_AIR);
							continue;
						}
						if (FLAMEABLE_BLOCKS.contains(currentBlockType)) {
							undo.put(currentBlock);
							currentBlock.setType(Material.LEGACY_FIRE);
							continue;
						}
						if (currentBlockType != Material.LEGACY_AIR) {
							double airDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double fireDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double cobbleDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double obsidianDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							if (obsidianDensity >= REQUIRED_OBSIDIAN_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.LEGACY_OBSIDIAN) {
									currentBlock.setType(Material.LEGACY_OBSIDIAN);
								}
							} else if (cobbleDensity >= REQUIRED_COBBLE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.LEGACY_COBBLESTONE) {
									currentBlock.setType(Material.LEGACY_COBBLESTONE);
								}
							} else if (fireDensity >= REQUIRED_FIRE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != Material.LEGACY_FIRE) {
									currentBlock.setType(Material.LEGACY_FIRE);
								}
							} else if (airDensity >= REQUIRED_AIR_DENSITY) {
								undo.put(currentBlock);
								currentBlock.setType(Material.LEGACY_AIR);
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
