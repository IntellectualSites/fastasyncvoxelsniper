package com.thevoxelbox.voxelsniper.performer.type.material;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class ExcludeMaterialPerformer extends AbstractPerformer {

	private List<BlockData> excludeList;
	private Material type;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.type = toolkitProperties.getBlockType();
		this.excludeList = toolkitProperties.getVoxelList();
	}

	@Override
	public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
		BlockData blockData = BukkitAdapter.adapt(block);
		if (!this.excludeList.contains(blockData)) {
			Undo undo = getUndo();
			undo.put(block);
			setBlockType(editSession, x, y, z, type);
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.voxelListMessage()
			.blockTypeMessage()
			.send();
	}
}
