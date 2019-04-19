/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * @author Voxel
 */
public abstract class AbstractPerformer implements Performer {

	private String name = "Performer";
	@Nullable
	protected Undo undo;
	protected World world;

	@Override
	public void setUndo() {
		this.undo = new Undo();
	}

	@Override
	@Nullable
	public Undo getUndo() {
		Undo temp = this.undo;
		this.undo = null;
		return temp;
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return false;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
