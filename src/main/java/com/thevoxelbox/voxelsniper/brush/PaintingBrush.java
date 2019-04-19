package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.PaintingWrapper;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * Painting scrolling Brush.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Painting_Picker_Brush
 *
 * @author Voxel
 */
public class PaintingBrush extends AbstractBrush {

	/**
	 *
	 */
	public PaintingBrush() {
		this.setName("Painting");
	}

	/**
	 * Scroll painting forward.
	 *
	 * @param snipeData Sniper caller
	 */
	@Override
	protected final void arrow(SnipeData snipeData) {
		PaintingWrapper.paint(snipeData.getOwner()
			.getPlayer(), true, false, 0);
	}

	/**
	 * Scroll painting backwards.
	 *
	 * @param snipeData Sniper caller
	 */
	@Override
	protected final void powder(SnipeData snipeData) {
		PaintingWrapper.paint(snipeData.getOwner()
			.getPlayer(), true, true, 0);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.painting";
	}
}
