package com.thevoxelbox.voxelsniper;

import java.util.HashMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.jetbrains.annotations.Nullable;

public class CommandRegistry {

	private Map<String, VoxelCommand> commands = new HashMap<>();

	public void registerCommand(VoxelCommand command) {
		String identifier = command.getIdentifier();
		String identifierLowered = identifier.toLowerCase();
		this.commands.put(identifierLowered, command);
	}

	@Nullable
	public VoxelCommand getCommand(String identifier) {
		return this.commands.get(identifier);
	}
}
