package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;

import java.util.stream.Stream;

public class SpiralStaircaseBrush extends AbstractBrush {

	private String stairType = "block"; // "block" 1x1 blocks (default), "step" alternating step double step, "stair" staircase with blocks on corners
	private String sdirect = "c"; // "c" clockwise (default), "cc" counter-clockwise
	private String sopen = "n"; // "n" north (default), "e" east, "world" south, "world" west

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.GOLD + "Spiral Staircase Parameters:");
			messenger.sendMessage(ChatColor.AQUA + "/b sstair 'block' (default) | 'step' | 'woodstair' | 'cobblestair' -- set the type of staircase");
			messenger.sendMessage(ChatColor.AQUA + "/b sstair 'c' (default) | 'cc' -- set the turning direction of staircase");
			messenger.sendMessage(ChatColor.AQUA + "/b sstair 'n' (default) | 'e' | 's' | 'world' -- set the opening direction of staircase");
			return;
		}
		for (String parameter : parameters) {
			if (Stream.of("block", "step", "woodstair", "cobblestair")
				.anyMatch(parameter::equalsIgnoreCase)) {
				this.stairType = parameter;
				messenger.sendMessage(ChatColor.BLUE + "Staircase type: " + this.stairType);
			} else if (parameter.equalsIgnoreCase("c") || parameter.equalsIgnoreCase("cc")) {
				this.sdirect = parameter;
				messenger.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
			} else if (Stream.of("n", "e", "s", "world")
				.anyMatch(parameter::equalsIgnoreCase)) {
				this.sopen = parameter;
				messenger.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
			} else {
				messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		digStairWell(snipe, targetBlock); // make stairwell below target
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		buildStairWell(snipe, lastBlock); // make stairwell above target
	}

	private void buildStairWell(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		SnipeMessenger messenger = snipe.createMessenger();
		int voxelHeight = toolkitProperties.getVoxelHeight();
		if (voxelHeight < 1) {
			toolkitProperties.setVoxelHeight(1);
			messenger.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
		}
		int brushSize = toolkitProperties.getBrushSize();
		// locate first block in staircase
		// Note to self, fix these
		int startX;
		int startZ;
		if (this.sdirect.equalsIgnoreCase("cc")) {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 2 * brushSize;
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * brushSize;
				startZ = 0;
			} else {
				startX = 2 * brushSize;
				startZ = 2 * brushSize;
			}
		} else {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 2 * brushSize;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * brushSize;
				startZ = 2 * brushSize;
			} else {
				startX = 0;
				startZ = 2 * brushSize;
			}
		}
		int toggle = 0;
		int zOffset = 0;
		int xOffset = 0;
		int y = 0;
		int[][][] spiral = new int[2 * brushSize + 1][voxelHeight][2 * brushSize + 1];
		while (y < voxelHeight) {
			if (this.stairType.equalsIgnoreCase("block")) {
				// 1x1x1 voxel material steps
				spiral[startX + xOffset][y][startZ + zOffset] = 1;
				y++;
			} else if (this.stairType.equalsIgnoreCase("step")) {
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
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset++;
					} else {
						zOffset++;
					}
				} else if (startZ + zOffset == 2 * brushSize) { // NORTHWEST
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset--;
					} else {
						xOffset++;
					}
				} else { // JUST PLAIN NORTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset--;
					} else {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset++;
					}
				}
			} else if (startX + xOffset == 2 * brushSize) { // ALL SOUTH
				if (startZ + zOffset == 0) { // SOUTHEAST
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset++;
					} else {
						xOffset--;
					}
				} else if (startZ + zOffset == 2 * brushSize) { // SOUTHWEST
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset--;
					} else {
						zOffset--;
					}
				} else { // JUST PLAIN SOUTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset++;
					} else {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset--;
					}
				}
			} else if (startZ + zOffset == 0) { // JUST PLAIN EAST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset++;
				} else {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset--;
				}
			} else { // JUST PLAIN WEST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset--;
				} else {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset++;
				}
			}
		}
		Undo undo = new Undo();
		// Make the changes
		for (int x = 2 * brushSize; x >= 0; x--) {
			for (int i = voxelHeight - 1; i >= 0; i--) {
				for (int z = 2 * brushSize; z >= 0; z--) {
					int blockPositionX = targetBlock.getX();
					int blockPositionY = targetBlock.getY();
					int blockPositionZ = targetBlock.getZ();
					BlockVector3 position = BlockVector3.at(blockPositionX - brushSize + x, blockPositionY + i, blockPositionZ - brushSize + z);
					Material blockType = getBlockType(position);
					if (spiral[x][i][z] == 0) {
						if (i == voxelHeight - 1) {
							if (!Materials.isEmpty(blockType)) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.AIR);
						} else {
							if (!((this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) && spiral[x][i + 1][z] == 1)) {
								if (!Materials.isEmpty(blockType)) {
									undo.put(clampY(position));
								}
								setBlockType(position, Material.AIR);
							}
						}
					} else if (spiral[x][i][z] == 1) {
						if (this.stairType.equalsIgnoreCase("block")) {
							if (blockType != toolkitProperties.getBlockType()) {
								undo.put(clampY(position));
							}
							setBlockType(position, toolkitProperties.getBlockType());
						} else if (this.stairType.equalsIgnoreCase("step")) {
							if (!Tag.SLABS.isTagged(blockType)) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.OAK_SLAB);
							clampY(position).setBlockData(toolkitProperties.getBlockData());
						} else if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							if (getBlockType(blockPositionX - brushSize + x, blockPositionY + i - 1, blockPositionZ - brushSize + z) != toolkitProperties.getBlockType()) {
								undo.put(clampY(blockPositionX - brushSize + x, blockPositionY + i - 1, blockPositionZ - brushSize + z));
							}
							setBlockType(blockPositionX - brushSize + x, blockPositionY + i - 1, blockPositionZ - brushSize + z, toolkitProperties.getBlockType());
						}
					} else if (spiral[x][i][z] == 2) {
						if (this.stairType.equalsIgnoreCase("step")) {
							BlockData blockData = getBlockData(position);
							if (!Tag.SLABS.isTagged(blockType) && blockData instanceof Slab && ((Slab) blockData).getType() == Type.DOUBLE) {
								undo.put(clampY(position));
							}
							setBlockData(position, Material.OAK_SLAB.createBlockData(data -> ((Slab) data).setType(Type.DOUBLE)));
							clampY(position).setBlockData(toolkitProperties.getBlockData());
						} else if (this.stairType.equalsIgnoreCase("woodstair")) {
							if (!Tag.WOODEN_STAIRS.isTagged(blockType)) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.OAK_STAIRS);
							clampY(position).setBlockData(Material.OAK_STAIRS.createBlockData(data -> ((Directional) data).setFacing(BlockFace.EAST)));
						} else if (this.stairType.equalsIgnoreCase("cobblestair")) {
							if (blockType != Material.COBBLESTONE_STAIRS) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.COBBLESTONE_STAIRS);
							clampY(position).setBlockData(Material.COBBLESTONE_STAIRS.createBlockData(data -> ((Directional) data).setFacing(BlockFace.EAST)));
						}
					} else {
						if (this.stairType.equalsIgnoreCase("woodstair")) {
							if (!Tag.WOODEN_STAIRS.isTagged(blockType)) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.OAK_STAIRS);
							setStairsDirection(position, spiral[x][i][z] - 2);
						} else if (this.stairType.equalsIgnoreCase("cobblestair")) {
							if (blockType != Material.COBBLESTONE_STAIRS) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.COBBLESTONE_STAIRS);
							setStairsDirection(position, spiral[x][i][z] - 2);
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private void digStairWell(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		SnipeMessenger messenger = snipe.createMessenger();
		int voxelHeight = toolkitProperties.getVoxelHeight();
		if (voxelHeight < 1) {
			toolkitProperties.setVoxelHeight(1);
			messenger.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
		}
		// initialize array
		int brushSize = toolkitProperties.getBrushSize();
		// locate first block in staircase
		// Note to self, fix these
		int startX;
		int startZ;
		if (this.sdirect.equalsIgnoreCase("cc")) {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 2 * brushSize;
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * brushSize;
				startZ = 0;
			} else {
				startX = 2 * brushSize;
				startZ = 2 * brushSize;
			}
		} else {
			if (this.sopen.equalsIgnoreCase("n")) {
				startX = 0;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("e")) {
				startX = 2 * brushSize;
				startZ = 0;
			} else if (this.sopen.equalsIgnoreCase("s")) {
				startX = 2 * brushSize;
				startZ = 2 * brushSize;
			} else {
				startX = 0;
				startZ = 2 * brushSize;
			}
		}
		int toggle = 0;
		int zOffset = 0;
		int xOffset = 0;
		int y = 0;
		int[][][] spiral = new int[2 * brushSize + 1][voxelHeight][2 * brushSize + 1];
		while (y < voxelHeight) {
			if (this.stairType.equalsIgnoreCase("block")) {
				// 1x1x1 voxel material steps
				spiral[startX + xOffset][y][startZ + zOffset] = 1;
				y++;
			} else if (this.stairType.equalsIgnoreCase("step")) {
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
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset++;
					} else {
						zOffset++;
					}
				} else if (startZ + zOffset == 2 * brushSize) { // NORTHWEST
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset--;
					} else {
						xOffset++;
					}
				} else { // JUST PLAIN NORTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset--;
					} else {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset++;
					}
				}
			} else if (startX + xOffset == 2 * brushSize) { // ALL SOUTH
				if (startZ + zOffset == 0) { // SOUTHEAST
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						zOffset++;
					} else {
						xOffset--;
					}
				} else if (startZ + zOffset == 2 * brushSize) { // SOUTHWEST
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 1;
					}
					if (this.sdirect.equalsIgnoreCase("c")) {
						xOffset--;
					} else {
						zOffset--;
					}
				} else { // JUST PLAIN SOUTH
					if (this.sdirect.equalsIgnoreCase("c")) {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 5;
							y++;
						}
						zOffset++;
					} else {
						if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							spiral[startX + xOffset][y][startZ + zOffset] = 4;
							y++;
						}
						zOffset--;
					}
				}
			} else if (startZ + zOffset == 0) { // JUST PLAIN EAST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset++;
				} else {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset--;
				}
			} else { // JUST PLAIN WEST
				if (this.sdirect.equalsIgnoreCase("c")) {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 2;
						y++;
					}
					xOffset--;
				} else {
					if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
						spiral[startX + xOffset][y][startZ + zOffset] = 3;
						y++;
					}
					xOffset++;
				}
			}
		}
		Undo undo = new Undo();
		// Make the changes
		for (int x = 2 * brushSize; x >= 0; x--) {
			for (int i = voxelHeight - 1; i >= 0; i--) {
				for (int z = 2 * brushSize; z >= 0; z--) {
					int blockPositionX = targetBlock.getX();
					int blockPositionY = targetBlock.getY();
					int blockPositionZ = targetBlock.getZ();
					BlockVector3 position = BlockVector3.at(blockPositionX - brushSize + x, blockPositionY - i, blockPositionZ - brushSize + z);
					Material blockType = getBlockType(position);
					if (spiral[x][i][z] == 0) {
						if (!Materials.isEmpty(blockType)) {
							undo.put(clampY(position));
						}
						setBlockType(position, Material.AIR);
					} else if (spiral[x][i][z] == 1) {
						if (this.stairType.equalsIgnoreCase("block")) {
							if (blockType != toolkitProperties.getBlockType()) {
								undo.put(clampY(position));
							}
							setBlockType(position, toolkitProperties.getBlockType());
						} else if (this.stairType.equalsIgnoreCase("step")) {
							if (!Tag.SLABS.isTagged(blockType)) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.OAK_SLAB);
							clampY(position).setBlockData(toolkitProperties.getBlockData());
						} else if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
							if (blockType != toolkitProperties.getBlockType()) {
								undo.put(clampY(position));
							}
							setBlockType(position, toolkitProperties.getBlockType());
						}
					} else if (spiral[x][i][z] == 2) {
						if (this.stairType.equalsIgnoreCase("step")) {
							BlockData blockData = getBlockData(position);
							if (!Tag.SLABS.isTagged(blockType) && blockData instanceof Slab && ((Slab) blockData).getType() == Type.DOUBLE) {
								undo.put(clampY(position));
							}
							setBlockData(position, Material.OAK_SLAB.createBlockData(data -> ((Slab) data).setType(Type.DOUBLE)));
							clampY(position).setBlockData(toolkitProperties.getBlockData());
						} else if (this.stairType.equalsIgnoreCase("woodstair")) {
							if (!Tag.WOODEN_STAIRS.isTagged(blockType)) {
								undo.put(clampY(blockPositionX - brushSize - x, blockPositionY + i, blockPositionZ - brushSize + z));
							}
							setBlockType(position, Material.OAK_STAIRS);
							clampY(position).setBlockData(Material.OAK_STAIRS.createBlockData(data -> ((Directional) data).setFacing(BlockFace.EAST)));
						} else if (this.stairType.equalsIgnoreCase("cobblestair")) {
							if (blockType != Material.COBBLESTONE_STAIRS) {
								undo.put(clampY(position));
							}
							setBlockType(position, Material.COBBLESTONE_STAIRS);
							clampY(position).setBlockData(Material.COBBLESTONE_STAIRS.createBlockData(data -> ((Directional) data).setFacing(BlockFace.EAST)));
						}
					} else {
						if (this.stairType.equalsIgnoreCase("woodstair")) {
							if (!Tag.WOODEN_STAIRS.isTagged(blockType)) {
								undo.put(this.clampY(position));
							}
							setBlockType(position, Material.OAK_STAIRS);
							setStairsDirection(position, spiral[x][i][z] - 2);
						} else if (this.stairType.equalsIgnoreCase("cobblestair")) {
							if (blockType != Material.COBBLESTONE_STAIRS) {
								undo.put(this.clampY(position));
							}
							setBlockType(position, Material.COBBLESTONE_STAIRS);
							setStairsDirection(position, spiral[x][i][z] - 2);
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private void setStairsDirection(BlockVector3 position, int data) {
		Block block = clampY(position);
		BlockData blockData = block.getBlockData();
		if (!(blockData instanceof Directional)) {
			return;
		}
		Directional directional = (Directional) blockData;
		BlockFace direction = dataToDirection(data);
		directional.setFacing(direction);
		block.setBlockData(blockData);
	}

	private BlockFace dataToDirection(int data) {
		return switch (data) {
			case 3 -> BlockFace.NORTH;
			case 2 -> BlockFace.SOUTH;
			case 1 -> BlockFace.WEST;
			default -> BlockFace.EAST;
		};
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendBlockTypeMessage();
		messenger.sendVoxelHeightMessage();
		messenger.sendBlockDataMessage();
		messenger.sendMessage(ChatColor.BLUE + "Staircase type: " + this.stairType);
		messenger.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
		messenger.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
	}
}
