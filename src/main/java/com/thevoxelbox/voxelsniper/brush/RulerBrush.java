package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ruler_Brush
 *
 * @author Gavjenks
 */
public class RulerBrush extends AbstractBrush {

	private boolean first = true;
	private Vector coords = new Vector(0, 0, 0);

	private int xOff;
	private int yOff;
	private int zOff;

	/**
	 *
	 */
	public RulerBrush() {
		this.setName("Ruler");
	}

	@Override
	protected final void arrow(SnipeData v) {
		int voxelMaterialId = v.getVoxelId();
		this.coords = this.getTargetBlock()
			.getLocation()
			.toVector();
		if (this.xOff == 0 && this.yOff == 0 && this.zOff == 0) {
			v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
			this.first = !this.first;
		} else {
			Undo undo = new Undo();
			undo.put(this.clampY(this.getTargetBlock()
				.getX() + this.xOff, this.getTargetBlock()
				.getY() + this.yOff, this.getTargetBlock()
				.getZ() + this.zOff));
			this.setBlockIdAt(this.getTargetBlock()
				.getZ() + this.zOff, this.getTargetBlock()
				.getX() + this.xOff, this.getTargetBlock()
				.getY() + this.yOff, voxelMaterialId);
			v.getOwner()
				.storeUndo(undo);
		}
	}

	@Override
	protected final void powder(SnipeData v) {
		if (this.coords == null || this.coords.lengthSquared() == 0) {
			v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow. Comparing to point 0,0,0 instead.");
			return;
		}
		v.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
		v.sendMessage(ChatColor.AQUA + "X change: " + (this.getTargetBlock()
			.getX() - this.coords.getX()));
		v.sendMessage(ChatColor.AQUA + "Y change: " + (this.getTargetBlock()
			.getY() - this.coords.getY()));
		v.sendMessage(ChatColor.AQUA + "Z change: " + (this.getTargetBlock()
			.getZ() - this.coords.getZ()));
		double distance = Math.round(this.getTargetBlock()
			.getLocation()
			.toVector()
			.subtract(this.coords)
			.length() * 100) / 100.0;
		double blockDistance = Math.round((Math.abs(Math.max(Math.max(Math.abs(this.getTargetBlock()
			.getX() - this.coords.getX()), Math.abs(this.getTargetBlock()
			.getY() - this.coords.getY())), Math.abs(this.getTargetBlock()
			.getZ() - this.coords.getZ()))) + 1) * 100) / 100.0;
		v.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance);
		v.sendMessage(ChatColor.AQUA + "Block distance = " + blockDistance);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.voxel();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
				snipeData.sendMessage(ChatColor.LIGHT_PURPLE + "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
				snipeData.sendMessage(ChatColor.BLUE + "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");
				return;
			} else if (parameter.startsWith("x")) {
				this.xOff = Integer.parseInt(parameter.replace("x", ""));
				snipeData.sendMessage(ChatColor.AQUA + "X offset set to " + this.xOff);
			} else if (parameter.startsWith("y")) {
				this.yOff = Integer.parseInt(parameter.replace("y", ""));
				snipeData.sendMessage(ChatColor.AQUA + "Y offset set to " + this.yOff);
			} else if (parameter.startsWith("z")) {
				this.zOff = Integer.parseInt(parameter.replace("z", ""));
				snipeData.sendMessage(ChatColor.AQUA + "Z offset set to " + this.zOff);
			} else if (parameter.startsWith("ruler")) {
				this.zOff = 0;
				this.yOff = 0;
				this.xOff = 0;
				snipeData.sendMessage(ChatColor.BLUE + "Ruler mode.");
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ruler";
	}
}
