package com.thevoxelbox.voxelsniper.brush.performer.type;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class InkNoPhysicsPerformer extends AbstractPerformer {

	private BlockData blockData;

	public InkNoPhysicsPerformer() {
		super("Ink, No Physics");
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.blockData = snipeData.getBlockData();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.blockData();
	}

	@Override
	public void perform(Block block) {
		this.undo.put(block);
		block.setBlockData(this.blockData, false);
	}
}
