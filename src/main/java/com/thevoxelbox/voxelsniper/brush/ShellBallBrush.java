package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Shell_Brushes
 *
 * @author Voxel
 */
public class ShellBallBrush extends AbstractBrush {

	/**
	 *
	 */
	public ShellBallBrush() {
		super("Shell Ball");
	}

	// parameters isn't an abstract method, gilt. You can just leave it out if there are none.
	private void bShell(SnipeData v, Block targetBlock) {
		int brushSize = v.getBrushSize();
		int brushSizeDoubled = 2 * brushSize;
		int[][][] oldMaterials = new int[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1]; // Array that holds the original materials plus a buffer
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		// Log current materials into oldmats
		for (int x = 0; x <= 2 * (brushSize + 1); x++) {
			for (int y = 0; y <= 2 * (brushSize + 1); y++) {
				for (int z = 0; z <= 2 * (brushSize + 1); z++) {
					oldMaterials[x][y][z] = this.getBlockIdAt(blockPositionX - brushSize - 1 + x, blockPositionY - brushSize - 1 + y, blockPositionZ - brushSize - 1 + z);
				}
			}
		}
		// Log current materials into newmats
		// Array that holds the hollowed materials
		int[][][] newMaterials = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int y = 0; y <= brushSizeDoubled; y++) {
				System.arraycopy(oldMaterials[x + 1][y + 1], 1, newMaterials[x][y], 0, brushSizeDoubled + 1);
			}
		}
		// Hollow Brush Area
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int y = 0; y <= brushSizeDoubled; y++) {
				for (int z = 0; z <= brushSizeDoubled; z++) {
					int temp = 0;
					if (oldMaterials[x + 1 + 1][y + 1][z + 1] == v.getReplaceId()) {
						temp++;
					}
					if (oldMaterials[x + 1 - 1][y + 1][z + 1] == v.getReplaceId()) {
						temp++;
					}
					if (oldMaterials[x + 1][y + 1 + 1][z + 1] == v.getReplaceId()) {
						temp++;
					}
					if (oldMaterials[x + 1][y + 1 - 1][z + 1] == v.getReplaceId()) {
						temp++;
					}
					if (oldMaterials[x + 1][y + 1][z + 1 + 1] == v.getReplaceId()) {
						temp++;
					}
					if (oldMaterials[x + 1][y + 1][z + 1 - 1] == v.getReplaceId()) {
						temp++;
					}
					if (temp == 0) {
						newMaterials[x][y][z] = v.getVoxelId();
					}
				}
			}
		}
		// Make the changes
		Undo undo = new Undo();
		double rSquared = Math.pow(brushSize + 0.5, 2);
		for (int x = brushSizeDoubled; x >= 0; x--) {
			double xSquared = Math.pow(x - brushSize, 2);
			for (int y = 0; y <= 2 * brushSize; y++) {
				double ySquared = Math.pow(y - brushSize, 2);
				for (int z = 2 * brushSize; z >= 0; z--) {
					if (xSquared + ySquared + Math.pow(z - brushSize, 2) <= rSquared) {
						if (this.getBlockIdAt(blockPositionX - brushSize + x, blockPositionY - brushSize + y, blockPositionZ - brushSize + z) != newMaterials[x][y][z]) {
							undo.put(this.clampY(blockPositionX - brushSize + x, blockPositionY - brushSize + y, blockPositionZ - brushSize + z));
						}
						this.setBlockIdAt(blockPositionZ - brushSize + z, blockPositionX - brushSize + x, blockPositionY - brushSize + y, newMaterials[x][y][z]);
					}
				}
			}
		}
		v.getOwner()
			.storeUndo(undo);
		// This is needed because most uses of this brush will not be sible to the sniper.
		v.getOwner()
			.getPlayer()
			.sendMessage(ChatColor.AQUA + "Shell complete.");
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.bShell(snipeData, this.getTargetBlock());
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.bShell(snipeData, this.getLastBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.blockDataType();
		message.replaceBlockDataType();
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.shellball";
	}
}
