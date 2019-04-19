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
public class MatMatNoPhysPerformer extends AbstractPerformer {

	private int i;
	private int r;

	public MatMatNoPhysPerformer() {
		this.setName("Mat-Mat No-Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.i = snipeData.getVoxelId();
		this.r = snipeData.getReplaceId();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxel();
		message.replace();
	}

	@Override
	public void perform(Block block) {
		if (block.getTypeId() == this.r) {
			this.undo.put(block);
			block.setTypeId(this.i, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
