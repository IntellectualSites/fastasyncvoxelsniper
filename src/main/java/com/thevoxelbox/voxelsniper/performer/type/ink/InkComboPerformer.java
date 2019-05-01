package com.thevoxelbox.voxelsniper.performer.type.ink;

import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class InkComboPerformer extends AbstractPerformer {

	private BlockData blockData;
	private BlockData replaceBlockData;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceBlockData = toolkitProperties.getReplaceBlockData();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (blockData.equals(this.replaceBlockData)) {
			Undo undo = getUndo();
			undo.put(block);
			block.setBlockData(this.blockData);
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.replaceBlockTypeMessage()
			.blockDataMessage()
			.replaceBlockDataMessage()
			.send();
	}
}
