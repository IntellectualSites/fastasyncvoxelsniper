/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.performer.type;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Voxel
 */
public class ComboMaterialPerformer extends AbstractPerformer {

	private BlockData blockData;
	private BlockData replaceBlockData;

	public ComboMaterialPerformer() {
		super("Combo-Material");
	}

	@Override
	public void init(SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.blockData = snipeData.getBlockData();
		this.replaceBlockData = snipeData.getReplaceBlockData();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockDataType();
		messages.replaceBlockDataType();
		messages.blockData();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() == this.replaceBlockData.getMaterial()) {
			this.undo.put(block);
			block.setBlockData(this.blockData);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
