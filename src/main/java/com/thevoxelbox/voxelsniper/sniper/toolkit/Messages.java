package com.thevoxelbox.voxelsniper.sniper.toolkit;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

@Deprecated
public class Messages {

	private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;

	private ToolkitProperties toolkitProperties;
	private CommandSender sender;

	public Messages(Snipe snipe) {
		this.toolkitProperties = snipe.getToolkitProperties();
		Sniper sniper = snipe.getSniper();
		this.sender = sniper.getPlayer();
	}

	/**
	 * Send a brush message styled message to the player.
	 */
	public void brushMessage(String brushMessage) {
		this.sender.sendMessage(ChatColor.LIGHT_PURPLE + brushMessage);
	}

	/**
	 * Display Brush Name.
	 */
	public void brushName(String brushName) {
		this.sender.sendMessage(ChatColor.AQUA + "Brush Type: " + ChatColor.LIGHT_PURPLE + brushName);
	}

	/**
	 * Display Center Parameter.
	 */
	public void center() {
		this.sender.sendMessage(ChatColor.DARK_BLUE + "Brush Center: " + ChatColor.DARK_RED + this.toolkitProperties.getCylinderCenter());
	}

	/**
	 * Display custom message.
	 */
	public void custom(String message) {
		this.sender.sendMessage(message);
	}

	/**
	 * Display data value.
	 */
	public void blockData() {
		BlockData blockData = this.toolkitProperties.getBlockData();
		this.sender.sendMessage(ChatColor.BLUE + "Data Variable: " + ChatColor.DARK_RED + blockData.getAsString(true));
	}

	/**
	 * Display voxel height.
	 */
	public void height() {
		this.sender.sendMessage(ChatColor.DARK_AQUA + "Brush Height: " + ChatColor.DARK_RED + this.toolkitProperties.getVoxelHeight());
	}

	/**
	 * Display performer.
	 */
	public void performerName(String performerName) {
		this.sender.sendMessage(ChatColor.DARK_PURPLE + "Performer: " + ChatColor.DARK_GREEN + performerName);
	}

	/**
	 * Display replace material.
	 */
	public void replaceBlockDataType() {
		Material replaceBlockDataType = this.toolkitProperties.getReplaceBlockType();
		this.sender.sendMessage(ChatColor.AQUA + "Replace Material: " + ChatColor.RED + replaceBlockDataType.getKey());
	}

	/**
	 * Display replace data value.
	 */
	public void replaceBlockData() {
		BlockData replaceBlockData = this.toolkitProperties.getReplaceBlockData();
		this.sender.sendMessage(ChatColor.DARK_GRAY + "Replace Data Variable: " + ChatColor.DARK_RED + replaceBlockData.getAsString(true));
	}

	/**
	 * Display brush size.
	 */
	public void size() {
		this.sender.sendMessage(ChatColor.GREEN + "Brush Size: " + ChatColor.DARK_RED + this.toolkitProperties.getBrushSize());
		if (this.toolkitProperties.getBrushSize() >= BRUSH_SIZE_WARNING_THRESHOLD) {
			this.sender.sendMessage(ChatColor.RED + "WARNING: Large brush size selected!");
		}
	}

	/**
	 * Display voxel type.
	 */
	public void blockDataType() {
		Material blockDataType = this.toolkitProperties.getBlockType();
		this.sender.sendMessage(ChatColor.GOLD + "Voxel: " + ChatColor.RED + blockDataType.getKey());
	}

	/**
	 * Display voxel list.
	 */
	public void voxelList() {
		if (this.toolkitProperties.getVoxelList()
			.isEmpty()) {
			this.sender.sendMessage(ChatColor.DARK_GREEN + "No blocks selected!");
		} else {
			StringBuilder returnValueBuilder = new StringBuilder();
			returnValueBuilder.append(ChatColor.DARK_GREEN);
			returnValueBuilder.append("Block Types Selected: ");
			returnValueBuilder.append(ChatColor.AQUA);
			for (BlockData blockData : this.toolkitProperties.getVoxelList()) {
				String dataAsString = blockData.getAsString(true);
				returnValueBuilder.append(dataAsString);
				returnValueBuilder.append(" ");
			}
			this.sender.sendMessage(returnValueBuilder.toString());
		}
	}
}
