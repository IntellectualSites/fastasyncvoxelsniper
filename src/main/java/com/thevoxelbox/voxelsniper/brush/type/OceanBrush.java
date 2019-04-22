package com.thevoxelbox.voxelsniper.brush.type;

import java.util.EnumSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_OCEANATOR_5000
 *
 * @author Voxel
 */
public class OceanBrush extends AbstractBrush {

	private static final int WATER_LEVEL_DEFAULT = 62; // y=63 -- we are using array indices here
	private static final int WATER_LEVEL_MIN = 12;
	private static final int LOW_CUT_LEVEL = 12;
	private static final Set<Material> EXCLUDED_MATERIALS = EnumSet.of(Material.LEGACY_AIR, Material.LEGACY_SAPLING, Material.LEGACY_WATER, Material.LEGACY_STATIONARY_WATER, Material.LEGACY_LAVA, Material.LEGACY_STATIONARY_LAVA, Material.LEGACY_LOG, Material.LEGACY_LEAVES, Material.LEGACY_YELLOW_FLOWER, Material.LEGACY_RED_ROSE, Material.LEGACY_RED_MUSHROOM, Material.LEGACY_BROWN_MUSHROOM, Material.LEGACY_MELON_BLOCK, Material.LEGACY_MELON_STEM, Material.LEGACY_PUMPKIN, Material.LEGACY_PUMPKIN_STEM, Material.LEGACY_COCOA, Material.LEGACY_SNOW, Material.LEGACY_SNOW_BLOCK, Material.LEGACY_ICE, Material.LEGACY_SUGAR_CANE_BLOCK, Material.LEGACY_LONG_GRASS);

	private int waterLevel = WATER_LEVEL_DEFAULT;
	private boolean coverFloor;

	public OceanBrush() {
		super("OCEANATOR 5000(tm)");
	}

	private int getHeight(int bx, int bz) {
		World world = getWorld();
		for (int y = world.getHighestBlockYAt(bx, bz); y > 0; y--) {
			Block clamp = this.clampY(bx, y, bz);
			Material material = clamp.getType();
			if (!EXCLUDED_MATERIALS.contains(material)) {
				return y;
			}
		}
		return 0;
	}

	protected final void oceanator(SnipeData snipeData, Undo undo) {
		World world = getWorld();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockZ = targetBlock.getZ();
		int brushSize = snipeData.getBrushSize();
		int minX = (int) Math.floor(targetBlockX - brushSize);
		int minZ = (int) Math.floor(targetBlockZ - brushSize);
		int maxX = (int) Math.floor(targetBlockX + brushSize);
		int maxZ = (int) Math.floor(targetBlockZ + brushSize);
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				int currentHeight = getHeight(x, z);
				int wLevelDiff = currentHeight - (this.waterLevel - 1);
				int newSeaFloorLevel = ((this.waterLevel - wLevelDiff) >= LOW_CUT_LEVEL) ? this.waterLevel - wLevelDiff : LOW_CUT_LEVEL;
				int highestY = world.getHighestBlockYAt(x, z);
				// go down from highest Y block down to new sea floor
				for (int y = highestY; y > newSeaFloorLevel; y--) {
					Block block = world.getBlockAt(x, y, z);
					if (block.getType() != Material.AIR) {
						undo.put(block);
						block.setType(Material.AIR);
					}
				}
				// go down from water level to new sea level
				for (int y = this.waterLevel; y > newSeaFloorLevel; y--) {
					Block block = world.getBlockAt(x, y, z);
					if (block.getType() != Material.WATER) {
						// do not put blocks into the undo we already put into
						if (block.getType() != Material.AIR) {
							undo.put(block);
						}
						block.setType(Material.WATER);
					}
				}
				// cover the sea floor of required
				if (this.coverFloor && (newSeaFloorLevel < this.waterLevel)) {
					Block block = world.getBlockAt(x, newSeaFloorLevel, z);
					if (block.getType() != snipeData.getBlockDataType()) {
						undo.put(block);
						block.setType(snipeData.getBlockDataType());
					}
				}
			}
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		Undo undo = new Undo();
		this.oceanator(snipeData, undo);
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(undo);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		arrow(snipeData);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 0; i < parameters.length; i++) {
			String parameter = parameters[i];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					snipeData.sendMessage(ChatColor.BLUE + "Parameters:");
					snipeData.sendMessage(ChatColor.GREEN + "-wlevel #  " + ChatColor.BLUE + "--  Sets the water level (e.g. -wlevel 64)");
					snipeData.sendMessage(ChatColor.GREEN + "-cfloor [y|n]  " + ChatColor.BLUE + "--  Enables or disables sea floor cover (e.g. -cfloor y) (Cover material will be your voxel material)");
				} else if (parameter.equalsIgnoreCase("-wlevel")) {
					if ((i + 1) >= parameters.length) {
						snipeData.sendMessage(ChatColor.RED + "Missing parameter. Correct syntax: -wlevel [#] (e.g. -wlevel 64)");
						continue;
					}
					int temp = Integer.parseInt(parameters[++i]);
					if (temp <= WATER_LEVEL_MIN) {
						snipeData.sendMessage(ChatColor.RED + "Error: Your specified water level was below 12.");
						continue;
					}
					this.waterLevel = temp - 1;
					snipeData.sendMessage(ChatColor.BLUE + "Water level set to " + ChatColor.GREEN + (this.waterLevel + 1)); // +1 since we are working with 0-based array indices
				} else if (parameter.equalsIgnoreCase("-cfloor") || parameter.equalsIgnoreCase("-coverfloor")) {
					if ((i + 1) >= parameters.length) {
						snipeData.sendMessage(ChatColor.RED + "Missing parameter. Correct syntax: -cfloor [y|n] (e.g. -cfloor y)");
						continue;
					}
					this.coverFloor = parameters[++i].equalsIgnoreCase("y");
					snipeData.sendMessage(ChatColor.BLUE + String.format("Floor cover %s.", ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")));
				}
			} catch (NumberFormatException exception) {
				snipeData.sendMessage(ChatColor.RED + String.format("Error while parsing parameter: %s", parameter));
				exception.printStackTrace();
			}
		}
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.custom(ChatColor.BLUE + "Water level set to " + ChatColor.GREEN + (this.waterLevel + 1)); // +1 since we are working with 0-based array indices
		messages.custom(ChatColor.BLUE + String.format("Floor cover %s.", ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")));
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ocean";
	}
}
