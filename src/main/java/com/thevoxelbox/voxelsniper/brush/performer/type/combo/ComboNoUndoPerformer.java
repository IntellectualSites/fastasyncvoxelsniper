/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type.combo;

import com.thevoxelbox.voxelsniper.brush.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ComboNoUndoPerformer extends AbstractPerformer {

	private BlockData blockData;

	public ComboNoUndoPerformer() {
		super("Combo, No-Undo"); // made name more descriptive - Giltwist
	}

	@Override
	public void init(ToolkitProperties toolkitProperties) {
		this.world = toolkitProperties.getWorld();
		this.blockData = toolkitProperties.getBlockData();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockDataType();
		messages.blockData();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (blockData.equals(this.blockData)) {
			block.setBlockData(this.blockData);
		}
	}
}
