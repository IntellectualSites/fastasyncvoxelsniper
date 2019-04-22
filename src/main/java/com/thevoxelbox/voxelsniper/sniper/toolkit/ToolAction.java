package com.thevoxelbox.voxelsniper.sniper.toolkit;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public enum ToolAction {

	ARROW,
	GUNPOWDER;

	@Nullable
	public static ToolAction getToolAction(String name) {
		return Arrays.stream(values())
			.filter(toolAction -> name.equalsIgnoreCase(toolAction.name()))
			.findFirst()
			.orElse(null);
	}
}
