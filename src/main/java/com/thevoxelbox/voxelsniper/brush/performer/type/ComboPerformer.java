/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ComboPerformer extends AbstractPerformer {

	private BlockData blockData;

	public ComboPerformer() {
		super("Combo");
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
		this.undo.put(block);
		block.setBlockData(this.blockData);
	}
}
