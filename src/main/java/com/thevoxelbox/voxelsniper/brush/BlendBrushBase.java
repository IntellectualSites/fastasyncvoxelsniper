package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * @author Monofraps
 */

public abstract class BlendBrushBase extends AbstractBrush {

	private static int maxBlockMaterialID;
	protected boolean excludeAir = true;
	protected boolean excludeWater = true;

	static {
		// Find highest placeable block ID
		for (Material material : Material.values()) {
			maxBlockMaterialID = ((material.isBlock() && (material.getId() > maxBlockMaterialID)) ? material.getId() : maxBlockMaterialID);
		}
	}

	/**
	 *
	 */
	protected abstract void blend(SnipeData snipeData);

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.excludeAir = false;
		this.blend(snipeData);
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.excludeAir = true;
		this.blend(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.voxel();
		message.custom(ChatColor.BLUE + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
	}

	@Override
	public void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; ++i) {
			if (parameters[i].equalsIgnoreCase("water")) {
				this.excludeWater = !this.excludeWater;
				snipeData.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
			}
		}
	}

	/**
	 *
	 */
	protected static int getMaxBlockMaterialID() {
		return maxBlockMaterialID;
	}

	/**
	 *
	 */
	protected static void setMaxBlockMaterialID(int maxBlockMaterialID) {
		BlendBrushBase.maxBlockMaterialID = maxBlockMaterialID;
	}

	/**
	 *
	 */
	protected final boolean isExcludeAir() {
		return this.excludeAir;
	}

	/**
	 *
	 */
	protected final void setExcludeAir(boolean excludeAir) {
		this.excludeAir = excludeAir;
	}

	/**
	 *
	 */
	protected final boolean isExcludeWater() {
		return this.excludeWater;
	}

	/**
	 *
	 */
	protected final void setExcludeWater(boolean excludeWater) {
		this.excludeWater = excludeWater;
	}
}
