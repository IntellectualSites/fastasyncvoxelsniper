/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * @author Voxel
 */
public abstract class vPerformer {

	private String name = "Performer";
	@Nullable
	protected Undo h;
	protected World world;

	public abstract void info(Message vm);

	public abstract void init(com.thevoxelbox.voxelsniper.SnipeData v);

	public void setUndo() {
		this.h = new Undo();
	}

	public abstract void perform(Block block);

	@Nullable
	public Undo getUndo() {
		Undo temp = this.h;
		this.h = null;
		return temp;
	}

	public boolean isUsingReplaceMaterial() {
		return false;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
