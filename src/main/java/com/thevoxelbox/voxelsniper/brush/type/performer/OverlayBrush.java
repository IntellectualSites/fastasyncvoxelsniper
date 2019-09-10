package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class OverlayBrush extends AbstractPerformerBrush {

	private static final int DEFAULT_DEPTH = 3;

	private int depth = DEFAULT_DEPTH;
	private boolean allBlocks;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Overlay brush parameters:");
				messenger.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
				messenger.sendMessage(ChatColor.BLUE + "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
				try {
					this.depth = Integer.parseInt(parameter.replace("d", ""));
					if (this.depth < 1) {
						this.depth = 1;
					}
					messenger.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
				} catch (NumberFormatException e) {
					messenger.sendMessage(ChatColor.RED + "Depth isn't a number.");
				}
			} else if (parameter.startsWith("all")) {
				this.allBlocks = true;
				messenger.sendMessage(ChatColor.BLUE + "Will overlay over any block." + this.depth);
			} else if (parameter.startsWith("some")) {
				this.allBlocks = false;
				messenger.sendMessage(ChatColor.BLUE + "Will overlay only natural block types." + this.depth);
			} else {
				messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		overlay(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		overlayTwo(snipe);
	}

	private void overlay(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				// check if column is valid
				// column is valid if it has no solid block right above the clicked layer
				Block targetBlock = getTargetBlock();
				Material material = getBlockType(targetBlock.getX() + x, targetBlock.getY() + 1, targetBlock.getZ() + z);
				if (isIgnoredBlock(material)) {
					if (Math.pow(x, 2) + Math.pow(z, 2) <= brushSizeSquared) {
						for (int y = targetBlock.getY(); y > 0; y--) {
							// check for surface
							Material layerBlockType = getBlockType(targetBlock.getX() + x, y, targetBlock.getZ() + z);
							if (!isIgnoredBlock(layerBlockType)) {
								for (int currentDepth = y; y - currentDepth < this.depth; currentDepth--) {
									Material currentBlockType = getBlockType(targetBlock.getX() + x, currentDepth, targetBlock.getZ() + z);
									if (isOverrideableMaterial(currentBlockType)) {
										this.performer.perform(clampY(targetBlock.getX() + x, currentDepth, targetBlock.getZ() + z));
									}
								}
								break;
							}
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@SuppressWarnings("deprecation")
	private boolean isIgnoredBlock(Material material) {
		return material == Material.WATER || material.isTransparent() || material == Material.CACTUS;
	}

	private boolean isOverrideableMaterial(Material material) {
		if (this.allBlocks && !Materials.isEmpty(material)) {
			return true;
		}
		return MaterialSets.OVERRIDEABLE.contains(material);
	}

	private void overlayTwo(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
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
							if (!Materials.isEmpty(getBlockType(targetBlockX + x, y - 1, targetBlockZ + z))) { // if not a floating block (like one of Notch'world pools)
								if (Materials.isEmpty(getBlockType(targetBlockX + x, y + 1, targetBlockZ + z))) { // must start at surface... this prevents it filling stuff in if
									// you click in a wall and it starts out below surface.
									if (this.allBlocks) {
										for (int index = 1; (index < this.depth + 1); index++) {
											this.performer.perform(this.clampY(targetBlockX + x, y + index, targetBlockZ + z)); // fills down as many layers as you specify in
											// parameters
											memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
										}
										surfaceFound = true;
									} else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
										Material type = getBlockType(targetBlockX + x, y, targetBlockZ + z);
										if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(type)) {
											for (int index = 1; (index < this.depth + 1); index++) {
												this.performer.perform(this.clampY(targetBlockX + x, y + index, targetBlockZ + z)); // fills down as many layers as you specify
												// in parameters
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
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.send();
	}
}
