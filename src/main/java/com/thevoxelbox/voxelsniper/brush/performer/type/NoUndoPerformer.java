/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
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
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.material = snipeData.getBlockDataType();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.blockDataType();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() != this.material) {
			block.setType(this.material);
		}
	}
}
