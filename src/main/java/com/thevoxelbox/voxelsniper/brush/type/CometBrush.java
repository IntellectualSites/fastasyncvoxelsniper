package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

/**
 * @author Gavjenks Heavily revamped from ruler brush blockPositionY
 * @author Giltwist
 * @author Monofraps (Merged Meteor brush)
 */
public class CometBrush extends AbstractBrush {

	private boolean useBigBalls;

	public CometBrush() {
		super("Comet");
	}

	private void doFireball(ToolkitProperties toolkitProperties) {
		Block targetBlock = getTargetBlock();
		int x = targetBlock.getX();
		int y = targetBlock.getY();
		int z = targetBlock.getZ();
		Vector targetCoordinates = new Vector(x + 0.5 * x / Math.abs(x), y + 0.5, z + 0.5 * z / Math.abs(z));
		Sniper owner = toolkitProperties.getOwner();
		Player player = owner.getPlayer();
		if (player == null) {
			return;
		}
		Location playerLocation = player.getEyeLocation();
		Vector slope = targetCoordinates.subtract(playerLocation.toVector());
		Vector normalizedSlope = slope.normalize();
		if (this.useBigBalls) {
			LargeFireball fireball = player.launchProjectile(LargeFireball.class);
			fireball.setVelocity(normalizedSlope);
		} else {
			SmallFireball fireball = player.launchProjectile(SmallFireball.class);
			fireball.setVelocity(normalizedSlope);
		}
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 0; i < parameters.length; ++i) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				toolkitProperties.sendMessage("Parameters:");
				toolkitProperties.sendMessage("balls [big|small]  -- Sets your ball size.");
			}
			if (parameter.equalsIgnoreCase("balls")) {
				if (i + 1 >= parameters.length) {
					toolkitProperties.sendMessage("The balls parameter expects a ball size after it.");
				}
				String newBallSize = parameters[++i];
				if (newBallSize.equalsIgnoreCase("big")) {
					this.useBigBalls = true;
					toolkitProperties.sendMessage("Your balls are " + ChatColor.DARK_RED + ("BIG"));
				} else if (newBallSize.equalsIgnoreCase("small")) {
					this.useBigBalls = false;
					toolkitProperties.sendMessage("Your balls are " + ChatColor.DARK_RED + ("small"));
				} else {
					toolkitProperties.sendMessage("Unknown ball size.");
				}
			}
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.doFireball(toolkitProperties);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.doFireball(toolkitProperties);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.blockDataType();
		messages.custom("Your balls are " + ChatColor.DARK_RED + (this.useBigBalls ? "BIG" : "small"));
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.comet";
	}
}
