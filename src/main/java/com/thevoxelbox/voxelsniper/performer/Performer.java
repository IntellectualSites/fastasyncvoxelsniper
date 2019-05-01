package com.thevoxelbox.voxelsniper.performer;

import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public interface Performer {

	void info(Messages messages);

	void init(ToolkitProperties toolkitProperties);

	void setUndo();

	void perform(Block block);

	@Nullable Undo getUndo();

	boolean isUsingReplaceMaterial();

	String getName();

	void setName(String name);
}
