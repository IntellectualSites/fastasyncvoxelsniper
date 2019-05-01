package com.thevoxelbox.voxelsniper.brush.performer.type.material;

import com.thevoxelbox.voxelsniper.brush.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class MaterialComboNoPhysicsPerformer extends AbstractPerformer {

	private BlockData blockData;
	private BlockData replaceBlockData;

	public MaterialComboNoPhysicsPerformer() {
		super("Material-Combo, No Physics");
	}

	@Override
	public void init(ToolkitProperties toolkitProperties) {
		this.world = toolkitProperties.getWorld();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceBlockData = toolkitProperties.getReplaceBlockData();
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.blockDataType();
		messages.replaceBlockDataType();
		messages.replaceBlockData();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (blockData.equals(this.replaceBlockData)) {
			this.undo.put(block);
			block.setType(this.blockData.getMaterial(), false);
		}
	}

	@Override
	public boolean isUsingReplaceMaterial() {
		return true;
	}
}
