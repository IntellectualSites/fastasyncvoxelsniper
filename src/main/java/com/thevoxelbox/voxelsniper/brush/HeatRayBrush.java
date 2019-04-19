package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
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

	/**
	 * @author MikeMatrix
	 */
	private enum FlameableBlock {
		WOOD(Material.WOOD),
		SAPLING(Material.SAPLING),
		LOG(Material.LOG),
		LEAVES(Material.LEAVES),
		SPONGE(Material.SPONGE),
		WEB(Material.WEB),
		LONG_GRASS(Material.LONG_GRASS),
		DEAD_BUSH(Material.DEAD_BUSH),
		WOOL(Material.WOOL),
		YELLOW_FLOWER(Material.YELLOW_FLOWER),
		RED_ROSE(Material.RED_ROSE),
		TORCH(Material.TORCH),
		FIRE(Material.FIRE),
		WOOD_STAIRS(Material.WOOD_STAIRS),
		CROPS(Material.CROPS),
		SIGN_POST(Material.SIGN_POST),
		WOODEN_DOOR(Material.WOODEN_DOOR),
		LADDER(Material.LADDER),
		WALL_SIGN(Material.WALL_SIGN),
		WOOD_PLATE(Material.WOOD_PLATE),
		SNOW(Material.SNOW),
		ICE(Material.ICE),
		SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK),
		FENCE(Material.FENCE),
		TRAP_DOOR(Material.TRAP_DOOR),
		VINE(Material.VINE),
		FENCE_GATE(Material.FENCE_GATE),
		WATER_LILLY(Material.WATER_LILY);

		private Material material;

		FlameableBlock(Material material) {
			this.material = material;
		}
	}

	private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
	private static final double REQUIRED_COBBLE_DENSITY = 0.5;
	private static final double REQUIRED_FIRE_DENSITY = -0.25;
	private static final double REQUIRED_AIR_DENSITY = 0;

	private static final List<Material> FLAMABLE_BLOCKS = new ArrayList<>();

	private int octaves = 5;
	private double frequency = 1;

	private double amplitude = 0.3;

	static {
		for (FlameableBlock flameableBlock : FlameableBlock.values()) {
			FLAMABLE_BLOCKS.add(flameableBlock.material);
		}
	}

	/**
	 * Default Constructor.
	 */
	public HeatRayBrush() {
		this.setName("Heat Ray");
	}

	/**
	 * Heat Ray executer.
	 */
	public final void heatRay(SnipeData v) {
		PerlinNoiseGenerator generator = new PerlinNoiseGenerator(new Random());
		Vector targetLocation = this.getTargetBlock()
			.getLocation()
			.toVector();
		Location currentLocation = new Location(this.getTargetBlock()
			.getWorld(), 0, 0, 0);
		Undo undo = new Undo();
		for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
			for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
				for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
					currentLocation.setX(this.getTargetBlock()
						.getX() + x);
					currentLocation.setY(this.getTargetBlock()
						.getY() + y);
					currentLocation.setZ(this.getTargetBlock()
						.getZ() + z);
					if (currentLocation.toVector()
						.isInSphere(targetLocation, v.getBrushSize())) {
						Block currentBlock = currentLocation.getBlock();
						if (currentBlock == null || currentBlock.getType() == Material.CHEST) {
							continue;
						}
						if (currentBlock.isLiquid()) {
							undo.put(currentBlock);
							currentBlock.setType(Material.AIR);
							continue;
						}
						if (FLAMABLE_BLOCKS.contains(currentBlock.getType())) {
							undo.put(currentBlock);
							currentBlock.setType(Material.FIRE);
							continue;
						}
						if (!currentBlock.getType()
							.equals(Material.AIR)) {
							double airDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double fireDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double cobbleDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							double obsidianDensity = generator.noise(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), this.octaves, this.frequency, this.amplitude);
							if (obsidianDensity >= REQUIRED_OBSIDIAN_DENSITY) {
								undo.put(currentBlock);
								if (currentBlock.getType() != Material.OBSIDIAN) {
									currentBlock.setType(Material.OBSIDIAN);
								}
							} else if (cobbleDensity >= REQUIRED_COBBLE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlock.getType() != Material.COBBLESTONE) {
									currentBlock.setType(Material.COBBLESTONE);
								}
							} else if (fireDensity >= REQUIRED_FIRE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlock.getType() != Material.FIRE) {
									currentBlock.setType(Material.FIRE);
								}
							} else if (airDensity >= REQUIRED_AIR_DENSITY) {
								undo.put(currentBlock);
								if (currentBlock.getType() != Material.AIR) {
									currentBlock.setType(Material.AIR);
								}
							}
						}
					}
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.heatRay(v);
	}

	@Override
	protected final void powder(SnipeData v) {
		this.heatRay(v);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom(ChatColor.GREEN + "Octaves: " + this.octaves);
		message.custom(ChatColor.GREEN + "Amplitude: " + this.amplitude);
		message.custom(ChatColor.GREEN + "Frequency: " + this.frequency);
		message.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i].toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Heat Ray brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
				snipeData.sendMessage(ChatColor.AQUA + "/b hr amp[float] -- Amplitude parameter for the noise generator.");
				snipeData.sendMessage(ChatColor.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
			}
			if (parameter.startsWith("oct")) {
				this.octaves = Integer.valueOf(parameter.replace("oct", ""));
				snipeData.getVoxelMessage()
					.custom(ChatColor.GREEN + "Octaves: " + this.octaves);
			} else if (parameter.startsWith("amp")) {
				this.amplitude = Double.valueOf(parameter.replace("amp", ""));
				snipeData.getVoxelMessage()
					.custom(ChatColor.GREEN + "Amplitude: " + this.amplitude);
			} else if (parameter.startsWith("freq")) {
				this.frequency = Double.valueOf(parameter.replace("freq", ""));
				snipeData.getVoxelMessage()
					.custom(ChatColor.GREEN + "Frequency: " + this.frequency);
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.heatray";
	}
}
