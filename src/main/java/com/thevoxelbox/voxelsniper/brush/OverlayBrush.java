package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Overlay_.2F_Topsoil_Brush
 *
 * @author Gavjenks
 */
public class OverlayBrush extends PerformBrush {

	private static final int DEFAULT_DEPTH = 3;
	private int depth = DEFAULT_DEPTH;
	private boolean allBlocks;

	/**
	 *
	 */
	public OverlayBrush() {
		this.setName("Overlay (Topsoil Filling)");
	}

	private void overlay(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				// check if column is valid
				// column is valid if it has no solid block right above the clicked layer
				int materialId = this.getBlockIdAt(this.getTargetBlock()
					.getX() + x, this.getTargetBlock()
					.getY() + 1, this.getTargetBlock()
					.getZ() + z);
				if (isIgnoredBlock(materialId)) {
					if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) {
						for (int y = this.getTargetBlock()
							.getY(); y > 0; y--) {
							// check for surface
							int layerBlockId = this.getBlockIdAt(this.getTargetBlock()
								.getX() + x, y, this.getTargetBlock()
								.getZ() + z);
							if (!isIgnoredBlock(layerBlockId)) {
								for (int currentDepth = y; y - currentDepth < this.depth; currentDepth--) {
									int currentBlockId = this.getBlockIdAt(this.getTargetBlock()
										.getX() + x, currentDepth, this.getTargetBlock()
										.getZ() + z);
									if (isOverrideableMaterial(currentBlockId)) {
										this.current.perform(this.clampY(this.getTargetBlock()
											.getX() + x, currentDepth, this.getTargetBlock()
											.getZ() + z));
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

	@SuppressWarnings("deprecation")
	private boolean isIgnoredBlock(int materialId) {
		return materialId == 9 || materialId == 8 || Material.getMaterial(materialId)
			.isTransparent() || materialId == Material.LEGACY_CACTUS.getId();
	}

	@SuppressWarnings("deprecation")
	private boolean isOverrideableMaterial(int materialId) {
		if (this.allBlocks && materialId != Material.LEGACY_AIR.getId()) {
			return true;
		}
		switch (materialId) {
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

	private void overlayTwo(SnipeData v) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
		int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				boolean surfaceFound = false;
				for (int y = this.getTargetBlock()
					.getY(); y > 0 && !surfaceFound; y--) { // start scanning from the height you clicked at
					if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
							if (this.getBlockIdAt(this.getTargetBlock()
								.getX() + x, y - 1, this.getTargetBlock()
								.getZ() + z) != 0) { // if not a floating block (like one of Notch'world pools)
								if (this.getBlockIdAt(this.getTargetBlock()
									.getX() + x, y + 1, this.getTargetBlock()
									.getZ() + z) == 0) { // must start at surface... this prevents it filling stuff in if
									// you click in a wall and it starts out below surface.
									if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
										switch (this.getBlockIdAt(this.getTargetBlock()
											.getX() + x, y, this.getTargetBlock()
											.getZ() + z)) {
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
													this.current.perform(this.clampY(this.getTargetBlock()
														.getX() + x, y + d, this.getTargetBlock()
														.getZ() + z)); // fills down as many layers as you specify
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
											this.current.perform(this.clampY(this.getTargetBlock()
												.getX() + x, y + d, this.getTargetBlock()
												.getZ() + z)); // fills down as many layers as you specify in
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
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.overlay(snipeData);
	}

	@Override
	protected final void powder(SnipeData snipeData) {
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
			if (parameter.startsWith("d")) {
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
