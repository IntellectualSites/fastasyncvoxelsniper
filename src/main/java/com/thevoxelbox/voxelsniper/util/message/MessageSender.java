package com.thevoxelbox.voxelsniper.util.message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

public class MessageSender {

	private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;

	private CommandSender sender;
	private List<String> messages = new ArrayList<>(0);

	public MessageSender(CommandSender sender) {
		this.sender = sender;
	}

	public MessageSender brushNameMessage(String brushName) {
		this.messages.add(ChatColor.AQUA + "Brush Type: " + ChatColor.LIGHT_PURPLE + brushName);
		return this;
	}

	public MessageSender performerNameMessage(String performerName) {
		this.messages.add(ChatColor.DARK_PURPLE + "Performer: " + ChatColor.DARK_GREEN + performerName);
		return this;
	}

	public MessageSender blockTypeMessage(Material blockType) {
		this.messages.add(ChatColor.GOLD + "Voxel: " + ChatColor.RED + blockType.getKey());
		return this;
	}

	public MessageSender blockDataMessage(BlockData blockData) {
		this.messages.add(ChatColor.BLUE + "Data Variable: " + ChatColor.DARK_RED + blockData.getAsString(true));
		return this;
	}

	public MessageSender replaceBlockTypeMessage(Material replaceBlockType) {
		this.messages.add(ChatColor.AQUA + "Replace Material: " + ChatColor.RED + replaceBlockType.getKey());
		return this;
	}

	public MessageSender replaceBlockDataMessage(BlockData replaceBlockData) {
		this.messages.add(ChatColor.DARK_GRAY + "Replace Data Variable: " + ChatColor.DARK_RED + replaceBlockData.getAsString(true));
		return this;
	}

	public MessageSender brushSizeMessage(int brushSize) {
		this.messages.add(ChatColor.GREEN + "Brush Size: " + ChatColor.DARK_RED + brushSize);
		if (brushSize >= BRUSH_SIZE_WARNING_THRESHOLD) {
			this.messages.add(ChatColor.RED + "WARNING: Large brush size selected!");
		}
		return this;
	}

	public MessageSender cylinderCenterMessage(int cylinderCenter) {
		this.messages.add(ChatColor.DARK_BLUE + "Brush Center: " + ChatColor.DARK_RED + cylinderCenter);
		return this;
	}

	public MessageSender voxelHeightMessage(int voxelHeight) {
		this.messages.add(ChatColor.DARK_AQUA + "Brush Height: " + ChatColor.DARK_RED + voxelHeight);
		return this;
	}

	public MessageSender voxelListMessage(List<? extends BlockData> voxelList) {
		if (voxelList.isEmpty()) {
			this.messages.add(ChatColor.DARK_GREEN + "No blocks selected!");
		}
		String message = voxelList.stream()
			.map(blockData -> blockData.getAsString(true))
			.map(dataAsString -> dataAsString + " ")
			.collect(Collectors.joining("", ChatColor.DARK_GREEN + "Block Types Selected: " + ChatColor.AQUA, ""));
		this.messages.add(message);
		return this;
	}

	public MessageSender message(String message) {
		this.messages.add(message);
		return this;
	}

	public void send() {
		this.messages.forEach(this.sender::sendMessage);
	}
}
