/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ExcludeInkPerformer extends AbstractPerformer {

	private List<BlockData> excludeList;
	private BlockData blockData;

	public ExcludeInkPerformer() {
		super("Exclude Ink");
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxelList();
		message.blockData();
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.blockData = snipeData.getBlockData();
		this.excludeList = snipeData.getVoxelList();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (!this.excludeList.contains(blockData)) {
			this.undo.put(block);
			block.setBlockData(this.blockData);
		}
	}
}
