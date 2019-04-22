package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Ruler_Brush
 *
 * @author Gavjenks
 */
public class RulerBrush extends AbstractBrush {

	private boolean first = true;
	private Vector coordinates = new Vector(0, 0, 0);
	private int offsetX;
	private int offsetY;
	private int offsetZ;

	public RulerBrush() {
		super("Ruler");
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		Material blockDataType = snipeData.getBlockDataType();
		Block targetBlock = getTargetBlock();
		Location location = targetBlock.getLocation();
		this.coordinates = location.toVector();
		if (this.offsetX == 0 && this.offsetY == 0 && this.offsetZ == 0) {
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
			this.first = !this.first;
		} else {
			Undo undo = new Undo();
			int x = targetBlock.getX();
			int y = targetBlock.getY();
			int z = targetBlock.getZ();
			undo.put(clampY(x + this.offsetX, y + this.offsetY, z + this.offsetZ));
			setBlockType(z + this.offsetZ, x + this.offsetX, y + this.offsetY, blockDataType);
			Sniper owner = snipeData.getOwner();
			owner.storeUndo(undo);
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		if (this.coordinates == null || this.coordinates.lengthSquared() == 0) {
			snipeData.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow. Comparing to point 0,0,0 instead.");
			return;
		}
		snipeData.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
		Block targetBlock = getTargetBlock();
		snipeData.sendMessage(ChatColor.AQUA + "X change: " + (targetBlock.getX() - this.coordinates.getX()));
		snipeData.sendMessage(ChatColor.AQUA + "Y change: " + (targetBlock.getY() - this.coordinates.getY()));
		snipeData.sendMessage(ChatColor.AQUA + "Z change: " + (targetBlock.getZ() - this.coordinates.getZ()));
		Location location = targetBlock.getLocation();
		double distance = Math.round(location.toVector()
			.subtract(this.coordinates)
			.length() * 100) / 100.0;
		double blockDistance = Math.round((Math.abs(Math.max(Math.max(Math.abs(targetBlock.getX() - this.coordinates.getX()), Math.abs(targetBlock.getY() - this.coordinates.getY())), Math.abs(targetBlock.getZ() - this.coordinates.getZ()))) + 1) * 100) / 100.0;
		snipeData.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance);
		snipeData.sendMessage(ChatColor.AQUA + "Block distance = " + blockDistance);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.blockDataType();
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
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
				this.offsetX = Integer.parseInt(parameter.replace("x", ""));
				snipeData.sendMessage(ChatColor.AQUA + "X offset set to " + this.offsetX);
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
				this.offsetY = Integer.parseInt(parameter.replace("y", ""));
				snipeData.sendMessage(ChatColor.AQUA + "Y offset set to " + this.offsetY);
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'z') {
				this.offsetZ = Integer.parseInt(parameter.replace("z", ""));
				snipeData.sendMessage(ChatColor.AQUA + "Z offset set to " + this.offsetZ);
			} else if (parameter.startsWith("ruler")) {
				this.offsetZ = 0;
				this.offsetY = 0;
				this.offsetX = 0;
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
