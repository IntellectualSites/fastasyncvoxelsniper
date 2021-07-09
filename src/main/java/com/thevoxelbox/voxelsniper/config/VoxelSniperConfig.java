package com.thevoxelbox.voxelsniper.config;

import org.bukkit.Material;

import java.util.List;

public class VoxelSniperConfig {

	private boolean messageOnLoginEnabled;
	private int litesniperMaxBrushSize;
	private List<Material> litesniperRestrictedMaterials;

	public VoxelSniperConfig(boolean messageOnLoginEnabled, int litesniperMaxBrushSize, List<Material> litesniperRestrictedMaterials) {
		this.messageOnLoginEnabled = messageOnLoginEnabled;
		this.litesniperMaxBrushSize = litesniperMaxBrushSize;
		this.litesniperRestrictedMaterials = litesniperRestrictedMaterials;
	}

	public boolean isMessageOnLoginEnabled() {
		return this.messageOnLoginEnabled;
	}

	public int getLitesniperMaxBrushSize() {
		return this.litesniperMaxBrushSize;
	}

	public List<Material> getLitesniperRestrictedMaterials() {
		return this.litesniperRestrictedMaterials;
	}
}
