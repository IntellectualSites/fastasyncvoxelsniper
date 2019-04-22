package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Snow_cone_brush
 *
 * @author Voxel
 */
public class SnowConeBrush extends AbstractBrush {

	public SnowConeBrush() {
		super("Snow Cone");
	}

	private void addSnow(ToolkitProperties toolkitProperties, Block targetBlock) {
		int brushSize;
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		if (getBlockType(blockPositionX, blockPositionY, blockPositionZ).isEmpty()) {
			brushSize = 0;
		} else {
			brushSize = blockDataToSnowLayers(clampY(blockPositionX, blockPositionY, blockPositionZ).getBlockData()) + 1;
		}
		int brushSizeDoubled = 2 * brushSize;
		Material[][] snowCone = new Material[brushSizeDoubled + 1][brushSizeDoubled + 1]; // Will hold block IDs
		BlockData[][] snowConeData = new BlockData[brushSizeDoubled + 1][brushSizeDoubled + 1]; // Will hold data values for snowCone
		int[][] yOffset = new int[brushSizeDoubled + 1][brushSizeDoubled + 1];
		// prime the arrays
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int z = 0; z <= brushSizeDoubled; z++) {
				boolean flag = true;
				for (int i = 0; i < 10; i++) { // overlay
					if (flag) {
						if ((getBlockType(blockPositionX - brushSize + x, blockPositionY - i, blockPositionZ - brushSize + z).isEmpty() || getBlockType(blockPositionX - brushSize + x, blockPositionY - i, blockPositionZ - brushSize + z) == Material.SNOW) && !getBlockType(blockPositionX - brushSize + x, blockPositionY - i - 1, blockPositionZ - brushSize + z).isEmpty() && getBlockType(blockPositionX - brushSize + x, blockPositionY - i - 1, blockPositionZ - brushSize + z) != Material.SNOW) {
							flag = false;
							yOffset[x][z] = i;
						}
					}
				}
				snowCone[x][z] = getBlockType(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z);
				snowConeData[x][z] = clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z).getBlockData();
			}
		}
		// figure out new snowheights
		for (int x = 0; x <= brushSizeDoubled; x++) {
			double xSquared = Math.pow(x - brushSize, 2);
			for (int z = 0; z <= 2 * brushSize; z++) {
				double zSquared = Math.pow(z - brushSize, 2);
				double dist = Math.pow(xSquared + zSquared, 0.5); // distance from center of array
				int snowData = brushSize - (int) Math.ceil(dist);
				if (snowData >= 0) { // no funny business
					// Increase snowtile size, if smaller than target
					if (snowData == 0) {
						if (snowCone[x][z].isEmpty()) {
							snowCone[x][z] = Material.SNOW;
							snowConeData[x][z] = Material.SNOW.createBlockData();
						}
					} else if (snowData == 7) { // Turn largest snowtile into snowblock
						if (snowCone[x][z] == Material.SNOW) {
							snowCone[x][z] = Material.SNOW_BLOCK;
							snowConeData[x][z] = Material.SNOW_BLOCK.createBlockData();
						}
					} else {
						if (snowData > blockDataToSnowLayers(snowConeData[x][z])) {
							if (snowCone[x][z].isEmpty()) {
								setSnowLayers(snowConeData[x][z], snowData);
								snowCone[x][z] = Material.SNOW;
							} else if (snowCone[x][z] == Material.SNOW) {
								setSnowLayers(snowConeData[x][z], snowData);
							}
						} else if (yOffset[x][z] > 0 && snowCone[x][z] == Material.SNOW) {
							setSnowLayers(snowConeData[x][z], blockDataToSnowLayers(snowConeData[x][z]) + 1);
							if (blockDataToSnowLayers(snowConeData[x][z]) == 7) {
								snowConeData[x][z] = Material.SNOW.createBlockData();
								snowCone[x][z] = Material.SNOW_BLOCK;
							}
						}
					}
				}
			}
		}
		Undo undo = new Undo();
		for (int x = 0; x <= brushSizeDoubled; x++) {
			for (int z = 0; z <= brushSizeDoubled; z++) {
				if (getBlockType(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z) != snowCone[x][z] || !clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z).getBlockData()
					.equals(snowConeData[x][z])) {
					undo.put(clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z));
				}
				setBlockType(blockPositionZ - brushSize + z, blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], snowCone[x][z]);
				clampY(blockPositionX - brushSize + x, blockPositionY - yOffset[x][z], blockPositionZ - brushSize + z).setBlockData(snowConeData[x][z]);
			}
		}
		Sniper owner = toolkitProperties.getOwner();
		owner.storeUndo(undo);
	}

	private int blockDataToSnowLayers(BlockData blockData) {
		if (!(blockData instanceof Snow)) {
			return 0;
		}
		Snow snow = (Snow) blockData;
		return snow.getLayers();
	}

	private void setSnowLayers(BlockData blockData, int layers) {
		if (!(blockData instanceof Snow)) {
			return;
		}
		Snow snow = (Snow) blockData;
		snow.setLayers(layers);
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block targetBlock = getTargetBlock();
		if (targetBlock.getType() == Material.SNOW) {
			this.addSnow(toolkitProperties, targetBlock);
		} else {
			Block blockAbove = targetBlock.getRelative(BlockFace.UP);
			Material type = blockAbove.getType();
			if (type.isEmpty()) {
				addSnow(toolkitProperties, blockAbove);
			} else {
				Sniper owner = toolkitProperties.getOwner();
				owner.sendMessage(ChatColor.RED + "Error: Center block neither snow nor air.");
			}
		}
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName("Snow Cone");
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		String firstParameter = parameters[1];
		if (firstParameter.equalsIgnoreCase("info")) {
			toolkitProperties.sendMessage(ChatColor.GOLD + "Snow Cone Parameters:");
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.snowcone";
	}
}
