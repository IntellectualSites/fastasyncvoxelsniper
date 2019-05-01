package com.thevoxelbox.voxelsniper.performer.type.combo;

import java.util.List;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class ExcludeComboPerformer extends AbstractPerformer {

	private List<BlockData> excludeList;
	private BlockData blockData;

	public ExcludeComboPerformer() {
		super("Exclude Combo");
	}

	@Override
	public void info(Messages messages) {
		messages.performerName(this.getName());
		messages.voxelList();
		messages.blockDataType();
		messages.blockData();
	}

	@Override
	public void init(ToolkitProperties toolkitProperties) {
		this.world = toolkitProperties.getWorld();
		this.blockData = toolkitProperties.getBlockData();
		this.excludeList = toolkitProperties.getVoxelList();
	}

	@Override
	public void perform(Block block) {
		BlockData blockData = block.getBlockData();
		if (!this.excludeList.contains(blockData)) {
			this.undo.put(block);
			block.setBlockData(this.blockData);
		}
	}
}
