package com.thevoxelbox.voxelsniper.brush;

import com.flowpowered.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.util.BlockWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
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
	private Block lastBlock;
	/**
	 * Brush name.
	 */
	private String name;

	public AbstractBrush(String name) {
		this.name = name;
	}

	/**
	 * @return {@link Block}
	 */
	public final Block clampY(int x, int y, int z) {
		int clampedY = y;
		World world = this.targetBlock.getWorld();
		if (clampedY < 0) {
			clampedY = 0;
		} else {
			int maxHeight = world.getMaxHeight();
			if (clampedY > maxHeight) {
				clampedY = maxHeight;
			}
		}
		return world.getBlockAt(x, clampedY, z);
	}

	@Override
	public boolean perform(SnipeAction action, SnipeData data, Block targetBlock, Block lastBlock) {
		this.targetBlock = targetBlock;
		this.lastBlock = lastBlock;
		switch (action) {
			case ARROW:
				arrow(data);
				return true;
			case GUNPOWDER:
				powder(data);
				return true;
			default:
				return false;
		}
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
	protected final boolean getTarget(SnipeData snipeData, Block clickedBlock, BlockFace clickedFace) {
		Sniper owner = snipeData.getOwner();
		String toolId = owner.getCurrentToolId();
		if (toolId == null) {
			return false;
		}
		SnipeData ownerSnipeData = owner.getSnipeData(toolId);
		if (ownerSnipeData == null) {
			return false;
		}
		World targetBlockWorld = this.targetBlock.getWorld();
		if (clickedBlock != null) {
			this.targetBlock = clickedBlock;
			this.lastBlock = clickedBlock.getRelative(clickedFace);
			if (this.lastBlock.isEmpty()) {
				snipeData.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
				return false;
			}
			if (ownerSnipeData.isLightningEnabled()) {
				targetBlockWorld.strikeLightning(this.targetBlock.getLocation());
			}
			return true;
		} else {
			RangeBlockHelper rangeBlockHelper;
			Player ownerPlayer = owner.getPlayer();
			if (ownerSnipeData.isRanged()) {
				rangeBlockHelper = new RangeBlockHelper(ownerPlayer, ownerPlayer.getWorld(), ownerSnipeData.getRange());
				this.targetBlock = rangeBlockHelper.getRangeBlock();
			} else {
				rangeBlockHelper = new RangeBlockHelper(ownerPlayer, ownerPlayer.getWorld());
				this.targetBlock = rangeBlockHelper.getTargetBlock();
			}
			if (this.targetBlock != null) {
				this.lastBlock = rangeBlockHelper.getLastBlock();
				if (this.lastBlock == null) {
					snipeData.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
					return false;
				}
				if (ownerSnipeData.isLightningEnabled()) {
					targetBlockWorld.strikeLightning(this.targetBlock.getLocation());
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

	protected Vector3i getTargetBlockPosition() {
		Location location = this.targetBlock.getLocation();
		int blockX = location.getBlockX();
		int blockY = location.getBlockY();
		int blockZ = location.getBlockZ();
		return new Vector3i(blockX, blockY, blockZ);
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
	protected final void setLastBlock(Block lastBlock) {
		this.lastBlock = lastBlock;
	}

	protected Material getBlockType(Vector3i position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		return getBlockType(x, y, z);
	}

	protected Material getBlockType(int x, int y, int z) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		return block.getType();
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

	protected void setBlockType(Vector3i position, Material type) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		setBlockType(x, y, z, type);
	}

	protected void setBlockType(int x, int y, int z, Material type) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		block.setType(type);
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
