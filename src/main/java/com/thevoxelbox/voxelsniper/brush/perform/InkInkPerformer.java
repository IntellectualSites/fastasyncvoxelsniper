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
public class InkInkPerformer extends AbstractPerformer {

	private byte d;
	private byte dr;

	public InkInkPerformer() {
		this.setName("Ink-Ink");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.d = snipeData.getData();
		this.dr = snipeData.getReplaceData();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.data();
		message.replaceData();
	}

	@Override
	public void perform(Block block) {
		if (block.getData() == this.dr) {
			this.undo.put(block);
			block.setData(this.d, true);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
