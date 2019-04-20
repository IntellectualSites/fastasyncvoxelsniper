/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ExcludeMatPerformer extends AbstractPerformer {

	private List<BlockData> excludeList;
	private Material type;

	public ExcludeMatPerformer() {
		super("Exclude Material");
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxelList();
		message.blockDataType();
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.type = snipeData.getBlockDataType();
		this.excludeList = snipeData.getVoxelList();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (!this.excludeList.contains(blockData)) {
			this.undo.put(block);
			block.setType(this.type);
		}
	}
}
