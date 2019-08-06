package com.thevoxelbox.voxelsniper.brush.type.blend;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.math.Vector3i;
import com.thevoxelbox.voxelsniper.util.painter.Painters;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
		int largeCircleArea = (int) MathHelper.circleArea(brushSize + 2);
		Map<Vector3i, Block> largeCircle = new HashMap<>(largeCircleArea);
		Block targetBlock = getTargetBlock();
		Painters.circle()
			.center(targetBlock)
			.radius(brushSize + 2)
			.blockSetter(position -> {
				Block block = getBlock(position);
				largeCircle.put(position, block);
			})
			.paint();
		int smallCircleArea = (int) MathHelper.circleArea(brushSize);
		Map<Vector3i, Block> smallCircle = new HashMap<>(smallCircleArea);
		Map<Vector3i, Material> smallCircleMaterials = new HashMap<>(smallCircleArea);
		Painters.circle()
			.center(targetBlock)
			.radius(brushSize)
			.blockSetter(position -> {
				Block block = largeCircle.get(position);
				smallCircle.put(position, block);
				smallCircleMaterials.put(position, block.getType());
			})
			.paint();
		for (Block smallCircleBlock : smallCircle.values()) {
			Vector3i blockPosition = new Vector3i(smallCircleBlock);
			Map<Material, Integer> materialsFrequencies = new EnumMap<>(Material.class);
			Painters.square()
				.center(smallCircleBlock)
				.radius(1)
				.blockSetter(position -> {
					if (position.equals(blockPosition)) {
						return;
					}
					Block block = largeCircle.get(position);
					Material material = block.getType();
					materialsFrequencies.merge(material, 1, Integer::sum);
				})
				.paint();
			CommonMaterial commonMaterial = findCommonMaterial(materialsFrequencies);
			Material material = commonMaterial.getMaterial();
			if (material != null) {
				smallCircleMaterials.put(blockPosition, material);
			}
		}
		Undo undo = new Undo();
		setBlocks(smallCircleMaterials, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
