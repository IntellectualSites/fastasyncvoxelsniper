/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class IncludeMatPerformer extends AbstractPerformer {

	private List<BlockData> includeList;
	private Material type;

	public IncludeMatPerformer() {
		super("Include Material");
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
		this.includeList = snipeData.getVoxelList();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (this.includeList.contains(blockData)) {
			this.undo.put(block);
			block.setType(this.type);
		}
	}
}
