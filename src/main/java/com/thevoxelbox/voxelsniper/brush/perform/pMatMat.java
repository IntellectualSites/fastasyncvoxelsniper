/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pMatMat extends vPerformer {

	private int i;
	private int r;

	public pMatMat() {
		this.setName("Mat-Mat");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.i = v.getVoxelId();
		this.r = v.getReplaceId();
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.voxel();
		vm.replace();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getTypeId() == this.r) {
			this.h.put(block);
			block.setTypeId(this.i);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
