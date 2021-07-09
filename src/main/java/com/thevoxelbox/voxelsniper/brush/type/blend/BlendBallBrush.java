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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlendBallBrush extends AbstractBlendBrush {

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
			messenger.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default: exclude) water");
			return;
		}
		super.handleCommand(parameters, snipe);
	}

	@Override
	public void blend(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		BlockVector3 targetBlock = getTargetBlock();
		int smallSphereVolume = (int) MathHelper.sphereVolume(brushSize);
		Set<BlockVector3> smallSphere = new HashSet<>(smallSphereVolume);
		Map<BlockVector3, Material> smallSphereMaterials = new HashMap<>(smallSphereVolume);
		Painters.sphere()
			.center(targetBlock)
			.radius(brushSize)
			.blockSetter(position -> {
				Material material = getBlockType(position);
				smallSphere.add(position);
				smallSphereMaterials.put(position, material);
			})
			.paint();
		for (BlockVector3 smallSphereBlock : smallSphere) {
			Map<Material, Integer> materialsFrequencies = new EnumMap<>(Material.class);
			Painters.cube()
				.center(smallSphereBlock)
				.radius(1)
				.blockSetter(position -> {
					if (position.equals(smallSphereBlock)) {
						return;
					}
					Material material = getBlockType(position);
					materialsFrequencies.merge(material, 1, Integer::sum);
				})
				.paint();
			CommonMaterial commonMaterial = findCommonMaterial(materialsFrequencies);
			Material material = commonMaterial.getMaterial();
			if (material != null) {
				smallSphereMaterials.put(smallSphereBlock, material);
			}
		}
		Undo undo = new Undo();
		setBlocks(smallSphereMaterials, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}
}
