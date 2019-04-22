package com.thevoxelbox.voxelsniper.brush.type.stamp;

import java.util.HashSet;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.util.LegacyMaterialConverter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;


public class StampBrush extends AbstractBrush {

	public StampBrush(String name) {
		super(name);
	}

	/**
	 * @author Voxel
	 */
	protected static class StampBrushBlockWrapper {

		private BlockData blockData;
		private int x;
		private int y;
		private int z;

		public StampBrushBlockWrapper(Block block, int x, int y, int z) {
			this.blockData = block.getBlockData();
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public BlockData getBlockData() {
			return this.blockData;
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
	}

	/**
	 * @author Monofraps
	 */
	protected enum StampType {
		NO_AIR,
		FILL,
		DEFAULT
	}

	protected Set<StampBrushBlockWrapper> clone = new HashSet<>();
	protected Set<StampBrushBlockWrapper> fall = new HashSet<>();
	protected Set<StampBrushBlockWrapper> drop = new HashSet<>();
	protected Set<StampBrushBlockWrapper> solid = new HashSet<>();
	protected Undo undo;
	protected boolean sorted;

	protected StampType stamp = StampType.DEFAULT;

	public StampBrush() {
		super("Stamp");
	}

	public final void reSort() {
		this.sorted = false;
	}

	protected final boolean falling(Material material) {
		int id = LegacyMaterialConverter.getLegacyMaterialId(material);
		return (id > 7 && id < 14);
	}

	protected final boolean fallsOff(Material material) {
		int id = LegacyMaterialConverter.getLegacyMaterialId(material);
		switch (id) {
			// 6, 37, 38, 39, 40, 50, 51, 55, 59, 63, 64, 65, 66, 69, 70, 71, 72, 75, 76, 77, 83
			case 6:
			case 37:
			case 38:
			case 39:
			case 40:
			case 50:
			case 51:
			case 55:
			case 59:
			case 63:
			case 64:
			case 65:
			case 66:
			case 68:
			case 69:
			case 70:
			case 71:
			case 72:
			case 75:
			case 76:
			case 77:
			case 78:
			case 83:
			case 93:
			case 94:
			default:
				return false;
		}
	}

	protected final void setBlock(StampBrushBlockWrapper blockWrapper) {
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX() + blockWrapper.getX(), targetBlock.getY() + blockWrapper.getY(), targetBlock.getZ() + blockWrapper.getZ());
		this.undo.put(block);
		block.setBlockData(blockWrapper.getBlockData());
	}

	protected final void setBlockFill(StampBrushBlockWrapper blockWrapper) {
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX() + blockWrapper.getX(), targetBlock.getY() + blockWrapper.getY(), targetBlock.getZ() + blockWrapper.getZ());
		if (block.isEmpty()) {
			this.undo.put(block);
			block.setBlockData(blockWrapper.getBlockData());
		}
	}

	protected final void setStamp(StampType type) {
		this.stamp = type;
	}

	protected final void stamp(SnipeData snipeData) {
		this.undo = new Undo();
		if (this.sorted) {
			for (StampBrushBlockWrapper block : this.solid) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlock(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (StampBrushBlockWrapper block : this.clone) {
				BlockData blockData = block.getBlockData();
				Material material = blockData.getMaterial();
				if (this.fallsOff(material)) {
					this.fall.add(block);
				} else if (this.falling(material)) {
					this.drop.add(block);
				} else {
					this.solid.add(block);
					this.setBlock(block);
				}
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlock(block);
			}
			this.sorted = true;
		}
		snipeData.getOwner()
			.storeUndo(this.undo);
	}

	protected final void stampFill(SnipeData snipeData) {
		this.undo = new Undo();
		if (this.sorted) {
			for (StampBrushBlockWrapper block : this.solid) {
				this.setBlockFill(block);
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlockFill(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlockFill(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (StampBrushBlockWrapper block : this.clone) {
				BlockData blockData = block.getBlockData();
				Material material = blockData.getMaterial();
				if (fallsOff(material)) {
					this.fall.add(block);
				} else if (falling(material)) {
					this.drop.add(block);
				} else if (!material.isEmpty()) {
					this.solid.add(block);
					this.setBlockFill(block);
				}
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlockFill(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlockFill(block);
			}
			this.sorted = true;
		}
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(this.undo);
	}

	protected final void stampNoAir(SnipeData snipeData) {
		this.undo = new Undo();
		if (this.sorted) {
			for (StampBrushBlockWrapper block : this.solid) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlock(block);
			}
		} else {
			this.fall.clear();
			this.drop.clear();
			this.solid.clear();
			for (StampBrushBlockWrapper block : this.clone) {
				BlockData blockData = block.getBlockData();
				Material material = blockData.getMaterial();
				if (this.fallsOff(material)) {
					this.fall.add(block);
				} else if (this.falling(material)) {
					this.drop.add(block);
				} else if (!material.isEmpty()) {
					this.solid.add(block);
					this.setBlock(block);
				}
			}
			for (StampBrushBlockWrapper block : this.drop) {
				this.setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				this.setBlock(block);
			}
			this.sorted = true;
		}
		snipeData.getOwner()
			.storeUndo(this.undo);
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		switch (this.stamp) {
			case DEFAULT:
				this.stamp(snipeData);
				break;
			case NO_AIR:
				this.stampNoAir(snipeData);
				break;
			case FILL:
				this.stampFill(snipeData);
				break;
			default:
				snipeData.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
				break;
		}
	}

	@Override
	public void powder(SnipeData snipeData) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void info(Message message) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.stamp";
	}
}
