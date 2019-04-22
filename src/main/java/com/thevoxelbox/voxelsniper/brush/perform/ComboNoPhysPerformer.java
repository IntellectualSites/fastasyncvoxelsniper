/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ComboNoPhysPerformer extends AbstractPerformer {

	private BlockData blockData;

	public ComboNoPhysPerformer() {
		super("Combo NoPhysics");
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.blockData = snipeData.getBlockData();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.blockDataType();
		message.blockData();
	}

	@Override
	public void perform(Block block) {
		this.undo.put(block);
		block.setBlockData(this.blockData, false);
	}
}
