package com.thevoxelbox.voxelsniper.brush.type.blend;

import java.util.EnumMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendBallBrush extends AbstractBlendBrush {

	public BlendBallBrush() {
		super("Blend Ball");
	}

	@Override
	protected final void blend(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		// Array that holds the original materials plus a buffer
		Material[][][] oldMaterials = new Material[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1];
		// Array that holds the blended materials
		// Log current materials into oldmats
		Block targetBlock = getTargetBlock();
		for (int x = 0; x <= 2 * (brushSize + 1); x++) {
			for (int y = 0; y <= 2 * (brushSize + 1); y++) {
				for (int z = 0; z <= 2 * (brushSize + 1); z++) {
					oldMaterials[x][y][z] = this.getBlockType(targetBlock.getX() - brushSize - 1 + x, targetBlock.getY() - brushSize - 1 + y, targetBlock.getZ() - brushSize - 1 + z);
				}
			}
		}
		// Log current materials into newmats
		int brushSizeDoubled = 2 * brushSize;
		Material[][][] newMaterials = new Material[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int y = 0; y <= brushSizeDoubled; y++) {
				System.arraycopy(oldMaterials[x + 1][y + 1], 1, newMaterials[x][y], 0, brushSizeDoubled + 1);
			}
		}
		// Blend materials
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int y = 0; y <= brushSizeDoubled; y++) {
				for (int z = 0; z <= brushSizeDoubled; z++) {
					Map<Material, Integer> materialFrequency = new EnumMap<>(Material.class); //Map that tracks frequency of materials neighboring given block
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							for (int o = -1; o <= 1; o++) {
								if (!(m == 0 && n == 0 && o == 0)) {
									Material oldMaterial = oldMaterials[x + 1 + m][y + 1 + n][z + 1 + o];
									materialFrequency.computeIfPresent(oldMaterial, (key, value) -> value + 1);
								}
							}
						}
					}
					// Find most common neighboring material.
					Material mostCommonMaterial = null;
					int mostCommonFrequency = 0;
					for (Material material : BLOCKS) {
						int frequency = materialFrequency.getOrDefault(material, 0);
						if (frequency > mostCommonFrequency && !(this.excludeAir && material.isEmpty()) && !(this.excludeWater && material == Material.WATER)) {
							mostCommonFrequency = frequency;
							mostCommonMaterial = material;
						}
					}
					// Make sure there'world not a tie for most common
					boolean tieCheck = true;
					for (Material material : BLOCKS) {
						if (materialFrequency.getOrDefault(material, 0) == mostCommonFrequency && !(this.excludeAir && material.isEmpty()) && !(this.excludeWater && material == Material.WATER)) {
							tieCheck = false;
						}
					}
					// Record most common neighbor material for this block
					if (tieCheck && mostCommonMaterial != null) {
						newMaterials[x][y][z] = mostCommonMaterial;
					}
				}
			}
		}
		Undo undo = new Undo();
		double rSquared = Math.pow(brushSize + 1, 2);
		// Make the changes
		for (int x = brushSizeDoubled; x >= 0; x--) {
			double xSquared = Math.pow(x - brushSize - 1, 2);
			for (int y = 0; y <= brushSizeDoubled; y++) {
				double ySquared = Math.pow(y - brushSize - 1, 2);
				for (int z = brushSizeDoubled; z >= 0; z--) {
					if (xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
						Material material = newMaterials[x][y][z];
						if (!(this.excludeAir && material.isEmpty() || this.excludeWater && material == Material.WATER)) {
							if (getBlockType(targetBlock.getX() - brushSize + x, targetBlock.getY() - brushSize + y, targetBlock.getZ() - brushSize + z) != material) {
								undo.put(clampY(targetBlock.getX() - brushSize + x, targetBlock.getY() - brushSize + y, targetBlock.getZ() - brushSize + z));
							}
							setBlockType(targetBlock.getZ() - brushSize + z, targetBlock.getX() - brushSize + x, targetBlock.getY() - brushSize + y, material);
						}
					}
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(undo);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		if (parameters[1].equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
			snipeData.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default: exclude) water");
			return;
		}
		super.parameters(parameters, snipeData);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.blendball";
	}
}
