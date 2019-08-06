package com.thevoxelbox.voxelsniper.brush.type.stamp;

import java.util.HashSet;
import java.util.Set;
import com.destroystokyo.paper.MaterialTags;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public abstract class AbstractStampBrush extends AbstractBrush {

	protected Set<StampBrushBlockWrapper> clone = new HashSet<>();
	protected Set<StampBrushBlockWrapper> fall = new HashSet<>();
	protected Set<StampBrushBlockWrapper> drop = new HashSet<>();
	protected Set<StampBrushBlockWrapper> solid = new HashSet<>();
	protected Undo undo;
	protected boolean sorted;
	protected StampType stamp = StampType.DEFAULT;

	@Override
	public void handleArrowAction(Snipe snipe) {
		switch (this.stamp) {
			case DEFAULT:
				stamp(snipe);
				break;
			case NO_AIR:
				stampNoAir(snipe);
				break;
			case FILL:
				stampFill(snipe);
				break;
			default:
				SnipeMessenger messenger = snipe.createMessenger();
				messenger.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
				break;
		}
	}

	public void reSort() {
		this.sorted = false;
	}

	protected boolean falling(Material material) {
		return MaterialSets.FALLING.contains(material);
	}

	protected boolean fallsOff(Material material) {
		MaterialSet fallsOff = MaterialSet.builder()
			.with(Tag.SAPLINGS)
			.with(Tag.DOORS)
			.with(Tag.RAILS)
			.with(Tag.BUTTONS)
			.with(MaterialTags.SIGNS)
			.with(MaterialTags.PRESSURE_PLATES)
			.with(MaterialSets.FLOWERS)
			.with(MaterialSets.MUSHROOMS)
			.with(MaterialSets.TORCHES)
			.with(MaterialSets.REDSTONE_TORCHES)
			.add(Material.FIRE)
			.add(Material.REDSTONE_WIRE)
			.add(Material.WHEAT)
			.add(Material.LADDER)
			.add(Material.LEVER)
			.add(Material.SNOW)
			.add(Material.SUGAR_CANE)
			.add(Material.REPEATER)
			.add(Material.COMPARATOR)
			.build();
		return fallsOff.contains(material);
	}

	protected void setBlock(StampBrushBlockWrapper blockWrapper) {
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX() + blockWrapper.getX(), targetBlock.getY() + blockWrapper.getY(), targetBlock.getZ() + blockWrapper.getZ());
		this.undo.put(block);
		block.setBlockData(blockWrapper.getBlockData());
	}

	protected void setBlockFill(StampBrushBlockWrapper blockWrapper) {
		Block targetBlock = getTargetBlock();
		Block block = clampY(targetBlock.getX() + blockWrapper.getX(), targetBlock.getY() + blockWrapper.getY(), targetBlock.getZ() + blockWrapper.getZ());
		if (block.getType().isEmpty()) {
			this.undo.put(block);
			block.setBlockData(blockWrapper.getBlockData());
		}
	}

	protected void stamp(Snipe snipe) {
		this.undo = new Undo();
		if (this.sorted) {
			for (StampBrushBlockWrapper block : this.solid) {
				setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.drop) {
				setBlock(block);
			}
			for (StampBrushBlockWrapper block : this.fall) {
				setBlock(block);
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
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.undo);
	}

	protected void stampFill(Snipe snipe) {
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
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.undo);
	}

	protected void stampNoAir(Snipe snipe) {
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
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.undo);
	}

	public StampType getStamp() {
		return this.stamp;
	}

	public void setStamp(StampType stamp) {
		this.stamp = stamp;
	}

	protected enum StampType {
		NO_AIR,
		FILL,
		DEFAULT
	}

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
}

























