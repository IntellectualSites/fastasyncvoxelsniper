package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * @author Piotr
 */
public class PullBrush extends Brush {

	private final Set<BlockWrapper> surface = new HashSet<>();
	private int vh;
	private double c1 = 1;
	private double c2;

	/**
	 * Default Constructor.
	 */
	public PullBrush() {
		this.setName("Soft Selection");
	}

	@Override
	public final void info(Message vm) {
		vm.brushName(this.getName());
		vm.size();
		vm.height();
		vm.custom(ChatColor.AQUA + "Pinch " + (-this.c1 + 1));
		vm.custom(ChatColor.AQUA + "Bubble " + this.c2);
	}

	@Override
	public final void parameters(String[] par, SnipeData v) {
		try {
			double pinch = Double.parseDouble(par[1]);
			double bubble = Double.parseDouble(par[2]);
			this.c1 = 1 - pinch;
			this.c2 = bubble;
		} catch (NumberFormatException exception) {
			v.sendMessage(ChatColor.RED + "Invalid brush parameters!");
		}
	}

	/**
	 * @return double
	 */
	private double getStr(double t) {
		double lt = 1 - t;
		return (lt * lt * lt) + 3 * (lt * lt) * t * this.c1 + 3 * lt * (t * t) * this.c2; // My + (t * ((By + (t * ((c2 + (t * (0 - c2))) - By))) - My));
	}

	/**
	 *
	 */
	private void getSurface(SnipeData v) {
		this.surface.clear();
		double bSquared = Math.pow(v.getBrushSize() + 0.5, 2);
		for (int z = -v.getBrushSize(); z <= v.getBrushSize(); z++) {
			double zSquared = Math.pow(z, 2);
			int actualZ = this.getTargetBlock()
				.getZ() + z;
			for (int x = -v.getBrushSize(); x <= v.getBrushSize(); x++) {
				double xSquared = Math.pow(x, 2);
				int actualX = this.getTargetBlock()
					.getX() + x;
				for (int y = -v.getBrushSize(); y <= v.getBrushSize(); y++) {
					double volume = (xSquared + Math.pow(y, 2) + zSquared);
					if (volume <= bSquared) {
						if (this.isSurface(actualX, this.getTargetBlock()
							.getY() + y, actualZ)) {
							this.surface.add(new BlockWrapper(this.clampY(actualX, this.getTargetBlock()
								.getY() + y, actualZ), this.getStr(((volume / bSquared)))));
						}
					}
				}
			}
		}
	}

	/**
	 * @return boolean
	 */
	private boolean isSurface(int x, int y, int z) {
		return this.getBlockIdAt(x, y, z) != 0 && ((this.getBlockIdAt(x, y - 1, z) == 0) || (this.getBlockIdAt(x, y + 1, z) == 0) || (this.getBlockIdAt(x + 1, y, z) == 0) || (this.getBlockIdAt(x - 1, y, z) == 0) || (this.getBlockIdAt(x, y, z + 1) == 0) || (this.getBlockIdAt(x, y, z - 1) == 0));
	}

