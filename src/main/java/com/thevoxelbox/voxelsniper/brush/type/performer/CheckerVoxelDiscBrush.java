package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author MikeMatrix
 */
public class CheckerVoxelDiscBrush extends AbstractPerformerBrush {

	private boolean useWorldCoordinates = true;

	/**
	 * Default constructor.
	 */
	public CheckerVoxelDiscBrush() {
		super("Checker Voxel Disc");
	}

	private void applyBrush(ToolkitProperties v, Block target) {
		for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
			for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
				int sum = this.useWorldCoordinates ? target.getX() + x + target.getZ() + y : x + y;
				if (sum % 2 != 0) {
					this.performer.perform(this.clampY(target.getX() + x, target.getY(), target.getZ() + y));
				}
			}
		}
		v.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.applyBrush(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.applyBrush(toolkitProperties, this.getLastBlock());
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.size();
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int x = 1; x < parameters.length; x++) {
			String parameter = parameters[x].toLowerCase();
			if (parameter.equals("info")) {
				toolkitProperties.sendMessage(ChatColor.GOLD + this.getName() + " Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "true  -- Enables using World Coordinates.");
				toolkitProperties.sendMessage(ChatColor.AQUA + "false -- Disables using World Coordinates.");
				return;
			}
			if (parameter.startsWith("true")) {
				this.useWorldCoordinates = true;
				toolkitProperties.sendMessage(ChatColor.AQUA + "Enabled using World Coordinates.");
			} else if (parameter.startsWith("false")) {
				this.useWorldCoordinates = false;
				toolkitProperties.sendMessage(ChatColor.AQUA + "Disabled using World Coordinates.");
			} else {
				toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
				break;
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.checkervoxeldisc";
	}
}
