/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pIncludeInk extends vPerformer {

	private VoxelList includeList;
	private byte data;

	public pIncludeInk() {
		this.setName("Include Ink");
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.voxelList();
		vm.data();
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.data = v.getData();
		this.includeList = v.getVoxelList();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (this.includeList.contains(new int[] {block.getTypeId(), block.getData()})) {
			this.h.put(block);
			block.setData(this.data);
		}
	}
}
