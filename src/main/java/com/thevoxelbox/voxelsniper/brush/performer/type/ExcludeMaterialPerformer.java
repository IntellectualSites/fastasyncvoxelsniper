/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import java.util.List;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ExcludeMaterialPerformer extends AbstractPerformer {

	private List<BlockData> excludeList;
	private Material type;

	public ExcludeMaterialPerformer() {
		super("Exclude Material");
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.voxelList();
		messages.blockDataType();
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
