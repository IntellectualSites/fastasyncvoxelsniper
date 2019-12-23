package com.thevoxelbox.voxelsniper.config;

import org.bukkit.Material;

import java.util.List;

public class VoxelSniperConfig {

	private int undoCacheSize;
	private boolean messageOnLoginEnabled;
	private int litesniperMaxBrushSize;
	private List<Material> litesniperRestrictedMaterials;

	public VoxelSniperConfig(int undoCacheSize, boolean messageOnLoginEnabled, int litesniperMaxBrushSize, List<Material> litesniperRestrictedMaterials) {
		this.undoCacheSize = undoCacheSize;
		this.messageOnLoginEnabled = messageOnLoginEnabled;
		this.litesniperMaxBrushSize = litesniperMaxBrushSize;
		this.litesniperRestrictedMaterials = litesniperRestrictedMaterials;
	}

	public int getUndoCacheSize() {
		return this.undoCacheSize;
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
