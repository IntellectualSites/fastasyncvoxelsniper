package com.thevoxelbox.voxelsniper.brush.performer.type.ink;

import com.thevoxelbox.voxelsniper.brush.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class InkMaterialNoPhysicsPerformer extends AbstractPerformer {

	private BlockData blockData;
	private Material replaceMaterial;

	public InkMaterialNoPhysicsPerformer() {
		super("Ink-Material, No Physics");
	}

	@Override
	public void init(ToolkitProperties toolkitProperties) {
		this.world = toolkitProperties.getWorld();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceMaterial = toolkitProperties.getReplaceBlockType();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockData();
		messages.replaceBlockDataType();
	}

	@Override
	public void perform(Block block) {
		if (block.getType() == this.replaceMaterial) {
			this.undo.put(block);
			block.setBlockData(this.blockData, false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
