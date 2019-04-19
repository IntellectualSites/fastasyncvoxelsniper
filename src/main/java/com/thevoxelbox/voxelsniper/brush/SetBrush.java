package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Set_Brush
 *
 * @author Voxel
 */
public class SetBrush extends PerformBrush {

	private static final int SELECTION_SIZE_MAX = 5000000;
	@Nullable
	private Block block;

	/**
	 *
	 */
	public SetBrush() {
		this.setName("Set");
	}

	private boolean set(Block bl, SnipeData v) {
		if (this.block == null) {
			this.block = bl;
			return true;
		} else {
			if (!this.block.getWorld()
				.getName()
				.equals(bl.getWorld()
					.getName())) {
				v.sendMessage(ChatColor.RED + "You selected points in different worlds!");
				this.block = null;
				return true;
			}
			int lowX = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
			int lowY = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
			int lowZ = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
			int highX = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
			int highY = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
			int highZ = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();
			if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > SELECTION_SIZE_MAX) {
				v.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
			} else {
				for (int y = lowY; y <= highY; y++) {
					for (int x = lowX; x <= highX; x++) {
						for (int z = lowZ; z <= highZ; z++) {
							this.current.perform(this.clampY(x, y, z));
						}
					}
				}
			}
			this.block = null;
			return false;
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		if (this.set(this.getTargetBlock(), snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			snipeData.getOwner()
				.storeUndo(this.current.getUndo());
		}
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		if (this.set(this.getLastBlock(), snipeData)) {
			snipeData.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			snipeData.getOwner()
				.storeUndo(this.current.getUndo());
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
