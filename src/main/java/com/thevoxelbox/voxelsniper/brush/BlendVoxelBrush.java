package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Blend_Brushes
 */
public class BlendVoxelBrush extends BlendBrushBase {

	/**
	 *
	 */
	public BlendVoxelBrush() {
		this.setName("Blend Voxel");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected final void blend(SnipeData v) {
		int brushSize = v.getBrushSize();
		int brushSizeDoubled = 2 * brushSize;
		// Array that holds the original materials plus a buffer
		int[][][] oldMaterials = new int[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1];
		// Array that holds the blended materials
		// Log current materials into oldmats
		for (int x = 0; x <= 2 * (brushSize + 1); x++) {
			for (int y = 0; y <= 2 * (brushSize + 1); y++) {
				for (int z = 0; z <= 2 * (brushSize + 1); z++) {
					oldMaterials[x][y][z] = this.getBlockIdAt(this.getTargetBlock()
						.getX() - brushSize - 1 + x, this.getTargetBlock()
						.getY() - brushSize - 1 + y, this.getTargetBlock()
						.getZ() - brushSize - 1 + z);
				}
			}
		}
		// Log current materials into newmats
		int[][][] newMaterials = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int y = 0; y <= brushSizeDoubled; y++) {
				System.arraycopy(oldMaterials[x + 1][y + 1], 1, newMaterials[x][y], 0, brushSizeDoubled + 1);
			}
		}
		// Blend materials
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int y = 0; y <= brushSizeDoubled; y++) {
				for (int z = 0; z <= brushSizeDoubled; z++) {
					int[] materialFrequency = new int[BlendBrushBase.getMaxBlockMaterialID() + 1]; // Array that tracks frequency of materials neighboring given block
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							for (int o = -1; o <= 1; o++) {
								if (!(m == 0 && n == 0 && o == 0)) {
									materialFrequency[oldMaterials[x + 1 + m][y + 1 + n][z + 1 + o]]++;
								}
							}
						}
					}
					// Find most common neighboring material.
					int modeMatId = 0;
					int modeMatCount = 0;
					for (int i = 0; i <= BlendBrushBase.getMaxBlockMaterialID(); i++) {
						if (materialFrequency[i] > modeMatCount && !(this.excludeAir && i == Material.LEGACY_AIR.getId()) && !(this.excludeWater && (i == Material.LEGACY_WATER.getId() || i == Material.LEGACY_STATIONARY_WATER.getId()))) {
							modeMatCount = materialFrequency[i];
							modeMatId = i;
						}
					}
					// Make sure there'world not a tie for most common
					boolean tiecheck = true;
					for (int i = 0; i < modeMatId; i++) {
						if (materialFrequency[i] == modeMatCount && !(this.excludeAir && i == Material.LEGACY_AIR.getId()) && !(this.excludeWater && (i == Material.LEGACY_WATER.getId() || i == Material.LEGACY_STATIONARY_WATER.getId()))) {
							tiecheck = false;
						}
					}
					// Record most common neighbor material for this block
					if (tiecheck) {
						newMaterials[x][y][z] = modeMatId;
					}
				}
			}
		}
		Undo undo = new Undo();
		// Make the changes
		for (int x = brushSizeDoubled; x >= 0; x--) {
			for (int y = 0; y <= brushSizeDoubled; y++) {
				for (int z = brushSizeDoubled; z >= 0; z--) {
					if (!(this.excludeAir && newMaterials[x][y][z] == Material.LEGACY_AIR.getId()) && !(this.excludeWater && (newMaterials[x][y][z] == Material.LEGACY_WATER.getId() || newMaterials[x][y][z] == Material.LEGACY_STATIONARY_WATER.getId()))) {
						if (this.getBlockIdAt(this.getTargetBlock()
							.getX() - brushSize + x, this.getTargetBlock()
							.getY() - brushSize + y, this.getTargetBlock()
							.getZ() - brushSize + z) != newMaterials[x][y][z]) {
							undo.put(this.clampY(this.getTargetBlock()
								.getX() - brushSize + x, this.getTargetBlock()
								.getY() - brushSize + y, this.getTargetBlock()
								.getZ() - brushSize + z));
						}
						this.setBlockIdAt(this.getTargetBlock()
							.getZ() - brushSize + z, this.getTargetBlock()
							.getX() - brushSize + x, this.getTargetBlock()
							.getY() - brushSize + y, newMaterials[x][y][z]);
					}
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		if (parameters[1].equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "Blend Voxel Parameters:");
			snipeData.sendMessage(ChatColor.AQUA + "/b bv water -- toggle include or exclude (default) water");
			return;
		}
		super.parameters(parameters, snipeData);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.blendvoxel";
	}
}
