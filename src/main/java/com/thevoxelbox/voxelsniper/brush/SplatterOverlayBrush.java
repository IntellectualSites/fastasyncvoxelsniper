package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Splatter_Overlay_Brush
 *
 * @author Gavjenks Splatterized blockPositionY Giltwist
 */
public class SplatterOverlayBrush extends PerformBrush {

	private static final int GROW_PERCENT_MIN = 1;
	private static final int GROW_PERCENT_DEFAULT = 1000;
	private static final int GROW_PERCENT_MAX = 9999;
	private static final int SEED_PERCENT_MIN = 1;
	private static final int SEED_PERCENT_DEFAULT = 1000;
	private static final int SEED_PERCENT_MAX = 9999;
	private static final int SPLATREC_PERCENT_MIN = 1;
	private static final int SPLATREC_PERCENT_DEFAULT = 3;
	private static final int SPLATREC_PERCENT_MAX = 10;
	private int seedPercent; // Chance block on first pass is made active
	private int growPercent; // chance block on recursion pass is made active
	private int splatterRecursions; // How many times you grow the seeds
	private int yOffset;
	private boolean randomizeHeight;
	private Random generator = new Random();
	private int depth = 3;
	private boolean allBlocks;

	/**
	 *
	 */
	public SplatterOverlayBrush() {
		this.setName("Splatter Overlay");
	}

