package com.thevoxelbox.voxelsniper.brush;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;

/**
 * @author Gavjenks
 */
public class StencilListBrush extends AbstractBrush {

	private byte pasteOption = 1; // 0 = full, 1 = fill, 2 = replace
	private String filename = "NoFileLoaded";
	private short x;
	private short z;
	private short y;
	private short xRef;
	private short zRef;
	private short yRef;
	private byte pasteParam;
	private HashMap<Integer, String> stencilList = new HashMap<>();

	/**
	 *
	 */
	public StencilListBrush() {
		this.setName("StencilList");
	}

	private String readRandomStencil(SnipeData snipeData) {
		double rand = Math.random() * (this.stencilList.size());
		int choice = (int) rand;
		return this.stencilList.get(choice);
	}

	private void readStencilList(String listname, SnipeData snipeData) {
		File file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
		if (file.exists()) {
			try {
				Scanner scanner = new Scanner(file);
				int counter = 0;
				while (scanner.hasNext()) {
					this.stencilList.put(counter, scanner.nextLine());
					counter++;
				}
				scanner.close();
			} catch (FileNotFoundException exception) {
				exception.printStackTrace();
			}
		}
	}

	private void stencilPaste(SnipeData snipeData) {
		if (this.filename.matches("NoFileLoaded")) {
			snipeData.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
			return;
		}
		String stencilName = this.readRandomStencil(snipeData);
		snipeData.sendMessage(stencilName);
		Undo undo = new Undo();
		File file = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");
		Sniper owner = snipeData.getOwner();
		if (file.exists()) {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				this.x = in.readShort();
				this.z = in.readShort();
				this.y = in.readShort();
				this.xRef = in.readShort();
				this.zRef = in.readShort();
				this.yRef = in.readShort();
				int numRuns = in.readInt();
				// Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
				int volume = this.x * this.y * this.z;
				owner.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
				int currX = -this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
				// corner, for example.
				int currZ = -this.zRef;
				int currY = -this.yRef;
				int id;
				int data;
				if (this.pasteOption == 0) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
								currX++;
								if (currX == this.x - this.xRef) {
									currX = -this.xRef;
									currZ++;
									if (currZ == this.z - this.zRef) {
										currZ = -this.zRef;
										currY++;
									}
								}
							}
						} else {
							undo.put(this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ));
							this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
							currX++;
							if (currX == this.x - this.xRef) {
								currX = -this.xRef;
								currZ++;
								if (currZ == this.z - this.zRef) {
									currZ = -this.zRef;
									currY++;
								}
							}
						}
					}
				} else if (this.pasteOption == 1) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								if (id != 0 && this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.getTypeId() == 0) {
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) (data), false);
								}
								currX++;
								if (currX == this.x - this.xRef) {
									currX = -this.xRef;
									currZ++;
									if (currZ == this.z - this.zRef) {
										currZ = -this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0 && this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.getTypeId() == 0) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) (data), false);
							}
							currX++;
							if (currX == this.x - this.xRef) {
								currX = -this.xRef;
								currZ++;
								if (currZ == this.z - this.zRef) {
									currZ = -this.zRef;
									currY++;
								}
							}
						}
					}
				} else { // replace
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < (numLoops); j++) {
								if (id != 0) {
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) data, false);
								}
								currX++;
								if (currX == this.x - this.xRef) {
									currX = -this.xRef;
									currZ++;
									if (currZ == this.z - this.zRef) {
										currZ = -this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
							}
							currX++;
							if (currX == this.x) {
								currX = 0;
								currZ++;
								if (currZ == this.z) {
									currZ = 0;
									currY++;
								}
							}
						}
					}
				}
				in.close();
				owner.storeUndo(undo);
			} catch (IOException exception) {
				owner.sendMessage(ChatColor.RED + "Something went wrong.");
				exception.printStackTrace();
			}
		} else {
			owner.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
		}
	}

	private void stencilPaste180(SnipeData snipeData) {
		Sniper owner = snipeData.getOwner();
		if (this.filename.matches("NoFileLoaded")) {
			owner.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
			return;
		}
		String stencilName = this.readRandomStencil(snipeData);
		Undo undo = new Undo();
		File file = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");
		if (file.exists()) {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				this.x = in.readShort();
				this.z = in.readShort();
				this.y = in.readShort();
				this.xRef = in.readShort();
				this.zRef = in.readShort();
				this.yRef = in.readShort();
				int numRuns = in.readInt();
				// Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
				int volume = this.x * this.y * this.z;
				owner.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
				int currX = this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
				// corner, for example.
				int currZ = this.zRef;
				int currY = -this.yRef;
				int id;
				int data;
				if (this.pasteOption == 0) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
								currX--;
								if (currX == -this.x + this.xRef) {
									currX = this.xRef;
									currZ--;
									if (currZ == -this.z + this.zRef) {
										currZ = this.zRef;
										currY++;
									}
								}
							}
						} else {
							undo.put(this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ));
							this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
							currX--;
							if (currX == -this.x + this.xRef) {
								currX = this.xRef;
								currZ--;
								if (currZ == -this.z + this.zRef) {
									currZ = this.zRef;
									currY++;
								}
							}
						}
					}
				} else if (this.pasteOption == 1) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								if (id != 0 && this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.getTypeId() == 0) {
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) (data), false);
								}
								currX--;
								if (currX == -this.x + this.xRef) {
									currX = this.xRef;
									currZ--;
									if (currZ == -this.z + this.zRef) {
										currZ = this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0 && this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.getTypeId() == 0) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) (data), false);
							}
							currX--;
							if (currX == -this.x + this.xRef) {
								currX = this.xRef;
								currZ--;
								if (currZ == -this.z + this.zRef) {
									currZ = this.zRef;
									currY++;
								}
							}
						}
					}
				} else { // replace
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < (numLoops); j++) {
								if (id != 0) {
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) data, false);
								}
								currX--;
								if (currX == -this.x + this.xRef) {
									currX = this.xRef;
									currZ--;
									if (currZ == -this.z + this.zRef) {
										currZ = this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
							}
							currX--;
							if (currX == -this.x + this.xRef) {
								currX = this.xRef;
								currZ--;
								if (currZ == -this.z + this.zRef) {
									currZ = this.zRef;
									currY++;
								}
							}
						}
					}
				}
				in.close();
				owner.storeUndo(undo);
			} catch (IOException exception) {
				owner.sendMessage(ChatColor.RED + "Something went wrong.");
				exception.printStackTrace();
			}
		} else {
			owner.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
		}
	}

	private void stencilPaste270(SnipeData snipeData) {
		Sniper owner = snipeData.getOwner();
		if (this.filename.matches("NoFileLoaded")) {
			owner.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
			return;
		}
		String stencilName = this.readRandomStencil(snipeData);
		Undo undo = new Undo();
		File file = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");
		if (file.exists()) {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				this.x = in.readShort();
				this.z = in.readShort();
				this.y = in.readShort();
				this.xRef = in.readShort();
				this.zRef = in.readShort();
				this.yRef = in.readShort();
				int numRuns = in.readInt();
				// Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
				int volume = this.x * this.y * this.z;
				owner.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
				int currX = this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
				// corner, for example.
				int currZ = -this.xRef;
				int currY = -this.yRef;
				int id;
				int data;
				if (this.pasteOption == 0) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
								currZ++;
								if (currZ == this.x - this.xRef) {
									currZ = -this.xRef;
									currX--;
									if (currX == -this.z + this.zRef) {
										currX = this.zRef;
										currY++;
									}
								}
							}
						} else {
							undo.put(this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ));
							this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
							currZ++;
							currZ++;
							if (currZ == this.x - this.xRef) {
								currZ = -this.xRef;
								currX--;
								if (currX == -this.z + this.zRef) {
									currX = this.zRef;
									currY++;
								}
							}
						}
					}
				} else if (this.pasteOption == 1) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								if (id != 0 && this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.getTypeId() == 0) { // no reason to paste air over
									// air, and it prevents us
									// most of the time from
									// having to even check the
									// block.
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) (data), false);
								}
								currZ++;
								if (currZ == this.x - this.xRef) {
									currZ = -this.xRef;
									currX--;
									if (currX == -this.z + this.zRef) {
										currX = this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0 && this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.getTypeId() == 0) { // no reason to paste air over
								// air, and it prevents us most of
								// the time from having to even
								// check the block.
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) (data), false);
							}
							currZ++;
							if (currZ == this.x - this.xRef) {
								currZ = -this.xRef;
								currX--;
								if (currX == -this.z + this.zRef) {
									currX = this.zRef;
									currY++;
								}
							}
						}
					}
				} else { // replace
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < (numLoops); j++) {
								if (id != 0) {
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) data, false);
								}
								currZ++;
								if (currZ == this.x - this.xRef) {
									currZ = -this.xRef;
									currX--;
									if (currX == -this.z + this.zRef) {
										currX = this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
							}
							currZ++;
							if (currZ == this.x - this.xRef) {
								currZ = -this.xRef;
								currX--;
								if (currX == -this.z + this.zRef) {
									currX = this.zRef;
									currY++;
								}
							}
						}
					}
				}
				in.close();
				owner.storeUndo(undo);
			} catch (IOException exception) {
				owner.sendMessage(ChatColor.RED + "Something went wrong.");
				exception.printStackTrace();
			}
		} else {
			owner.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
		}
	}

	private void stencilPaste90(SnipeData snipeData) {
		if (this.filename.matches("NoFileLoaded")) {
			snipeData.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
			return;
		}
		String stencilName = this.readRandomStencil(snipeData);
		Undo undo = new Undo();
		File file = new File("plugins/VoxelSniper/stencils/" + stencilName + ".vstencil");
		Sniper owner = snipeData.getOwner();
		if (file.exists()) {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				this.x = in.readShort();
				this.z = in.readShort();
				this.y = in.readShort();
				this.xRef = in.readShort();
				this.zRef = in.readShort();
				this.yRef = in.readShort();
				int numRuns = in.readInt();
				// Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
				int volume = this.x * this.y * this.z;
				snipeData.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
				int currX = -this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
				// corner, for example.
				int currZ = this.xRef;
				int currY = -this.yRef;
				int id;
				int data;
				if (this.pasteOption == 0) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
								currZ--;
								if (currZ == -this.x + this.xRef) {
									currZ = this.xRef;
									currX++;
									if (currX == this.z - this.zRef) {
										currX = -this.zRef;
										currY++;
									}
								}
							}
						} else {
							undo.put(this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ));
							this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.setTypeIdAndData((in.readByte() + 128), (byte) (in.readByte() + 128), false);
							currZ--;
							if (currZ == -this.x + this.xRef) {
								currZ = this.xRef;
								currX++;
								if (currX == this.z - this.zRef) {
									currX = -this.zRef;
									currY++;
								}
							}
						}
					}
				} else if (this.pasteOption == 1) {
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < numLoops; j++) {
								if (id != 0 && this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.getTypeId() == 0) {
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) (data), false);
								}
								currZ--;
								if (currZ == -this.x + this.xRef) {
									currZ = this.xRef;
									currX++;
									if (currX == this.z - this.zRef) {
										currX = -this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0 && this.clampY(this.getTargetBlock()
								.getX() + currX, this.getTargetBlock()
								.getY() + currY, this.getTargetBlock()
								.getZ() + currZ)
								.getTypeId() == 0) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) (data), false);
							}
							currZ--;
							if (currZ == -this.x + this.xRef) {
								currZ = this.xRef;
								currX++;
								if (currX == this.z - this.zRef) {
									currX = -this.zRef;
									currY++;
								}
							}
						}
					}
				} else { // replace
					for (int i = 1; i < numRuns + 1; i++) {
						if (in.readBoolean()) {
							int numLoops = in.readByte() + 128;
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							for (int j = 0; j < (numLoops); j++) {
								if (id != 0) {
									undo.put(this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ));
									this.clampY(this.getTargetBlock()
										.getX() + currX, this.getTargetBlock()
										.getY() + currY, this.getTargetBlock()
										.getZ() + currZ)
										.setTypeIdAndData(id, (byte) data, false);
								}
								currZ--;
								if (currZ == -this.x + this.xRef) {
									currZ = this.xRef;
									currX++;
									if (currX == this.z - this.zRef) {
										currX = -this.zRef;
										currY++;
									}
								}
							}
						} else {
							id = (in.readByte() + 128);
							data = (in.readByte() + 128);
							if (id != 0) {
								undo.put(this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ));
								this.clampY(this.getTargetBlock()
									.getX() + currX, this.getTargetBlock()
									.getY() + currY, this.getTargetBlock()
									.getZ() + currZ)
									.setTypeIdAndData(id, (byte) data, false);
							}
							currZ--;
							if (currZ == -this.x + this.xRef) {
								currZ = this.xRef;
								currX++;
								if (currX == this.z - this.zRef) {
									currX = -this.zRef;
									currY++;
								}
							}
						}
					}
				}
				in.close();
				owner.storeUndo(undo);
			} catch (IOException exception) {
				snipeData.sendMessage(ChatColor.RED + "Something went wrong.");
				exception.printStackTrace();
			}
		} else {
			owner.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
		}
	}

	private void stencilPasteRotation(SnipeData v) {
		// just randomly chooses a rotation and then calls stencilPaste.
		this.readStencilList(this.filename, v);
		double random = Math.random();
		if (random < 0.26) {
			this.stencilPaste(v);
		} else if (random < 0.51) {
			this.stencilPaste90(v);
		} else if (random < 0.76) {
			this.stencilPaste180(v);
		} else {
			this.stencilPaste270(v);
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.stencilPaste(snipeData);
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.stencilPasteRotation(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom("File loaded: " + this.filename);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		String secondParameter = parameters[1];
		if (secondParameter.equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "Stencil List brush Parameters:");
			snipeData.sendMessage(ChatColor.AQUA + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified stencil list.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
			return;
		} else if (secondParameter.equalsIgnoreCase("full")) {
			this.pasteOption = 0;
			this.pasteParam = 1;
		} else if (secondParameter.equalsIgnoreCase("fill")) {
			this.pasteOption = 1;
			this.pasteParam = 1;
		} else if (secondParameter.equalsIgnoreCase("replace")) {
			this.pasteOption = 2;
			this.pasteParam = 1;
		}
		try {
			this.filename = parameters[1 + this.pasteParam];
			File file = new File("plugins/VoxelSniper/stencilLists/" + this.filename + ".txt");
			if (file.exists()) {
				snipeData.sendMessage(ChatColor.RED + "Stencil List '" + this.filename + "' exists and was loaded.");
				this.readStencilList(this.filename, snipeData);
			} else {
				snipeData.sendMessage(ChatColor.AQUA + "Stencil List '" + this.filename + "' does not exist.  This brush will not function without a valid stencil list.");
				this.filename = "NoFileLoaded";
			}
		} catch (RuntimeException exception) {
			snipeData.sendMessage(ChatColor.RED + "You need to type a stencil name.");
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.stencillist";
	}
}
