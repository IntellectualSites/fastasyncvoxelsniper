package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.Painter;
import org.bukkit.entity.Player;

/**
 * Painting scrolling Brush.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Painting_Picker_Brush
 *
 * @author Voxel
 */
public class PaintingBrush extends AbstractBrush {

	/**
	 * Scroll painting forward.
	 *
	 * @param snipe Sniper caller
	 */
	@Override
	public void handleArrowAction(Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		Painter.paint(player, true, false, 0);
	}

	/**
	 * Scroll painting backwards.
	 *
	 * @param snipe Sniper caller
	 */
	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		Painter.paint(player, true, true, 0);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
