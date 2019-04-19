package com.thevoxelbox.voxelsniper.brush;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
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
public class MoveBrush extends Brush {

	/**
	 * Breakable Blocks to determine if no-physics should be used.
	 */
	private static final Set<Material> BREAKABLE_MATERIALS = new TreeSet<>();

	static {
		BREAKABLE_MATERIALS.add(Material.SAPLING);
		BREAKABLE_MATERIALS.add(Material.BED_BLOCK);
		BREAKABLE_MATERIALS.add(Material.POWERED_RAIL);
		BREAKABLE_MATERIALS.add(Material.DETECTOR_RAIL);
		BREAKABLE_MATERIALS.add(Material.LONG_GRASS);
		BREAKABLE_MATERIALS.add(Material.DEAD_BUSH);
		BREAKABLE_MATERIALS.add(Material.PISTON_EXTENSION);
		BREAKABLE_MATERIALS.add(Material.YELLOW_FLOWER);
		BREAKABLE_MATERIALS.add(Material.RED_ROSE);
		BREAKABLE_MATERIALS.add(Material.BROWN_MUSHROOM);
		BREAKABLE_MATERIALS.add(Material.RED_MUSHROOM);
		BREAKABLE_MATERIALS.add(Material.TORCH);
		BREAKABLE_MATERIALS.add(Material.FIRE);
		BREAKABLE_MATERIALS.add(Material.CROPS);
		BREAKABLE_MATERIALS.add(Material.SIGN_POST);
		BREAKABLE_MATERIALS.add(Material.WOODEN_DOOR);
		BREAKABLE_MATERIALS.add(Material.LADDER);
		BREAKABLE_MATERIALS.add(Material.RAILS);
		BREAKABLE_MATERIALS.add(Material.WALL_SIGN);
		BREAKABLE_MATERIALS.add(Material.LEVER);
		BREAKABLE_MATERIALS.add(Material.STONE_PLATE);
		BREAKABLE_MATERIALS.add(Material.IRON_DOOR_BLOCK);
		BREAKABLE_MATERIALS.add(Material.WOOD_PLATE);
		BREAKABLE_MATERIALS.add(Material.REDSTONE_TORCH_OFF);
		BREAKABLE_MATERIALS.add(Material.REDSTONE_TORCH_ON);
		BREAKABLE_MATERIALS.add(Material.STONE_BUTTON);
		BREAKABLE_MATERIALS.add(Material.SNOW);
		BREAKABLE_MATERIALS.add(Material.CACTUS);
		BREAKABLE_MATERIALS.add(Material.SUGAR_CANE_BLOCK);
		BREAKABLE_MATERIALS.add(Material.CAKE_BLOCK);
		BREAKABLE_MATERIALS.add(Material.DIODE_BLOCK_OFF);
		BREAKABLE_MATERIALS.add(Material.DIODE_BLOCK_ON);
		BREAKABLE_MATERIALS.add(Material.TRAP_DOOR);
		BREAKABLE_MATERIALS.add(Material.PUMPKIN_STEM);
		BREAKABLE_MATERIALS.add(Material.MELON_STEM);
		BREAKABLE_MATERIALS.add(Material.VINE);
		BREAKABLE_MATERIALS.add(Material.WATER_LILY);
		BREAKABLE_MATERIALS.add(Material.NETHER_WARTS);
	}

	/**
	 * Saved direction.
	 */
	private final int[] moveDirections = {0, 0, 0};
	/**
	 * Saved selection.
	 */
	@Nullable
	private Selection selection;

	/**
	 *
	 */
	public MoveBrush() {
		this.setName("Move");
	}

