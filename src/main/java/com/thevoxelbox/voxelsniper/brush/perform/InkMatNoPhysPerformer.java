package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class InkMatNoPhysPerformer extends AbstractPerformer {

	private byte d;
	private int ir;

	public InkMatNoPhysPerformer() {
		super("Ink-Mat, No Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.d = snipeData.getData();
		this.ir = snipeData.getReplaceId();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.data();
		message.replace();
	}

	@Override
	public void perform(Block block) {
		if (block.getTypeId() == this.ir) {
			this.undo.put(block);
			block.setData(this.d, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
