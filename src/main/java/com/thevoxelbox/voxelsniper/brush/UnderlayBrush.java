package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Underlay_Brush
 *
 * @author jmck95 Credit to GavJenks for framework and 95 of code. Big Thank you to GavJenks
 */

public class UnderlayBrush extends PerformBrush {

	private static final int DEFAULT_DEPTH = 3;
	private int depth = DEFAULT_DEPTH;
	private boolean allBlocks;

	/**
	 *
	 */
	public UnderlayBrush() {
		super("Underlay (Reverse Overlay)");
	}

	private void underlay(SnipeData snipeData) {
		int[][] memory = new int[snipeData.getBrushSize() * 2 + 1][snipeData.getBrushSize() * 2 + 1];
		double brushSizeSquared = Math.pow(snipeData.getBrushSize() + 0.5, 2);
		for (int z = snipeData.getBrushSize(); z >= -snipeData.getBrushSize(); z--) {
			for (int x = snipeData.getBrushSize(); x >= -snipeData.getBrushSize(); x--) {
				for (int y = this.getTargetBlock()
					.getY(); y < this.getTargetBlock()
					.getY() + this.depth; y++) { // start scanning from the height you clicked at
					if (memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] != 1) { // if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
							if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
								switch (this.getBlockIdAt(this.getTargetBlock()
									.getX() + x, y, this.getTargetBlock()
									.getZ() + z)) {
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
										for (int d = 0; (d < this.depth); d++) {
											if (this.clampY(this.getTargetBlock()
												.getX() + x, y + d, this.getTargetBlock()
												.getZ() + z)
												.getTypeId() != 0) {
												this.current.perform(this.clampY(this.getTargetBlock()
													.getX() + x, y + d, this.getTargetBlock()
													.getZ() + z)); // fills down as many layers as you specify in
												// parameters
												memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
											}
										}
										break;
									default:
										break;
								}
							} else {
								for (int d = 0; (d < this.depth); d++) {
									if (this.clampY(this.getTargetBlock()
										.getX() + x, y + d, this.getTargetBlock()
										.getZ() + z)
										.getTypeId() != 0) {
										this.current.perform(this.clampY(this.getTargetBlock()
											.getX() + x, y + d, this.getTargetBlock()
											.getZ() + z)); // fills down as many layers as you specify in
										// parameters
										memory[x + snipeData.getBrushSize()][z + snipeData.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
									}
								}
							}
						}
					}
				}
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	private void underlay2(SnipeData v) {
		int[][] memory = new int[v.getBrushSize() * 2 + 1][v.getBrushSize() * 2 + 1];
		double brushSizeSquared = Math.pow(v.getBrushSize() + 0.5, 2);
		for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
			for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
				for (int y = this.getTargetBlock()
					.getY(); y < this.getTargetBlock()
					.getY() + this.depth; y++) { // start scanning from the height you clicked at
					if (memory[x + v.getBrushSize()][z + v.getBrushSize()] != 1) { // if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
							if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
								switch (this.getBlockIdAt(this.getTargetBlock()
									.getX() + x, y, this.getTargetBlock()
									.getZ() + z)) {
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
										for (int d = -1; (d < this.depth - 1); d++) {
											this.current.perform(this.clampY(this.getTargetBlock()
												.getX() + x, y - d, this.getTargetBlock()
												.getZ() + z)); // fills down as many layers as you specify in
											// parameters
											memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
										}
										break;
									default:
										break;
								}
							} else {
								for (int d = -1; (d < this.depth - 1); d++) {
									this.current.perform(this.clampY(this.getTargetBlock()
										.getX() + x, y - d, this.getTargetBlock()
										.getZ() + z)); // fills down as many layers as you specify in
									// parameters
									memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
								}
							}
						}
					}
				}
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
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
