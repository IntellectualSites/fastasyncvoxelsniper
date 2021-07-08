package com.thevoxelbox.voxelsniper.performer.type.material;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;

public class MaterialPerformer extends AbstractPerformer {

	private Material material;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.material = toolkitProperties.getBlockType();
	}

	@Override
	public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
		if (BukkitAdapter.adapt(block.getBlockType()) != this.material) {
			Undo undo = getUndo();
			undo.put(block);
			setBlockType(editSession, x, y, z, this.material);
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.blockTypeMessage()
			.send();
	}
}
