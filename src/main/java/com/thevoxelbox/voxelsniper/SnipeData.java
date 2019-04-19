package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.util.VoxelList;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

/**
 * @author Piotr
 */
public class SnipeData {

	public static final int DEFAULT_REPLACE_DATA_VALUE = 0;
	public static final int DEFAULT_CYLINDER_CENTER = 0;
	public static final int DEFAULT_VOXEL_HEIGHT = 1;
	public static final int DEFAULT_BRUSH_SIZE = 3;
	public static final int DEFAULT_DATA_VALUE = 0;
	public static final int DEFAULT_REPLACE_ID = 0;
	public static final int DEFAULT_VOXEL_ID = 0;

	private final Sniper owner;
	private Message voxelMessage;
	/**
	 * Brush size -- set blockPositionY /b #.
	 */
	private int brushSize = DEFAULT_BRUSH_SIZE;
	/**
	 * Voxel Id -- set blockPositionY /v (#,name).
	 */
	@Deprecated
	private int voxelId = DEFAULT_VOXEL_ID;
	/**
	 * Voxel Replace Id -- set blockPositionY /vr #.
	 */
	@Deprecated
	private int replaceId = DEFAULT_REPLACE_ID;
	/**
	 * Voxel 'ink' -- set blockPositionY /vi #.
	 */
	@Deprecated
	private byte data = DEFAULT_DATA_VALUE;
	/**
	 * Voxel 'ink' Replace -- set blockPositionY /vir #.
	 */
	@Deprecated
	private byte replaceData = DEFAULT_REPLACE_DATA_VALUE;
	/**
	 * Voxel List of ID's -- set blockPositionY /vl # # # -#.
	 */
	private VoxelList voxelList = new VoxelList();
	/**
	 * Voxel 'heigth' -- set blockPositionY /vh #.
	 */
	private int voxelHeight = DEFAULT_VOXEL_HEIGHT;
	/**
	 * Voxel centroid -- set Cylynder center /vc #.
	 */
	private int cCen = DEFAULT_CYLINDER_CENTER;
	private int range;
	private boolean ranged;
	private boolean lightning;
	private BlockData blockData = Material.AIR.createBlockData();

	/**
	 *
	 */
	public SnipeData(Sniper vs) {
		this.owner = vs;
	}

	/**
	 * @return the brushSize
	 */
	public final int getBrushSize() {
		return this.brushSize;
	}

	/**
	 * @return the cCen
	 */
	public final int getcCen() {
		return this.cCen;
	}

	/**
	 * @return the data
	 */
	@Deprecated
	public final byte getData() {
		return this.data;
	}

	/**
	 * @return the replaceData
	 */
	@Deprecated
	public final byte getReplaceData() {
		return this.replaceData;
	}

	/**
	 * @return the replaceId
	 */
	@Deprecated
	public final int getReplaceId() {
		return this.replaceId;
	}

	/**
	 * @return the voxelHeight
	 */
	public final int getVoxelHeight() {
		return this.voxelHeight;
	}

	/**
	 * @return the voxelId
	 */
	@Deprecated
	public final int getVoxelId() {
		return this.voxelId;
	}

	/**
	 * @return the voxelList
	 */
	public final VoxelList getVoxelList() {
		return this.voxelList;
	}

	/**
	 * @return the voxelMessage
	 */
	public final Message getVoxelMessage() {
		return this.voxelMessage;
	}

	/**
	 * @return World
	 */
	public final World getWorld() {
		return this.owner.getPlayer()
			.getWorld();
	}

	/**
	 * @return Sniper
	 */
	public final Sniper owner() {
		return this.owner;
	}

	/**
	 * Reset to default values.
	 */
	public final void reset() {
		this.voxelId = DEFAULT_VOXEL_ID;
		this.replaceId = DEFAULT_REPLACE_ID;
		this.data = DEFAULT_DATA_VALUE;
		this.brushSize = DEFAULT_BRUSH_SIZE;
		this.voxelHeight = DEFAULT_VOXEL_HEIGHT;
		this.cCen = DEFAULT_CYLINDER_CENTER;
		this.replaceData = DEFAULT_REPLACE_DATA_VALUE;
		this.voxelList = new VoxelList();
	}

	/**
	 *
	 */
	public final void sendMessage(String message) {
		this.owner.getPlayer()
			.sendMessage(message);
	}

	/**
	 * @param brushSize the brushSize to set
	 */
	public final void setBrushSize(int brushSize) {
		this.brushSize = brushSize;
	}

	/**
	 * @param cCen the cCen to set
	 */
	public final void setcCen(int cCen) {
		this.cCen = cCen;
	}

	/**
	 * @param data the data to set
	 */
	public final void setData(byte data) {
		this.data = data;
	}

	/**
	 * @param replaceData the replaceData to set
	 */
	public final void setReplaceData(byte replaceData) {
		this.replaceData = replaceData;
	}

	/**
	 * @param replaceId the replaceId to set
	 */
	public final void setReplaceId(int replaceId) {
		this.replaceId = replaceId;
	}

	/**
	 * @param voxelHeight the voxelHeight to set
	 */
	public final void setVoxelHeight(int voxelHeight) {
		this.voxelHeight = voxelHeight;
	}

	/**
	 * @param voxelId the voxelId to set
	 */
	public final void setVoxelId(int voxelId) {
		this.voxelId = voxelId;
	}

	/**
	 * @param voxelList the voxelList to set
	 */
	public final void setVoxelList(VoxelList voxelList) {
		this.voxelList = voxelList;
	}

	/**
	 * @param voxelMessage the voxelMessage to set
	 */
	public final void setVoxelMessage(Message voxelMessage) {
		this.voxelMessage = voxelMessage;
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
		return this.lightning;
	}

	public void setLightningEnabled(boolean lightning) {
		this.lightning = lightning;
	}

	public void setVoxelData(BlockData blockData) {
		this.blockData = blockData;
	}

	public BlockData getVoxelData() {
		return this.blockData;
	}
}
