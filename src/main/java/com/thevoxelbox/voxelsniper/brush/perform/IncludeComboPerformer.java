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
public class IncludeComboPerformer extends AbstractPerformer {

	private VoxelList includeList;
	private int id;
	private byte data;

	public IncludeComboPerformer() {
		this.setName("Include Combo");
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxelList();
		message.voxel();
		message.data();
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.id = snipeData.getVoxelId();
		this.data = snipeData.getData();
		this.includeList = snipeData.getVoxelList();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform(Block block) {
		if (this.includeList.contains(new int[] {block.getTypeId(), block.getData()})) {
			this.undo.put(block);
			block.setTypeIdAndData(this.id, this.data, true);
		}
	}
}
