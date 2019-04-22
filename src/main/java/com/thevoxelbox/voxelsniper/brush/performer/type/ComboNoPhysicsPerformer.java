/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ComboNoPhysicsPerformer extends AbstractPerformer {

	private BlockData blockData;

	public ComboNoPhysicsPerformer() {
		super("Combo NoPhysics");
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.blockData = snipeData.getBlockData();
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
		block.setBlockData(this.blockData, false);
	}
}
