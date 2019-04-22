package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public interface Performer {

	void info(Message message);

	void init(SnipeData snipeData);

	void setUndo();

	void perform(Block block);

	@Nullable Undo getUndo();

	boolean isUsingReplaceMaterial();

	String getName();

	void setName(String name);
}
