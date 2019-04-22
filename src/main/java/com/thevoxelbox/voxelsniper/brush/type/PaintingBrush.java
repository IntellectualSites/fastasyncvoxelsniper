package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Painter;
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
	 * @param toolkitProperties Sniper caller
	 */
	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		Sniper owner = toolkitProperties.getOwner();
		Player player = owner.getPlayer();
		if (player == null) {
			return;
		}
		Painter.paint(player, true, false, 0);
	}

	/**
	 * Scroll painting backwards.
	 *
	 * @param toolkitProperties Sniper caller
	 */
	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Sniper owner = toolkitProperties.getOwner();
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
