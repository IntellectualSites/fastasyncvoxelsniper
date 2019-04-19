package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Extrude_Brush
 *
 * @author psanker
 */
public class ExtrudeBrush extends Brush {

	private double trueCircle;

	/**
	 *
	 */
	public ExtrudeBrush() {
		this.setName("Extrude");
	}

	private void extrudeUpOrDown(SnipeData v, boolean isUp) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
					int direction = (isUp ? 1 : -1);
					for (int y = 0; y < Math.abs(v.getVoxelHeight()); y++) {
						int tempY = y * direction;
						undo = this.perform(this.clampY(this.getTargetBlock()
							.getX() + x, this.getTargetBlock()
							.getY() + tempY, this.getTargetBlock()
							.getZ() + z), this.clampY(this.getTargetBlock()
							.getX() + x, this.getTargetBlock()
							.getY() + tempY + direction, this.getTargetBlock()
							.getZ() + z), v, undo);
					}
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	private void extrudeNorthOrSouth(SnipeData v, boolean isSouth) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int y = -brushSize; y <= brushSize; y++) {
				if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
					int direction = (isSouth) ? 1 : -1;
					for (int z = 0; z < Math.abs(v.getVoxelHeight()); z++) {
						int tempZ = z * direction;
						undo = this.perform(this.clampY(this.getTargetBlock()
							.getX() + x, this.getTargetBlock()
							.getY() + y, this.getTargetBlock()
							.getZ() + tempZ), this.clampY(this.getTargetBlock()
							.getX() + x, this.getTargetBlock()
							.getY() + y, this.getTargetBlock()
							.getZ() + tempZ + direction), v, undo);
					}
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	private void extrudeEastOrWest(SnipeData v, boolean isEast) {
		int brushSize = v.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		for (int y = -brushSize; y <= brushSize; y++) {
			double ySquared = Math.pow(y, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if ((ySquared + Math.pow(z, 2)) <= brushSizeSquared) {
					int direction = (isEast) ? 1 : -1;
					for (int x = 0; x < Math.abs(v.getVoxelHeight()); x++) {
						int tempX = x * direction;
						undo = this.perform(this.clampY(this.getTargetBlock()
							.getX() + tempX, this.getTargetBlock()
							.getY() + y, this.getTargetBlock()
							.getZ() + z), this.clampY(this.getTargetBlock()
							.getX() + tempX + direction, this.getTargetBlock()
							.getY() + y, this.getTargetBlock()
							.getZ() + z), v, undo);
					}
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@SuppressWarnings("deprecation")
	private Undo perform(Block b1, Block b2, SnipeData v, Undo undo) {
		if (v.getVoxelList()
			.contains(new int[] {this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()), this.getBlockDataAt(b1.getX(), b1.getY(), b1.getZ())})) {
			undo.put(b2);
			this.setBlockIdAt(b2.getZ(), b2.getX(), b2.getY(), this.getBlockIdAt(b1.getX(), b1.getY(), b1.getZ()));
			this.clampY(b2.getX(), b2.getY(), b2.getZ())
				.setData(this.clampY(b1.getX(), b1.getY(), b1.getZ())
					.getData());
		}
		return undo;
	}

	private void selectExtrudeMethod(SnipeData v, BlockFace blockFace, boolean towardsUser) {
		if (blockFace == null || v.getVoxelHeight() == 0) {
			return;
		}
		switch (blockFace) {
			case UP:
				extrudeUpOrDown(v, towardsUser);
				break;
			case SOUTH:
				extrudeNorthOrSouth(v, towardsUser);
				break;
			case EAST:
				extrudeEastOrWest(v, towardsUser);
				break;
			default:
				break;
		}
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.selectExtrudeMethod(v, this.getTargetBlock()
			.getFace(this.getLastBlock()), false);
	}

	@Override
	protected final void powder(SnipeData v) {
		this.selectExtrudeMethod(v, this.getTargetBlock()
			.getFace(this.getLastBlock()), true);
	}

	@Override
	public final void info(Message vm) {
		vm.brushName(this.getName());
		vm.size();
		vm.height();
		vm.voxelList();
		vm.custom(ChatColor.AQUA + ((this.trueCircle == 0.5) ? "True circle mode ON" : "True circle mode OFF"));
	}

	@Override
	public final void parameters(String[] par, com.thevoxelbox.voxelsniper.SnipeData v) {
		for (int i = 1; i < par.length; i++) {
			String parameter = par[i];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					v.sendMessage(ChatColor.GOLD + "Extrude brush Parameters:");
					v.sendMessage(ChatColor.AQUA + "/b ex true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ex false will switch back. (false is default)");
					return;
				} else if (parameter.startsWith("true")) {
					this.trueCircle = 0.5;
					v.sendMessage(ChatColor.AQUA + "True circle mode ON.");
				} else if (parameter.startsWith("false")) {
					this.trueCircle = 0;
					v.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
				} else {
					v.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
					return;
				}
			} catch (RuntimeException exception) {
				v.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.extrude";
	}
}
