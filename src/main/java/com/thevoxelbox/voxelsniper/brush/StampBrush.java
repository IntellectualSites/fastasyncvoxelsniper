package com.thevoxelbox.voxelsniper.brush;

import java.util.HashSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 *
 */
public class StampBrush extends Brush {

	/**
	 * @author Voxel
	 */
	protected class BlockWrapper {

		private int id;
		private int x;
		private int y;
		private int z;
		private byte d;

		/**
		 *
		 */
		@SuppressWarnings("deprecation")
		public BlockWrapper(Block block, int blx, int bly, int blz) {
			this.id = block.getTypeId();
			this.d = block.getData();
			this.x = blx;
			this.y = bly;
			this.z = blz;
		}

		public int getId() {
			return this.id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getX() {
			return this.x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return this.y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getZ() {
			return this.z;
		}

		public void setZ(int z) {
			this.z = z;
		}

		public byte getD() {
			return this.d;
		}

		public void setD(byte d) {
			this.d = d;
		}
	}

	/**
	 * @author Monofraps
	 */
	protected enum StampType {
		NO_AIR,
		FILL,
		DEFAULT
	}

	protected Set<BlockWrapper> clone = new HashSet<>();
	protected Set<BlockWrapper> fall = new HashSet<>();
	protected Set<BlockWrapper> drop = new HashSet<>();
	protected Set<BlockWrapper> solid = new HashSet<>();
	protected Undo undo;
	protected boolean sorted;

	protected StampType stamp = StampType.DEFAULT;

	/**
	 *
	 */
	public StampBrush() {
		this.setName("Stamp");
	}

	/**
	 *
	 */
	public final void reSort() {
		this.sorted = false;
	}

	/**
	 *
	 */
	protected final boolean falling(int id) {
		return (id > 7 && id < 14);
	}

	/**
	 *
	 */
	protected final boolean fallsOff(int id) {
		switch (id) {
			// 6, 37, 38, 39, 40, 50, 51, 55, 59, 63, 64, 65, 66, 69, 70, 71, 72, 75, 76, 77, 83
			case (6):
			case (37):
			case (38):
			case (39):
			case (40):
			case (50):
			case (51):
			case (55):
			case (59):
			case (63):
			case (64):
			case (65):
			case (66):
			case (68):
			case (69):
			case (70):
			case (71):
			case (72):
			case (75):
			case (76):
			case (77):
			case (78):
			case (83):
			case (93):
			case (94):
			default:
				return false;
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected final void setBlock(BlockWrapper cb) {
		Block block = this.clampY(this.getTargetBlock()
			.getX() + cb.getX(), this.getTargetBlock()
			.getY() + cb.getY(), this.getTargetBlock()
			.getZ() + cb.getZ());
		this.undo.put(block);
		block.setTypeId(cb.getId());
		block.setData(cb.getD());
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected final void setBlockFill(BlockWrapper cb) {
		Block block = this.clampY(this.getTargetBlock()
			.getX() + cb.getX(), this.getTargetBlock()
			.getY() + cb.getY(), this.getTargetBlock()
			.getZ() + cb.getZ());
		if (block.getTypeId() == 0) {
			this.undo.put(block);
			block.setTypeId(cb.getId());
			block.setData(cb.getD());
		}
	}

	/**
	 *
	 */
	protected final void setStamp(StampType type) {
		this.stamp = type;
	}

	/**
	 *
	 */
	protected final void stamp(SnipeData v) {
		this.undo = new Undo();
		if (this.sorted) {
			for (BlockWrapper block : this.solid) {
				this.setBlock(block);
			}
			for (BlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (BlockWrapper block : this.fall) {
				this.setBlock(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (BlockWrapper block : this.clone) {
				if (this.fallsOff(block.getId())) {
					this.fall.add(block);
				} else if (this.falling(block.getId())) {
					this.drop.add(block);
				} else {
					this.solid.add(block);
					this.setBlock(block);
				}
			}
			for (BlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (BlockWrapper block : this.fall) {
				this.setBlock(block);
			}
			this.sorted = true;
		}
		v.owner()
			.storeUndo(this.undo);
	}

	/**
	 *
	 */
	protected final void stampFill(SnipeData v) {
		this.undo = new Undo();
		if (this.sorted) {
			for (BlockWrapper block : this.solid) {
				this.setBlockFill(block);
			}
			for (BlockWrapper block : this.drop) {
				this.setBlockFill(block);
			}
			for (BlockWrapper block : this.fall) {
				this.setBlockFill(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (BlockWrapper block : this.clone) {
				if (this.fallsOff(block.getId())) {
					this.fall.add(block);
				} else if (this.falling(block.getId())) {
					this.drop.add(block);
				} else if (block.getId() != 0) {
					this.solid.add(block);
					this.setBlockFill(block);
				}
			}
			for (BlockWrapper block : this.drop) {
				this.setBlockFill(block);
			}
			for (BlockWrapper block : this.fall) {
				this.setBlockFill(block);
			}
			this.sorted = true;
		}
		v.owner()
			.storeUndo(this.undo);
	}

	/**
	 *
	 */
	protected final void stampNoAir(SnipeData v) {
		this.undo = new Undo();
		if (this.sorted) {
			for (BlockWrapper block : this.solid) {
				this.setBlock(block);
			}
			for (BlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (BlockWrapper block : this.fall) {
				this.setBlock(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (BlockWrapper block : this.clone) {
				if (this.fallsOff(block.getId())) {
					this.fall.add(block);
				} else if (this.falling(block.getId())) {
					this.drop.add(block);
				} else if (block.getId() != 0) {
					this.solid.add(block);
					this.setBlock(block);
				}
			}
			for (BlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (BlockWrapper block : this.fall) {
				this.setBlock(block);
			}
			this.sorted = true;
		}
		v.owner()
			.storeUndo(this.undo);
	}

	@Override
	protected final void arrow(SnipeData v) {
		switch (this.stamp) {
			case DEFAULT:
				this.stamp(v);
				break;
			case NO_AIR:
				this.stampNoAir(v);
				break;
			case FILL:
				this.stampFill(v);
				break;
			default:
				v.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
				break;
		}
	}

	@Override
	protected void powder(SnipeData v) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void info(Message vm) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.stamp";
	}
}
