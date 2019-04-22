package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * @author DivineRage
 */
public class ScannerBrush extends AbstractBrush {

	private static final int DEPTH_MIN = 1;
	private static final int DEPTH_DEFAULT = 24;
	private static final int DEPTH_MAX = 64;

	private int depth = DEPTH_DEFAULT;
	private Material checkFor = Material.AIR;

	public ScannerBrush() {
		super("Scanner");
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

	private void scan(ToolkitProperties toolkitProperties, BlockFace blockFace) {
		if (blockFace == null) {
			return;
		}
		Block targetBlock = this.getTargetBlock();
		switch (blockFace) {
			case NORTH:
				// Scan south
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(targetBlock.getX() + i, targetBlock.getY(), targetBlock.getZ())
						.getType() == this.checkFor) {
						toolkitProperties.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
						return;
					}
				}
				toolkitProperties.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case SOUTH:
				// Scan north
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(targetBlock.getX() - i, targetBlock.getY(), targetBlock.getZ())
						.getType() == this.checkFor) {
						toolkitProperties.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
						return;
					}
				}
				toolkitProperties.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case EAST:
				// Scan west
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() + i)
						.getType() == this.checkFor) {
						toolkitProperties.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
						return;
					}
				}
				toolkitProperties.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case WEST:
				// Scan east
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() - i)
						.getType() == this.checkFor) {
						toolkitProperties.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
						return;
					}
				}
				toolkitProperties.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case UP:
				// Scan down
				for (int i = 1; i < this.depth + 1; i++) {
					if ((targetBlock.getY() - i) <= 0) {
						break;
					}
					if (this.clampY(targetBlock.getX(), targetBlock.getY() - i, targetBlock.getZ())
						.getType() == this.checkFor) {
						toolkitProperties.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
						return;
					}
				}
				toolkitProperties.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case DOWN:
				// Scan up
				for (int i = 1; i < this.depth + 1; i++) {
					World world = toolkitProperties.getWorld();
					if (world == null) {
						continue;
					}
					if ((targetBlock.getY() + i) >= world.getMaxHeight()) {
						break;
					}
					if (this.clampY(targetBlock.getX(), targetBlock.getY() + i, targetBlock.getZ())
						.getType() == this.checkFor) {
						toolkitProperties.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
						return;
					}
				}
				toolkitProperties.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			default:
				break;
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.checkFor = toolkitProperties.getBlockDataType();
		Block targetBlock = this.getTargetBlock();
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		this.scan(toolkitProperties, face);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.checkFor = toolkitProperties.getBlockDataType();
		Block targetBlock = this.getTargetBlock();
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		BlockFace face = targetBlock.getFace(lastBlock);
		if (face == null) {
			return;
		}
		this.scan(toolkitProperties, face);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.custom(ChatColor.GREEN + "Scanner depth set to " + this.depth);
		messages.custom(ChatColor.GREEN + "Scanner scans for " + this.checkFor + " (change with /v #)");
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				toolkitProperties.sendMessage(ChatColor.GOLD + "Scanner brush Parameters:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
				this.depth = this.clamp(Integer.parseInt(parameter.substring(1)), DEPTH_MIN, DEPTH_MAX);
				toolkitProperties.sendMessage(ChatColor.AQUA + "Scanner depth set to " + this.depth);
			} else {
				toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.scanner";
	}
}
