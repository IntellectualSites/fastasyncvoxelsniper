package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class Messages {

	private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;

	private SnipeData snipeData;

	public Messages(SnipeData snipeData) {
		this.snipeData = snipeData;
	}

	/**
	 * Send a brush message styled message to the player.
	 */
	public void brushMessage(String brushMessage) {
		this.snipeData.sendMessage(ChatColor.LIGHT_PURPLE + brushMessage);
	}

	/**
	 * Display Brush Name.
	 */
	public void brushName(String brushName) {
		this.snipeData.sendMessage(ChatColor.AQUA + "Brush Type: " + ChatColor.LIGHT_PURPLE + brushName);
	}

	/**
	 * Display Center Parameter.
	 */
	public void center() {
		this.snipeData.sendMessage(ChatColor.DARK_BLUE + "Brush Center: " + ChatColor.DARK_RED + this.snipeData.getCylinderCenter());
	}

	/**
	 * Display custom message.
	 */
	public void custom(String message) {
		this.snipeData.sendMessage(message);
	}

	/**
	 * Display data value.
	 */
	public void blockData() {
		BlockData blockData = this.snipeData.getBlockData();
		this.snipeData.sendMessage(ChatColor.BLUE + "Data Variable: " + ChatColor.DARK_RED + blockData.getAsString(true));
	}

	/**
	 * Display voxel height.
	 */
	public void height() {
		this.snipeData.sendMessage(ChatColor.DARK_AQUA + "Brush Height: " + ChatColor.DARK_RED + this.snipeData.getVoxelHeight());
	}

	/**
	 * Display performer.
	 */
	public void performerName(String performerName) {
		this.snipeData.sendMessage(ChatColor.DARK_PURPLE + "Performer: " + ChatColor.DARK_GREEN + performerName);
	}

	/**
	 * Display replace material.
	 */
	public void replaceBlockDataType() {
		Material replaceBlockDataType = this.snipeData.getReplaceBlockDataType();
		this.snipeData.sendMessage(ChatColor.AQUA + "Replace Material: " + ChatColor.RED + replaceBlockDataType.getKey());
	}

	/**
	 * Display replace data value.
	 */
	public void replaceBlockData() {
		BlockData replaceBlockData = this.snipeData.getReplaceBlockData();
		this.snipeData.sendMessage(ChatColor.DARK_GRAY + "Replace Data Variable: " + ChatColor.DARK_RED + replaceBlockData.getAsString(true));
	}

	/**
	 * Display brush size.
	 */
	public void size() {
		this.snipeData.sendMessage(ChatColor.GREEN + "Brush Size: " + ChatColor.DARK_RED + this.snipeData.getBrushSize());
		if (this.snipeData.getBrushSize() >= BRUSH_SIZE_WARNING_THRESHOLD) {
			this.snipeData.sendMessage(ChatColor.RED + "WARNING: Large brush size selected!");
		}
	}

	/**
	 * Display toggle lightning message.
	 */
	public void toggleLightning() {
		Sniper owner = this.snipeData.getOwner();
		String currentToolId = owner.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = owner.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		this.snipeData.sendMessage(ChatColor.GOLD + "Lightning mode has been toggled " + ChatColor.DARK_RED + (snipeData.isLightningEnabled() ? "on" : "off"));
	}

	/**
	 * Display toggle printout message.
	 */
	public final void togglePrintout() {
		Sniper owner = this.snipeData.getOwner();
		String currentToolId = owner.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = owner.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		this.snipeData.sendMessage(ChatColor.GOLD + "Brush info printout mode has been toggled " + ChatColor.DARK_RED + (snipeData.isLightningEnabled() ? "on" : "off"));
	}

	/**
	 * Display toggle range message.
	 */
	public void toggleRange() {
		Sniper owner = this.snipeData.getOwner();
		String currentToolId = owner.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = owner.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		this.snipeData.sendMessage(ChatColor.GOLD + "Distance Restriction toggled " + ChatColor.DARK_RED + (snipeData.isRanged() ? "on" : "off") + ChatColor.GOLD + ". Range is " + ChatColor.LIGHT_PURPLE + snipeData.getRange());
	}

	/**
	 * Display voxel type.
	 */
	public void blockDataType() {
		Material blockDataType = this.snipeData.getBlockDataType();
		this.snipeData.sendMessage(ChatColor.GOLD + "Voxel: " + ChatColor.RED + blockDataType.getKey());
	}

	/**
	 * Display voxel list.
	 */
	public void voxelList() {
		if (this.snipeData.getVoxelList()
			.isEmpty()) {
			this.snipeData.sendMessage(ChatColor.DARK_GREEN + "No blocks selected!");
		} else {
			StringBuilder returnValueBuilder = new StringBuilder();
			returnValueBuilder.append(ChatColor.DARK_GREEN);
			returnValueBuilder.append("Block Types Selected: ");
			returnValueBuilder.append(ChatColor.AQUA);
			for (BlockData blockData : this.snipeData.getVoxelList()) {
				String dataAsString = blockData.getAsString(true);
				returnValueBuilder.append(dataAsString);
				returnValueBuilder.append(" ");
			}
			this.snipeData.sendMessage(returnValueBuilder.toString());
		}
	}
}
