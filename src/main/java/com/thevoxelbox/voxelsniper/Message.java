package com.thevoxelbox.voxelsniper;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Message {

	private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;

	private SnipeData snipeData;

	public Message(SnipeData snipeData) {
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
	public void data() {
		this.snipeData.sendMessage(ChatColor.BLUE + "Data Variable: " + ChatColor.DARK_RED + this.snipeData.getData());
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
	 * Displaye replace material.
	 */

	public void replace() {
		this.snipeData.sendMessage(ChatColor.AQUA + "Replace Material: " + ChatColor.RED + this.snipeData.getReplaceId() + ChatColor.GRAY + " (" + Material.getMaterial(this.snipeData.getReplaceId()) + ")");
	}

	/**
	 * Display replace data value.
	 */
	public void replaceData() {
		this.snipeData.sendMessage(ChatColor.DARK_GRAY + "Replace Data Variable: " + ChatColor.DARK_RED + this.snipeData.getReplaceData());
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
		this.snipeData.sendMessage(ChatColor.GOLD + "Lightning mode has been toggled " + ChatColor.DARK_RED + ((owner.getSnipeData(currentToolId)
			.isLightningEnabled()) ? "on" : "off"));
	}

	/**
	 * Display toggle printout message.
	 */
	public final void togglePrintout() {
		Sniper owner = this.snipeData.getOwner();
		String currentToolId = owner.getCurrentToolId();
		this.snipeData.sendMessage(ChatColor.GOLD + "Brush info printout mode has been toggled " + ChatColor.DARK_RED + ((owner.getSnipeData(currentToolId)
			.isLightningEnabled()) ? "on" : "off"));
	}

	/**
	 * Display toggle range message.
	 */
	public void toggleRange() {
		Sniper owner = this.snipeData.getOwner();
		String currentToolId = owner.getCurrentToolId();
		this.snipeData.sendMessage(ChatColor.GOLD + "Distance Restriction toggled " + ChatColor.DARK_RED + ((owner.getSnipeData(currentToolId)
			.isRanged()) ? "on" : "off") + ChatColor.GOLD + ". Range is " + ChatColor.LIGHT_PURPLE + (double) owner.getSnipeData(currentToolId)
			.getRange());
	}

	/**
	 * Display voxel type.
	 */

	public void voxel() {
		this.snipeData.sendMessage(ChatColor.GOLD + "Voxel: " + ChatColor.RED + this.snipeData.getVoxelId() + ChatColor.GRAY + " (" + Material.getMaterial(this.snipeData.getVoxelId()) + ")");
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
			for (int[] valuePair : this.snipeData.getVoxelList()
				.getList()) {
				returnValueBuilder.append(valuePair[0]);
				if (valuePair[1] != -1) {
					returnValueBuilder.append(":");
					returnValueBuilder.append(valuePair[1]);
				}
				returnValueBuilder.append(" ");
			}
			this.snipeData.sendMessage(returnValueBuilder.toString());
		}
	}
}
