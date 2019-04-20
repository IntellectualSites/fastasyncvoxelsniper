package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author Piotr
 */
public class Rot2DBrush extends AbstractBrush {

	private int mode;
	private int brushSize;
	private BlockWrapper[][][] snap;
	private double angle;

	public Rot2DBrush() {
		super("2D Rotation");
	}

	private void getMatrix() {
		int brushSize = (this.brushSize * 2) + 1;
		this.snap = new BlockWrapper[brushSize][brushSize][brushSize];
		double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
		Block targetBlock = this.getTargetBlock();
		int sx = targetBlock.getX() - this.brushSize;
		int sy = targetBlock.getY() - this.brushSize;
		int sz = targetBlock.getZ() - this.brushSize;
		for (int x = 0; x < this.snap.length; x++) {
			sz = targetBlock.getZ() - this.brushSize;
			double xSquared = Math.pow(x - this.brushSize, 2);
			for (int y = 0; y < this.snap.length; y++) {
				sy = targetBlock.getY() - this.brushSize;
				if (xSquared + Math.pow(y - this.brushSize, 2) <= brushSizeSquared) {
					for (int z = 0; z < this.snap.length; z++) {
						Block block = this.clampY(sx, sy, sz); // why is this not sx + x, sy + y sz + z?
						this.snap[x][z][y] = new BlockWrapper(block);
						block.setType(Material.AIR);
						sy++;
					}
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
		Block targetBlock = getTargetBlock();
		for (int x = 0; x < this.snap.length; x++) {
			int xx = x - this.brushSize;
			double xSquared = Math.pow(xx, 2);
			for (int y = 0; y < this.snap.length; y++) {
				int zz = y - this.brushSize;
				if (xSquared + Math.pow(zz, 2) <= brushSizeSquared) {
					double newX = (xx * cos) - (zz * sin);
					double newZ = (xx * sin) + (zz * cos);
					doNotFill[(int) newX + this.brushSize][(int) newZ + this.brushSize] = true;
					for (int currentY = 0; currentY < this.snap.length; currentY++) {
						int yy = currentY - this.brushSize;
						BlockWrapper block = this.snap[x][currentY][y];
						Material type = block.getType();
						if (type.isEmpty()) {
							continue;
						}
						setBlockData(targetBlock.getX() + (int) newX, targetBlock.getY() + yy, targetBlock.getZ() + (int) newZ, block.getBlockData());
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
							Material a = this.getBlockType(fx + 1, fy, fz);
							Material b = this.getBlockType(fx, fy, fz - 1);
							Material c = this.getBlockType(fx, fy, fz + 1);
							Material d = this.getBlockType(fx - 1, fy, fz);
							BlockData aData = this.getBlockData(fx + 1, fy, fz);
							BlockData dData = this.getBlockData(fx - 1, fy, fz);
							BlockData bData = this.getBlockData(fx, fy, fz - 1);
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
							setBlockData(fx, fy, fz, winner);
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
			snipeData.sendMessage(ChatColor.RED + "Something went wrong.");
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.brushSize = snipeData.getBrushSize();
		if (this.mode == 0) {
			this.getMatrix();
			this.rotate(snipeData);
		} else {
			snipeData.sendMessage(ChatColor.RED + "Something went wrong.");
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		this.angle = Math.toRadians(Double.parseDouble(parameters[1]));
		snipeData.sendMessage(ChatColor.GREEN + "Angle set to " + this.angle);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.rot2d";
	}
}
