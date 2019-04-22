package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.util.Painter;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.entity.Player;

/**
 * Painting scrolling Brush.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Painting_Picker_Brush
 *
 * @author Voxel
 */
public class PaintingBrush extends AbstractBrush {

	public PaintingBrush() {
		super("Painting");
	}

	/**
	 * Scroll painting forward.
	 *
	 * @param snipeData Sniper caller
	 */
	@Override
	public final void arrow(SnipeData snipeData) {
		Sniper owner = snipeData.getOwner();
		Player player = owner.getPlayer();
		if (player == null) {
			return;
		}
		Painter.paint(player, true, false, 0);
	}

	/**
	 * Scroll painting backwards.
	 *
	 * @param snipeData Sniper caller
	 */
	@Override
	public final void powder(SnipeData snipeData) {
		Sniper owner = snipeData.getOwner();
		Player player = owner.getPlayer();
		if (player == null) {
			return;
		}
		Painter.paint(player, true, true, 0);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.painting";
	}
}
