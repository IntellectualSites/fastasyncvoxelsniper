package com.thevoxelbox.voxelsniper.brush.type.blend;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

class CommonMaterial {

	@Nullable
	private Material material;
	private int frequency;

	@Nullable
	public Material getMaterial() {
		return this.material;
	}

	public void setMaterial(@Nullable Material material) {
		this.material = material;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
