/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.util.VoxelList;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class ExcludeMatPerformer extends AbstractPerformer {

	private VoxelList excludeList;
	private int id;

	public ExcludeMatPerformer() {
		this.setName("Exclude Material");
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxelList();
		message.voxel();
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.id = snipeData.getVoxelId();
		this.excludeList = snipeData.getVoxelList();
	}

	@Override
	public void perform(Block block) {
		if (!this.excludeList.contains(new int[] {block.getTypeId(), block.getData()})) {
			this.undo.put(block);
			block.setTypeId(this.id);
		}
	}
}
