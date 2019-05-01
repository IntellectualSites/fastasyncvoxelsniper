package com.thevoxelbox.voxelsniper.performer.type.ink;

import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class InkMaterialNoPhysicsPerformer extends AbstractPerformer {

	private BlockData blockData;
	private Material replaceMaterial;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceMaterial = toolkitProperties.getReplaceBlockType();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() == this.replaceMaterial) {
			Undo undo = getUndo();
			undo.put(block);
			block.setBlockData(this.blockData, false);
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.blockDataMessage()
			.replaceBlockTypeMessage()
			.send();
	}
}
