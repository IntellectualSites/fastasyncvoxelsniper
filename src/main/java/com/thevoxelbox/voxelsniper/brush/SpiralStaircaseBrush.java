package com.thevoxelbox.voxelsniper.brush;

import java.util.stream.Stream;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Spiral_Staircase_Brush
 *
 * @author giltwist
 */
public class SpiralStaircaseBrush extends AbstractBrush {

	private String stairtype = "block"; // "block" 1x1 blocks (default), "step" alternating step double step, "stair" staircase with blocks on corners
	private String sdirect = "c"; // "c" clockwise (default), "cc" counter-clockwise
	private String sopen = "n"; // "n" north (default), "e" east, "world" south, "world" west

	/**
	 *
	 */
	public SpiralStaircaseBrush() {
		super("Spiral Staircase");
	}

	private void buildStairWell(SnipeData snipeData, Block targetBlock) {
		if (snipeData.getVoxelHeight() < 1) {
			snipeData.setVoxelHeight(1);
			snipeData.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
		}
		int[][][] spiral = new int[2 * snipeData.getBrushSize() + 1][snipeData.getVoxelHeight()][2 * snipeData.getBrushSize() + 1];
		// locate first block in staircase
		// Note to self, fix these
		int startX;
		int startZ;
		if (this.sdirect.equalsIgnoreCase("cc")) {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 2 * snipeData.getBrushSize();
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * snipeData.getBrushSize();
				startZ = 0;
			} else {
				startX = 2 * snipeData.getBrushSize();
				startZ = 2 * snipeData.getBrushSize();
			}
		} else {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 2 * snipeData.getBrushSize();
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * snipeData.getBrushSize();
				startZ = 2 * snipeData.getBrushSize();
			} else {
				startX = 0;
				startZ = 2 * snipeData.getBrushSize();
			}
		}
		int toggle = 0;
		int zOffset = 0;
		int xOffset = 0;
		int y = 0;
		while (y < snipeData.getVoxelHeight()) {
			if (this.stairtype.equalsIgnoreCase("block")) {
				// 1x1x1 voxel material steps
				spiral[startX + xOffset][y][startZ + zOffset] = 1;
				y++;
			} else if (this.stairtype.equalsIgnoreCase("step")) {
				// alternating step-doublestep, uses data value to determine type
				switch (toggle) {
					case 0:
					case 1:
						toggle = 2;
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
						break;
					case 2:
						toggle = 1;
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
						break;
					default:
						break;
				}
			}
			// Adjust horizontal position and do stair-option array stuff
			if (startX + xOffset == 0) { // All North
				if (startZ + zOffset == 0) { // NORTHEAST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset++;
					} else {
						zOffset++;
					}
				} else if (startZ + zOffset == 2 * snipeData.getBrushSize()) { // NORTHWEST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset--;
					} else {
						xOffset++;
					}
				} else { // JUST PLAIN NORTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset--;
					} else {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset++;
					}
				}
			} else if (startX + xOffset == 2 * snipeData.getBrushSize()) { // ALL SOUTH
				if (startZ + zOffset == 0) { // SOUTHEAST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset++;
					} else {
						xOffset--;
					}
				} else if (startZ + zOffset == 2 * snipeData.getBrushSize()) { // SOUTHWEST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset--;
					} else {
						zOffset--;
					}
				} else { // JUST PLAIN SOUTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset++;
					} else {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset--;
					}
				}
			} else if (startZ + zOffset == 0) { // JUST PLAIN EAST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset++;
				} else {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset--;
				}
			} else { // JUST PLAIN WEST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset--;
				} else {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset++;
				}
			}
		}
		Undo undo = new Undo();
		// Make the changes
		for (int x = 2 * snipeData.getBrushSize(); x >= 0; x--) {
			for (int i = snipeData.getVoxelHeight() - 1; i >= 0; i--) {
				for (int z = 2 * snipeData.getBrushSize(); z >= 0; z--) {
					int blockPositionX = targetBlock.getX();
					int blockPositionY = targetBlock.getY();
					int blockPositionZ = targetBlock.getZ();
					switch (spiral[x][i][z]) {
						case 0:
							if (i != snipeData.getVoxelHeight() - 1) {
								if (!((this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) && spiral[x][i + 1][z] == 1)) {
									if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 0) {
										undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
									}
									this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 0);
								}
							} else {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 0) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 0);
							}
							break;
						case 1:
							if (this.stairtype.equalsIgnoreCase("block")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != snipeData.getVoxelId()) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, snipeData.getVoxelId());
							} else if (this.stairtype.equalsIgnoreCase("step")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 44) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 44);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData(snipeData.getData());
							} else if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i - 1, blockPositionZ - snipeData.getBrushSize() + z) != snipeData.getVoxelId()) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i - 1, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i - 1, snipeData.getVoxelId());
							}
							break;
						case 2:
							if (this.stairtype.equalsIgnoreCase("step")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 43) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 43);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData(snipeData.getData());
							} else if (this.stairtype.equalsIgnoreCase("woodstair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 53) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 53);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) 0);
							} else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 67) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 67);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) 0);
							}
							break;
						default:
							if (this.stairtype.equalsIgnoreCase("woodstair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 53) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 53);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) (spiral[x][i][z] - 2));
							} else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z) != 67) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, 67);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) (spiral[x][i][z] - 2));
							}
							break;
					}
				}
			}
		}
		snipeData.getOwner()
			.storeUndo(undo);
	}

	private void digStairWell(SnipeData snipeData, Block targetBlock) {
		if (snipeData.getVoxelHeight() < 1) {
			snipeData.setVoxelHeight(1);
			snipeData.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
		}
		// initialize array
		int[][][] spiral = new int[2 * snipeData.getBrushSize() + 1][snipeData.getVoxelHeight()][2 * snipeData.getBrushSize() + 1];
		// locate first block in staircase
		// Note to self, fix these
		int startX;
		int startZ;
		if (this.sdirect.equalsIgnoreCase("cc")) {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 2 * snipeData.getBrushSize();
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * snipeData.getBrushSize();
				startZ = 0;
			} else {
				startX = 2 * snipeData.getBrushSize();
				startZ = 2 * snipeData.getBrushSize();
			}
		} else {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 2 * snipeData.getBrushSize();
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * snipeData.getBrushSize();
				startZ = 2 * snipeData.getBrushSize();
			} else {
				startX = 0;
				startZ = 2 * snipeData.getBrushSize();
			}
		}
		int toggle = 0;
		int zOffset = 0;
		int xOffset = 0;
		int y = 0;
		while (y < snipeData.getVoxelHeight()) {
			if (this.stairtype.equalsIgnoreCase("block")) {
				// 1x1x1 voxel material steps
				spiral[startX + xOffset][y][startZ + zOffset] = 1;
				y++;
			} else if (this.stairtype.equalsIgnoreCase("step")) {
				// alternating step-doublestep, uses data value to determine type
				switch (toggle) {
					case 0:
					case 1:
						toggle = 2;
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						break;
					case 2:
						toggle = 1;
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
						y++;
						break;
					default:
						break;
				}
			}
			// Adjust horizontal position and do stair-option array stuff
			if (startX + xOffset == 0) { // All North
				if (startZ + zOffset == 0) { // NORTHEAST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset++;
					} else {
						zOffset++;
					}
				} else if (startZ + zOffset == 2 * snipeData.getBrushSize()) { // NORTHWEST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset--;
					} else {
						xOffset++;
					}
				} else { // JUST PLAIN NORTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset--;
					} else {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset++;
					}
				}
			} else if (startX + xOffset == 2 * snipeData.getBrushSize()) { // ALL SOUTH
				if (startZ + zOffset == 0) { // SOUTHEAST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset++;
					} else {
						xOffset--;
					}
				} else if (startZ + zOffset == 2 * snipeData.getBrushSize()) { // SOUTHWEST
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset--;
					} else {
						zOffset--;
					}
				} else { // JUST PLAIN SOUTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset++;
					} else {
						if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset--;
					}
				}
			} else if (startZ + zOffset == 0) { // JUST PLAIN EAST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset++;
				} else {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset--;
				}
			} else { // JUST PLAIN WEST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset--;
				} else {
					if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset++;
				}
			}
		}
		Undo undo = new Undo();
		// Make the changes
		for (int x = 2 * snipeData.getBrushSize(); x >= 0; x--) {
			for (int i = snipeData.getVoxelHeight() - 1; i >= 0; i--) {
				for (int z = 2 * snipeData.getBrushSize(); z >= 0; z--) {
					int blockPositionX = targetBlock.getX();
					int blockPositionY = targetBlock.getY();
					int blockPositionZ = targetBlock.getZ();
					switch (spiral[x][i][z]) {
						case 0:
							if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != 0) {
								undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
							}
							this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, 0);
							break;
						case 1:
							if (this.stairtype.equalsIgnoreCase("block")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != snipeData.getVoxelId()) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, snipeData.getVoxelId());
							} else if (this.stairtype.equalsIgnoreCase("step")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != 44) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, 44);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData(snipeData.getData());
							} else if (this.stairtype.equalsIgnoreCase("woodstair") || this.stairtype.equalsIgnoreCase("cobblestair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != snipeData.getVoxelId()) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, snipeData.getVoxelId());
							}
							break;
						case 2:
							if (this.stairtype.equalsIgnoreCase("step")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != 43) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, 43);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData(snipeData.getData());
							} else if (this.stairtype.equalsIgnoreCase("woodstair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != 53) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() - x, blockPositionY + i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, 53);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) 0);
							} else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != 67) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, 67);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) 0);
							}
							break;
						default:
							if (this.stairtype.equalsIgnoreCase("woodstair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != 53) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, 53);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) (spiral[x][i][z] - 2));
							} else if (this.stairtype.equalsIgnoreCase("cobblestair")) {
								if (this.getBlockIdAt(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z) != 67) {
									undo.put(this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z));
								}
								this.setBlockIdAt(blockPositionZ - snipeData.getBrushSize() + z, blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, 67);
								this.clampY(blockPositionX - snipeData.getBrushSize() + x, blockPositionY - i, blockPositionZ - snipeData.getBrushSize() + z)
									.setData((byte) (spiral[x][i][z] - 2));
							}
							break;
					}
				}
			}
		}
		snipeData.getOwner()
			.storeUndo(undo);
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.digStairWell(snipeData, this.getTargetBlock()); // make stairwell below target
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.buildStairWell(snipeData, this.getLastBlock()); // make stairwell above target
	}

	@Override
	public final void info(Message message) {
		message.brushName("Spiral Staircase");
		message.size();
		message.blockDataType();
		message.height();
		message.blockData();
		message.custom(ChatColor.BLUE + "Staircase type: " + this.stairtype);
		message.custom(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
		message.custom(ChatColor.BLUE + "Staircase opens: " + this.sopen);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		if (parameters[1].equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "Spiral Staircase Parameters:");
			snipeData.sendMessage(ChatColor.AQUA + "/b sstair 'block' (default) | 'step' | 'woodstair' | 'cobblestair' -- set the type of staircase");
			snipeData.sendMessage(ChatColor.AQUA + "/b sstair 'c' (default) | 'cc' -- set the turning direction of staircase");
			snipeData.sendMessage(ChatColor.AQUA + "/b sstair 'n' (default) | 'e' | 's' | 'world' -- set the opening direction of staircase");
			return;
		}
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (Stream.of("block", "step", "woodstair", "cobblestair")
				.anyMatch(parameter::equalsIgnoreCase)) {
				this.stairtype = parameter;
				snipeData.sendMessage(ChatColor.BLUE + "Staircase type: " + this.stairtype);
			} else if (parameter.equalsIgnoreCase("c") || parameter.equalsIgnoreCase("cc")) {
				this.sdirect = parameter;
				snipeData.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
			} else if (Stream.of("n", "e", "s", "world")
				.anyMatch(parameter::equalsIgnoreCase)) {
				this.sopen = parameter;
				snipeData.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.spiralstaircase";
	}
}
