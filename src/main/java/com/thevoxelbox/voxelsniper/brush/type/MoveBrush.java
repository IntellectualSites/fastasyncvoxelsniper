package com.thevoxelbox.voxelsniper.brush.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Moves a selection blockPositionY a certain amount.
 */
public class MoveBrush extends AbstractBrush {

	/**
	 * Breakable Blocks to determine if no-physics should be used.
	 */
	private static final MaterialSet BREAKABLE_MATERIALS = MaterialSet.builder()
		.with(Tag.SAPLINGS)
		.with(Tag.RAILS)
		.with(Tag.TRAPDOORS)
		.with(Tag.DOORS)
		.with(Tag.BUTTONS)
		.with(MaterialSets.PISTONS)
		.with(MaterialSets.PRESSURE_PLATES)
		.with(MaterialSets.SIGNS)
		.with(MaterialSets.BEDS)
		.with(MaterialSets.REDSTONE_TORCHES)
		.with(MaterialSets.TORCHES)
		.with(MaterialSets.FLORA)
		.add(Material.FIRE)
		.add(Material.REPEATER)
		.add(Material.SNOW)
		.add(Material.CAKE)
		.add(Material.LADDER)
		.add(Material.LEVER)
		.build();

	/**
	 * Saved direction.
	 */
	private int[] moveDirections = {0, 0, 0};
	/**
	 * Saved selection.
	 */
	@Nullable
	private Selection selection;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		BrushProperties brushProperties = snipe.getBrushProperties();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + brushProperties.getName() + " Parameters:");
				messenger.sendMessage(ChatColor.AQUA + "/b mv x[int] -- set the x direction (positive => east)");
				messenger.sendMessage(ChatColor.AQUA + "/b mv y[int] -- set the y direction (positive => up)");
				messenger.sendMessage(ChatColor.AQUA + "/b mv z[int] -- set the z direction (positive => south)");
				messenger.sendMessage(ChatColor.AQUA + "/b mv reset -- reset the brush (x:0 y:0 z:0)");
				messenger.sendMessage(ChatColor.AQUA + "Use arrow and gunpowder to define two points.");
			}
			if (parameter.equalsIgnoreCase("reset")) {
				this.moveDirections[0] = 0;
				this.moveDirections[1] = 0;
				this.moveDirections[2] = 0;
				messenger.sendMessage(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
				messenger.sendMessage(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
				messenger.sendMessage(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
			}
			String parameterLowered = parameter.toLowerCase();
			if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'x') {
				this.moveDirections[0] = Integer.parseInt(parameter.substring(1));
				messenger.sendMessage(ChatColor.AQUA + "X direction set to: " + this.moveDirections[0]);
			} else if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'y') {
				this.moveDirections[1] = Integer.parseInt(parameter.substring(1));
				messenger.sendMessage(ChatColor.AQUA + "Y direction set to: " + this.moveDirections[1]);
			} else if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'z') {
				this.moveDirections[2] = Integer.parseInt(parameter.substring(1));
				messenger.sendMessage(ChatColor.AQUA + "Z direction set to: " + this.moveDirections[2]);
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation1(this.getTargetBlock()
			.getLocation());
		messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Point 1 set.");
		try {
			if (this.selection.calculateRegion()) {
				moveSelection(snipe, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (RuntimeException exception) {
			messenger.sendMessage(exception.getMessage());
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation2(this.getTargetBlock()
			.getLocation());
		messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Point 2 set.");
		try {
			if (this.selection.calculateRegion()) {
				this.moveSelection(snipe, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (RuntimeException exception) {
			messenger.sendMessage(exception.getMessage());
		}
	}

	/**
	 * Moves the given selection blockPositionY the amount given in direction and saves an undo for the player.
	 */
	private void moveSelection(Snipe snipe, Selection selection, int[] direction) {
		SnipeMessenger messenger = snipe.createMessenger();
		Sniper sniper = snipe.getSniper();
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
				messenger.sendMessage(ChatColor.LIGHT_PURPLE + "The new Selection has more blocks than the original selection. This should never happen!");
			}
			Set<Block> undoSet = blockStates.stream()
				.map(BlockState::getBlock)
				.collect(Collectors.toSet());
			newSelection.getBlockStates()
				.stream()
				.map(BlockState::getBlock)
				.forEach(undoSet::add);
			undoSet.forEach(undo::put);
			sniper.storeUndo(undo);
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
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(ChatColor.BLUE + "Move selection blockPositionY " + ChatColor.GOLD + "x:" + this.moveDirections[0] + " y:" + this.moveDirections[1] + " z:" + this.moveDirections[2]);
	}

	private static class Selection {

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
					int lowX = Math.min(x1, x2);
					int lowY = Math.min(y1, y2);
					int lowZ = Math.min(z1, z2);
					int highX = Math.max(x1, x2);
					int highY = Math.max(y1, y2);
					int highZ = Math.max(z1, z2);
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
}
