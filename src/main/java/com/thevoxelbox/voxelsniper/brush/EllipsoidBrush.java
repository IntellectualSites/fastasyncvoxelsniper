package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Ellipsoid_Brush
 */
public class EllipsoidBrush extends PerformBrush {

	private double xRad;
	private double yRad;
	private double zRad;
	private boolean istrue;

	/**
	 *
	 */
	public EllipsoidBrush() {
		super("Ellipsoid");
	}

	private void execute(SnipeData v, Block targetBlock) {
		this.current.perform(targetBlock);
		double istrueoffset = this.istrue ? 0.5 : 0;
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		for (double x = 0; x <= this.xRad; x++) {
			double xSquared = (x / (this.xRad + istrueoffset)) * (x / (this.xRad + istrueoffset));
			for (double z = 0; z <= this.zRad; z++) {
				double zSquared = (z / (this.zRad + istrueoffset)) * (z / (this.zRad + istrueoffset));
				for (double y = 0; y <= this.yRad; y++) {
					double ySquared = (y / (this.yRad + istrueoffset)) * (y / (this.yRad + istrueoffset));
					if (xSquared + ySquared + zSquared <= 1) {
						this.current.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
						this.current.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
						this.current.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
						this.current.perform(this.clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
						this.current.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
						this.current.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
						this.current.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
						this.current.perform(this.clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
					}
				}
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.execute(snipeData, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.execute(snipeData, this.getLastBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xRad);
		message.custom(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yRad);
		message.custom(ChatColor.AQUA + "Z-size set to: " + ChatColor.DARK_AQUA + this.zRad);
	}

	@Override
	public final void parameters(String[] parameters, com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		this.istrue = false;
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					snipeData.sendMessage(ChatColor.GOLD + "Ellipse brush parameters");
					snipeData.sendMessage(ChatColor.AQUA + "x[n]: Set X radius to n");
					snipeData.sendMessage(ChatColor.AQUA + "y[n]: Set Y radius to n");
					snipeData.sendMessage(ChatColor.AQUA + "z[n]: Set Z radius to n");
					return;
				} else if (parameter.startsWith("x")) {
					this.xRad = Integer.parseInt(parameters[i].replace("x", ""));
					snipeData.sendMessage(ChatColor.AQUA + "X radius set to: " + this.xRad);
				} else if (parameter.startsWith("y")) {
					this.yRad = Integer.parseInt(parameters[i].replace("y", ""));
					snipeData.sendMessage(ChatColor.AQUA + "Y radius set to: " + this.yRad);
				} else if (parameter.startsWith("z")) {
					this.zRad = Integer.parseInt(parameters[i].replace("z", ""));
					snipeData.sendMessage(ChatColor.AQUA + "Z radius set to: " + this.zRad);
				} else if (parameter.equalsIgnoreCase("true")) {
					this.istrue = true;
				} else {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
				}
			} catch (NumberFormatException exception) {
				snipeData.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ellipsoid";
	}
}
