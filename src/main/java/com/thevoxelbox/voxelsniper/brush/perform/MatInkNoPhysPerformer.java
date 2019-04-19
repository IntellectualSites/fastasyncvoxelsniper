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
public class MatInkNoPhysPerformer extends AbstractPerformer {

	private int i;
	private byte dr;

	public MatInkNoPhysPerformer() {
		this.setName("Mat-Ink, No Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.i = snipeData.getVoxelId();
		this.dr = snipeData.getReplaceData();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxel();
		message.replaceData();
	}

	@Override
	public void perform(Block block) {
		if (block.getData() == this.dr) {
			this.undo.put(block);
			block.setTypeId(this.i, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
