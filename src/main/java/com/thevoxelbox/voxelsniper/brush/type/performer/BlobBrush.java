package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.Random;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Blob_Brush
 *
 * @author Giltwist
 */
public class BlobBrush extends AbstractPerformerBrush {

	private static final int GROW_PERCENT_DEFAULT = 1000;
	private static final int GROW_PERCENT_MIN = 1;
	private static final int GROW_PERCENT_MAX = 9999;

	private Random randomGenerator = new Random();
	private int growPercent = GROW_PERCENT_DEFAULT; // chance block on recursion pass is made active

	public BlobBrush() {
		super("Blob");
	}

	private void checkValidGrowPercent(@Nullable SnipeData snipeData) {
		if (this.growPercent < GROW_PERCENT_MIN || this.growPercent > GROW_PERCENT_MAX) {
			this.growPercent = GROW_PERCENT_DEFAULT;
			if (snipeData == null) {
				return;
			}
			snipeData.sendMessage(ChatColor.BLUE + "Growth percent set to: 10%");
		}
	}

	private void digBlob(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		this.checkValidGrowPercent(snipeData);
		// Seed the array
		int brushSizeDoubled = 2 * brushSize;
		int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
		for (int x = brushSizeDoubled; x >= 0; x--) {
			for (int y = brushSizeDoubled; y >= 0; y--) {
				for (int z = brushSizeDoubled; z >= 0; z--) {
					if ((x == 0 || y == 0 | z == 0 || x == brushSizeDoubled || y == brushSizeDoubled || z == brushSizeDoubled) && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
						splat[x][y][z] = 0;
					} else {
						splat[x][y][z] = 1;
					}
				}
			}
		}
		// Grow the seed
		int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
		for (int r = 0; r < brushSize; r++) {
			for (int x = brushSizeDoubled; x >= 0; x--) {
				for (int y = brushSizeDoubled; y >= 0; y--) {
					for (int z = brushSizeDoubled; z >= 0; z--) {
						tempSplat[x][y][z] = splat[x][y][z];
						double growCheck = 0;
						if (splat[x][y][z] == 1) {
							if (x != 0 && splat[x - 1][y][z] == 0) {
								growCheck++;
							}
							if (y != 0 && splat[x][y - 1][z] == 0) {
								growCheck++;
							}
							if (z != 0 && splat[x][y][z - 1] == 0) {
								growCheck++;
							}
							if (x != 2 * brushSize && splat[x + 1][y][z] == 0) {
								growCheck++;
							}
							if (y != 2 * brushSize && splat[x][y + 1][z] == 0) {
								growCheck++;
							}
							if (z != 2 * brushSize && splat[x][y][z + 1] == 0) {
								growCheck++;
							}
						}
						if (growCheck >= 1 && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
							tempSplat[x][y][z] = 0; // prevent bleed into splat
						}
					}
				}
			}
			// shouldn't this just be splat = tempsplat;? -Gavjenks
			// integrate tempsplat back into splat at end of iteration
			for (int x = brushSizeDoubled; x >= 0; x--) {
				for (int y = brushSizeDoubled; y >= 0; y--) {
					System.arraycopy(tempSplat[x][y], 0, splat[x][y], 0, brushSizeDoubled + 1);
				}
			}
		}
		double rSquared = Math.pow(brushSize + 1, 2);
		// Make the changes
		for (int x = brushSizeDoubled; x >= 0; x--) {
			double xSquared = Math.pow(x - brushSize - 1, 2);
			for (int y = brushSizeDoubled; y >= 0; y--) {
				double ySquared = Math.pow(y - brushSize - 1, 2);
				for (int z = brushSizeDoubled; z >= 0; z--) {
					if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
						Block targetBlock = this.getTargetBlock();
						this.current.perform(this.clampY(targetBlock.getX() - brushSize + x, targetBlock.getY() - brushSize + z, targetBlock.getZ() - brushSize + y));
					}
				}
			}
		}
		snipeData.getOwner()
			.storeUndo(this.current.getUndo());
	}

	private void growBlob(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		this.checkValidGrowPercent(snipeData);
		// Seed the array
		int brushSizeDoubled = 2 * brushSize;
		int[][][] splat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
		splat[brushSize][brushSize][brushSize] = 1;
		// Grow the seed
		int[][][] tempSplat = new int[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
		for (int r = 0; r < brushSize; r++) {
			for (int x = brushSizeDoubled; x >= 0; x--) {
				for (int y = brushSizeDoubled; y >= 0; y--) {
					for (int z = brushSizeDoubled; z >= 0; z--) {
						tempSplat[x][y][z] = splat[x][y][z];
						int growCheck = 0;
						if (splat[x][y][z] == 0) {
							if (x != 0 && splat[x - 1][y][z] == 1) {
								growCheck++;
							}
							if (y != 0 && splat[x][y - 1][z] == 1) {
								growCheck++;
							}
							if (z != 0 && splat[x][y][z - 1] == 1) {
								growCheck++;
							}
							if (x != 2 * brushSize && splat[x + 1][y][z] == 1) {
								growCheck++;
							}
							if (y != 2 * brushSize && splat[x][y + 1][z] == 1) {
								growCheck++;
							}
							if (z != 2 * brushSize && splat[x][y][z + 1] == 1) {
								growCheck++;
							}
						}
						if (growCheck >= 1 && this.randomGenerator.nextInt(GROW_PERCENT_MAX + 1) <= this.growPercent) {
							// prevent bleed into splat
							tempSplat[x][y][z] = 1;
						}
					}
				}
			}
			// integrate tempsplat back into splat at end of iteration
			for (int x = brushSizeDoubled; x >= 0; x--) {
				for (int y = brushSizeDoubled; y >= 0; y--) {
					System.arraycopy(tempSplat[x][y], 0, splat[x][y], 0, brushSizeDoubled + 1);
				}
			}
		}
		double rSquared = Math.pow(brushSize + 1, 2);
		// Make the changes
		for (int x = brushSizeDoubled; x >= 0; x--) {
			double xSquared = Math.pow(x - brushSize - 1, 2);
			for (int y = brushSizeDoubled; y >= 0; y--) {
				double ySquared = Math.pow(y - brushSize - 1, 2);
				for (int z = brushSizeDoubled; z >= 0; z--) {
					if (splat[x][y][z] == 1 && xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
						Block targetBlock = this.getTargetBlock();
						this.current.perform(this.clampY(targetBlock.getX() - brushSize + x, targetBlock.getY() - brushSize + z, targetBlock.getZ() - brushSize + y));
					}
				}
			}
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.growBlob(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.digBlob(snipeData);
	}

	@Override
	public final void info(Messages messages) {
		this.checkValidGrowPercent(null);
		messages.brushName(this.getName());
		messages.size();
		messages.custom(ChatColor.BLUE + "Growth percent set to: " + this.growPercent / 100 + "%");
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Blob brush Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b blob g[int] -- set a growth percentage (" + GROW_PERCENT_MIN + "-" + GROW_PERCENT_MAX + ").  Default is " + GROW_PERCENT_DEFAULT);
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'g') {
				int temp = Integer.parseInt(parameter.replace("g", ""));
				if (temp >= GROW_PERCENT_MIN && temp <= GROW_PERCENT_MAX) {
					snipeData.sendMessage(ChatColor.AQUA + "Growth percent set to: " + (float) temp / 100 + "%");
					this.growPercent = temp;
				} else {
					snipeData.sendMessage(ChatColor.RED + "Growth percent must be an integer " + GROW_PERCENT_MIN + "-" + GROW_PERCENT_MAX + "!");
				}
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.blob";
	}
}
