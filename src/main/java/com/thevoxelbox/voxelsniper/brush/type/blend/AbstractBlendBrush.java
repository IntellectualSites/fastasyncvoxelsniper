package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractBlendBrush extends AbstractBrush {

	private boolean airExcluded = true;
	private boolean waterExcluded = true;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("water")) {
				this.waterExcluded = !this.waterExcluded;
				messenger.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.waterExcluded ? "exclude" : "include"));
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		this.airExcluded = false;
		blend(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		this.airExcluded = true;
		blend(snipe);
	}

	public abstract void blend(Snipe snipe);

	protected void setBlocks(Map<BlockVector3, Material> materials, Undo undo) {
		for (Entry<BlockVector3, Material> entry : materials.entrySet()) {
			BlockVector3 position = entry.getKey();
			Material material = entry.getValue();
			if (checkExclusions(material)) {
				Material currentBlockType = getBlockType(position);
				if (currentBlockType != material) {
					Block clamped = clampY(position);
					undo.put(clamped);
				}
				setBlockType(position, material);
			}
		}
	}

	protected CommonMaterial findCommonMaterial(Map<Material, Integer> materialsFrequencies) {
		CommonMaterial commonMaterial = new CommonMaterial();
		for (Entry<Material, Integer> entry : materialsFrequencies.entrySet()) {
			Material material = entry.getKey();
			int frequency = entry.getValue();
			if (frequency > commonMaterial.getFrequency() && checkExclusions(material)) {
				commonMaterial.setMaterial(material);
				commonMaterial.setFrequency(frequency);
			}
		}
		return commonMaterial;
	}

	private boolean checkExclusions(Material material) {
		return (!this.airExcluded || !Materials.isEmpty(material)) && (!this.waterExcluded || material != Material.WATER);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.blockTypeMessage()
			.message(ChatColor.BLUE + "Water Mode: " + (this.waterExcluded ? "exclude" : "include"))
			.send();
	}
}
