package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
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

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int index = 1; index < parameters.length; index++) {
			String parameter = parameters[index];
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Ruler Brush instructions: Right click first point with the arrow. Right click with powder for distances from that block (can repeat without getting a new first block.) For placing blocks, use arrow and input the desired coordinates with parameters.");
				messenger.sendMessage(ChatColor.LIGHT_PURPLE + "/b r x[x value] y[y value] z[z value] -- Will place blocks one at a time of the type you have set with /v at the location you click + this many units away.  If you don't include a value, it will be zero.  Don't include ANY values, and the brush will just measure distance.");
				messenger.sendMessage(ChatColor.BLUE + "/b r ruler -- will reset the tool to just measure distances, not layout blocks.");
				return;
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
				this.offsetX = Integer.parseInt(parameter.replace("x", ""));
				messenger.sendMessage(ChatColor.AQUA + "X offset set to " + this.offsetX);
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
				this.offsetY = Integer.parseInt(parameter.replace("y", ""));
				messenger.sendMessage(ChatColor.AQUA + "Y offset set to " + this.offsetY);
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'z') {
				this.offsetZ = Integer.parseInt(parameter.replace("z", ""));
				messenger.sendMessage(ChatColor.AQUA + "Z offset set to " + this.offsetZ);
			} else if (parameter.startsWith("ruler")) {
				this.offsetZ = 0;
				this.offsetY = 0;
				this.offsetX = 0;
				messenger.sendMessage(ChatColor.BLUE + "Ruler mode.");
			} else {
				messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		Material blockDataType = toolkitProperties.getBlockType();
		Block targetBlock = getTargetBlock();
		Location location = targetBlock.getLocation();
		this.coordinates = location.toVector();
		if (this.offsetX == 0 && this.offsetY == 0 && this.offsetZ == 0) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
			this.first = !this.first;
		} else {
			Undo undo = new Undo();
			int x = targetBlock.getX();
			int y = targetBlock.getY();
			int z = targetBlock.getZ();
			undo.put(clampY(x + this.offsetX, y + this.offsetY, z + this.offsetZ));
			setBlockType(z + this.offsetZ, x + this.offsetX, y + this.offsetY, blockDataType);
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(undo);
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (this.coordinates == null || this.coordinates.lengthSquared() == 0) {
			messenger.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow. Comparing to point 0,0,0 instead.");
			return;
		}
		messenger.sendMessage(ChatColor.BLUE + "Format = (second coord - first coord)");
		Block targetBlock = getTargetBlock();
		messenger.sendMessage(ChatColor.AQUA + "X change: " + (targetBlock.getX() - this.coordinates.getX()));
		messenger.sendMessage(ChatColor.AQUA + "Y change: " + (targetBlock.getY() - this.coordinates.getY()));
		messenger.sendMessage(ChatColor.AQUA + "Z change: " + (targetBlock.getZ() - this.coordinates.getZ()));
		Location location = targetBlock.getLocation();
		double distance = Math.round(location.toVector()
			.subtract(this.coordinates)
			.length() * 100) / 100.0;
		double blockDistance = Math.round((Math.abs(Math.max(Math.max(Math.abs(targetBlock.getX() - this.coordinates.getX()), Math.abs(targetBlock.getY() - this.coordinates.getY())), Math.abs(targetBlock.getZ() - this.coordinates.getZ()))) + 1) * 100) / 100.0;
		messenger.sendMessage(ChatColor.AQUA + "Euclidean distance = " + distance);
		messenger.sendMessage(ChatColor.AQUA + "Block distance = " + blockDistance);
	}

	@Override
	public final void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBlockTypeMessage();
	}
}
