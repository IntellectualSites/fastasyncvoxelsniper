/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class MaterialNoPhysicsPerformer extends AbstractPerformer {

	private Material material;

	public MaterialNoPhysicsPerformer() {
		super("Set, No-Physics");
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.material = snipeData.getBlockDataType();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockDataType();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() != this.material) {
			this.undo.put(block);
			block.setType(this.material, false);
		}
	}
}
