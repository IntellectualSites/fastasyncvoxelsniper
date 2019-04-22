package com.thevoxelbox.voxelsniper.brush;

import java.util.stream.Stream;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
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
public class SetRedstoneFlipBrush extends AbstractBrush {

	@Nullable
	private Block block;
	private Undo undo;
	private boolean northSouth = true;

	public SetRedstoneFlipBrush() {
		super("Set Redstone Flip");
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
			int lowX = x1 <= x2 ? x1 : x2;
			int lowY = y1 <= y2 ? y1 : y2;
			int lowZ = z1 <= z2 ? z1 : z2;
			int highX = x1 >= x2 ? x1 : x2;
			int highY = y1 >= y2 ? y1 : y2;
			int highZ = z1 >= z2 ? z1 : z2;
			for (int y = lowY; y <= highY; y++) {
				for (int x = lowX; x <= highX; x++) {
					for (int z = lowZ; z <= highZ; z++) {
						this.perform(this.clampY(x, y, z));
					}
				}
			}
			this.block = null;
			return false;
		}
	}

	private void perform(Block block) {
		if (block.getType() == Material.REPEATER) {
			BlockData blockData = block.getBlockData();
			Repeater repeater = (Repeater) blockData;
			int delay = repeater.getDelay();
			if (this.northSouth) {
				if ((delay % 4) == 1) {
					this.undo.put(block);
					repeater.setDelay(delay + 2);
				} else if ((delay % 4) == 3) {
					this.undo.put(block);
					repeater.setDelay(delay - 2);
				}
			} else {
				if ((delay % 4) == 2) {
					this.undo.put(block);
					repeater.setDelay(delay - 2);
				} else if ((delay % 4) == 0) {
					this.undo.put(block);
					repeater.setDelay(delay + 2);
				}
			}
			block.setBlockData(repeater);
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		if (this.set(this.getTargetBlock())) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			snipeData.getOwner()
				.storeUndo(this.undo);
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		if (this.set(lastBlock)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			Sniper owner = snipeData.getOwner();
			owner.storeUndo(this.undo);
		}
	}

	@Override
	public final void info(Message message) {
		this.block = null;
		message.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Set Repeater Flip Parameters:");
				snipeData.sendMessage(ChatColor.AQUA + "/b setrf <direction> -- valid direction inputs are(n,s,e,world), Set the direction that you wish to flip your repeaters, defaults to north/south.");
				return;
			}
			if (Stream.of("n", "s", "ns")
				.anyMatch(parameter::startsWith)) {
				this.northSouth = true;
				snipeData.sendMessage(ChatColor.AQUA + "Flip direction set to north/south");
			} else if (Stream.of("e", "world", "ew")
				.anyMatch(parameter::startsWith)) {
				this.northSouth = false;
				snipeData.sendMessage(ChatColor.AQUA + "Flip direction set to east/west.");
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.setredstoneflip";
	}
}
