package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author Monofraps
 */

public abstract class BlendBrushBase extends AbstractBrush {

	protected static final Set<Material> BLOCKS = Arrays.stream(Material.values())
		.filter(Material::isBlock)
		.collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));

	protected boolean excludeAir = true;
	protected boolean excludeWater = true;

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
