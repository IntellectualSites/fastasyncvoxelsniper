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
public class pComboCombo extends vPerformer {

	private byte d;
	private byte dr;
	private int i;
	private int ir;

	public pComboCombo() {
		this.setName("Combo-Combo");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData v) {
		this.world = v.getWorld();
		this.d = v.getData();
		this.dr = v.getReplaceData();
		this.i = v.getVoxelId();
		this.ir = v.getReplaceId();
	}

	@Override
	public void info(Message vm) {
		vm.performerName(this.getName());
		vm.voxel();
		vm.replace();
		vm.data();
		vm.replaceData();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getTypeId() == this.ir && block.getData() == this.dr) {
			this.h.put(block);
			block.setTypeIdAndData(this.i, this.d, true);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
