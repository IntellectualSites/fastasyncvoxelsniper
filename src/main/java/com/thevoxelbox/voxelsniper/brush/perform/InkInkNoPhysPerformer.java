package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class InkInkNoPhysPerformer extends AbstractPerformer {

	private byte d;
	private byte dr;

	public InkInkNoPhysPerformer() {
		this.setName("Ink-Ink, No Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.d = snipeData.getData();
		this.dr = snipeData.getReplaceData();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.data();
		message.replaceData();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (block.getData() == this.dr) {
			this.undo.put(block);
			block.setData(this.d, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
