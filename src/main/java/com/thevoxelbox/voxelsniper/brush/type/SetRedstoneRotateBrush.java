package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Repeater;
import org.jetbrains.annotations.Nullable;

/**
 * @author Voxel
 */
public class SetRedstoneRotateBrush extends AbstractBrush {

	@Nullable
	private Block block;
	private Undo undo;

	public SetRedstoneRotateBrush() {
		super("Set Redstone Rotate");
	}

	private boolean set(Block block) {
		if (this.block == null) {
			this.block = block;
			return true;
		} else {
			this.undo = new Undo();
			int x1 = this.block.getX();
			int x2 = block.getX();
			int y1 = this.block.getY();
			int y2 = block.getY();
			int z1 = this.block.getZ();
			int z2 = block.getZ();
			int lowX = (x1 <= x2) ? x1 : x2;
			int lowY = (y1 <= y2) ? y1 : y2;
			int lowZ = (z1 <= z2) ? z1 : z2;
			int highX = (x1 >= x2) ? x1 : x2;
			int highY = (y1 >= y2) ? y1 : y2;
			int highZ = (z1 >= z2) ? z1 : z2;
			for (int y = lowY; y <= highY; y++) {
				for (int x = lowX; x <= highX; x++) {
					for (int z = lowZ; z <= highZ; z++) {
						perform(clampY(x, y, z));
					}
				}
			}
			this.block = null;
			return false;
		}
	}

	private void perform(Block block) {
		Material type = block.getType();
		if (type == Material.REPEATER) {
			this.undo.put(block);
			BlockData blockData = block.getBlockData();
			Repeater repeater = (Repeater) blockData;
			int delay = repeater.getDelay();
			repeater.setDelay(delay % 4 + 1 < 5 ? (byte) (delay + 1) : (byte) (delay - 4));
			block.setBlockData(blockData);
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		Block targetBlock = getTargetBlock();
		Sniper owner = snipeData.getOwner();
		if (set(targetBlock)) {
			owner.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			owner.storeUndo(this.undo);
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = getLastBlock();
		if (lastBlock == null) {
			return;
		}
		Sniper owner = snipeData.getOwner();
		if (set(lastBlock)) {
			owner.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			owner.storeUndo(this.undo);
		}
	}

	@Override
	public final void info(Messages messages) {
		this.block = null;
		messages.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		super.parameters(parameters, snipeData);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.setredstonerotate";
	}
}
