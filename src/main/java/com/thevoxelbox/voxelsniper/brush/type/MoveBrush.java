package com.thevoxelbox.voxelsniper.brush.type;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Moves a selection blockPositionY a certain amount.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Move_Brush
 *
 * @author MikeMatrix
 */
public class MoveBrush extends AbstractBrush {

	/**
	 * Breakable Blocks to determine if no-physics should be used.
	 */
	private static final Set<Material> BREAKABLE_MATERIALS = EnumSet.of(Material.LEGACY_SAPLING, Material.LEGACY_BED_BLOCK, Material.LEGACY_POWERED_RAIL, Material.LEGACY_DETECTOR_RAIL, Material.LEGACY_LONG_GRASS, Material.LEGACY_DEAD_BUSH, Material.LEGACY_PISTON_EXTENSION, Material.LEGACY_YELLOW_FLOWER, Material.LEGACY_RED_ROSE, Material.LEGACY_BROWN_MUSHROOM, Material.LEGACY_RED_MUSHROOM, Material.LEGACY_TORCH, Material.LEGACY_FIRE, Material.LEGACY_CROPS, Material.LEGACY_SIGN_POST, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_LADDER, Material.LEGACY_RAILS, Material.LEGACY_WALL_SIGN, Material.LEGACY_LEVER, Material.LEGACY_STONE_PLATE, Material.LEGACY_IRON_DOOR_BLOCK, Material.LEGACY_WOOD_PLATE, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_REDSTONE_TORCH_ON, Material.LEGACY_STONE_BUTTON, Material.LEGACY_SNOW, Material.LEGACY_CACTUS, Material.LEGACY_SUGAR_CANE_BLOCK, Material.LEGACY_CAKE_BLOCK, Material.LEGACY_DIODE_BLOCK_OFF, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_TRAP_DOOR, Material.LEGACY_PUMPKIN_STEM, Material.LEGACY_MELON_STEM, Material.LEGACY_VINE, Material.LEGACY_WATER_LILY, Material.LEGACY_NETHER_WARTS);

	/**
	 * Saved direction.
	 */
	private int[] moveDirections = {0, 0, 0};
	/**
	 * Saved selection.
	 */
	@Nullable
	private Selection selection;

	public MoveBrush() {
		super("Move");
	}

	/**
	 * Moves the given selection blockPositionY the amount given in direction and saves an undo for the player.
	 */