	@SuppressWarnings("deprecation")
	private void sOverlay(SnipeData v) {
		// Splatter Time
		int[][] splat = new int[2 * v.getBrushSize() + 1][2 * v.getBrushSize() + 1];
		// Seed the array
		for (int x = 2 * v.getBrushSize(); x >= 0; x--) {
			for (int y = 2 * v.getBrushSize(); y >= 0; y--) {
				if (this.generator.nextInt(SEED_PERCENT_MAX + 1) <= this.seedPercent) {
					splat[x][y] = 1;
				}
			}
		}
		// Grow the seeds
		int gref = this.growPercent;
		int[][] tempSplat = new int[2 * v.getBrushSize() + 1][2 * v.getBrushSize() + 1];
		for (int r = 0; r < this.splatterRecursions; r++) {
			this.growPercent = gref - ((gref / this.splatterRecursions) * (r));
			for (int x = 2 * v.getBrushSize(); x >= 0; x--) {
				for (int y = 2 * v.getBrushSize(); y >= 0; y--) {
					tempSplat[x][y] = splat[x][y]; // prime tempsplat
					int growcheck = 0;
					if (splat[x][y] == 0) {
						if (x != 0 && splat[x - 1][y] == 1) {
							growcheck++;
						}
						if (y != 0 && splat[x][y - 1] == 1) {
							growcheck++;
						}
						if (x != 2 * v.getBrushSize() && splat[x + 1][y] == 1) {
							growcheck++;
						}
						if (y != 2 * v.getBrushSize() && splat[x][y + 1] == 1) {
							growcheck++;
						}
					}
					if (growcheck >= 1 && this.generator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
						tempSplat[x][y] = 1; // prevent bleed into splat
					}
				}
			}
			// integrate tempsplat back into splat at end of iteration
			for (int x = 2 * v.getBrushSize(); x >= 0; x--) {
				if (2 * v.getBrushSize() + 1 >= 0)
					System.arraycopy(tempSplat[x], 0, splat[x], 0, 2 * v.getBrushSize() + 1);
			}
		}
		this.growPercent = gref;
		int[][] memory = new int[2 * v.getBrushSize() + 1][2 * v.getBrushSize() + 1];
		double brushSizeSquared = Math.pow(v.getBrushSize() + 0.5, 2);
		for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
			for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
				for (int y = this.getTargetBlock()
					.getY(); y > 0; y--) {
					// start scanning from the height you clicked at
					if (memory[x + v.getBrushSize()][z + v.getBrushSize()] != 1) {
						// if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared && splat[x + v.getBrushSize()][z + v.getBrushSize()] == 1) {
							// if inside of the column && if to be splattered
							int check = this.getBlockIdAt(this.getTargetBlock()
								.getX() + x, y + 1, this.getTargetBlock()
								.getZ() + z);
							if (check == 0 || check == 8 || check == 9) {
								// must start at surface... this prevents it filling stuff in if you click in a wall
								// and it starts out below surface.
								if (!this.allBlocks) {
									// if the override parameter has not been activated, go to the switch that filters out manmade stuff.
									switch (this.getBlockIdAt(this.getTargetBlock()
										.getX() + x, y, this.getTargetBlock()
										.getZ() + z)) {
										case 1:
										case 2:
										case 3:
										case 12:
										case 13:
										case 24:// These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to mess with.
										case 48:
										case 82:
										case 49:
										case 78:
											int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
											for (int d = this.depth - 1; ((this.depth - d) <= depth); d--) {
												if (this.clampY(this.getTargetBlock()
													.getX() + x, y - d, this.getTargetBlock()
													.getZ() + z)
													.getTypeId() != 0) {
													// fills down as many layers as you specify in parameters
													this.current.perform(this.clampY(this.getTargetBlock()
														.getX() + x, y - d + this.yOffset, this.getTargetBlock()
														.getZ() + z));
													// stop it from checking any other blocks in this vertical 1x1 column.
													memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1;
												}
											}
											break;
										default:
											break;
									}
								} else {
									int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
									for (int d = this.depth - 1; ((this.depth - d) <= depth); d--) {
										if (this.clampY(this.getTargetBlock()
											.getX() + x, y - d, this.getTargetBlock()
											.getZ() + z)
											.getTypeId() != 0) {
											// fills down as many layers as you specify in parameters
											this.current.perform(this.clampY(this.getTargetBlock()
												.getX() + x, y - d + this.yOffset, this.getTargetBlock()
												.getZ() + z));
											// stop it from checking any other blocks in this vertical 1x1 column.
											memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	private void soverlayTwo(SnipeData v) {
		// Splatter Time
		int[][] splat = new int[2 * v.getBrushSize() + 1][2 * v.getBrushSize() + 1];
		// Seed the array
		for (int x = 2 * v.getBrushSize(); x >= 0; x--) {
			for (int y = 2 * v.getBrushSize(); y >= 0; y--) {
				if (this.generator.nextInt(SEED_PERCENT_MAX + 1) <= this.seedPercent) {
					splat[x][y] = 1;
				}
			}
		}
		// Grow the seeds
		int gref = this.growPercent;
		int[][] tempsplat = new int[2 * v.getBrushSize() + 1][2 * v.getBrushSize() + 1];
		for (int r = 0; r < this.splatterRecursions; r++) {
			this.growPercent = gref - ((gref / this.splatterRecursions) * (r));
			for (int x = 2 * v.getBrushSize(); x >= 0; x--) {
				for (int y = 2 * v.getBrushSize(); y >= 0; y--) {
					tempsplat[x][y] = splat[x][y]; // prime tempsplat
					int growcheck = 0;
					if (splat[x][y] == 0) {
						if (x != 0 && splat[x - 1][y] == 1) {
							growcheck++;
						}
						if (y != 0 && splat[x][y - 1] == 1) {
							growcheck++;
						}
						if (x != 2 * v.getBrushSize() && splat[x + 1][y] == 1) {
							growcheck++;
						}
						if (y != 2 * v.getBrushSize() && splat[x][y + 1] == 1) {
							growcheck++;
						}
					}
					if (growcheck >= 1 && this.generator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
						tempsplat[x][y] = 1; // prevent bleed into splat
					}
				}
			}
			// integrate tempsplat back into splat at end of iteration
			for (int x = 2 * v.getBrushSize(); x >= 0; x--) {
				if (2 * v.getBrushSize() + 1 >= 0)
					System.arraycopy(tempsplat[x], 0, splat[x], 0, 2 * v.getBrushSize() + 1);
			}
		}
		this.growPercent = gref;
		int[][] memory = new int[v.getBrushSize() * 2 + 1][v.getBrushSize() * 2 + 1];
		double brushSizeSquared = Math.pow(v.getBrushSize() + 0.5, 2);
		for (int z = v.getBrushSize(); z >= -v.getBrushSize(); z--) {
			for (int x = v.getBrushSize(); x >= -v.getBrushSize(); x--) {
				for (int y = this.getTargetBlock()
					.getY(); y > 0; y--) { // start scanning from the height you clicked at
					if (memory[x + v.getBrushSize()][z + v.getBrushSize()] != 1) { // if haven't already found the surface in this column
						if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared && splat[x + v.getBrushSize()][z + v.getBrushSize()] == 1) { // if inside of the column...&& if to be splattered
							if (this.getBlockIdAt(this.getTargetBlock()
								.getX() + x, y - 1, this.getTargetBlock()
								.getZ() + z) != 0) { // if not a floating block (like one of Notch'world pools)
								if (this.getBlockIdAt(this.getTargetBlock()
									.getX() + x, y + 1, this.getTargetBlock()
									.getZ() + z) == 0) { // must start at surface... this prevents it filling stuff in if
									// you click in a wall and it starts out below surface.
									if (!this.allBlocks) { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
										switch (this.getBlockIdAt(this.getTargetBlock()
											.getX() + x, y, this.getTargetBlock()
											.getZ() + z)) {
											case 1:
											case 2:
											case 3:
											case 12:
											case 13:
											case 14: // These cases filter out any manufactured or refined blocks, any trees and leas, etc. that you don't want to
												// mess with.
											case 15:
											case 16:
											case 24:
											case 48:
											case 82:
											case 49:
											case 78:
												int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
												for (int d = 1; (d < depth + 1); d++) {
													this.current.perform(this.clampY(this.getTargetBlock()
														.getX() + x, y + d + this.yOffset, this.getTargetBlock()
														.getZ() + z)); // fills down as many layers as you specify
													// in parameters
													memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
												}
												break;
											default:
												break;
										}
									} else {
										int depth = this.randomizeHeight ? this.generator.nextInt(this.depth) : this.depth;
										for (int d = 1; (d < depth + 1); d++) {
											this.current.perform(this.clampY(this.getTargetBlock()
												.getX() + x, y + d + this.yOffset, this.getTargetBlock()
												.getZ() + z)); // fills down as many layers as you specify in
											// parameters
											memory[x + v.getBrushSize()][z + v.getBrushSize()] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
										}
									}
								}
							}
						}
					}
				}
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.sOverlay(v);
	}

	@Override
	protected final void powder(SnipeData v) {
		this.soverlayTwo(v);
	}

	@Override
	public final void info(Message message) {
		if (this.seedPercent < SEED_PERCENT_MIN || this.seedPercent > SEED_PERCENT_MAX) {
			this.seedPercent = SEED_PERCENT_DEFAULT;
		}
		if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX) {
			this.growPercent = GROW_PERCENT_DEFAULT;
		}
		if (this.splatterRecursions < SPLATREC_PERCENT_MIN || this.splatterRecursions > SPLATREC_PERCENT_MAX) {
			this.splatterRecursions = SPLATREC_PERCENT_DEFAULT;
		}
		message.brushName(this.getName());
		message.size();
		message.custom(ChatColor.BLUE + "Seed percent set to: " + this.seedPercent / 100 + "%");
		message.custom(ChatColor.BLUE + "Growth percent set to: " + this.growPercent / 100 + "%");
		message.custom(ChatColor.BLUE + "Recursions set to: " + this.splatterRecursions);
		message.custom(ChatColor.BLUE + "Y-Offset set to: " + this.yOffset);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					snipeData.sendMessage(ChatColor.GOLD + "Splatter Overlay brush parameters:");
					snipeData.sendMessage(ChatColor.AQUA + "d[number] (ex:  d3) How many blocks deep you want to replace from the surface.");
					snipeData.sendMessage(ChatColor.BLUE + "all (ex:  /b over all) Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).  The parameter /some will set it back to default.");
					snipeData.sendMessage(ChatColor.AQUA + "/b sover s[int] -- set a seed percentage (1-9999). 100 = 1% Default is 1000");
					snipeData.sendMessage(ChatColor.AQUA + "/b sover g[int] -- set a growth percentage (1-9999).  Default is 1000");
					snipeData.sendMessage(ChatColor.AQUA + "/b sover r[int] -- set a recursion (1-10).  Default is 3");
					return;
				} else if (parameter.startsWith("d")) {
					this.depth = Integer.parseInt(parameter.replace("d", ""));
					snipeData.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
					if (this.depth < 1) {
						this.depth = 1;
					}
				} else if (parameter.startsWith("all")) {
					this.allBlocks = true;
					snipeData.sendMessage(ChatColor.BLUE + "Will overlay over any block." + this.depth);
				} else if (parameter.startsWith("some")) {
					this.allBlocks = false;
					snipeData.sendMessage(ChatColor.BLUE + "Will overlay only natural block types." + this.depth);
				} else if (parameters[i].startsWith("s")) {
					double temp = Integer.parseInt(parameter.replace("s", ""));
					if (temp >= SEED_PERCENT_MIN && temp <= SEED_PERCENT_MAX) {
						snipeData.sendMessage(ChatColor.AQUA + "Seed percent set to: " + temp / 100 + "%");
						this.seedPercent = (int) temp;
					} else {
						snipeData.sendMessage(ChatColor.RED + "Seed percent must be an integer 1-9999!");
					}
				} else if (parameter.startsWith("g")) {
					double temp = Integer.parseInt(parameter.replace("g", ""));
					if (temp >= GROW_PERCENT_MIN && temp <= GROW_PERCENT_MAX) {
						snipeData.sendMessage(ChatColor.AQUA + "Growth percent set to: " + temp / 100 + "%");
						this.growPercent = (int) temp;
					} else {
						snipeData.sendMessage(ChatColor.RED + "Growth percent must be an integer 1-9999!");
					}
				} else if (parameter.startsWith("randh")) {
					this.randomizeHeight = !this.randomizeHeight;
					snipeData.sendMessage(ChatColor.RED + "RandomizeHeight set to: " + this.randomizeHeight);
				} else if (parameter.startsWith("r")) {
					int temp = Integer.parseInt(parameter.replace("r", ""));
					if (temp >= SPLATREC_PERCENT_MIN && temp <= SPLATREC_PERCENT_MAX) {
						snipeData.sendMessage(ChatColor.AQUA + "Recursions set to: " + temp);
						this.splatterRecursions = temp;
					} else {
						snipeData.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
					}
				} else if (parameter.startsWith("yoff")) {
					int temp = Integer.parseInt(parameter.replace("yoff", ""));
					if (temp >= SPLATREC_PERCENT_MIN && temp <= SPLATREC_PERCENT_MAX) {
						snipeData.sendMessage(ChatColor.AQUA + "Y-Offset set to: " + temp);
						this.yOffset = temp;
					} else {
						snipeData.sendMessage(ChatColor.RED + "Recursions must be an integer 1-10!");
					}
				} else {
					snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
				}
			} catch (NumberFormatException exception) {
				snipeData.sendMessage(String.format("An error occured while processing parameter %s.", parameter));
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.splatteroverlay";
	}
}