	@SuppressWarnings("deprecation")
	private void setBlock(BlockWrapper block) {
		Block currentBlock = this.clampY(block.getX(), block.getY() + (int) (this.vh * block.getStr()), block.getZ());
		if (this.getBlockIdAt(block.getX(), block.getY() - 1, block.getZ()) == 0) {
			currentBlock.setTypeId(block.getId());
			currentBlock.setData(block.getD());
			for (int y = block.getY(); y < currentBlock.getY(); y++) {
				this.setBlockIdAt(block.getZ(), block.getX(), y, 0);
			}
		} else {
			currentBlock.setTypeId(block.getId());
			currentBlock.setData(block.getD());
			for (int y = block.getY() - 1; y < currentBlock.getY(); y++) {
				Block current = this.clampY(block.getX(), y, block.getZ());
				current.setTypeId(block.getId());
				current.setData(block.getD());
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setBlockDown(BlockWrapper block) {
		Block currentBlock = this.clampY(block.getX(), block.getY() + (int) (this.vh * block.getStr()), block.getZ());
		currentBlock.setTypeId(block.getId());
		currentBlock.setData(block.getD());
		for (int y = block.getY(); y > currentBlock.getY(); y--) {
			this.setBlockIdAt(block.getZ(), block.getX(), y, 0);
		}
		// }
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.vh = v.getVoxelHeight();
		this.getSurface(v);
		if (this.vh > 0) {
			for (BlockWrapper block : this.surface) {
				this.setBlock(block);
			}
		} else if (this.vh < 0) {
			for (BlockWrapper block : this.surface) {
				this.setBlockDown(block);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected final void powder(SnipeData v) {
		this.vh = v.getVoxelHeight();
		this.surface.clear();
		int lastY;
		double brushSizeSquared = Math.pow(v.getBrushSize() + 0.5, 2);
		// Are we pulling up ?
		if (this.vh > 0) {
			// Z - Axis
			for (int z = -v.getBrushSize(); z <= v.getBrushSize(); z++) {
				int zSquared = z * z;
				int actualZ = this.getTargetBlock()
					.getZ() + z;
				// X - Axis
				for (int x = -v.getBrushSize(); x <= v.getBrushSize(); x++) {
					int xSquared = x * x;
					int actualX = this.getTargetBlock()
						.getX() + x;
					// Down the Y - Axis
					for (int y = v.getBrushSize(); y >= -v.getBrushSize(); y--) {
						double volume = zSquared + xSquared + (y * y);
						// Is this in the range of the brush?
						if (volume <= brushSizeSquared && this.getWorld()
							.getBlockTypeIdAt(actualX, this.getTargetBlock()
								.getY() + y, actualZ) != 0) {
							int actualY = this.getTargetBlock()
								.getY() + y;
							// Starting strength and new Position
							double str = this.getStr(volume / brushSizeSquared);
							int lastStr = (int) (this.vh * str);
							lastY = actualY + lastStr;
							this.clampY(actualX, lastY, actualZ)
								.setTypeId(this.getWorld()
									.getBlockTypeIdAt(actualX, actualY, actualZ));
							if (str == 1) {
								str = 0.8;
							}
							while (lastStr > 0) {
								if (actualY < this.getTargetBlock()
									.getY()) {
									str *= str;
								}
								lastStr = (int) (this.vh * str);
								int newY = actualY + lastStr;
								int id = this.getWorld()
									.getBlockTypeIdAt(actualX, actualY, actualZ);
								for (int i = newY; i < lastY; i++) {
									this.clampY(actualX, i, actualZ)
										.setTypeId(id);
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
			for (int z = -v.getBrushSize(); z <= v.getBrushSize(); z++) {
				double zSquared = Math.pow(z, 2);
				int actualZ = this.getTargetBlock()
					.getZ() + z;
				for (int x = -v.getBrushSize(); x <= v.getBrushSize(); x++) {
					double xSquared = Math.pow(x, 2);
					int actualX = this.getTargetBlock()
						.getX() + x;
					for (int y = -v.getBrushSize(); y <= v.getBrushSize(); y++) {
						double volume = (xSquared + Math.pow(y, 2) + zSquared);
						if (volume <= brushSizeSquared && this.getWorld()
							.getBlockTypeIdAt(actualX, this.getTargetBlock()
								.getY() + y, actualZ) != 0) {
							int actualY = this.getTargetBlock()
								.getY() + y;
							lastY = actualY + (int) (this.vh * this.getStr(volume / brushSizeSquared));
							this.clampY(actualX, lastY, actualZ)
								.setTypeId(this.getWorld()
									.getBlockTypeIdAt(actualX, actualY, actualZ));
							y++;
							double volume2 = (xSquared + Math.pow(y, 2) + zSquared);
							while (volume2 <= brushSizeSquared) {
								int blockY = this.getTargetBlock()
									.getY() + y + (int) (this.vh * this.getStr(volume2 / brushSizeSquared));
								int blockId = this.getWorld()
									.getBlockTypeIdAt(actualX, this.getTargetBlock()
										.getY() + y, actualZ);
								for (int i = blockY; i < lastY; i++) {
									this.clampY(actualX, i, actualZ)
										.setTypeId(blockId);
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

	/**
	 * @author Piotr
	 */
	private final class BlockWrapper {

		private final int id;
		private final byte d;
		private final double str;
		private final int x;
		private final int y;
		private final int z;

		/**
		 *
		 */
		@SuppressWarnings("deprecation")
		private BlockWrapper(Block block, double st) {
			this.id = block.getTypeId();
			this.d = block.getData();
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
			this.str = st;
		}

		/**
		 * @return the d
		 */
		public byte getD() {
			return this.d;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return this.id;
		}

		/**
		 * @return the str
		 */
		public double getStr() {
			return this.str;
		}

		/**
		 * @return the x
		 */
		public int getX() {
			return this.x;
		}

		/**
		 * @return the y
		 */
		public int getY() {
			return this.y;
		}

		/**
		 * @return the z
		 */
		public int getZ() {
			return this.z;
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.pull";
	}
}
