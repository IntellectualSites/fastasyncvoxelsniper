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
public class pComboInkNoPhys extends vPerformer {

	private byte d;
	private byte dr;
	private int i;

	public pComboInkNoPhys() {
		this.setName("Combo-Ink, No Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.d = v.getData();
		this.dr = v.getReplaceData();
		this.i = v.getVoxelId();
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.voxel();
		vm.data();
		vm.replaceData();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getData() == this.dr) {
			this.h.put(block);
			block.setTypeIdAndData(this.i, this.d, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
