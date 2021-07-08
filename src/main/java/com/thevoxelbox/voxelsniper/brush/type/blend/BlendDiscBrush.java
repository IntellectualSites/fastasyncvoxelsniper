package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
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

public class BlendDiscBrush extends AbstractBlendBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.GOLD + "Blend Disc Parameters:");
			messenger.sendMessage(ChatColor.AQUA + "/b bd water -- toggle include or exclude (default) water");
			return;
		}
		super.handleCommand(parameters, snipe);
	}

	@Override
	public void blend(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		BlockVector3 targetBlock = getTargetBlock();
		int smallCircleArea = (int) MathHelper.circleArea(brushSize);
		Set<BlockVector3> smallCircle = new HashSet<>(smallCircleArea);
		Map<BlockVector3, Material> smallCircleMaterials = new HashMap<>(smallCircleArea);
		Painters.circle()
			.center(targetBlock)
			.radius(brushSize)
			.blockSetter(position -> {
				Material material = getBlockType(position);
				smallCircle.add(position);
				smallCircleMaterials.put(position, material);
			})
			.paint();
		for (BlockVector3 smallCircleBlock : smallCircle) {
			Map<Material, Integer> materialsFrequencies = new EnumMap<>(Material.class);
			Painters.square()
				.center(smallCircleBlock)
				.radius(1)
				.blockSetter(position -> {
					if (position.equals(smallCircleBlock)) {
						return;
					}
					Material material = getBlockType(position);
					materialsFrequencies.merge(material, 1, Integer::sum);
				})
				.paint();
			CommonMaterial commonMaterial = findCommonMaterial(materialsFrequencies);
			Material material = commonMaterial.getMaterial();
			if (material != null) {
				smallCircleMaterials.put(smallCircleBlock, material);
			}
		}
		Undo undo = new Undo();
		setBlocks(smallCircleMaterials, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
