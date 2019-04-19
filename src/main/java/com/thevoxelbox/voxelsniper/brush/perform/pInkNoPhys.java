package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class pInkNoPhys extends vPerformer {

	private byte d;

	public pInkNoPhys() {
		this.setName("Ink, No Physics");
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
		this.h.put(block);
		block.setData(this.d, false);
	}
}
