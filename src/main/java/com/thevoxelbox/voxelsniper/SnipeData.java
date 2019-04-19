package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.util.VoxelList;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author Piotr
 */
public class SnipeData {

	private static final Material DEFAULT_BLOCK_MATERIAL = Material.AIR;
	private static final Material DEFAULT_REPLACE_BLOCK_MATERIAL = Material.AIR;
	private static final int DEFAULT_BRUSH_SIZE = 3;
	private static final int DEFAULT_VOXEL_HEIGHT = 1;
	private static final int DEFAULT_CYLINDER_CENTER = 0;

	private Sniper owner;
	private Message message;
	private BlockData blockData = DEFAULT_BLOCK_MATERIAL.createBlockData();
	private BlockData replaceBlockData = DEFAULT_REPLACE_BLOCK_MATERIAL.createBlockData();
	/**
	 * Brush size -- set blockPositionY /b #.
	 */
	private int brushSize = DEFAULT_BRUSH_SIZE;
	/**
	 * Voxel 'heigth' -- set blockPositionY /vh #.
	 */
	private int voxelHeight = DEFAULT_VOXEL_HEIGHT;
	/**
	 * Voxel centroid -- set Cylynder center /vc #.
	 */
	private int cylinderCenter = DEFAULT_CYLINDER_CENTER;
	private int range;
	private boolean ranged;
	private boolean lightningEnabled;
	/**
	 * Voxel List of ID's -- set blockPositionY /vl # # # -#.
	 */
	private VoxelList voxelList = new VoxelList();

	public SnipeData(Sniper owner) {
		this.owner = owner;
	}

	/**
	 * Reset to default values.
	 */
	public void reset() {
		this.blockData = DEFAULT_BLOCK_MATERIAL.createBlockData();
		this.replaceBlockData = DEFAULT_REPLACE_BLOCK_MATERIAL.createBlockData();
		this.brushSize = DEFAULT_BRUSH_SIZE;
		this.voxelHeight = DEFAULT_VOXEL_HEIGHT;
		this.cylinderCenter = DEFAULT_CYLINDER_CENTER;
		this.voxelList = new VoxelList();
	}

	public void sendMessage(String message) {
		Player player = this.owner.getPlayer();
		if (player == null) {
			return;
		}
		player.sendMessage(message);
	}

	@Nullable
	public World getWorld() {
		Player player = this.owner.getPlayer();
		if (player == null) {
			return null;
		}
		return player.getWorld();
	}

	public Sniper getOwner() {
		return this.owner;
	}

	public Message getMessage() {
		return this.message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setBlockData(BlockData blockData) {
		this.blockData = blockData;
	}

	public BlockData getBlockData() {
		return this.blockData;
	}

	public BlockData getReplaceBlockData() {
		return this.replaceBlockData;
	}

	public void setReplaceBlockData(BlockData replaceBlockData) {
		this.replaceBlockData = replaceBlockData;
	}

	public int getBrushSize() {
		return this.brushSize;
	}

	public void setBrushSize(int brushSize) {
		this.brushSize = brushSize;
	}

	public int getVoxelHeight() {
		return this.voxelHeight;
	}

	public void setVoxelHeight(int voxelHeight) {
		this.voxelHeight = voxelHeight;
	}

	public int getCylinderCenter() {
		return this.cylinderCenter;
	}

	public void setCylinderCenter(int cylinderCenter) {
		this.cylinderCenter = cylinderCenter;
	}

	public int getRange() {
		return this.range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public boolean isRanged() {
		return this.ranged;
	}

	public void setRanged(boolean ranged) {
		this.ranged = ranged;
	}

	public boolean isLightningEnabled() {
		return this.lightningEnabled;
	}

	public void setLightningEnabled(boolean lightningEnabled) {
		this.lightningEnabled = lightningEnabled;
	}

	public VoxelList getVoxelList() {
		return this.voxelList;
	}

	public void setVoxelList(VoxelList voxelList) {
		this.voxelList = voxelList;
	}
}
