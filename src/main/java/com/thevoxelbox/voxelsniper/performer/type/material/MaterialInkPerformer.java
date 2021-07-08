package com.thevoxelbox.voxelsniper.performer.type.material;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.data.BlockData;

public class MaterialInkPerformer extends AbstractPerformer {

	private BlockData blockData;
	private BlockData replaceBlockData;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceBlockData = toolkitProperties.getReplaceBlockData();
	}

	@Override
	public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
		BlockData blockData = BukkitAdapter.adapt(block);
		if (blockData.equals(this.replaceBlockData)) {
			Undo undo = getUndo();
			undo.put(block);
			setBlockType(editSession, x, y, z, this.blockData.getMaterial());
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.blockTypeMessage()
			.replaceBlockDataMessage()
			.send();
	}
}
