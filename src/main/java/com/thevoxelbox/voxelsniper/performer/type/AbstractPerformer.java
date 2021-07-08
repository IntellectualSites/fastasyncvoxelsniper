package com.thevoxelbox.voxelsniper.performer.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public abstract class AbstractPerformer implements Performer {

	private Undo undo;

	public void setBlockType(EditSession editSession, int x, int y, int z, Material type) {
		try {
			editSession.setBlock(x, y, z, BukkitAdapter.asBlockType(type).getDefaultState());
		} catch (WorldEditException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBlockData(EditSession editSession, int x, int y, int z, BlockData blockData) {
		try {
			editSession.setBlock(x, y, z, BukkitAdapter.adapt(blockData));
		} catch (WorldEditException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initializeUndo() {
		this.undo = new Undo();
	}

	@Override
	public Undo getUndo() {
		return this.undo;
	}
}
