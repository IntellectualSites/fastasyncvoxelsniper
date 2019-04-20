/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class IncludeComboPerformer extends AbstractPerformer {

	private List<BlockData> includeList;
	private BlockData blockData;

	public IncludeComboPerformer() {
		super("Include Combo");
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxelList();
		message.blockDataType();
		message.blockData();
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.blockData = snipeData.getBlockData();
		this.includeList = snipeData.getVoxelList();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (this.includeList.contains(blockData)) {
			this.undo.put(block);
			block.setBlockData(this.blockData);
		}
	}
}
