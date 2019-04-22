package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Set_Brush
 *
 * @author Voxel
 */
public class SetBrush extends AbstractPerformerBrush {

	private static final int SELECTION_SIZE_MAX = 5000000;
	@Nullable
	private Block block;

	public SetBrush() {
		super("Set");
	}

	private boolean set(Block block, SnipeData snipeData) {
		if (this.block == null) {
			this.block = block;
			return true;
		} else {
			World thisBlockWorld = this.block.getWorld();
			String name = thisBlockWorld.getName();
			World parameterBlockWorld = block.getWorld();
			String parameterBlockWorldName = parameterBlockWorld.getName();
			if (!name.equals(parameterBlockWorldName)) {
				snipeData.sendMessage(ChatColor.RED + "You selected points in different worlds!");
				this.block = null;
				return true;
			}
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
			if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > SELECTION_SIZE_MAX) {
				snipeData.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
			} else {
				for (int y = lowY; y <= highY; y++) {
					for (int x = lowX; x <= highX; x++) {
						for (int z = lowZ; z <= highZ; z++) {
							this.current.perform(clampY(x, y, z));
						}
					}
				}
			}
			this.block = null;
			return false;
		}
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		Block targetBlock = getTargetBlock();
		if (set(targetBlock, snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			Sniper owner = snipeData.getOwner();
			owner.storeUndo(this.current.getUndo());
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		Block lastBlock = getLastBlock();
		if (lastBlock == null) {
			return;
		}
		if (set(lastBlock, snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			Sniper owner = snipeData.getOwner();
			owner.storeUndo(this.current.getUndo());
		}
	}

	@Override
	public final void info(Message message) {
		this.block = null;
		message.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		super.parameters(parameters, snipeData);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.set";
	}
}
