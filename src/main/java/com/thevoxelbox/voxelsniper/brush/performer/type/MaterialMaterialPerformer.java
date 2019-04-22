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
public class MaterialMaterialPerformer extends AbstractPerformer {

	private Material material;
	private Material replaceMaterial;

	public MaterialMaterialPerformer() {
		super("Material-Material");
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.material = snipeData.getBlockDataType();
		this.replaceMaterial = snipeData.getReplaceBlockDataType();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockDataType();
		messages.replaceBlockDataType();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() == this.replaceMaterial) {
			this.undo.put(block);
			block.setType(this.material);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
