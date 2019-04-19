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
public class InkNoUndoPerformer extends AbstractPerformer {

	private byte d;

	public InkNoUndoPerformer() {
		this.setName("Ink, No-Undo"); // made name more descriptive - Giltwist
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
		if (block.getData() != this.d) {
			block.setData(this.d);
		}
	}
}
