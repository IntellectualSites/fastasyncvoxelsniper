/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class NoUndoPerformer extends AbstractPerformer {

	private Material material;

	public NoUndoPerformer() {
		super("BOMB SQUAD");
	}

	@Override
	public void init(ToolkitProperties toolkitProperties) {
		this.world = toolkitProperties.getWorld();
		this.material = toolkitProperties.getBlockDataType();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockDataType();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() != this.material) {
			block.setType(this.material);
		}
	}
}
