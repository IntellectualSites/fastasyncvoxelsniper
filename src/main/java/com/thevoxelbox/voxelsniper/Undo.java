package com.thevoxelbox.voxelsniper;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block
 * locations back to the recorded states.
 */
public class Undo {

	private static final Set<Material> FALLING_MATERIALS = EnumSet.of(Material.LEGACY_WATER, Material.LEGACY_STATIONARY_WATER, Material.LEGACY_LAVA, Material.LEGACY_STATIONARY_LAVA);
	private static final Set<Material> FALLOFF_MATERIALS = EnumSet.of(Material.LEGACY_SAPLING, Material.LEGACY_BED_BLOCK, Material.LEGACY_POWERED_RAIL, Material.LEGACY_DETECTOR_RAIL, Material.LEGACY_LONG_GRASS, Material.LEGACY_DEAD_BUSH, Material.LEGACY_PISTON_EXTENSION, Material.LEGACY_YELLOW_FLOWER, Material.LEGACY_RED_ROSE, Material.LEGACY_BROWN_MUSHROOM, Material.LEGACY_RED_MUSHROOM, Material.LEGACY_TORCH, Material.LEGACY_FIRE, Material.LEGACY_CROPS, Material.LEGACY_SIGN_POST, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_LADDER, Material.LEGACY_RAILS, Material.LEGACY_WALL_SIGN, Material.LEGACY_LEVER, Material.LEGACY_STONE_PLATE, Material.LEGACY_IRON_DOOR_BLOCK, Material.LEGACY_WOOD_PLATE, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_REDSTONE_TORCH_ON, Material.LEGACY_REDSTONE_WIRE, Material.LEGACY_STONE_BUTTON, Material.LEGACY_SNOW, Material.LEGACY_CACTUS, Material.LEGACY_SUGAR_CANE_BLOCK, Material.LEGACY_CAKE_BLOCK, Material.LEGACY_DIODE_BLOCK_OFF, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_TRAP_DOOR, Material.LEGACY_PUMPKIN_STEM, Material.LEGACY_MELON_STEM, Material.LEGACY_VINE, Material.LEGACY_WATER_LILY, Material.LEGACY_NETHER_WARTS);

	private Set<Vector> containing;
	private List<BlockState> all;
	private List<BlockState> falloff;
	private List<BlockState> dropDown;

	/**
	 * Default constructor of a Undo container.
	 */
	public Undo() {
		this.containing = new HashSet<>();
		this.all = new LinkedList<>();
		this.falloff = new LinkedList<>();
		this.dropDown = new LinkedList<>();
	}

	/**
	 * Get the number of blocks in the collection.
	 *
	 * @return size of the Undo collection
	 */
	public int getSize() {
		return this.containing.size();
	}

	/**
	 * Adds a Block to the collection.
	 *
	 * @param block Block to be added
	 */
	public void put(Block block) {
		Location location = block.getLocation();
		Vector position = location.toVector();
		if (this.containing.contains(position)) {
			return;
		}
		this.containing.add(position);
		Material type = block.getType();
		BlockState state = block.getState();
		if (FALLING_MATERIALS.contains(type)) {
			this.dropDown.add(state);
		} else if (FALLOFF_MATERIALS.contains(type)) {
			this.falloff.add(state);
		} else {
			this.all.add(state);
		}
	}

	/**
	 * Set the blockstates of all recorded blocks back to the state when they
	 * were inserted.
	 */
	public void undo() {
		for (BlockState blockState : this.all) {
			blockState.update(true, false);
			updateSpecialBlocks(blockState);
		}
		for (BlockState blockState : this.falloff) {
			blockState.update(true, false);
			updateSpecialBlocks(blockState);
		}
		for (BlockState blockState : this.dropDown) {
			blockState.update(true, false);
			updateSpecialBlocks(blockState);
		}
	}

	private void updateSpecialBlocks(BlockState previousState) {
		Block block = previousState.getBlock();
		BlockState currentState = block.getState();
		if (previousState instanceof InventoryHolder && currentState instanceof InventoryHolder) {
			updateInventoryHolder((InventoryHolder) previousState, (InventoryHolder) currentState);
		}
		if (previousState instanceof Chest && currentState instanceof Chest) {
			updateChest((Chest) previousState, (Chest) currentState);
		}
		if (previousState instanceof CreatureSpawner && currentState instanceof CreatureSpawner) {
			updateCreatureSpawner((CreatureSpawner) previousState, (CreatureSpawner) currentState);
		}
		if (previousState instanceof Furnace && currentState instanceof Furnace) {
			updateFurnace((Furnace) previousState, (Furnace) currentState);
		}
		if (previousState instanceof Sign && currentState instanceof Sign) {
			updateSign((Sign) previousState, (Sign) currentState);
		}
		currentState.update();
	}

	private void updateInventoryHolder(InventoryHolder previousState, InventoryHolder currentState) {
		Inventory currentInventory = currentState.getInventory();
		Inventory previousInventory = previousState.getInventory();
		ItemStack[] previousContents = previousInventory.getContents();
		currentInventory.setContents(previousContents);
	}

	private void updateChest(Chest previousState, Chest currentState) {
		Inventory currentBlockInventory = currentState.getBlockInventory();
		Inventory previousBlockInventory = previousState.getBlockInventory();
		ItemStack[] previousBlockContents = previousBlockInventory.getContents();
		currentBlockInventory.setContents(previousBlockContents);
		currentState.update();
	}

	private void updateCreatureSpawner(CreatureSpawner previousState, CreatureSpawner currentState) {
		EntityType spawnedType = previousState.getSpawnedType();
		currentState.setSpawnedType(spawnedType);
	}

	private void updateFurnace(Furnace previousState, Furnace currentState) {
		short previousBurnTime = previousState.getBurnTime();
		currentState.setBurnTime(previousBurnTime);
		short previousCookTime = previousState.getCookTime();
		currentState.setCookTime(previousCookTime);
	}

	private void updateSign(Sign previousState, Sign currentState) {
		String[] previousLines = previousState.getLines();
		for (int index = 0; index < previousLines.length; index++) {
			String previousLine = previousLines[index];
			currentState.setLine(index, previousLine);
		}
	}
}
