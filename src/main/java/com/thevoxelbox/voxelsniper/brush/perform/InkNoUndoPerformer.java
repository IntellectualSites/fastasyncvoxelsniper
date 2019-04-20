/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class InkNoUndoPerformer extends AbstractPerformer {

	private BlockData blockData;

	public InkNoUndoPerformer() {
		super("Ink, No-Undo"); // made name more descriptive - Giltwist
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
		BlockData blockData = block.getBlockData();
		if (!blockData.equals(this.blockData)) {
			block.setBlockData(this.blockData);
		}
	}
}
