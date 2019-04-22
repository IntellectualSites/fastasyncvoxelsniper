package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Ellipsoid_Brush
 */
public class EllipsoidBrush extends AbstractPerformerBrush {

	private double xRad;
	private double yRad;
	private double zRad;
	private boolean istrue;

	public EllipsoidBrush() {
		super("Ellipsoid");
	}

	private void execute(ToolkitProperties toolkitProperties, Block targetBlock) {
		this.performer.perform(targetBlock);
		double trueOffset = this.istrue ? 0.5 : 0;
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		for (double x = 0; x <= this.xRad; x++) {
			double xSquared = (x / (this.xRad + trueOffset)) * (x / (this.xRad + trueOffset));
			for (double z = 0; z <= this.zRad; z++) {
				double zSquared = (z / (this.zRad + trueOffset)) * (z / (this.zRad + trueOffset));
				for (double y = 0; y <= this.yRad; y++) {
					double ySquared = (y / (this.yRad + trueOffset)) * (y / (this.yRad + trueOffset));
					if (xSquared + ySquared + zSquared <= 1) {
						this.performer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
						this.performer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
						this.performer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
						this.performer.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
						this.performer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
						this.performer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
						this.performer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
						this.performer.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
					}
				}
			}
		}
		Sniper owner = toolkitProperties.getOwner();
		owner.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.execute(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.execute(toolkitProperties, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.custom(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xRad);
		messages.custom(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yRad);
		messages.custom(ChatColor.AQUA + "Z-size set to: " + ChatColor.DARK_AQUA + this.zRad);
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		this.istrue = false;
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					toolkitProperties.sendMessage(ChatColor.GOLD + "Ellipse brush parameters");
					toolkitProperties.sendMessage(ChatColor.AQUA + "x[n]: Set X radius to n");
					toolkitProperties.sendMessage(ChatColor.AQUA + "y[n]: Set Y radius to n");
					toolkitProperties.sendMessage(ChatColor.AQUA + "z[n]: Set Z radius to n");
					return;
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
					this.xRad = Integer.parseInt(parameters[i].replace("x", ""));
					toolkitProperties.sendMessage(ChatColor.AQUA + "X radius set to: " + this.xRad);
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
					this.yRad = Integer.parseInt(parameters[i].replace("y", ""));
					toolkitProperties.sendMessage(ChatColor.AQUA + "Y radius set to: " + this.yRad);
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'z') {
					this.zRad = Integer.parseInt(parameters[i].replace("z", ""));
					toolkitProperties.sendMessage(ChatColor.AQUA + "Z radius set to: " + this.zRad);
				} else if (parameter.equalsIgnoreCase("true")) {
					this.istrue = true;
				} else {
					toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
				}
			} catch (NumberFormatException exception) {
				toolkitProperties.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ellipsoid";
	}
}
