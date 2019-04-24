/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type.material;

import com.thevoxelbox.voxelsniper.brush.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
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
	public void init(ToolkitProperties toolkitProperties) {
		this.world = toolkitProperties.getWorld();
		this.material = toolkitProperties.getBlockDataType();
		this.replaceMaterial = toolkitProperties.getReplaceBlockDataType();
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
