package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author Monofraps
 */

public abstract class BlendBrushBase extends AbstractBrush {

	@Deprecated
	protected static final int MAX_BLOCK_MATERIAL_ID;

	protected boolean excludeAir = true;
	protected boolean excludeWater = true;

	static {
		// Find highest placeable block ID
		int a = -1;
		for (Material material : Material.values()) {
			a = material.isBlock() && material.getId() > a ? material.getId() : a;
		}
		MAX_BLOCK_MATERIAL_ID = a;
	}

	public BlendBrushBase(String name) {
		super(name);
	}

	protected abstract void blend(SnipeData snipeData);

	@Override
	public final void arrow(SnipeData snipeData) {
		this.excludeAir = false;
		this.blend(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.excludeAir = true;
		this.blend(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.blockDataType();
		message.custom(ChatColor.BLUE + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
	}

	@Override
	public void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; ++i) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("water")) {
				this.excludeWater = !this.excludeWater;
				snipeData.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
			}
		}
	}

	protected final boolean isExcludeAir() {
		return this.excludeAir;
	}

	protected final void setExcludeAir(boolean excludeAir) {
		this.excludeAir = excludeAir;
	}

	protected final boolean isExcludeWater() {
		return this.excludeWater;
	}

	protected final void setExcludeWater(boolean excludeWater) {
		this.excludeWater = excludeWater;
	}
}
