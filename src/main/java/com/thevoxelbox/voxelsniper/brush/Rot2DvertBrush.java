package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author Gavjenks, hack job from the other 2d rotation brush blockPositionY piotr
 */
// The X Y and Z variable names in this file do NOT MAKE ANY SENSE. Do not attempt to actually figure out what on earth is going on here. Just go to the
// original 2d horizontal brush if you wish to make anything similar to this, and start there. I didn't bother renaming everything.
public class Rot2DvertBrush extends AbstractBrush {

	private int mode;
	private int bSize;
	private int brushSize;
	private BlockWrapper[][][] snap;
	private double se;

	/**
	 *
	 */
	public Rot2DvertBrush() {
		super("2D Rotation");
	}

	private void getMatrix() {
		this.brushSize = (this.bSize * 2) + 1;
		this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];
		int sx = this.getTargetBlock()
			.getX() - this.bSize;
		int sy = this.getTargetBlock()
			.getY() - this.bSize;
		int sz = this.getTargetBlock()
			.getZ() - this.bSize;
		for (int x = 0; x < this.snap.length; x++) {
			sz = this.getTargetBlock()
				.getZ() - this.bSize;
			for (int z = 0; z < this.snap.length; z++) {
				sy = this.getTargetBlock()
					.getY() - this.bSize;
				for (int y = 0; y < this.snap.length; y++) {
					Block block = this.clampY(sx, sy, sz); // why is this not sx + x, sy + y sz + z?
					this.snap[x][y][z] = new BlockWrapper(block);
					block.setTypeId(0);
					sy++;
				}
				sz++;
			}
			sx++;
		}
	}

	private void rotate(SnipeData v) {
		double brushSizeSquared = Math.pow(this.bSize + 0.5, 2);
		double cos = Math.cos(this.se);
		double sin = Math.sin(this.se);
		boolean[][] doNotFill = new boolean[this.snap.length][this.snap.length];
		// I put y in the inside loop, since it doesn't have any power functions, should be much faster.
		// Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
		// do a targeted filling of only those columns later that were left out.
		for (int x = 0; x < this.snap.length; x++) {
			int xx = x - this.bSize;
			double xSquared = Math.pow(xx, 2);
			for (int z = 0; z < this.snap.length; z++) {
				int zz = z - this.bSize;
				if (xSquared + Math.pow(zz, 2) <= brushSizeSquared) {
					double newX = (xx * cos) - (zz * sin);
					double newZ = (xx * sin) + (zz * cos);
					doNotFill[(int) newX + this.bSize][(int) newZ + this.bSize] = true;
					for (int y = 0; y < this.snap.length; y++) {
						int yy = y - this.bSize;
						BlockWrapper block = this.snap[y][x][z];
						if (block.getId() == 0) {
							continue;
						}
						this.setBlockIdAndDataAt(this.getTargetBlock()
							.getX() + yy, this.getTargetBlock()
							.getY() + (int) newX, this.getTargetBlock()
							.getZ() + (int) newZ, block.getId(), block.getBlockData());
					}
				}
			}
		}
		for (int x = 0; x < this.snap.length; x++) {
			double xSquared = Math.pow(x - this.bSize, 2);
			int fx = x + this.getTargetBlock()
				.getX() - this.bSize;
			for (int z = 0; z < this.snap.length; z++) {
				if (xSquared + Math.pow(z - this.bSize, 2) <= brushSizeSquared) {
					int fz = z + this.getTargetBlock()
						.getZ() - this.bSize;
					if (!doNotFill[x][z]) {
						// smart fill stuff
						for (int y = 0; y < this.snap.length; y++) {
							int fy = y + this.getTargetBlock()
								.getY() - this.bSize;
							int a = this.getBlockIdAt(fy, fx + 1, fz);
							byte aData = this.getBlockDataAt(fy, fx + 1, fz);
							int d = this.getBlockIdAt(fy, fx - 1, fz);
							byte dData = this.getBlockDataAt(fy, fx - 1, fz);
							int c = this.getBlockIdAt(fy, fx, fz + 1);
							int b = this.getBlockIdAt(fy, fx, fz - 1);
							byte bData = this.getBlockDataAt(fy, fx, fz - 1);
							int winner;
							byte winnerData;
							if (a == b || a == c || a == d) { // I figure that since we are already narrowing it down to ONLY the holes left behind, it
								// should
								// be fine to do all 5 checks needed to be legit about it.
								winner = a;
								winnerData = aData;
							} else if (b == d || c == d) {
								winner = d;
								winnerData = dData;
							} else {
								winner = b; // blockPositionY making this default, it will also automatically cover situations where B = C;
								winnerData = bData;
							}
							this.setBlockIdAndDataAt(fy, fx, fz, winner, winnerData);
						}
					}
				}
			}
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.bSize = snipeData.getBrushSize();
		switch (this.mode) {
			case 0:
				this.getMatrix();
				this.rotate(snipeData);
				break;
			default:
				snipeData.getOwner()
					.getPlayer()
					.sendMessage(ChatColor.RED + "Something went wrong.");
				break;
		}
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.bSize = snipeData.getBrushSize();
		switch (this.mode) {
			case 0:
				this.getMatrix();
				this.rotate(snipeData);
				break;
			default:
				snipeData.getOwner()
					.getPlayer()
					.sendMessage(ChatColor.RED + "Something went wrong.");
				break;
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		try {
			this.se = Math.toRadians(Double.parseDouble(parameters[1]));
			snipeData.sendMessage(ChatColor.GREEN + "Angle set to " + this.se);
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
