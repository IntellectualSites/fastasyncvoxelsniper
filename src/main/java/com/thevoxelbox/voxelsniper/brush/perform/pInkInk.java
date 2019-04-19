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
public class pInkInk extends vPerformer {

	private byte d;
	private byte dr;

	public pInkInk() {
		this.setName("Ink-Ink");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.d = v.getData();
		this.dr = v.getReplaceData();
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.data();
		vm.replaceData();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getData() == this.dr) {
			this.h.put(block);
			block.setData(this.d, true);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
