package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

	/**
	 *
	 */
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

	private void scan(SnipeData snipeData, BlockFace blockFace) {
		if (blockFace == null) {
			return;
		}
		switch (blockFace) {
			case NORTH:
				// Scan south
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(this.getTargetBlock()
						.getX() + i, this.getTargetBlock()
						.getY(), this.getTargetBlock()
						.getZ())
						.getType() == this.checkFor) {
						snipeData.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
						return;
					}
				}
				snipeData.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case SOUTH:
				// Scan north
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(this.getTargetBlock()
						.getX() - i, this.getTargetBlock()
						.getY(), this.getTargetBlock()
						.getZ())
						.getType() == this.checkFor) {
						snipeData.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
						return;
					}
				}
				snipeData.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case EAST:
				// Scan west
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(this.getTargetBlock()
						.getX(), this.getTargetBlock()
						.getY(), this.getTargetBlock()
						.getZ() + i)
						.getType() == this.checkFor) {
						snipeData.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
						return;
					}
				}
				snipeData.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case WEST:
				// Scan east
				for (int i = 1; i < this.depth + 1; i++) {
					if (this.clampY(this.getTargetBlock()
						.getX(), this.getTargetBlock()
						.getY(), this.getTargetBlock()
						.getZ() - i)
						.getType() == this.checkFor) {
						snipeData.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
						return;
					}
				}
				snipeData.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case UP:
				// Scan down
				for (int i = 1; i < this.depth + 1; i++) {
					if ((this.getTargetBlock()
						.getY() - i) <= 0) {
						break;
					}
					if (this.clampY(this.getTargetBlock()
						.getX(), this.getTargetBlock()
						.getY() - i, this.getTargetBlock()
						.getZ())
						.getType() == this.checkFor) {
						snipeData.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
						return;
					}
				}
				snipeData.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			case DOWN:
				// Scan up
				for (int i = 1; i < this.depth + 1; i++) {
					if ((this.getTargetBlock()
						.getY() + i) >= snipeData.getWorld()
						.getMaxHeight()) {
						break;
					}
					if (this.clampY(this.getTargetBlock()
						.getX(), this.getTargetBlock()
						.getY() + i, this.getTargetBlock()
						.getZ())
						.getType() == this.checkFor) {
						snipeData.sendMessage(ChatColor.GREEN + "" + this.checkFor + " found after " + i + " blocks.");
						return;
					}
				}
				snipeData.sendMessage(ChatColor.GRAY + "Nope.");
				break;
			default:
				break;
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.checkFor = Material.getMaterial(snipeData.getVoxelId());
		this.scan(snipeData, this.getTargetBlock()
			.getFace(this.getLastBlock()));
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.checkFor = Material.getMaterial(snipeData.getVoxelId());
		this.scan(snipeData, this.getTargetBlock()
			.getFace(this.getLastBlock()));
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom(ChatColor.GREEN + "Scanner depth set to " + this.depth);
		message.custom(ChatColor.GREEN + "Scanner scans for " + this.checkFor + " (change with /v #)");
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Scanner brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
				this.depth = this.clamp(Integer.parseInt(parameter.substring(1)), DEPTH_MIN, DEPTH_MAX);
				snipeData.sendMessage(ChatColor.AQUA + "Scanner depth set to " + this.depth);
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.scanner";
	}
}
