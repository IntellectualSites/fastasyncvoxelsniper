package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * @author Voxel
 */
public class SetRedstoneRotateBrush extends Brush {

	@Nullable
	private Block block;
	private Undo undo;

	/**
	 *
	 */
	public SetRedstoneRotateBrush() {
		this.setName("Set Redstone Rotate");
	}

	private boolean set(Block bl) {
		if (this.block == null) {
			this.block = bl;
			return true;
		} else {
			this.undo = new Undo();
			int lowX = (this.block.getX() <= bl.getX()) ? this.block.getX() : bl.getX();
			int lowY = (this.block.getY() <= bl.getY()) ? this.block.getY() : bl.getY();
			int lowZ = (this.block.getZ() <= bl.getZ()) ? this.block.getZ() : bl.getZ();
			int highX = (this.block.getX() >= bl.getX()) ? this.block.getX() : bl.getX();
			int highY = (this.block.getY() >= bl.getY()) ? this.block.getY() : bl.getY();
			int highZ = (this.block.getZ() >= bl.getZ()) ? this.block.getZ() : bl.getZ();
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

	@SuppressWarnings("deprecation")
	private void perform(Block bl) {
		if (bl.getType() == Material.DIODE_BLOCK_ON || bl.getType() == Material.DIODE_BLOCK_OFF) {
			this.undo.put(bl);
			bl.setData((((bl.getData() % 4) + 1 < 5) ? (byte) (bl.getData() + 1) : (byte) (bl.getData() - 4)));
		}
	}

	@Override
	protected final void arrow(SnipeData v) {
		if (this.set(this.getTargetBlock())) {
			v.owner()
				.getPlayer()
				.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			v.owner()
				.storeUndo(this.undo);
		}
	}

	@Override
	protected final void powder(SnipeData v) {
		if (this.set(this.getLastBlock())) {
			v.owner()
				.getPlayer()
				.sendMessage(ChatColor.GRAY + "Point one");
		} else {
			v.owner()
				.storeUndo(this.undo);
		}
	}

	@Override
	public final void info(Message vm) {
		this.block = null;
		vm.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] par, SnipeData v) {
		super.parameters(par, v);
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.setredstonerotate";
	}
}