	/**
	 * Moves the given selection blockPositionY the amount given in direction and saves an undo for the player.
	 */
	@SuppressWarnings("deprecation")
	private void moveSelection(SnipeData v, Selection selection, int[] direction) {
		if (!selection.getBlockStates()
			.isEmpty()) {
			World world = selection.getBlockStates()
				.get(0)
				.getWorld();
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
			} catch (Exception exception) {
				v.getVoxelMessage()
					.brushMessage("The new Selection has more blocks than the original selection. This should never happen!");
			}
			Set<Block> undoSet = new HashSet<>();
			for (BlockState blockState : selection.getBlockStates()) {
				undoSet.add(blockState.getBlock());
			}
			for (BlockState blockState : newSelection.getBlockStates()) {
				undoSet.add(blockState.getBlock());
			}
			for (Block block : undoSet) {
				undo.put(block);
			}
			v.owner()
				.storeUndo(undo);
			for (BlockState blockState : selection.getBlockStates()) {
				blockState.getBlock()
					.setType(Material.AIR);
			}
			for (BlockState blockState : selection.getBlockStates()) {
				Block affectedBlock = world.getBlockAt(blockState.getX() + direction[0], blockState.getY() + direction[1], blockState.getZ() + direction[2]);
				affectedBlock.setTypeId(blockState.getTypeId(), !BREAKABLE_MATERIALS.contains(blockState.getType()));
				affectedBlock.setData(blockState.getRawData());
			}
		}
	}

	@Override
	protected final void arrow(SnipeData v) {
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation1(this.getTargetBlock()
			.getLocation());
		v.getVoxelMessage()
			.brushMessage("Point 1 set.");
		try {
			if (this.selection.calculateRegion()) {
				this.moveSelection(v, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (Exception exception) {
			v.sendMessage(exception.getMessage());
		}
	}

	@Override
	protected final void powder(SnipeData v) {
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation2(this.getTargetBlock()
			.getLocation());
		v.getVoxelMessage()
			.brushMessage("Point 2 set.");
		try {
			if (this.selection.calculateRegion()) {
				this.moveSelection(v, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (Exception exception) {
			v.sendMessage(exception.getMessage());
		}
	}

	@Override
	public final void info(Message vm) {
		vm.brushName(this.getName());
		vm.custom(ChatColor.BLUE + "Move selection blockPositionY " + ChatColor.GOLD + "x:" + this.moveDirections[0] + " y:" + this.moveDirections[1] + " z:" + this.moveDirections[2]);
	}

	@Override
	public final void parameters(String[] par, SnipeData v) {
		for (int i = 1; i < par.length; i++) {
			if (par[i].equalsIgnoreCase("info")) {
				v.getVoxelMessage()
					.custom(ChatColor.GOLD + this.getName() + " Parameters:");
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "/b mv x[int] -- set the x direction (positive => east)");
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "/b mv y[int] -- set the y direction (positive => up)");
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "/b mv z[int] -- set the z direction (positive => south)");
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "/b mv reset -- reset the brush (x:0 y:0 z:0)");
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "Use arrow and gunpowder to define two points.");
			}
			if (par[i].equalsIgnoreCase("reset")) {
				this.moveDirections[0] = 0;
				this.moveDirections[1] = 0;
				this.moveDirections[2] = 0;
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
			}
			if (par[i].toLowerCase()
				.startsWith("x")) {
				this.moveDirections[0] = Integer.valueOf(par[i].substring(1));
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
			} else if (par[i].toLowerCase()
				.startsWith("y")) {
				this.moveDirections[1] = Integer.valueOf(par[i].substring(1));
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
			} else if (par[i].toLowerCase()
				.startsWith("z")) {
				this.moveDirections[2] = Integer.valueOf(par[i].substring(1));
				v.getVoxelMessage()
					.custom(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
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
		private final ArrayList<BlockState> blockStates = new ArrayList<>();
		/**
		 *
		 */
		private Location location1;
		/**
		 *
		 */
		private Location location2;

		/**
		 * Calculates region, then saves all Blocks as BlockState.
		 *
		 * @return boolean success.
		 * @throws Exception Message to be sent to the player.
		 */
		public boolean calculateRegion() throws Exception {
			if (this.location1 != null && this.location2 != null) {
				if (this.location1.getWorld()
					.equals(this.location2.getWorld())) {
					int lowX = ((this.location1.getBlockX() <= this.location2.getBlockX()) ? this.location1.getBlockX() : this.location2.getBlockX());
					int lowY = (this.location1.getBlockY() <= this.location2.getBlockY()) ? this.location1.getBlockY() : this.location2.getBlockY();
					int lowZ = (this.location1.getBlockZ() <= this.location2.getBlockZ()) ? this.location1.getBlockZ() : this.location2.getBlockZ();
					int highX = (this.location1.getBlockX() >= this.location2.getBlockX()) ? this.location1.getBlockX() : this.location2.getBlockX();
					int highY = (this.location1.getBlockY() >= this.location2.getBlockY()) ? this.location1.getBlockY() : this.location2.getBlockY();
					int highZ = (this.location1.getBlockZ() >= this.location2.getBlockZ()) ? this.location1.getBlockZ() : this.location2.getBlockZ();
					if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > MAX_BLOCK_COUNT) {
						throw new RuntimeException(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
					}
					World world = this.location1.getWorld();
					for (int y = lowY; y <= highY; y++) {
						for (int x = lowX; x <= highX; x++) {
							for (int z = lowZ; z <= highZ; z++) {
								this.blockStates.add(world.getBlockAt(x, y, z)
									.getState());
							}
						}
					}
					return true;
				}
			}
			return false;
		}

		/**
		 * @return ArrayList<BlockState> calculated BlockStates of defined region.
		 */
		public List<BlockState> getBlockStates() {
			return this.blockStates;
		}

		/**
		 * @return Location
		 */
		public Location getLocation1() {
			return this.location1;
		}

		/**
		 *
		 */
		public void setLocation1(Location location1) {
			this.location1 = location1;
		}

		/**
		 * @return Location
		 */
		public Location getLocation2() {
			return this.location2;
		}

		/**
		 *
		 */
		public void setLocation2(Location location2) {
			this.location2 = location2;
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.move";
	}
}
