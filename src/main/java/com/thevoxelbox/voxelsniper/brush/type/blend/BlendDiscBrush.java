package com.thevoxelbox.voxelsniper.brush.type.blend;

import java.util.EnumMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendDiscBrush extends AbstractBlendBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[1].equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.GOLD + "Blend Disc Parameters:");
			messenger.sendMessage(ChatColor.AQUA + "/b bd water -- toggle include or exclude (default) water");
			return;
		}
		super.handleCommand(parameters, snipe);
	}

	@Override
	public void blend(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		Material[][] oldMaterials = new Material[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1]; // Array that holds the original materials plus a buffer
		// Log current materials into oldmats
		Block targetBlock = this.getTargetBlock();
		for (int x = 0; x <= 2 * (brushSize + 1); x++) {
			for (int z = 0; z <= 2 * (brushSize + 1); z++) {
				oldMaterials[x][z] = this.getBlockType(targetBlock.getX() - brushSize - 1 + x, targetBlock.getY(), targetBlock.getZ() - brushSize - 1 + z);
			}
		}
		// Log current materials into newmats
		// Array that holds the blended materials
		int brushSizeDoubled = 2 * brushSize;
		Material[][] newMaterials = new Material[brushSizeDoubled + 1][brushSizeDoubled + 1];
		for (int x = 0; x <= brushSizeDoubled; x++) {
			System.arraycopy(oldMaterials[x + 1], 1, newMaterials[x], 0, brushSizeDoubled + 1);
		}
		// Blend materials
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int z = 0; z <= brushSizeDoubled; z++) {
				Map<Material, Integer> materialFrequency = new EnumMap<>(Material.class); //Map that tracks frequency of materials neighboring given block
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						if (!(m == 0 && n == 0)) {
							Material oldMaterial = oldMaterials[x + 1 + m][z + 1 + n];
							materialFrequency.computeIfPresent(oldMaterial, (key, value) -> value + 1);
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
					newMaterials[x][z] = mostCommonMaterial;
				}
			}
		}
		Undo undo = new Undo();
		double rSquared = Math.pow(brushSize + 1, 2);
		// Make the changes
		for (int x = brushSizeDoubled; x >= 0; x--) {
			double xSquared = Math.pow(x - brushSize - 1, 2);
			for (int z = brushSizeDoubled; z >= 0; z--) {
				if (xSquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
					Material material = newMaterials[x][z];
					if (!(this.excludeAir && material.isEmpty() || this.excludeWater && material == Material.WATER)) {
						if (this.getBlockType(targetBlock.getX() - brushSize + x, targetBlock.getY(), targetBlock.getZ() - brushSize + z) != material) {
							undo.put(this.clampY(targetBlock.getX() - brushSize + x, targetBlock.getY(), targetBlock.getZ() - brushSize + z));
						}
						setBlockType(targetBlock.getZ() - brushSize + z, targetBlock.getX() - brushSize + x, targetBlock.getY(), material);
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
