package com.thevoxelbox.voxelsniper.brush.property;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BrushProperties {

	private String name;
	@Nullable
	private String permission;
	private List<String> aliases;
	private BrushCreator creator;

	public static BrushPropertiesBuilder builder() {
		return new BrushPropertiesBuilder();
	}

	BrushProperties(String name, @Nullable String permission, List<String> aliases, BrushCreator creator) {
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
		this.creator = creator;
	}

	public String getName() {
		return this.name;
	}

	@Nullable
	public String getPermission() {
		return this.permission;
	}

	public List<String> getAliases() {
		return this.aliases;
	}

	public BrushCreator getCreator() {
		return this.creator;
	}
}
