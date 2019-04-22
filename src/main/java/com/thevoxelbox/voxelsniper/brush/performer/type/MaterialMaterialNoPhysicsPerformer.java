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
public class MaterialMaterialNoPhysicsPerformer extends AbstractPerformer {

	private Material material;
	private Material replaceMaterial;

	public MaterialMaterialNoPhysicsPerformer() {
		super("Material-Material No-Physics");
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.material = snipeData.getBlockDataType();
		this.replaceMaterial = snipeData.getReplaceBlockDataType();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.blockDataType();
		message.replaceBlockDataType();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() == this.replaceMaterial) {
			this.undo.put(block);
			block.setType(this.material, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
