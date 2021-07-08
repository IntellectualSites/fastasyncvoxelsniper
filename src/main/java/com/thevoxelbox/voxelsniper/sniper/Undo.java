package com.thevoxelbox.voxelsniper.sniper;

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

/**
 * Holds {@link BlockState}s that can be later on used to reset those block
 * locations back to the recorded states.
 */
public class Undo {

//	private Set<Vector> positions = new HashSet<>(); //FAWE REMOVE
//	private List<BlockState> blockStates = new LinkedList<>(); //FAWE REMOVE

	private int size = 0; //FAWE add

	/**
	 * Adds a Block to the collection.
	 *
	 * @param block Block to be added
	 */
	public void put(com.sk89q.worldedit.world.block.BlockState block) {
		size++; //FAWE ADD
		/* //FAWE REMOVE
		Location location = block.getLocation();
		Vector position = location.toVector();
		if (this.positions.contains(position)) {
			return;
		}
		this.positions.add(position);
		BlockState state = block.getState();
		this.blockStates.add(state);
		*/
	}

	public boolean isEmpty() {
//		return this.positions.isEmpty(); //FAWE REMOVE
		return size == 0; //FAWE ADD
	}

	/**
	 * Get the number of blocks in the collection.
	 *
	 * @return size of the Undo collection
	 */
	public int getSize() {
		return size;//FAWE
//		return this.positions.size();//FAWE REMOVE
	}

	/**
	 * Set the block states of all recorded blocks back to the state when they
	 * were inserted.
	 */
	public void undo() {
		/*//FAWE
		for (BlockState blockState : this.blockStates) {
			blockState.update(true, false);
			updateSpecialBlocks(blockState);
		}
		*/
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
