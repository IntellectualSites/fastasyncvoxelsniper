package com.thevoxelbox.voxelsniper;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block
 * locations back to the recorded states.
 */
public class Undo {

	private static final Set<Material> FALLING_MATERIALS = EnumSet.of(Material.LEGACY_WATER, Material.LEGACY_STATIONARY_WATER, Material.LEGACY_LAVA, Material.LEGACY_STATIONARY_LAVA);
	private static final Set<Material> FALLOFF_MATERIALS = EnumSet.of(Material.LEGACY_SAPLING, Material.LEGACY_BED_BLOCK, Material.LEGACY_POWERED_RAIL, Material.LEGACY_DETECTOR_RAIL, Material.LEGACY_LONG_GRASS, Material.LEGACY_DEAD_BUSH, Material.LEGACY_PISTON_EXTENSION, Material.LEGACY_YELLOW_FLOWER, Material.LEGACY_RED_ROSE, Material.LEGACY_BROWN_MUSHROOM, Material.LEGACY_RED_MUSHROOM, Material.LEGACY_TORCH, Material.LEGACY_FIRE, Material.LEGACY_CROPS, Material.LEGACY_SIGN_POST, Material.LEGACY_WOODEN_DOOR, Material.LEGACY_LADDER, Material.LEGACY_RAILS, Material.LEGACY_WALL_SIGN, Material.LEGACY_LEVER, Material.LEGACY_STONE_PLATE, Material.LEGACY_IRON_DOOR_BLOCK, Material.LEGACY_WOOD_PLATE, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_REDSTONE_TORCH_ON, Material.LEGACY_REDSTONE_WIRE, Material.LEGACY_STONE_BUTTON, Material.LEGACY_SNOW, Material.LEGACY_CACTUS, Material.LEGACY_SUGAR_CANE_BLOCK, Material.LEGACY_CAKE_BLOCK, Material.LEGACY_DIODE_BLOCK_OFF, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_TRAP_DOOR, Material.LEGACY_PUMPKIN_STEM, Material.LEGACY_MELON_STEM, Material.LEGACY_VINE, Material.LEGACY_WATER_LILY, Material.LEGACY_NETHER_WARTS);
	private final Set<Vector> containing = Sets.newHashSet();
	private final List<BlockState> all;
	private final List<BlockState> falloff;
	private final List<BlockState> dropdown;

	/**
	 * Default constructor of a Undo container.
	 */
	public Undo() {
		this.all = new LinkedList<>();
		this.falloff = new LinkedList<>();
		this.dropdown = new LinkedList<>();
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
		Vector pos = block.getLocation()
			.toVector();
		if (this.containing.contains(pos)) {
			return;
		}
		this.containing.add(pos);
		if (FALLING_MATERIALS.contains(block.getType())) {
			this.dropdown.add(block.getState());
		} else if (FALLOFF_MATERIALS.contains(block.getType())) {
			this.falloff.add(block.getState());
		} else {
			this.all.add(block.getState());
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
		for (BlockState blockState : this.dropdown) {
			blockState.update(true, false);
			updateSpecialBlocks(blockState);
		}
	}

	/**
	 *
	 */
	private void updateSpecialBlocks(BlockState blockState) {
		BlockState currentState = blockState.getBlock()
			.getState();
		if (blockState instanceof BrewingStand && currentState instanceof BrewingStand) {
			((BrewingStand) currentState).getInventory()
				.setContents(((BrewingStand) blockState).getInventory()
					.getContents());
		} else if (blockState instanceof Chest && currentState instanceof Chest) {
			((Chest) currentState).getInventory()
				.setContents(((Chest) blockState).getInventory()
					.getContents());
			((Chest) currentState).getBlockInventory()
				.setContents(((Chest) blockState).getBlockInventory()
					.getContents());
			currentState.update();
		} else if (blockState instanceof CreatureSpawner && currentState instanceof CreatureSpawner) {
			((CreatureSpawner) currentState).setSpawnedType(((CreatureSpawner) currentState).getSpawnedType());
			currentState.update();
		} else if (blockState instanceof Dispenser && currentState instanceof Dispenser) {
			((Dispenser) currentState).getInventory()
				.setContents(((Dispenser) blockState).getInventory()
					.getContents());
			currentState.update();
		} else if (blockState instanceof Furnace && currentState instanceof Furnace) {
			((Furnace) currentState).getInventory()
				.setContents(((Furnace) blockState).getInventory()
					.getContents());
			((Furnace) currentState).setBurnTime(((Furnace) blockState).getBurnTime());
			((Furnace) currentState).setCookTime(((Furnace) blockState).getCookTime());
			currentState.update();
		} else if (blockState instanceof NoteBlock && currentState instanceof NoteBlock) {
			((NoteBlock) currentState).setNote(((NoteBlock) blockState).getNote());
			currentState.update();
		} else if (blockState instanceof Sign && currentState instanceof Sign) {
			int i = 0;
			for (String text : ((Sign) blockState).getLines()) {
				((Sign) currentState).setLine(i++, text);
			}
			currentState.update();
		}
	}
}
