package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 */
public class Rot3DBrush extends AbstractBrush {

	private int bSize;
	private int brushSize;
	private BlockWrapper[][][] snap;
	private double seYaw;
	private double sePitch;
	private double seRoll;

	/**
	 *
	 */
	public Rot3DBrush() {
		super("3D Rotation");
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.brushMessage("Rotates Yaw (XZ), then Pitch(XY), then Roll(ZY), in order.");
	}
	// after all rotations, compare snapshot to new state of world, and store changed blocks to undo?
	// --> agreed. Do what erode does and store one snapshot with Block pointers and int id of what the block started with, afterwards simply go thru that
	// matrix and compare Block.getId with 'id' if different undo.add( new BlockWrapper ( Block, oldId ) )

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			// which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Rotate brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "p[0-359] -- set degrees of pitch rotation (rotation about the Z axis).");
				snipeData.sendMessage(ChatColor.BLUE + "r[0-359] -- set degrees of roll rotation (rotation about the X axis).");
				snipeData.sendMessage(ChatColor.LIGHT_PURPLE + "y[0-359] -- set degrees of yaw rotation (Rotation about the Y axis).");
				return;
			} else if (parameter.startsWith("p")) {
				this.sePitch = Math.toRadians(Double.parseDouble(parameter.replace("p", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Around Z-axis degrees set to " + this.sePitch);
				if (this.sePitch < 0 || this.sePitch > 359) {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
				}
			} else if (parameter.startsWith("r")) {
				this.seRoll = Math.toRadians(Double.parseDouble(parameter.replace("r", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Around X-axis degrees set to " + this.seRoll);
				if (this.seRoll < 0 || this.seRoll > 359) {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
				}
			} else if (parameter.startsWith("y")) {
				this.seYaw = Math.toRadians(Double.parseDouble(parameter.replace("y", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Around Y-axis degrees set to " + this.seYaw);
				if (this.seYaw < 0 || this.seYaw > 359) {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
				}
			}
		}
	}

	private void getMatrix() { // only need to do once. But y needs to change + sphere
		double brushSizeSquared = Math.pow(this.bSize + 0.5, 2);
		this.brushSize = (this.bSize * 2) + 1;
		this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];
		int sx = this.getTargetBlock()
			.getX() - this.bSize;
		//int sy = this.getTargetBlock().getY() - this.bSize; Not used
		int sz = this.getTargetBlock()
			.getZ() - this.bSize;
		for (int x = 0; x < this.snap.length; x++) {
			double xSquared = Math.pow(x - this.bSize, 2);
			sz = this.getTargetBlock()
				.getZ() - this.bSize;
			for (int z = 0; z < this.snap.length; z++) {
				double zSquared = Math.pow(z - this.bSize, 2);
				sz = this.getTargetBlock()
					.getY() - this.bSize;
				for (int y = 0; y < this.snap.length; y++) {
					if (xSquared + zSquared + Math.pow(y - this.bSize, 2) <= brushSizeSquared) {
						Block block = this.clampY(sx, sz, sz);
						this.snap[x][y][z] = new BlockWrapper(block);
						block.setTypeId(0);
						sz++;
					}
				}
				sz++;
			}
			sx++;
		}
	}

	private void rotate(SnipeData v) {
		// basically 1) make it a sphere we are rotating in, not a cylinder
		// 2) do three rotations in a row, one in each dimension, unless some dimensions are set to zero or udnefined or whatever, then skip those.
		// --> Why not utilize Sniper'world new oportunities and have arrow rotate all 3, powder rotate x, goldsisc y, otherdisc z. Or something like that. Or
		// we
		// could just use arrow and powder and just differenciate between left and right click that gis 4 different situations
		// --> Well, there would be 7 different possibilities... X, Y, Z, XY, XZ, YZ, XYZ, and different numbers of parameters for each, so I think each having
		// and item is too confusing. How about this: arrow = rotate one dimension, based on the face you click, and takes 1 param... powder: rotates all three
		// at once, and takes 3 params.
		double brushSizeSquared = Math.pow(this.bSize + 0.5, 2);
		double cosYaw = Math.cos(this.seYaw);
		double sinYaw = Math.sin(this.seYaw);
		double cosPitch = Math.cos(this.sePitch);
		double sinPitch = Math.sin(this.sePitch);
		double cosRoll = Math.cos(this.seRoll);
		double sinRoll = Math.sin(this.seRoll);
		boolean[][][] doNotFill = new boolean[this.snap.length][this.snap.length][this.snap.length];
		Undo undo = new Undo();
		for (int x = 0; x < this.snap.length; x++) {
			int xx = x - this.bSize;
			double xSquared = Math.pow(xx, 2);
			for (int z = 0; z < this.snap.length; z++) {
				int zz = z - this.bSize;
				double zSquared = Math.pow(zz, 2);
				double newxzX = (xx * cosYaw) - (zz * sinYaw);
				double newxzZ = (xx * sinYaw) + (zz * cosYaw);
				for (int y = 0; y < this.snap.length; y++) {
					int yy = y - this.bSize;
					if (xSquared + zSquared + Math.pow(yy, 2) <= brushSizeSquared) {
						undo.put(this.clampY(this.getTargetBlock()
							.getX() + xx, this.getTargetBlock()
							.getY() + yy, this.getTargetBlock()
							.getZ() + zz)); // just store
						// whole sphere in undo, too complicated otherwise, since this brush both adds and remos things unpredictably.
						double newxyX = (newxzX * cosPitch) - (yy * sinPitch);
						double newxyY = (newxzX * sinPitch) + (yy * cosPitch); // calculates all three in succession in precise math space
						double newyzY = (newxyY * cosRoll) - (newxzZ * sinRoll);
						double newyzZ = (newxyY * sinRoll) + (newxzZ * cosRoll);
						doNotFill[(int) newxyX + this.bSize][(int) newyzY + this.bSize][(int) newyzZ + this.bSize] = true; // only rounds off to nearest
						// block
						// after all three, though.
						BlockWrapper block = this.snap[x][y][z];
						if (block.getId() == 0) {
							continue;
						}
						this.setBlockIdAndDataAt(this.getTargetBlock()
							.getX() + (int) newxyX, this.getTargetBlock()
							.getY() + (int) newyzY, this.getTargetBlock()
							.getZ() + (int) newyzZ, block.getId(), block.getBlockData());
					}
				}
			}
		}
		for (int x = 0; x < this.snap.length; x++) {
			double xSquared = Math.pow(x - this.bSize, 2);
			int fx = x + this.getTargetBlock()
				.getX() - this.bSize;
			for (int z = 0; z < this.snap.length; z++) {
				double zSquared = Math.pow(z - this.bSize, 2);
				int fz = z + this.getTargetBlock()
					.getZ() - this.bSize;
				for (int y = 0; y < this.snap.length; y++) {
					if (xSquared + zSquared + Math.pow(y - this.bSize, 2) <= brushSizeSquared) {
						if (!doNotFill[x][y][z]) {
							// smart fill stuff
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
		v.getOwner()
			.storeUndo(undo);
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.bSize = snipeData.getBrushSize();
		this.getMatrix();
		this.rotate(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.bSize = snipeData.getBrushSize();
		this.getMatrix();
		this.rotate(snipeData);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.rot3d";
	}
}
