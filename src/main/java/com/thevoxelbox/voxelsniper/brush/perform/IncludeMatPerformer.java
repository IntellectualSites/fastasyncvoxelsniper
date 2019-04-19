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
public class IncludeMatPerformer extends AbstractPerformer {

	private VoxelList includeList;
	private int id;

	public IncludeMatPerformer() {
		this.setName("Include Material");
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
		this.includeList = snipeData.getVoxelList();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (this.includeList.contains(new int[] {block.getTypeId(), block.getData()})) {
			this.undo.put(block);
			block.setTypeId(this.id);
		}
	}
}
