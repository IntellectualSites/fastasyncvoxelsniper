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
public class ComboComboNoPhysPerformer extends AbstractPerformer {

	private byte d;
	private byte dr;
	private int i;
	private int ir;

	public ComboComboNoPhysPerformer() {
		super("Combo-Combo No-Physics");
	}

	@Override
	public void init(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.world = snipeData.getWorld();
		this.d = snipeData.getData();
		this.dr = snipeData.getReplaceData();
		this.i = snipeData.getVoxelId();
		this.ir = snipeData.getReplaceId();
	}

	@Override
	public void info(Message message) {
		message.performerName(this.getName());
		message.voxel();
		message.replace();
		message.data();
		message.replaceData();
	}

	@Override
	public void perform(Block block) {
		if (block.getTypeId() == this.ir && block.getData() == this.dr) {
			this.undo.put(block);
			block.setTypeId(this.i, false);
			block.setData(this.d);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
