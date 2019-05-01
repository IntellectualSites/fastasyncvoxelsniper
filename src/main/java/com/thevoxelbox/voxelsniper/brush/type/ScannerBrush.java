package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * @author DivineRage
 */
public class ScannerBrush extends AbstractBrush {

	private static final int DEPTH_MIN = 1;
	private static final int DEPTH_DEFAULT = 24;
	private static final int DEPTH_MAX = 64;

	private int depth = DEPTH_DEFAULT;
	private Material checkFor = Material.AIR;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int index = 1; index < parameters.length; index++) {
			String parameter = parameters[index];
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Scanner brush Parameters:");
				messenger.sendMessage(ChatColor.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
				this.depth = this.clamp(Integer.parseInt(parameter.substring(1)), DEPTH_MIN, DEPTH_MAX);
				messenger.sendMessage(ChatColor.AQUA + "Scanner depth set to " + this.depth);
			} else {
				messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.checkFor = toolkitProperties.getBlockDataType();
		Block targetBlock = getTargetBlock();
		Block lastBlock = getLastBlock();
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		scan(snipe, face);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.checkFor = toolkitProperties.getBlockDataType();
		Block targetBlock = getTargetBlock();
		Block lastBlock = getLastBlock();
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		this.scan(snipe, face);
	}

	private int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}

	private void scan(Snipe snipe, BlockFace blockFace) {
		SnipeMessenger messenger = snipe.createMessenger();
		Block targetBlock = getTargetBlock();
		if (blockFace == BlockFace.NORTH) {// Scan south
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX() + i, targetBlock.getY(), targetBlock.getZ())
					.getType() == this.checkFor) {
					messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(ChatColor.GRAY + "Nope.");
		} else if (blockFace == BlockFace.SOUTH) {// Scan north
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX() - i, targetBlock.getY(), targetBlock.getZ())
					.getType() == this.checkFor) {
					messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(ChatColor.GRAY + "Nope.");
		} else if (blockFace == BlockFace.EAST) {// Scan west
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() + i)
					.getType() == this.checkFor) {
					messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(ChatColor.GRAY + "Nope.");
		} else if (blockFace == BlockFace.WEST) {// Scan east
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() - i)
					.getType() == this.checkFor) {
					messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(ChatColor.GRAY + "Nope.");
		} else if (blockFace == BlockFace.UP) {// Scan down
			for (int i = 1; i < this.depth + 1; i++) {
				if ((targetBlock.getY() - i) <= 0) {
					break;
				}
				if (this.clampY(targetBlock.getX(), targetBlock.getY() - i, targetBlock.getZ())
					.getType() == this.checkFor) {
					messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(ChatColor.GRAY + "Nope.");
		} else if (blockFace == BlockFace.DOWN) {// Scan up
			for (int i = 1; i < this.depth + 1; i++) {
				Sniper sniper = snipe.getSniper();
				Player player = sniper.getPlayer();
				World world = player.getWorld();
				if ((targetBlock.getY() + i) >= world.getMaxHeight()) {
					break;
				}
				if (this.clampY(targetBlock.getX(), targetBlock.getY() + i, targetBlock.getZ())
					.getType() == this.checkFor) {
					messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(ChatColor.GRAY + "Nope.");
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(ChatColor.GREEN + "Scanner depth set to " + this.depth);
		messenger.sendMessage(ChatColor.GREEN + "Scanner scans for " + this.checkFor + " (change with /v #)");
	}
}
