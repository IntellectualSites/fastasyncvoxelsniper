package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Volt-Meter_Brush
 *
 * @author Gavjenks
 */
public class VoltMeterBrush extends AbstractBrush {

	public VoltMeterBrush() {
		super("VoltMeter");
	}

	private void data(ToolkitProperties toolkitProperties) {
		Block targetBlock = this.getTargetBlock();
		Block block = this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		byte data = block.getData();
		toolkitProperties.sendMessage(ChatColor.AQUA + "Blocks until repeater needed: " + data);
	}

	private void volt(ToolkitProperties toolkitProperties) {
		Block targetBlock = this.getTargetBlock();
		Block block = this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		boolean indirect = block.isBlockIndirectlyPowered();
		boolean direct = block.isBlockPowered();
		toolkitProperties.sendMessage(ChatColor.AQUA + "Direct Power? " + direct + " Indirect Power? " + indirect);
		toolkitProperties.sendMessage(ChatColor.BLUE + "Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.UP));
		toolkitProperties.sendMessage(ChatColor.BLUE + "Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
		toolkitProperties.sendMessage(ChatColor.BLUE + "East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
		toolkitProperties.sendMessage(ChatColor.BLUE + "West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
		toolkitProperties.sendMessage(ChatColor.BLUE + "North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
		toolkitProperties.sendMessage(ChatColor.BLUE + "South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.volt(toolkitProperties);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.data(toolkitProperties);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voltmeter";
	}
}
