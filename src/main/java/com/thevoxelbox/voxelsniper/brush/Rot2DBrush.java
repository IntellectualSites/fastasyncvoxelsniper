package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author Piotr
 */
public class Rot2DBrush extends AbstractBrush {

	private int mode;
	private int bSize;
	private int brushSize;
	private BlockWrapper[][][] snap;
	private double se;

	/**
	 *
	 */
	public Rot2DBrush() {
		this.setName("2D Rotation");
	}

	private void getMatrix() {
		this.brushSize = (this.bSize * 2) + 1;
		this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];
		double brushSizeSquared = Math.pow(this.bSize + 0.5, 2);
		int sx = this.getTargetBlock()
			.getX() - this.bSize;
		int sy = this.getTargetBlock()
			.getY() - this.bSize;
		int sz = this.getTargetBlock()
			.getZ() - this.bSize;
		for (int x = 0; x < this.snap.length; x++) {
			sz = this.getTargetBlock()
				.getZ() - this.bSize;
			double xSquared = Math.pow(x - this.bSize, 2);
			for (int y = 0; y < this.snap.length; y++) {
				sy = this.getTargetBlock()
					.getY() - this.bSize;
				if (xSquared + Math.pow(y - this.bSize, 2) <= brushSizeSquared) {
					for (int z = 0; z < this.snap.length; z++) {
						Block block = this.clampY(sx, sy, sz); // why is this not sx + x, sy + y sz + z?
						this.snap[x][z][y] = new BlockWrapper(block);
						block.setTypeId(0);
						sy++;
					}
				}
				sz++;
			}
			sx++;
		}
	}

	private void rotate(SnipeData snipeData) {
		double brushSiyeSquared = Math.pow(this.bSize + 0.5, 2);
		double cos = Math.cos(this.se);
		double sin = Math.sin(this.se);
		boolean[][] doNotFill = new boolean[this.snap.length][this.snap.length];
		// I put y in the inside loop, since it doesn't have any power functions, should be much faster.
		// Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
		// do a targeted filling of only those columns later that were left out.
		for (int x = 0; x < this.snap.length; x++) {
			int xx = x - this.bSize;
			double xSquared = Math.pow(xx, 2);
			for (int y = 0; y < this.snap.length; y++) {
				int zz = y - this.bSize;
				if (xSquared + Math.pow(zz, 2) <= brushSiyeSquared) {
					double newX = (xx * cos) - (zz * sin);
					double newZ = (xx * sin) + (zz * cos);
					doNotFill[(int) newX + this.bSize][(int) newZ + this.bSize] = true;
					for (int currentY = 0; currentY < this.snap.length; currentY++) {
						int yy = currentY - this.bSize;
						BlockWrapper block = this.snap[x][currentY][y];
						if (block.getId() == 0) {
							continue;
						}
						this.setBlockIdAndDataAt(this.getTargetBlock()
							.getX() + (int) newX, this.getTargetBlock()
							.getY() + yy, this.getTargetBlock()
							.getZ() + (int) newZ, block.getId(), block.getData());
					}
				}
			}
		}
		for (int x = 0; x < this.snap.length; x++) {
			double xSquared = Math.pow(x - this.bSize, 2);
			int fx = x + this.getTargetBlock()
				.getX() - this.bSize;
			for (int z = 0; z < this.snap.length; z++) {
				if (xSquared + Math.pow(z - this.bSize, 2) <= brushSiyeSquared) {
					int fz = z + this.getTargetBlock()
						.getZ() - this.bSize;
					if (!doNotFill[x][z]) {
						// smart fill stuff
						for (int y = 0; y < this.snap.length; y++) {
							int fy = y + this.getTargetBlock()
								.getY() - this.bSize;
							int a = this.getBlockIdAt(fx + 1, fy, fz);
							byte aData = this.getBlockDataAt(fx + 1, fy, fz);
							int d = this.getBlockIdAt(fx - 1, fy, fz);
							byte dData = this.getBlockDataAt(fx - 1, fy, fz);
							int c = this.getBlockIdAt(fx, fy, fz + 1);
							int b = this.getBlockIdAt(fx, fy, fz - 1);
							byte bData = this.getBlockDataAt(fx, fy, fz - 1);
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
							this.setBlockIdAndDataAt(fx, fy, fz, winner, winnerData);
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
				snipeData.sendMessage(ChatColor.RED + "Something went wrong.");
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
				snipeData.sendMessage(ChatColor.RED + "Something went wrong.");
				break;
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		this.se = Math.toRadians(Double.parseDouble(parameters[1]));
		snipeData.sendMessage(ChatColor.GREEN + "Angle set to " + this.se);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.rot2d";
	}
}
