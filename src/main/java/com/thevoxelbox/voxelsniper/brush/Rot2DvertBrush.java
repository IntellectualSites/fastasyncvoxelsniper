package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Gavjenks, hack job from the other 2d rotation brush blockPositionY piotr
 */
// The X Y and Z variable names in this file do NOT MAKE ANY SENSE. Do not attempt to actually figure out what on earth is going on here. Just go to the
// original 2d horizontal brush if you wish to make anything similar to this, and start there. I didn't bother renaming everything.
public class Rot2DvertBrush extends AbstractBrush {

	private int mode;
	private int brushSize;
	private BlockWrapper[][][] snap;
	private double angle;

	public Rot2DvertBrush() {
		super("2D Rotation");
	}

	private void getMatrix() {
		int brushSize = (this.brushSize * 2) + 1;
		this.snap = new BlockWrapper[brushSize][brushSize][brushSize];
		Block targetBlock = this.getTargetBlock();
		int sx = targetBlock.getX() - this.brushSize;
		int sy = targetBlock.getY() - this.brushSize;
		int sz = targetBlock.getZ() - this.brushSize;
		for (int x = 0; x < this.snap.length; x++) {
			sz = targetBlock.getZ() - this.brushSize;
			for (int z = 0; z < this.snap.length; z++) {
				sy = targetBlock.getY() - this.brushSize;
				for (int y = 0; y < this.snap.length; y++) {
					Block block = this.clampY(sx, sy, sz); // why is this not sx + x, sy + y sz + z?
					this.snap[x][y][z] = new BlockWrapper(block);
					block.setType(Material.AIR);
					sy++;
				}
				sz++;
			}
			sx++;
		}
	}

	private void rotate(SnipeData snipeData) {
		double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
		double cos = Math.cos(this.angle);
		double sin = Math.sin(this.angle);
		boolean[][] doNotFill = new boolean[this.snap.length][this.snap.length];
		// I put y in the inside loop, since it doesn't have any power functions, should be much faster.
		// Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
		// do a targeted filling of only those columns later that were left out.
		Block targetBlock = this.getTargetBlock();
		for (int x = 0; x < this.snap.length; x++) {
			int xx = x - this.brushSize;
			double xSquared = Math.pow(xx, 2);
			for (int z = 0; z < this.snap.length; z++) {
				int zz = z - this.brushSize;
				if (xSquared + Math.pow(zz, 2) <= brushSizeSquared) {
					double newX = (xx * cos) - (zz * sin);
					double newZ = (xx * sin) + (zz * cos);
					doNotFill[(int) newX + this.brushSize][(int) newZ + this.brushSize] = true;
					for (int y = 0; y < this.snap.length; y++) {
						int yy = y - this.brushSize;
						BlockWrapper block = this.snap[y][x][z];
						Material type = block.getType();
						if (type.isEmpty()) {
							continue;
						}
						setBlockData(targetBlock.getX() + yy, targetBlock.getY() + (int) newX, targetBlock.getZ() + (int) newZ, block.getBlockData());
					}
				}
			}
		}
		for (int x = 0; x < this.snap.length; x++) {
			double xSquared = Math.pow(x - this.brushSize, 2);
			int fx = x + targetBlock.getX() - this.brushSize;
			for (int z = 0; z < this.snap.length; z++) {
				if (xSquared + Math.pow(z - this.brushSize, 2) <= brushSizeSquared) {
					int fz = z + targetBlock.getZ() - this.brushSize;
					if (!doNotFill[x][z]) {
						// smart fill stuff
						for (int y = 0; y < this.snap.length; y++) {
							int fy = y + targetBlock.getY() - this.brushSize;
							Material a = this.getBlockType(fy, fx + 1, fz);
							Material b = this.getBlockType(fy, fx, fz - 1);
							Material c = this.getBlockType(fy, fx, fz + 1);
							Material d = this.getBlockType(fy, fx - 1, fz);
							BlockData aData = this.getBlockData(fy, fx + 1, fz);
							BlockData bData = this.getBlockData(fy, fx, fz - 1);
							BlockData dData = this.getBlockData(fy, fx - 1, fz);
							BlockData winner;
							if (a == b || a == c || a == d) { // I figure that since we are already narrowing it down to ONLY the holes left behind, it
								// should
								// be fine to do all 5 checks needed to be legit about it.
								winner = aData;
							} else if (b == d || c == d) {
								winner = dData;
							} else {
								winner = bData; // blockPositionY making this default, it will also automatically cover situations where B = C;
							}
							this.setBlockData(fy, fx, fz, winner);
						}
					}
				}
			}
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.brushSize = snipeData.getBrushSize();
		if (this.mode == 0) {
			this.getMatrix();
			this.rotate(snipeData);
		} else {
			Sniper owner = snipeData.getOwner();
			owner.sendMessage(ChatColor.RED + "Something went wrong.");
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.brushSize = snipeData.getBrushSize();
		if (this.mode == 0) {
			this.getMatrix();
			this.rotate(snipeData);
		} else {
			Sniper owner = snipeData.getOwner();
			owner.sendMessage(ChatColor.RED + "Something went wrong.");
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		try {
			this.angle = Math.toRadians(Double.parseDouble(parameters[1]));
			snipeData.sendMessage(ChatColor.GREEN + "Angle set to " + this.angle);
		} catch (NumberFormatException exception) {
			snipeData.sendMessage("Exception while parsing parameter: " + parameters[1]);
			Bukkit.getLogger()
				.severe(exception.getMessage());
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.rot2dvert";
	}
}
