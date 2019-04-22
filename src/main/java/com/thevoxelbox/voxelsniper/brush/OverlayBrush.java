package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.util.LegacyMaterialConverter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Overlay_.2F_Topsoil_Brush
 *
 * @author Gavjenks
 */
public class OverlayBrush extends PerformBrush {

	private static final int DEFAULT_DEPTH = 3;
	private int depth = DEFAULT_DEPTH;
	private boolean allBlocks;

	public OverlayBrush() {
		super("Overlay (Topsoil Filling)");
	}

	private void overlay(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				// check if column is valid
				// column is valid if it has no solid block right above the clicked layer
				Block targetBlock = this.getTargetBlock();
				Material material = this.getBlockType(targetBlock.getX() + x, targetBlock.getY() + 1, targetBlock.getZ() + z);
				if (isIgnoredBlock(material)) {
					if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) {
						for (int y = targetBlock.getY(); y > 0; y--) {
							// check for surface
							Material layerBlockType = this.getBlockType(targetBlock.getX() + x, y, targetBlock.getZ() + z);
							if (!isIgnoredBlock(layerBlockType)) {
								for (int currentDepth = y; y - currentDepth < this.depth; currentDepth--) {
									Material currentBlockType = this.getBlockType(targetBlock.getX() + x, currentDepth, targetBlock.getZ() + z);
									if (isOverrideableMaterial(currentBlockType)) {
										this.current.perform(this.clampY(targetBlock.getX() + x, currentDepth, targetBlock.getZ() + z));
									}
								}
								break;
							}
						}
					}
				}
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	private boolean isIgnoredBlock(Material material) {
		return material == Material.WATER || material.isTransparent() || material == Material.CACTUS;
	}

	private boolean isOverrideableMaterial(Material material) {
		if (this.allBlocks && !material.isEmpty()) {
			return true;
		}
		int id = LegacyMaterialConverter.getLegacyMaterialId(material);
		switch (id) {
			case 1:
			case 2:
			case 3:
			case 12:
			case 13:
			case 24:
			case 48:
			case 82:
			case 49:
			case 78:
				return true;
			default:
				return false;
		}
	}

	private void overlayTwo(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
		int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				boolean surfaceFound = false;
				Block targetBlock = this.getTargetBlock();
				for (int y = targetBlock.getY(); y > 0 && !surfaceFound; y--) { // start scanning from the height you clicked at
					if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
							int targetBlockX = targetBlock.getX();
							int targetBlockZ = targetBlock.getZ();
							if (!getBlockType(targetBlockX + x, y - 1, targetBlockZ + z).isEmpty()) { // if not a floating block (like one of Notch'world pools)
								if (getBlockType(targetBlockX + x, y + 1, targetBlockZ + z).isEmpty()) { // must start at surface... this prevents it filling stuff in if
									// you click in a wall and it starts out below surface.
									if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
										Material type = getBlockType(targetBlockX + x, y, targetBlockZ + z);
										switch (LegacyMaterialConverter.getLegacyMaterialId(type)) {
											case 1:
											case 2:
											case 3:
											case 12:
											case 13:
											case 14:
											case 15:
											case 16:
											case 24:
											case 48:
											case 82:
											case 49:
											case 78:
												for (int d = 1; (d < this.depth + 1); d++) {
													this.current.perform(this.clampY(targetBlockX + x, y + d, targetBlockZ + z)); // fills down as many layers as you specify
													// in parameters
													memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
												}
												surfaceFound = true;
												break;
											default:
												break;
										}
									} else {
										for (int d = 1; (d < this.depth + 1); d++) {
											this.current.perform(this.clampY(targetBlockX + x, y + d, targetBlockZ + z)); // fills down as many layers as you specify in
											// parameters
											memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
										}
										surfaceFound = true;
									}
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
		this.overlay(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.overlayTwo(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Overlay brush parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
				snipeData.sendMessage(ChatColor.BLUE + "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
				try {
					this.depth = Integer.parseInt(parameter.replace("d", ""));
					if (this.depth < 1) {
						this.depth = 1;
					}
					snipeData.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
				} catch (NumberFormatException e) {
					snipeData.sendMessage(ChatColor.RED + "Depth isn't a number.");
				}
			} else if (parameter.startsWith("all")) {
				this.allBlocks = true;
				snipeData.sendMessage(ChatColor.BLUE + "Will overlay over any block." + this.depth);
			} else if (parameter.startsWith("some")) {
				this.allBlocks = false;
				snipeData.sendMessage(ChatColor.BLUE + "Will overlay only natural block types." + this.depth);
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.overlay";
	}
}
