package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
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

	private void data(SnipeData snipeData) {
		Block targetBlock = this.getTargetBlock();
		Block block = this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		byte data = block.getData();
		snipeData.sendMessage(ChatColor.AQUA + "Blocks until repeater needed: " + data);
	}

	private void volt(SnipeData snipeData) {
		Block targetBlock = this.getTargetBlock();
		Block block = this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		boolean indirect = block.isBlockIndirectlyPowered();
		boolean direct = block.isBlockPowered();
		snipeData.sendMessage(ChatColor.AQUA + "Direct Power? " + direct + " Indirect Power? " + indirect);
		snipeData.sendMessage(ChatColor.BLUE + "Top Direct? " + block.isBlockFacePowered(BlockFace.UP) + " Top Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.UP));
		snipeData.sendMessage(ChatColor.BLUE + "Bottom Direct? " + block.isBlockFacePowered(BlockFace.DOWN) + " Bottom Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.DOWN));
		snipeData.sendMessage(ChatColor.BLUE + "East Direct? " + block.isBlockFacePowered(BlockFace.EAST) + " East Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.EAST));
		snipeData.sendMessage(ChatColor.BLUE + "West Direct? " + block.isBlockFacePowered(BlockFace.WEST) + " West Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.WEST));
		snipeData.sendMessage(ChatColor.BLUE + "North Direct? " + block.isBlockFacePowered(BlockFace.NORTH) + " North Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.NORTH));
		snipeData.sendMessage(ChatColor.BLUE + "South Direct? " + block.isBlockFacePowered(BlockFace.SOUTH) + " South Indirect? " + block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH));
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.volt(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.data(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.brushMessage("Right click with arrow to see if blocks/faces are powered. Powder measures wire current.");
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.voltmeter";
	}
}
