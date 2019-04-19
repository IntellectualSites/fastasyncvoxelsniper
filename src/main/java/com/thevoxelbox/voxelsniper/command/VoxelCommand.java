package com.thevoxelbox.voxelsniper.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class VoxelCommand {

	private String name;
	private String identifier;
	@Nullable
	private String permission;

	public VoxelCommand(String name, String identifier) {
		this(name, identifier, null);
	}

	public VoxelCommand(String name, String identifier, @Nullable String permission) {
		this.name = name;
		this.permission = permission;
		this.identifier = identifier;
	}

	public abstract boolean onCommand(Player sender, String[] args);

	public String getName() {
		return this.name;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	@Nullable
	public String getPermission() {
		return this.permission;
	}
}
