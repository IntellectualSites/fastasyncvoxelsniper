package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author MikeMatrix
 */
public class WarpBrush extends AbstractBrush {

	/**
	 *
	 */
	public WarpBrush() {
		this.setName("Warp");
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		Player player = snipeData.getOwner()
			.getPlayer();
		Location location = this.getLastBlock()
			.getLocation();
		Location playerLocation = player.getLocation();
		location.setPitch(playerLocation.getPitch());
		location.setYaw(playerLocation.getYaw());
		player.teleport(location);
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		Player player = snipeData.getOwner()
			.getPlayer();
		Location location = this.getLastBlock()
			.getLocation();
		Location playerLocation = player.getLocation();
		location.setPitch(playerLocation.getPitch());
		location.setYaw(playerLocation.getYaw());
		getWorld().strikeLightning(location);
		player.teleport(location);
		getWorld().strikeLightning(location);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.warp";
	}
}
