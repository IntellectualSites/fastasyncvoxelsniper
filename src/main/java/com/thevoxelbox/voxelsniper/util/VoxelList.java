package com.thevoxelbox.voxelsniper.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Container class for multiple ID/Datavalue pairs.
 */
public class VoxelList {

	private static final BlockData AIR = Material.AIR.createBlockData();

	private List<BlockData[]> valuePairs = new ArrayList<>();

	/**
	 * Adds the specified id, data value pair to the VoxelList. A data value of -1 will operate on all data values of that id.
	 */
	public void add(BlockData[] i) {
		if (i[1] == AIR) {
			if (!this.valuePairs.contains(i)) {
				this.valuePairs.removeIf(in -> in[0] == i[0]);
				this.valuePairs.add(i);
			}
		} else {
			if (!this.valuePairs.contains(i)) {
				this.valuePairs.add(i);
			}
		}
	}

	/**
	 * Removes the specified id, data value pair from the VoxelList.
	 *
	 * @return true if this list contained the specified element
	 */
	public boolean removeValue(BlockData[] i) {
		if (this.valuePairs.isEmpty()) {
			return false;
		} else {
			boolean ret = false;
			if (i[1] == -1) {
				for (Iterator<int[]> it = this.valuePairs.iterator(); it.hasNext(); ) {
					int[] in = it.next();
					if (in[0] == i[0]) {
						it.remove();
						ret = true;
					}
				}
			} else {
				ret = this.valuePairs.remove(i);
			}
			return ret;
		}
	}

	/**
	 * @return true if this list contains the specified element
	 */
	public boolean contains(BlockData[] blockData) {
		for (BlockData[] in : this.valuePairs) {
			if (in[0].matches(blockData[0]) && (in[1] == blockData[1] || in[1] == AIR)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the VoxelList.
	 */
	public void clear() {
		this.valuePairs.clear();
	}

	/**
	 * Returns true if this list contains no elements.
	 *
	 * @return true if this list contains no elements
	 */
	public boolean isEmpty() {
		return this.valuePairs.isEmpty();
	}

	/**
	 * Returns a defensive copy of the List with pairs.
	 *
	 * @return defensive copy of the List with pairs
	 */
	public List<int[]> getList() {
		return List.copyOf(this.valuePairs);
	}
}
