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
public class pInkNoUndo extends vPerformer {

	private byte d;

	public pInkNoUndo() {
		this.setName("Ink, No-Undo"); // made name more descriptive - Giltwist
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.d = v.getData();
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.data();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getData() != this.d) {
			block.setData(this.d);
		}
	}
}
