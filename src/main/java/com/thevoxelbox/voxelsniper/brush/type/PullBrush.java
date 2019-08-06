package com.thevoxelbox.voxelsniper.brush.type;

import java.util.HashSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.NumericParser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class PullBrush extends AbstractBrush {

	private Set<PullBrushBlockWrapper> surface = new HashSet<>();
	private int voxelHeight;
	private double c1 = 1;
	private double c2;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		Double pinch = NumericParser.parseDouble(parameters[0]);
		Double bubble = NumericParser.parseDouble(parameters[1]);
		if (pinch == null || bubble == null) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(ChatColor.RED + "Invalid brush parameters!");
			return;
		}
		this.c1 = 1 - pinch;
		this.c2 = bubble;
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.voxelHeight = toolkitProperties.getVoxelHeight();
		getSurface(toolkitProperties);
		if (this.voxelHeight > 0) {
			for (PullBrushBlockWrapper block : this.surface) {
				setBlock(block);
			}
		} else if (this.voxelHeight < 0) {
			for (PullBrushBlockWrapper block : this.surface) {
				setBlockDown(block);
			}
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.voxelHeight = toolkitProperties.getVoxelHeight();
		this.surface.clear();
		int lastY;
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
		// Are we pulling up ?
		Block targetBlock = getTargetBlock();
		World world = getWorld();
		if (this.voxelHeight > 0) {
			// Z - Axis
			for (int z = -brushSize; z <= brushSize; z++) {
				int zSquared = z * z;
				int actualZ = targetBlock.getZ() + z;
				// X - Axis
				for (int x = -brushSize; x <= brushSize; x++) {
					int xSquared = x * x;
					int actualX = targetBlock.getX() + x;
					// Down the Y - Axis
					for (int y = brushSize; y >= -brushSize; y--) {
						double volume = zSquared + xSquared + (y * y);
						// Is this in the range of the brush?
						if (volume <= brushSizeSquared && !world.getBlockAt(actualX, targetBlock.getY() + y, actualZ).getType().isEmpty()) {
							int actualY = targetBlock.getY() + y;
							// Starting strength and new Position
							double str = this.getStr(volume / brushSizeSquared);
							int lastStr = (int) (this.voxelHeight * str);
							lastY = actualY + lastStr;
							Block clamp = clampY(actualX, lastY, actualZ);
							clamp.setType(world.getBlockAt(actualX, actualY, actualZ)
								.getType());
							if (Double.compare(str, 1.0) == 0) {
								str = 0.8;
							}
							while (lastStr > 0) {
								if (actualY < targetBlock.getY()) {
									str *= str;
								}
								lastStr = (int) (this.voxelHeight * str);
								int newY = actualY + lastStr;
								Block block = world.getBlockAt(actualX, actualY, actualZ);
								for (int i = newY; i < lastY; i++) {
									Block clamp2 = clampY(actualX, i, actualZ);
									clamp2.setType(block.getType());
								}
								lastY = newY;
								actualY--;
							}
							break;
						}
					}
				}
			}
		} else {
			for (int z = -brushSize; z <= brushSize; z++) {
				double zSquared = Math.pow(z, 2);
				int actualZ = targetBlock.getZ() + z;
				for (int x = -brushSize; x <= brushSize; x++) {
					double xSquared = Math.pow(x, 2);
					int actualX = targetBlock.getX() + x;
					for (int y = -brushSize; y <= brushSize; y++) {
						double volume = (xSquared + Math.pow(y, 2) + zSquared);
						if (volume <= brushSizeSquared && !world.getBlockAt(actualX, targetBlock.getY() + y, actualZ).getType().isEmpty()) {
							int actualY = targetBlock.getY() + y;
							lastY = actualY + (int) (this.voxelHeight * this.getStr(volume / brushSizeSquared));
							Block clamp = clampY(actualX, lastY, actualZ);
							Block block = world.getBlockAt(actualX, actualY, actualZ);
							clamp.setType(block.getType());
							y++;
							double volume2 = (xSquared + Math.pow(y, 2) + zSquared);
							while (volume2 <= brushSizeSquared) {
								int blockY = targetBlock.getY() + y + (int) (this.voxelHeight * this.getStr(volume2 / brushSizeSquared));
								Block block2 = world.getBlockAt(actualX, targetBlock.getY() + y, actualZ);
								for (int i = blockY; i < lastY; i++) {
									Block clamp2 = clampY(actualX, i, actualZ);
									clamp2.setType(block2.getType());
								}
								lastY = blockY;
								y++;
								volume2 = (xSquared + Math.pow(y, 2) + zSquared);
							}
							break;
						}
					}
				}
			}
		}
	}

	private double getStr(double t) {
		double lt = 1 - t;
		return (lt * lt * lt) + 3 * (lt * lt) * t * this.c1 + 3 * lt * (t * t) * this.c2; // My + (t * ((By + (t * ((c2 + (t * (0 - c2))) - By))) - My));
	}

	private void getSurface(ToolkitProperties toolkitProperties) {
		this.surface.clear();
		int brushSize = toolkitProperties.getBrushSize();
		double bSquared = Math.pow(brushSize + 0.5, 2);
		for (int z = -brushSize; z <= brushSize; z++) {
			double zSquared = Math.pow(z, 2);
			Block targetBlock = getTargetBlock();
			int actualZ = targetBlock.getZ() + z;
			for (int x = -brushSize; x <= brushSize; x++) {
				double xSquared = Math.pow(x, 2);
				int actualX = targetBlock.getX() + x;
				for (int y = -brushSize; y <= brushSize; y++) {
					double volume = (xSquared + Math.pow(y, 2) + zSquared);
					if (volume <= bSquared) {
						if (this.isSurface(actualX, targetBlock.getY() + y, actualZ)) {
							this.surface.add(new PullBrushBlockWrapper(this.clampY(actualX, targetBlock.getY() + y, actualZ), this.getStr(((volume / bSquared)))));
						}
					}
				}
			}
		}
	}

	private boolean isSurface(int x, int y, int z) {
		return !isEmpty(x, y, z) && (isEmpty(x, y - 1, z) || isEmpty(x, y + 1, z) || isEmpty(x + 1, y, z) || isEmpty(x - 1, y, z) || isEmpty(x, y, z + 1) || isEmpty(x, y, z - 1));
	}

	private boolean isEmpty(int x, int y, int i) {
		Material type = getBlockType(x, y, i);
		return type.isEmpty();
	}

	private void setBlock(PullBrushBlockWrapper block) {
		Block currentBlock = this.clampY(block.getX(), block.getY() + (int) (this.voxelHeight * block.getStr()), block.getZ());
		if (getBlockType(block.getX(), block.getY() - 1, block.getZ()).isEmpty()) {
			currentBlock.setBlockData(block.getBlockData());
			for (int y = block.getY(); y < currentBlock.getY(); y++) {
				setBlockType(block.getX(), y, block.getZ(), Material.AIR);
			}
		} else {
			currentBlock.setBlockData(block.getBlockData());
			for (int y = block.getY() - 1; y < currentBlock.getY(); y++) {
				Block current = this.clampY(block.getX(), y, block.getZ());
				current.setBlockData(block.getBlockData());
			}
		}
	}

	private void setBlockDown(PullBrushBlockWrapper block) {
		Block currentBlock = this.clampY(block.getX(), block.getY() + (int) (this.voxelHeight * block.getStr()), block.getZ());
		currentBlock.setBlockData(block.getBlockData());
		for (int y = block.getY(); y > currentBlock.getY(); y--) {
			this.setBlockType(block.getX(), y, block.getZ(), Material.AIR);
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendVoxelHeightMessage();
		messenger.sendMessage(ChatColor.AQUA + "Pinch " + (-this.c1 + 1));
		messenger.sendMessage(ChatColor.AQUA + "Bubble " + this.c2);
	}

	private static final class PullBrushBlockWrapper {

		private BlockData blockData;
		private final double str;
		private final int x;
		private final int y;
		private final int z;

		private PullBrushBlockWrapper(Block block, double str) {
			this.blockData = block.getBlockData();
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
			this.str = str;
		}

		public BlockData getBlockData() {
			return this.blockData;
		}

		public double getStr() {
			return this.str;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getZ() {
			return this.z;
		}
	}
}
