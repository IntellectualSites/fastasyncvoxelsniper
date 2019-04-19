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
public class MaterialPerformer extends AbstractPerformer {

	private int i;

	public MaterialPerformer() {
		this.setName("Material");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.i = snipeData.getVoxelId();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxel();
	}

	@Override
	public void perform(Block block) {
		if (block.getTypeId() != this.i) {
			this.undo.put(block);
			block.setTypeId(this.i);
		}
	}
}
