package com.thevoxelbox.voxelsniper.sniper.snipe;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
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
	private Messages messages;
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
	private List<BlockData> voxelList = new ArrayList<>();

	public SnipeData(Sniper owner) {
		this.owner = owner;
	}

	/**
	 * Reset to default values.
	 */
	public void reset() {
		resetBlockData();
		resetReplaceBlockData();
		this.brushSize = DEFAULT_BRUSH_SIZE;
		this.voxelHeight = DEFAULT_VOXEL_HEIGHT;
		this.cylinderCenter = DEFAULT_CYLINDER_CENTER;
		this.voxelList = new ArrayList<>();
	}

	public void resetBlockData() {
		this.blockData = DEFAULT_BLOCK_MATERIAL.createBlockData();
	}

	public void resetReplaceBlockData() {
		this.replaceBlockData = DEFAULT_REPLACE_BLOCK_MATERIAL.createBlockData();
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

	@Override
	public String toString() {
		return "SnipeData{" + "owner=" + this.owner + ", messages=" + this.messages + ", blockData=" + this.blockData + ", replaceBlockData=" + this.replaceBlockData + ", brushSize=" + this.brushSize + ", voxelHeight=" + this.voxelHeight + ", cylinderCenter=" + this.cylinderCenter + ", range=" + this.range + ", ranged=" + this.ranged + ", lightningEnabled=" + this.lightningEnabled + ", voxelList=" + this.voxelList + "}";
	}

	public Material getBlockDataType() {
		return this.blockData.getMaterial();
	}

	public void setBlockDataType(Material type) {
		this.blockData = type.createBlockData();
	}

	public Material getReplaceBlockDataType() {
		return this.replaceBlockData.getMaterial();
	}

	public void setReplaceBlockDataType(Material type) {
		this.replaceBlockData = type.createBlockData();
	}

	public void addToVoxelList(BlockData blockData) {
		this.voxelList.add(blockData);
	}

	public void removeFromVoxelList(BlockData blockData) {
		this.voxelList.remove(blockData);
	}

	public void clearVoxelList() {
		this.voxelList.clear();
	}

	public boolean isVoxelListContains(BlockData blockData) {
		return this.voxelList.contains(blockData);
	}

	public Sniper getOwner() {
		return this.owner;
	}

	public Messages getMessages() {
		return this.messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
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

	public List<BlockData> getVoxelList() {
		return List.copyOf(this.voxelList);
	}
}
