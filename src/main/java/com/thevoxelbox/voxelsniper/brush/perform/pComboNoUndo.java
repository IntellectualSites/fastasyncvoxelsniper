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
public class pComboNoUndo extends vPerformer {

	private int i;
	private byte d;

	public pComboNoUndo() {
		this.setName("Combo, No-Undo"); // made name more descriptive - Giltwist
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.i = v.getVoxelId();
		this.d = v.getData();
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.voxel();
		vm.data();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getTypeId() != this.i || block.getData() != this.d) {
			block.setTypeIdAndData(this.i, this.d, true);
		}
	}
}