	private void moveSelection(SnipeData snipeData, Selection selection, int[] direction) {
		List<BlockState> blockStates = selection.getBlockStates();
		if (!blockStates.isEmpty()) {
			BlockState firstState = blockStates.get(0);
			World world = firstState.getWorld();
			Undo undo = new Undo();
			Selection newSelection = new Selection();
			Location movedLocation1 = selection.getLocation1();
			movedLocation1.add(direction[0], direction[1], direction[2]);
			Location movedLocation2 = selection.getLocation2();
			movedLocation2.add(direction[0], direction[1], direction[2]);
			newSelection.setLocation1(movedLocation1);
			newSelection.setLocation2(movedLocation2);
			try {
				newSelection.calculateRegion();
			} catch (RuntimeException exception) {
				Messages messages = snipeData.getMessages();
				messages.brushMessage("The new Selection has more blocks than the original selection. This should never happen!");
			}
			Set<Block> undoSet = blockStates.stream()
				.map(BlockState::getBlock)
				.collect(Collectors.toSet());
			newSelection.getBlockStates()
				.stream()
				.map(BlockState::getBlock)
				.forEach(undoSet::add);
			undoSet.forEach(undo::put);
			Sniper owner = snipeData.getOwner();
			owner.storeUndo(undo);
			blockStates.stream()
				.map(BlockState::getBlock)
				.forEach(block -> block.setType(Material.AIR));
			for (BlockState blockState : blockStates) {
				Block affectedBlock = world.getBlockAt(blockState.getX() + direction[0], blockState.getY() + direction[1], blockState.getZ() + direction[2]);
				affectedBlock.setBlockData(blockState.getBlockData(), !BREAKABLE_MATERIALS.contains(blockState.getType()));
			}
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation1(this.getTargetBlock()
			.getLocation());
		snipeData.getMessages()
			.brushMessage("Point 1 set.");
		try {
			if (this.selection.calculateRegion()) {
				this.moveSelection(snipeData, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (RuntimeException exception) {
			snipeData.sendMessage(exception.getMessage());
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation2(this.getTargetBlock()
			.getLocation());
		snipeData.getMessages()
			.brushMessage("Point 2 set.");
		try {
			if (this.selection.calculateRegion()) {
				this.moveSelection(snipeData, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (RuntimeException exception) {
			snipeData.sendMessage(exception.getMessage());
		}
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.custom(ChatColor.BLUE + "Move selection blockPositionY " + ChatColor.GOLD + "x:" + this.moveDirections[0] + " y:" + this.moveDirections[1] + " z:" + this.moveDirections[2]);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			Messages messages = snipeData.getMessages();
			if (parameter.equalsIgnoreCase("info")) {
				messages.custom(ChatColor.GOLD + this.getName() + " Parameters:");
				messages.custom(ChatColor.AQUA + "/b mv x[int] -- set the x direction (positive => east)");
				messages.custom(ChatColor.AQUA + "/b mv y[int] -- set the y direction (positive => up)");
				messages.custom(ChatColor.AQUA + "/b mv z[int] -- set the z direction (positive => south)");
				messages.custom(ChatColor.AQUA + "/b mv reset -- reset the brush (x:0 y:0 z:0)");
				messages.custom(ChatColor.AQUA + "Use arrow and gunpowder to define two points.");
			}
			if (parameter.equalsIgnoreCase("reset")) {
				this.moveDirections[0] = 0;
				this.moveDirections[1] = 0;
				this.moveDirections[2] = 0;
				messages.custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
				messages.custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
				messages.custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
			}
			String parameterLowered = parameter.toLowerCase();
			if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'x') {
				this.moveDirections[0] = Integer.valueOf(parameter.substring(1));
				messages.custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
			} else if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'y') {
				this.moveDirections[1] = Integer.valueOf(parameter.substring(1));
				messages.custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
			} else if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'z') {
				this.moveDirections[2] = Integer.valueOf(parameter.substring(1));
				messages.custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
			}
		}
	}

	/**
	 * Selection Helper class.
	 *
	 * @author MikeMatrix
	 */
	private class Selection {

		/**
		 * Maximum amount of Blocks allowed blockPositionY the Selection.
		 */
		private static final int MAX_BLOCK_COUNT = 5000000;
		/**
		 * Calculated BlockStates of the selection.
		 */
		private List<BlockState> blockStates = new ArrayList<>();
		private Location location1;
		private Location location2;

		/**
		 * Calculates region, then saves all Blocks as BlockState.
		 *
		 * @return boolean success.
		 * @throws RuntimeException Messages to be sent to the player.
		 */
		public boolean calculateRegion() {
			if (this.location1 != null && this.location2 != null) {
				World world1 = this.location1.getWorld();
				World world2 = this.location2.getWorld();
				if (world1.equals(world2)) {
					int x1 = this.location1.getBlockX();
					int x2 = this.location2.getBlockX();
					int y1 = this.location1.getBlockY();
					int y2 = this.location2.getBlockY();
					int z1 = this.location1.getBlockZ();
					int z2 = this.location2.getBlockZ();
					int lowX = x1 <= x2 ? x1 : x2;
					int lowY = y1 <= y2 ? y1 : y2;
					int lowZ = z1 <= z2 ? z1 : z2;
					int highX = x1 >= x2 ? x1 : x2;
					int highY = y1 >= y2 ? y1 : y2;
					int highZ = z1 >= z2 ? z1 : z2;
					if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > MAX_BLOCK_COUNT) {
						throw new RuntimeException(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
					}
					for (int y = lowY; y <= highY; y++) {
						for (int x = lowX; x <= highX; x++) {
							for (int z = lowZ; z <= highZ; z++) {
								Block block = world1.getBlockAt(x, y, z);
								this.blockStates.add(block.getState());
							}
						}
					}
					return true;
				}
			}
			return false;
		}

		/**
		 * @return calculated BlockStates of defined region.
		 */
		public List<BlockState> getBlockStates() {
			return this.blockStates;
		}

		public Location getLocation1() {
			return this.location1;
		}

		public void setLocation1(Location location1) {
			this.location1 = location1;
		}

		public Location getLocation2() {
			return this.location2;
		}

		public void setLocation2(Location location2) {
			this.location2 = location2;
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.move";
	}
}
