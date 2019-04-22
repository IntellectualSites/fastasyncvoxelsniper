package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.util.LegacyMaterialConverter;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Underlay_Brush
 *
 * @author jmck95 Credit to GavJenks for framework and 95 of code. Big Thank you to GavJenks
 */

public class UnderlayBrush extends AbstractPerformerBrush {

	private static final int DEFAULT_DEPTH = 3;

	private int depth = DEFAULT_DEPTH;
	private boolean allBlocks;

	public UnderlayBrush() {
		super("Underlay (Reverse Overlay)");
	}

	private void underlay(SnipeData snipeData) {
		int[][] memory = new int[snipeData.getBrushSize() * 2 + 1][snipeData.getBrushSize() * 2 + 1];
		double brushSizeSquared = Math.pow(snipeData.getBrushSize() + 0.5, 2);
		for (int z = snipeData.getBrushSize(); z >= -snipeData.getBrushSize(); z--) {
			for (int x = snipeData.getBrushSize(); x >= -snipeData.getBrushSize(); x--) {
				Block targetBlock = this.getTargetBlock();
				for (int y = targetBlock.getY(); y < targetBlock.getY() + this.depth; y++) { // start scanning from the height you clicked at
					if (memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] != 1) { // if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
							if (this.allBlocks) {
								for (int i = 0; i < this.depth; i++) {
									if (!clampY(targetBlock.getX() + x, y + i, targetBlock.getZ() + z).getType()
										.isEmpty()) {
										this.current.perform(clampY(targetBlock.getX() + x, y + i, targetBlock.getZ() + z)); // fills down as many layers as you specify in
										// parameters
										memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
									}
								}
							} else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
								switch (LegacyMaterialConverter.getLegacyMaterialId(getBlockType(targetBlock.getX() + x, y, targetBlock.getZ() + z))) {
									case 1:
									case 2:
									case 3:
									case 12:
									case 13:
									case 24:// These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess with.
									case 48:
									case 82:
									case 49:
									case 78:
										for (int i = 0; (i < this.depth); i++) {
											if (!clampY(targetBlock.getX() + x, y + i, targetBlock.getZ() + z).getType()
												.isEmpty()) {
												this.current.perform(this.clampY(targetBlock.getX() + x, y + i, targetBlock.getZ() + z)); // fills down as many layers as you specify in
												// parameters
												memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
											}
										}
										break;
									default:
										break;
								}
							}
						}
					}
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(this.current.getUndo());
	}

	private void underlay2(SnipeData snipeData) {
		int[][] memory = new int[snipeData.getBrushSize() * 2 + 1][snipeData.getBrushSize() * 2 + 1];
		double brushSizeSquared = Math.pow(snipeData.getBrushSize() + 0.5, 2);
		for (int z = snipeData.getBrushSize(); z >= -snipeData.getBrushSize(); z--) {
			for (int x = snipeData.getBrushSize(); x >= -snipeData.getBrushSize(); x--) {
				Block targetBlock = this.getTargetBlock();
				for (int y = targetBlock.getY(); y < targetBlock.getY() + this.depth; y++) { // start scanning from the height you clicked at
					if (memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] != 1) { // if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
							if (this.allBlocks) {
								for (int i = -1; i < this.depth - 1; i++) {
									this.current.perform(this.clampY(targetBlock.getX() + x, y - i, targetBlock.getZ() + z)); // fills down as many layers as you specify in
									// parameters
									memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
								}
							} else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
								switch (LegacyMaterialConverter.getLegacyMaterialId(getBlockType(targetBlock.getX() + x, y, targetBlock.getZ() + z))) {
									case 1:
									case 2:
									case 3:
									case 12:
									case 13:
									case 14: // These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess
										// with.
									case 15:
									case 16:
									case 24:
									case 48:
									case 82:
									case 49:
									case 78:
										for (int i = -1; i < this.depth - 1; i++) {
											this.current.perform(this.clampY(targetBlock.getX() + x, y - i, targetBlock.getZ() + z)); // fills down as many layers as you specify in
											// parameters
											memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
										}
										break;
									default:
										break;
								}
							}
						}
					}
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.underlay(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.underlay2(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int index = 1; index < parameters.length; index++) {
			String parameter = parameters[index];
			Sniper owner = snipeData.getOwner();
			if (parameter.equalsIgnoreCase("info")) {
				owner.sendMessages(ChatColor.GOLD + "Reverse Overlay brush parameters:", ChatColor.AQUA + "d[number] (ex: d3) The number of blocks thick to change.", ChatColor.BLUE + "all (ex: /b reover all) Sets the brush to affect ALL materials");
				if (this.depth < 1) {
					this.depth = 1;
				}
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
				this.depth = Integer.parseInt(parameter.replace("d", ""));
				owner.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
			} else if (parameter.startsWith("all")) {
				this.allBlocks = true;
				owner.sendMessage(ChatColor.BLUE + "Will underlay over any block." + this.depth);
			} else if (parameter.startsWith("some")) {
				this.allBlocks = false;
				owner.sendMessage(ChatColor.BLUE + "Will underlay only natural block types." + this.depth);
			} else {
				owner.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.underlay";
	}
}
