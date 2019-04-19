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
public class pCombo extends vPerformer {

	private int i;
	private byte d;

	public pCombo() {
		this.setName("Combo");
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.voxel();
		vm.data();
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.i = v.getVoxelId();
		this.d = v.getData();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		this.h.put(block);
		block.setTypeIdAndData(this.i, this.d, true);
	}
}
