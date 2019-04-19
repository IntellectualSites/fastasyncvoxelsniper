package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
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
	 * @param snipeData Sniper caller
	 */
	protected void arrow(SnipeData snipeData) {}

	/**
	 * The powder action. Executed when a player RightClicks with Gunpowder
	 *
	 * @param snipeData Sniper caller
	 */
	protected void powder(SnipeData snipeData) {}

	@Override
	public void parameters(String[] parameters, SnipeData snipeData) {
		snipeData.sendMessage(ChatColor.RED + "This brush does not accept additional parameters.");
	}

	/**
	 * Overridable getTarget method.
	 *
	 * @return boolean
	 */
	protected final boolean getTarget(SnipeData snipeData, Block clickedBlock, BlockFace clickedFace) {
		Sniper owner = snipeData.getOwner();
		String toolId = owner.getCurrentToolId();
		if (clickedBlock != null) {
			this.targetBlock = clickedBlock;
			this.lastBlock = clickedBlock.getRelative(clickedFace);
			if (this.lastBlock == null) {
				snipeData.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
				return false;
			}
			if (owner.getSnipeData(toolId)
				.isLightningEnabled()) {
				this.getWorld()
					.strikeLightning(this.targetBlock.getLocation());
			}
			return true;
		} else {
			RangeBlockHelper rangeBlockHelper;
			if (owner.getSnipeData(toolId)
				.isRanged()) {
				rangeBlockHelper = new RangeBlockHelper(owner.getPlayer(), owner.getPlayer()
					.getWorld(), owner.getSnipeData(toolId)
					.getRange());
				this.targetBlock = rangeBlockHelper.getRangeBlock();
			} else {
				rangeBlockHelper = new RangeBlockHelper(owner.getPlayer(), owner.getPlayer()
					.getWorld());
				this.targetBlock = rangeBlockHelper.getTargetBlock();
			}
			if (this.targetBlock != null) {
				this.lastBlock = rangeBlockHelper.getLastBlock();
				if (this.lastBlock == null) {
					snipeData.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
					return false;
				}
				if (owner.getSnipeData(toolId)
					.isLightningEnabled()) {
					this.getWorld()
						.strikeLightning(this.targetBlock.getLocation());
				}
				return true;
			} else {
				snipeData.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
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

	protected BlockData getBlockData(Vector3i position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlockData(x, y, z);
	}

	protected BlockData getBlockData(int x, int y, int z) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		return block.getBlockData();
	}

	/**
	 * Set block data with supplied data over BlockWrapper.
	 *
	 * @param blockWrapper Block data wrapper
	 */
	protected final void setBlock(BlockWrapper blockWrapper) {
		Vector3i position = blockWrapper.getPosition();
		BlockData blockData = blockWrapper.getBlockData();
		setBlockData(position, blockData);
	}

	protected final void setBlockData(Vector3i position, BlockData blockData) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		setBlockData(x, y, z, blockData);
	}

	protected final void setBlockData(int x, int y, int z, BlockData blockData) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		block.setBlockData(blockData);
	}
}
