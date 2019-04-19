package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

/**
 * @author Gavjenks Heavily revamped from ruler brush blockPositionY
 * @author Giltwist
 * @author Monofraps (Merged Meteor brush)
 */
public class CometBrush extends AbstractBrush {

	private boolean useBigBalls;

	/**
	 *
	 */
	public CometBrush() {
		this.setName("Comet");
	}

	private void doFireball(SnipeData v) {
		Vector targetCoords = new Vector(this.getTargetBlock()
			.getX() + 0.5 * this.getTargetBlock()
			.getX() / Math.abs(this.getTargetBlock()
			.getX()), this.getTargetBlock()
			.getY() + 0.5, this.getTargetBlock()
			.getZ() + 0.5 * this.getTargetBlock()
			.getZ() / Math.abs(this.getTargetBlock()
			.getZ()));
		Location playerLocation = v.owner()
			.getPlayer()
			.getEyeLocation();
		Vector slope = targetCoords.subtract(playerLocation.toVector());
		if (this.useBigBalls) {
			v.owner()
				.getPlayer()
				.launchProjectile(LargeFireball.class)
				.setVelocity(slope.normalize());
		} else {
			v.owner()
				.getPlayer()
				.launchProjectile(SmallFireball.class)
				.setVelocity(slope.normalize());
		}
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 0; i < parameters.length; ++i) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage("Parameters:");
				snipeData.sendMessage("balls [big|small]  -- Sets your ball size.");
			}
			if (parameter.equalsIgnoreCase("balls")) {
				if (i + 1 >= parameters.length) {
					snipeData.sendMessage("The balls parameter expects a ball size after it.");
				}
				String newBallSize = parameters[++i];
				if (newBallSize.equalsIgnoreCase("big")) {
					this.useBigBalls = true;
					snipeData.sendMessage("Your balls are " + ChatColor.DARK_RED + ("BIG"));
				} else if (newBallSize.equalsIgnoreCase("small")) {
					this.useBigBalls = false;
					snipeData.sendMessage("Your balls are " + ChatColor.DARK_RED + ("small"));
				} else {
					snipeData.sendMessage("Unknown ball size.");
				}
			}
		}
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.doFireball(v);
	}

	@Override
	protected final void powder(SnipeData v) {
		this.doFireball(v);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.voxel();
		message.custom("Your balls are " + ChatColor.DARK_RED + (this.useBigBalls ? "BIG" : "small"));
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.comet";
	}
}
