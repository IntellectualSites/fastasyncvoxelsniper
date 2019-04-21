package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class Rot3DBrush extends AbstractBrush {

	private int brushSize;
	private BlockData[][][] snap;
	private double seYaw;
	private double sePitch;
	private double seRoll;

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
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'p') {
				this.sePitch = Math.toRadians(Double.parseDouble(parameter.replace("p", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Around Z-axis degrees set to " + this.sePitch);
				if (this.sePitch < 0 || this.sePitch > 359) {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
				}
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'r') {
				this.seRoll = Math.toRadians(Double.parseDouble(parameter.replace("r", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Around X-axis degrees set to " + this.seRoll);
				if (this.seRoll < 0 || this.seRoll > 359) {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
				}
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
				this.seYaw = Math.toRadians(Double.parseDouble(parameter.replace("y", "")));
				snipeData.sendMessage(ChatColor.AQUA + "Around Y-axis degrees set to " + this.seYaw);
				if (this.seYaw < 0 || this.seYaw > 359) {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! Angles must be from 1-359");
				}
			}
		}
	}

	private void getMatrix() { // only need to do once. But y needs to change + sphere
		double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
		int brushSize = (this.brushSize * 2) + 1;
		this.snap = new BlockData[brushSize][brushSize][brushSize];
		Block targetBlock = this.getTargetBlock();
		int sx = targetBlock.getX() - this.brushSize;
		//int sy = this.getTargetBlock().getY() - this.brushSize; Not used
		int sz = targetBlock.getZ() - this.brushSize;
		for (int x = 0; x < this.snap.length; x++) {
			double xSquared = Math.pow(x - this.brushSize, 2);
			sz = targetBlock.getZ() - this.brushSize;
			for (int z = 0; z < this.snap.length; z++) {
				double zSquared = Math.pow(z - this.brushSize, 2);
				sz = targetBlock.getY() - this.brushSize;
				for (int y = 0; y < this.snap.length; y++) {
					if (xSquared + zSquared + Math.pow(y - this.brushSize, 2) <= brushSizeSquared) {
						Block block = this.clampY(sx, sz, sz);
						this.snap[x][y][z] = block.getBlockData();
						block.setType(Material.AIR);
						sz++;
					}
				}
				sz++;
			}
			sx++;
		}
	}

	private void rotate(SnipeData snipeData) {
		// basically 1) make it a sphere we are rotating in, not a cylinder
		// 2) do three rotations in a row, one in each dimension, unless some dimensions are set to zero or udnefined or whatever, then skip those.
		// --> Why not utilize Sniper'world new oportunities and have arrow rotate all 3, powder rotate x, goldsisc y, otherdisc z. Or something like that. Or
		// we
		// could just use arrow and powder and just differenciate between left and right click that gis 4 different situations
		// --> Well, there would be 7 different possibilities... X, Y, Z, XY, XZ, YZ, XYZ, and different numbers of parameters for each, so I think each having
		// and item is too confusing. How about this: arrow = rotate one dimension, based on the face you click, and takes 1 param... powder: rotates all three
		// at once, and takes 3 params.
		double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
		double cosYaw = Math.cos(this.seYaw);
		double sinYaw = Math.sin(this.seYaw);
		double cosPitch = Math.cos(this.sePitch);
		double sinPitch = Math.sin(this.sePitch);
		double cosRoll = Math.cos(this.seRoll);
		double sinRoll = Math.sin(this.seRoll);
		boolean[][][] doNotFill = new boolean[this.snap.length][this.snap.length][this.snap.length];
		Undo undo = new Undo();
		Block targetBlock = this.getTargetBlock();
		for (int x = 0; x < this.snap.length; x++) {
			int xx = x - this.brushSize;
			double xSquared = Math.pow(xx, 2);
			for (int z = 0; z < this.snap.length; z++) {
				int zz = z - this.brushSize;
				double zSquared = Math.pow(zz, 2);
				double newxzX = (xx * cosYaw) - (zz * sinYaw);
				double newxzZ = (xx * sinYaw) + (zz * cosYaw);
				for (int y = 0; y < this.snap.length; y++) {
					int yy = y - this.brushSize;
					if (xSquared + zSquared + Math.pow(yy, 2) <= brushSizeSquared) {
						undo.put(this.clampY(targetBlock.getX() + xx, targetBlock.getY() + yy, targetBlock.getZ() + zz)); // just store
						// whole sphere in undo, too complicated otherwise, since this brush both adds and remos things unpredictably.
						double newxyX = (newxzX * cosPitch) - (yy * sinPitch);
						double newxyY = (newxzX * sinPitch) + (yy * cosPitch); // calculates all three in succession in precise math space
						double newyzY = (newxyY * cosRoll) - (newxzZ * sinRoll);
						double newyzZ = (newxyY * sinRoll) + (newxzZ * cosRoll);
						doNotFill[(int) newxyX + this.brushSize][(int) newyzY + this.brushSize][(int) newyzZ + this.brushSize] = true; // only rounds off to nearest
						// block
						// after all three, though.
						BlockData blockData = this.snap[x][y][z];
						Material type = blockData.getMaterial();
						if (type.isEmpty()) {
							continue;
						}
						this.setBlockData(targetBlock.getX() + (int) newxyX, targetBlock.getY() + (int) newyzY, targetBlock.getZ() + (int) newyzZ, blockData);
					}
				}
			}
		}
		for (int x = 0; x < this.snap.length; x++) {
			double xSquared = Math.pow(x - this.brushSize, 2);
			int fx = x + targetBlock.getX() - this.brushSize;
			for (int z = 0; z < this.snap.length; z++) {
				double zSquared = Math.pow(z - this.brushSize, 2);
				int fz = z + targetBlock.getZ() - this.brushSize;
				for (int y = 0; y < this.snap.length; y++) {
					if (xSquared + zSquared + Math.pow(y - this.brushSize, 2) <= brushSizeSquared) {
						if (!doNotFill[x][y][z]) {
							// smart fill stuff
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
							this.setBlockData(fx, fy, fz, winner);
						}
					}
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(undo);
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.brushSize = snipeData.getBrushSize();
		getMatrix();
		rotate(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.brushSize = snipeData.getBrushSize();
		getMatrix();
		rotate(snipeData);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.rot3d";
	}
}
