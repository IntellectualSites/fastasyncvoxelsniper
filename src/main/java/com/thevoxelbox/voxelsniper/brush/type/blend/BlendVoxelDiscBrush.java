package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.painter.Painters;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlendVoxelDiscBrush extends AbstractBlendBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.GOLD + "Blend Voxel Disc Parameters:");
			messenger.sendMessage(ChatColor.AQUA + "/b bvd water -- toggle include or exclude (default) water");
			return;
		}
		super.handleCommand(parameters, snipe);
	}

	@Override
	public void blend(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		int squareEdge = 2 * brushSize + 1;
		BlockVector3 targetBlock = getTargetBlock();
		int smallSquareArea = MathHelper.square(squareEdge);
		Set<BlockVector3> smallSquare = new HashSet<>(smallSquareArea);
		Map<BlockVector3, Material> smallSquareMaterials = new HashMap<>(smallSquareArea);
		Painters.square()
			.center(targetBlock)
			.radius(brushSize)
			.blockSetter(position -> {
				Material material = getBlockType(position);
				smallSquare.add(position);
				smallSquareMaterials.put(position, material);
			})
			.paint();
		for (BlockVector3 smallSquareBlock : smallSquare) {
			Map<Material, Integer> materialsFrequencies = new EnumMap<>(Material.class);
			Painters.square()
				.center(smallSquareBlock)
				.radius(1)
				.blockSetter(position -> {
					if (position.equals(smallSquareBlock)) {
						return;
					}
					Material material = getBlockType(position);
					materialsFrequencies.merge(material, 1, Integer::sum);
				})
				.paint();
			CommonMaterial commonMaterial = findCommonMaterial(materialsFrequencies);
			Material material = commonMaterial.getMaterial();
			if (material != null) {
				smallSquareMaterials.put(smallSquareBlock, material);
			}
		}
		Undo undo = new Undo();
		setBlocks(smallSquareMaterials, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
