package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract implementation of the {@link Brush} interface.
 */
public abstract class AbstractBrush implements Brush {

	protected static final int CHUNK_SIZE = 16;
	/**
	 * Targeted Block.
	 */
	private Block targetBlock;
	/**
	 * Last Block before targeted Block.
	 */
	@Nullable
	private Block lastBlock;
	/**
	 * Brush name.
	 */
	private String name = "Undefined";

	/**
	 * @return {@link Block}
	 */
	public final Block clampY(int x, int y, int z) {
		int clampedY = y;
		if (clampedY < 0) {
			clampedY = 0;
		} else if (clampedY > this.getWorld()
			.getMaxHeight()) {
			clampedY = this.getWorld()
				.getMaxHeight();
		}
		return this.getWorld()
			.getBlockAt(x, clampedY, z);
	}

	private boolean preparePerform(SnipeData snipeData, Block clickedBlock, BlockFace clickedFace) {
		if (this.getTarget(snipeData, clickedBlock, clickedFace)) {
			if (this instanceof PerformBrush) {
				((PerformBrush) this).initPerformer(snipeData);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean perform(SnipeAction action, SnipeData data, Block targetBlock, Block lastBlock) {
		this.targetBlock = targetBlock;
		this.lastBlock = lastBlock;
		switch (action) {
			case ARROW:
				this.arrow(data);
				return true;
			case GUNPOWDER:
				this.powder(data);
				return true;
			default:
				return false;
		}
	}

	/**
	 * The arrow action. Executed when a player RightClicks with an Arrow
	 *
	 * @param v Sniper caller
	 */
	protected void arrow(SnipeData v) {
	}

	/**
	 * The powder action. Executed when a player RightClicks with Gunpowder
	 *
	 * @param v Sniper caller
	 */
	protected void powder(SnipeData v) {
	}

	@Override
	public void parameters(String[] parameters, SnipeData snipeData) {
		snipeData.sendMessage(ChatColor.RED + "This brush does not accept additional parameters.");
	}

	/**
	 * Overridable getTarget method.
	 *
	 * @return boolean
	 */
	protected final boolean getTarget(SnipeData v, Block clickedBlock, BlockFace clickedFace) {
		if (clickedBlock != null) {
			this.targetBlock = clickedBlock;
			this.lastBlock = clickedBlock.getRelative(clickedFace);
			if (this.lastBlock == null) {
				v.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
				return false;
			}
			if (v.owner()
				.getSnipeData(v.owner()
					.getCurrentToolId())
				.isLightningEnabled()) {
				this.getWorld()
					.strikeLightning(this.targetBlock.getLocation());
			}
			return true;
		} else {
			RangeBlockHelper rangeBlockHelper;
			if (v.owner()
				.getSnipeData(v.owner()
					.getCurrentToolId())
				.isRanged()) {
				rangeBlockHelper = new RangeBlockHelper(v.owner()
					.getPlayer(), v.owner()
					.getPlayer()
					.getWorld(), v.owner()
					.getSnipeData(v.owner()
						.getCurrentToolId())
					.getRange());
				this.targetBlock = rangeBlockHelper.getRangeBlock();
			} else {
				rangeBlockHelper = new RangeBlockHelper(v.owner()
					.getPlayer(), v.owner()
					.getPlayer()
					.getWorld());
				this.targetBlock = rangeBlockHelper.getTargetBlock();
			}
			if (this.targetBlock != null) {
				this.lastBlock = rangeBlockHelper.getLastBlock();
				if (this.lastBlock == null) {
					v.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
					return false;
				}
				if (v.owner()
					.getSnipeData(v.owner()
						.getCurrentToolId())
					.isLightningEnabled()) {
					this.getWorld()
						.strikeLightning(this.targetBlock.getLocation());
				}
				return true;
			} else {
				v.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
				return false;
			}
		}
	}

	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public String getBrushCategory() {
		return "General";
	}

	/**
	 * @return the targetBlock
	 */
	protected final Block getTargetBlock() {
		return this.targetBlock;
	}

	/**
	 * @param targetBlock the targetBlock to set
	 */
	protected final void setTargetBlock(Block targetBlock) {
		this.targetBlock = targetBlock;
	}

	/**
	 * @return the world
	 */
	protected final World getWorld() {
		return this.targetBlock.getWorld();
	}

	/**
	 * Looks up Type ID of Block at given coordinates in the world of the targeted Block.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Type ID of Block at given coordinates in the world of the targeted Block.
	 */
	@SuppressWarnings("deprecation")
	protected int getBlockIdAt(int x, int y, int z) {
		return getWorld().getBlockTypeIdAt(x, y, z);
	}

	/**
	 * Looks up Block Data Value of Block at given coordinates in the world of the targeted Block.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return Block Data Value of Block at given coordinates in the world of the targeted Block.
	 */
	@SuppressWarnings("deprecation")
	protected byte getBlockDataAt(int x, int y, int z) {
		return this.getWorld()
			.getBlockAt(x, y, z)
			.getData();
	}

	/**
	 * @return Block before target Block.
	 */
	@Nullable
	protected final Block getLastBlock() {
		return this.lastBlock;
	}

	/**
	 * @param lastBlock Last Block before target Block.
	 */
	protected final void setLastBlock(@Nullable Block lastBlock) {
		this.lastBlock = lastBlock;
	}

	/**
	 * Set block data with supplied data over BlockWrapper.
	 *
	 * @param blockWrapper Block data wrapper
	 */
	@Deprecated
	protected final void setBlock(BlockWrapper blockWrapper) {
		this.getWorld()
			.getBlockAt(blockWrapper.getX(), blockWrapper.getY(), blockWrapper.getZ())
			.setTypeId(blockWrapper.getId());
	}

	/**
	 * Sets the Id of the block at the passed coordinate.
	 *
	 * @param z Z coordinate
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param id The id the block will be set to
	 */
	@SuppressWarnings("deprecation")
	protected final void setBlockIdAt(int z, int x, int y, int id) {
		this.getWorld()
			.getBlockAt(x, y, z)
			.setTypeId(id);
	}

	/**
	 * Sets the id and data value of the block at the passed coordinate.
	 *
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param id The id the block will be set to
	 * @param data The data value the block will be set to
	 */
	@SuppressWarnings("deprecation")
	protected final void setBlockIdAndDataAt(int x, int y, int z, int id, byte data) {
		this.getWorld()
			.getBlockAt(x, y, z)
			.setTypeIdAndData(id, data, true);
	}
}
