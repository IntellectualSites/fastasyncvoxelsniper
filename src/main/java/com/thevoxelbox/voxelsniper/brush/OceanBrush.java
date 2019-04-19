package com.thevoxelbox.voxelsniper.brush;

import java.util.LinkedList;
import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
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
	private static final List<Material> EXCLUDED_MATERIALS = new LinkedList<>();

	static {
		EXCLUDED_MATERIALS.add(Material.LEGACY_AIR);
		EXCLUDED_MATERIALS.add(Material.LEGACY_SAPLING);
		EXCLUDED_MATERIALS.add(Material.LEGACY_WATER);
		EXCLUDED_MATERIALS.add(Material.LEGACY_STATIONARY_WATER);
		EXCLUDED_MATERIALS.add(Material.LEGACY_LAVA);
		EXCLUDED_MATERIALS.add(Material.LEGACY_STATIONARY_LAVA);
		EXCLUDED_MATERIALS.add(Material.LEGACY_LOG);
		EXCLUDED_MATERIALS.add(Material.LEGACY_LEAVES);
		EXCLUDED_MATERIALS.add(Material.LEGACY_YELLOW_FLOWER);
		EXCLUDED_MATERIALS.add(Material.LEGACY_RED_ROSE);
		EXCLUDED_MATERIALS.add(Material.LEGACY_RED_MUSHROOM);
		EXCLUDED_MATERIALS.add(Material.LEGACY_BROWN_MUSHROOM);
		EXCLUDED_MATERIALS.add(Material.LEGACY_MELON_BLOCK);
		EXCLUDED_MATERIALS.add(Material.LEGACY_MELON_STEM);
		EXCLUDED_MATERIALS.add(Material.LEGACY_PUMPKIN);
		EXCLUDED_MATERIALS.add(Material.LEGACY_PUMPKIN_STEM);
		EXCLUDED_MATERIALS.add(Material.LEGACY_COCOA);
		EXCLUDED_MATERIALS.add(Material.LEGACY_SNOW);
		EXCLUDED_MATERIALS.add(Material.LEGACY_SNOW_BLOCK);
		EXCLUDED_MATERIALS.add(Material.LEGACY_ICE);
		EXCLUDED_MATERIALS.add(Material.LEGACY_SUGAR_CANE_BLOCK);
		EXCLUDED_MATERIALS.add(Material.LEGACY_LONG_GRASS);
		EXCLUDED_MATERIALS.add(Material.LEGACY_SNOW);
	}

	private int waterLevel = WATER_LEVEL_DEFAULT;
	private boolean coverFloor;

	/**
	 *
	 */
	public OceanBrush() {
		this.setName("OCEANATOR 5000(tm)");
	}

	private int getHeight(int bx, int bz) {
		for (int y = this.getWorld()
			.getHighestBlockYAt(bx, bz); y > 0; y--) {
			Material material = this.clampY(bx, y, bz)
				.getType();
			if (!EXCLUDED_MATERIALS.contains(material)) {
				return y;
			}
		}
		return 0;
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected final void oceanator(SnipeData snipeData, Undo undo) {
		World world = this.getWorld();
		int minX = (int) Math.floor((this.getTargetBlock()
			.getX() - snipeData.getBrushSize()));
		int minZ = (int) Math.floor((this.getTargetBlock()
			.getZ() - snipeData.getBrushSize()));
		int maxX = (int) Math.floor((this.getTargetBlock()
			.getX() + snipeData.getBrushSize()));
		int maxZ = (int) Math.floor((this.getTargetBlock()
			.getZ() + snipeData.getBrushSize()));
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				int currentHeight = getHeight(x, z);
				int wLevelDiff = currentHeight - (this.waterLevel - 1);
				int newSeaFloorLevel = ((this.waterLevel - wLevelDiff) >= LOW_CUT_LEVEL) ? this.waterLevel - wLevelDiff : LOW_CUT_LEVEL;
				int highestY = this.getWorld()
					.getHighestBlockYAt(x, z);
				// go down from highest Y block down to new sea floor
				for (int y = highestY; y > newSeaFloorLevel; y--) {
					Block block = world.getBlockAt(x, y, z);
					if (block.getType() != Material.LEGACY_AIR) {
						undo.put(block);
						block.setType(Material.LEGACY_AIR);
					}
				}
				// go down from water level to new sea level
				for (int y = this.waterLevel; y > newSeaFloorLevel; y--) {
					Block block = world.getBlockAt(x, y, z);
					if (block.getType() != Material.LEGACY_STATIONARY_WATER) {
						// do not put blocks into the undo we already put into
						if (block.getType() != Material.LEGACY_AIR) {
							undo.put(block);
						}
						block.setType(Material.LEGACY_STATIONARY_WATER);
					}
				}
				// cover the sea floor of required
				if (this.coverFloor && (newSeaFloorLevel < this.waterLevel)) {
					Block block = world.getBlockAt(x, newSeaFloorLevel, z);
					if (block.getTypeId() != snipeData.getVoxelId()) {
						undo.put(block);
						block.setTypeId(snipeData.getVoxelId());
					}
				}
			}
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		Undo undo = new Undo();
		this.oceanator(snipeData, undo);
		snipeData.getOwner()
			.storeUndo(undo);
	}

	@Override
	protected final void powder(SnipeData snipeData) {
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
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom(ChatColor.BLUE + "Water level set to " + ChatColor.GREEN + (this.waterLevel + 1)); // +1 since we are working with 0-based array indices
		message.custom(ChatColor.BLUE + String.format("Floor cover %s.", ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")));
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ocean";
	}
}
