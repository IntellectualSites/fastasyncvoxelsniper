package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import org.bukkit.ChatColor;
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
	public boolean perform(ToolAction action, ToolkitProperties toolkitProperties, Block targetBlock, Block lastBlock) {
		this.targetBlock = targetBlock;
		this.lastBlock = lastBlock;
		switch (action) {
			case ARROW:
				arrow(toolkitProperties);
				return true;
			case GUNPOWDER:
				powder(toolkitProperties);
				return true;
			default:
				return false;
		}
	}

	@Override
	public void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		toolkitProperties.sendMessage(ChatColor.RED + "This brush does not accept additional parameters.");
	}

	/**
	 * Overridable getTarget method.
	 *
	 * @return boolean
	 */
	protected final boolean getTarget(ToolkitProperties toolkitProperties, Block clickedBlock, BlockFace clickedFace) {
		Sniper owner = toolkitProperties.getOwner();
		Toolkit toolkit = owner.getCurrentToolkit();
		if (toolkit == null) {
			return false;
		}
		ToolkitProperties ownerToolkitProperties = toolkit.getProperties();
		if (ownerToolkitProperties == null) {
			return false;
		}
		World targetBlockWorld = this.targetBlock.getWorld();
		if (clickedBlock != null) {
			this.targetBlock = clickedBlock;
			this.lastBlock = clickedBlock.getRelative(clickedFace);
			if (this.lastBlock.isEmpty()) {
				toolkitProperties.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
				return false;
			}
		} else {
			Player ownerPlayer = owner.getPlayer();
			if (ownerPlayer == null) {
				return false;
			}
			BlockTracer blockTracer = ownerToolkitProperties.createBlockTracer(ownerPlayer);
			this.targetBlock = blockTracer.getTargetBlock();
			if (this.targetBlock.isEmpty()) {
				toolkitProperties.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
				return false;
			}
			this.lastBlock = blockTracer.getLastBlock();
			if (this.lastBlock == null) {
				toolkitProperties.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
				return false;
			}
		}
		if (ownerToolkitProperties.isLightningEnabled()) {
			targetBlockWorld.strikeLightning(this.targetBlock.getLocation());
		}
		return true;
	}

	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public final void setName(String name) {
		this.name = name;
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

	protected Material getBlockType(int x, int y, int z) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		return block.getType();
	}

	protected BlockData getBlockData(int x, int y, int z) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		return block.getBlockData();
	}

	protected void setBlockType(int x, int y, int z, Material type) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		block.setType(type);
	}

	protected final void setBlockData(int x, int y, int z, BlockData blockData) {
		World world = this.targetBlock.getWorld();
		Block block = world.getBlockAt(x, y, z);
		block.setBlockData(blockData);
	}
}
