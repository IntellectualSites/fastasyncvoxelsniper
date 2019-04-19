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
public class pInkMat extends vPerformer {

	private byte d;
	private int ir;

	public pInkMat() {
		this.setName("Ink-Mat");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.d = v.getData();
		this.ir = v.getReplaceId();
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.data();
		vm.replace();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getTypeId() == this.ir) {
			this.h.put(block);
			block.setData(this.d, true);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
