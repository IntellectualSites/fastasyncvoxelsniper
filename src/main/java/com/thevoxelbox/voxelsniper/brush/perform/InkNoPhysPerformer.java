package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class InkNoPhysPerformer extends AbstractPerformer {

	private byte d;

	public InkNoPhysPerformer() {
		this.setName("Ink, No Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.d = snipeData.getData();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.data();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		this.undo.put(block);
		block.setData(this.d, false);
	}
}
