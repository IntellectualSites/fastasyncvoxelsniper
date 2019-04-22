package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author MikeMatrix
 */
public class WarpBrush extends AbstractBrush {

	public WarpBrush() {
		super("Warp");
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		Sniper owner = snipeData.getOwner();
		Player player = owner.getPlayer();
		if (player == null) {
			return;
		}
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Location location = lastBlock.getLocation();
		Location playerLocation = player.getLocation();
		location.setPitch(playerLocation.getPitch());
		location.setYaw(playerLocation.getYaw());
		player.teleport(location);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Sniper owner = snipeData.getOwner();
		Player player = owner.getPlayer();
		if (player == null) {
			return;
		}
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Location location = lastBlock.getLocation();
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
