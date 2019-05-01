package com.thevoxelbox.voxelsniper.brush.type.shell;

import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * THIS BRUSH SHOULD NOT USE PERFORMERS.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Shell_Brushes
 *
 * @author Voxel
 */
public class ShellVoxelBrush extends AbstractBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[1].equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.GOLD + "Shell Voxel Parameters:");
		} else {
			messenger.sendMessage(ChatColor.RED + "Invalid parameter - see the info message for help.");
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		vShell(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		vShell(snipe, lastBlock);
	}

	private void vShell(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		Material[][][] oldMaterials = new Material[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1]; // Array that holds the original materials plus a  buffer
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		// Log current materials into oldmats
		for (int x = 0; x <= 2 * (brushSize + 1); x++) {
			for (int y = 0; y <= 2 * (brushSize + 1); y++) {
				for (int z = 0; z <= 2 * (brushSize + 1); z++) {
					oldMaterials[x][y][z] = getBlockType(blockPositionX - brushSize - 1 + x, blockPositionY - brushSize - 1 + y, blockPositionZ - brushSize - 1 + z);
				}
			}
		}
		// Log current materials into newmats
		// Array that holds the hollowed materials
		Material[][][] newMaterials = new Material[2 * brushSize + 1][2 * brushSize + 1][2 * brushSize + 1];
		int brushSizeSquared = 2 * brushSize;
		for (int x = 0; x <= brushSizeSquared; x++) {
			for (int y = 0; y <= brushSizeSquared; y++) {
				System.arraycopy(oldMaterials[x + 1][y + 1], 1, newMaterials[x][y], 0, brushSizeSquared + 1);
			}
		}
		// Hollow Brush Area
		for (int x = 0; x <= brushSizeSquared; x++) {
			for (int z = 0; z <= brushSizeSquared; z++) {
				for (int y = 0; y <= brushSizeSquared; y++) {
					int temp = 0;
					if (oldMaterials[x + 1 + 1][z + 1][y + 1] == toolkitProperties.getReplaceBlockType()) {
						temp++;
					}
					if (oldMaterials[x + 1 - 1][z + 1][y + 1] == toolkitProperties.getReplaceBlockType()) {
						temp++;
					}
					if (oldMaterials[x + 1][z + 1 + 1][y + 1] == toolkitProperties.getReplaceBlockType()) {
						temp++;
					}
					if (oldMaterials[x + 1][z + 1 - 1][y + 1] == toolkitProperties.getReplaceBlockType()) {
						temp++;
					}
					if (oldMaterials[x + 1][z + 1][y + 1 + 1] == toolkitProperties.getReplaceBlockType()) {
						temp++;
					}
					if (oldMaterials[x + 1][z + 1][y + 1 - 1] == toolkitProperties.getReplaceBlockType()) {
						temp++;
					}
					if (temp == 0) {
						newMaterials[x][z][y] = toolkitProperties.getBlockType();
					}
				}
			}
		}
		// Make the changes
		Undo undo = new Undo();
		for (int x = brushSizeSquared; x >= 0; x--) {
			for (int y = 0; y <= brushSizeSquared; y++) {
				for (int z = brushSizeSquared; z >= 0; z--) {
					if (this.getBlockType(blockPositionX - brushSize + x, blockPositionY - brushSize + y, blockPositionZ - brushSize + z) != newMaterials[x][y][z]) {
						undo.put(this.clampY(blockPositionX - brushSize + x, blockPositionY - brushSize + y, blockPositionZ - brushSize + z));
					}
					this.setBlockType(blockPositionZ - brushSize + z, blockPositionX - brushSize + x, blockPositionY - brushSize + y, newMaterials[x][y][z]);
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage(ChatColor.AQUA + "Shell complete.");
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.blockTypeMessage()
			.replaceBlockTypeMessage()
			.send();
	}
}
