package com.thevoxelbox.voxelsniper.sniper.snipe.message;

import java.util.List;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class SnipeMessenger {

	private ToolkitProperties toolkitProperties;
	private BrushProperties brushProperties;
	private Messenger messenger;

	public SnipeMessenger(ToolkitProperties toolkitProperties, BrushProperties brushProperties, Player player) {
		this.toolkitProperties = toolkitProperties;
		this.brushProperties = brushProperties;
		this.messenger = new Messenger(player);
	}

	public void sendBrushNameMessage() {
		String brushName = this.brushProperties.getName();
		this.messenger.sendBrushNameMessage(brushName);
	}

	public void sendBlockTypeMessage() {
		Material blockType = this.toolkitProperties.getBlockType();
		this.messenger.sendBlockTypeMessage(blockType);
	}

	public void sendBlockDataMessage() {
		BlockData blockData = this.toolkitProperties.getBlockData();
		this.messenger.sendBlockDataMessage(blockData);
	}

	public void sendReplaceBlockTypeMessage() {
		Material replaceBlockType = this.toolkitProperties.getReplaceBlockType();
		this.messenger.sendReplaceBlockTypeMessage(replaceBlockType);
	}

	public void sendReplaceBlockDataMessage() {
		BlockData replaceBlockData = this.toolkitProperties.getReplaceBlockData();
		this.messenger.sendReplaceBlockDataMessage(replaceBlockData);
	}

	public void sendBrushSizeMessage() {
		int brushSize = this.toolkitProperties.getBrushSize();
		this.messenger.sendBrushSizeMessage(brushSize);
	}

	public void sendCylinderCenterMessage() {
		int cylinderCenter = this.toolkitProperties.getCylinderCenter();
		this.messenger.sendCylinderCenterMessage(cylinderCenter);
	}

	public void sendVoxelHeightMessage() {
		int voxelHeight = this.toolkitProperties.getVoxelHeight();
		this.messenger.sendVoxelHeightMessage(voxelHeight);
	}

	public void sendVoxelListMessage() {
		List<BlockData> voxelList = this.toolkitProperties.getVoxelList();
		this.messenger.sendVoxelListMessage(voxelList);
	}

	public void sendMessage(String message) {
		this.messenger.sendMessage(message);
	}

	public Messenger getMessenger() {
		return this.messenger;
	}
}
