/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import org.bukkit.block.Block;

/**
 * @author Voxel
 */
public class ComboInkNoPhysPerformer extends AbstractPerformer {

	private byte d;
	private byte dr;
	private int i;

	public ComboInkNoPhysPerformer() {
		super("Combo-Ink, No Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.d = snipeData.getData();
		this.dr = snipeData.getReplaceData();
		this.i = snipeData.getVoxelId();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.blockDataType();
		message.blockData();
		message.replaceBlockData();
	}

	@Override
	public void perform(Block block) {
		if (block.getData() == this.dr) {
			this.undo.put(block);
			block.setTypeIdAndData(this.i, this.d, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
