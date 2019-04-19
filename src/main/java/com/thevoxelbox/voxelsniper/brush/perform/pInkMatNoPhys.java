package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pInkMatNoPhys extends vPerformer {

	private byte d;
	private int ir;

	public pInkMatNoPhys() {
		this.setName("Ink-Mat, No Physics");
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
			block.setData(this.d, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
