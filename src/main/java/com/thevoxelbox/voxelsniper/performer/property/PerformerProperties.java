package com.thevoxelbox.voxelsniper.performer.property;

import java.util.List;

public class PerformerProperties {

	private String name;
	private boolean usingReplaceMaterial;
	private List<String> aliases;
	private PerformerCreator creator;

	public static PerformerPropertiesBuilder builder() {
		return new PerformerPropertiesBuilder();
	}

	PerformerProperties(String name, boolean usingReplaceMaterial, List<String> aliases, PerformerCreator creator) {
		this.name = name;
		this.usingReplaceMaterial = usingReplaceMaterial;
		this.aliases = aliases;
		this.creator = creator;
	}

	public String getName() {
		return this.name;
	}

	public boolean isUsingReplaceMaterial() {
		return this.usingReplaceMaterial;
	}

	public List<String> getAliases() {
		return this.aliases;
	}

	public PerformerCreator getCreator() {
		return this.creator;
	}
}
