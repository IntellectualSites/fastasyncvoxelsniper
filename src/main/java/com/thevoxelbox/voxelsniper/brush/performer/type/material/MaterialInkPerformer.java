/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type.material;

import com.thevoxelbox.voxelsniper.brush.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class MaterialInkPerformer extends AbstractPerformer {

	private BlockData blockData;
	private BlockData replaceBlockData;

	public MaterialInkPerformer() {
		super("Material-Ink");
	}

	@Override
	public void init(ToolkitProperties toolkitProperties) {
		this.world = toolkitProperties.getWorld();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceBlockData = toolkitProperties.getReplaceBlockData();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockDataType();
		messages.replaceBlockData();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (blockData.equals(this.replaceBlockData)) {
			this.undo.put(block);
			block.setType(this.blockData.getMaterial(), true);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
