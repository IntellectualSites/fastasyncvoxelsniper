package com.thevoxelbox.voxelsniper.performer;

import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import org.bukkit.block.Block;

public interface Performer {

	void initialize(PerformerSnipe snipe);

	void perform(Block block);

	void sendInfo(PerformerSnipe snipe);

	void initializeUndo();

	Undo getUndo();
}
