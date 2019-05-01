package com.thevoxelbox.voxelsniper.performer.type;

import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.sniper.Undo;

public abstract class AbstractPerformer implements Performer {

	private Undo undo;

	@Override
	public void initializeUndo() {
		this.undo = new Undo();
	}

	@Override
	public Undo getUndo() {
		return this.undo;
	}
}
