package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * The CloneStamp class is used to create a collection of blocks in a cylinder shape according to the selection the player has set.
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Clone_and_CopyPasta_Brushes
 *
 * @author Voxel
 */
public class CloneStampBrush extends StampBrush {

	public CloneStampBrush() {
		super("Clone");
	}

	/**
	 * The clone method is used to grab a snapshot of the selected area dictated blockPositionY targetBlock.x y z v.brushSize v.voxelHeight and v.cCen.
	 * x y z -- initial center of the selection v.brushSize -- the radius of the cylinder v.voxelHeight -- the height of the cylinder c.cCen -- the offset on
	 * the Y axis of the selection ( bottom of the cylinder ) as blockPositionY: Bottom_Y = targetBlock.y + v.cCen;
	 *
	 * @param snipeData the caller
	 */
	private void clone(SnipeData snipeData) {
		int brushSize = snipeData.getBrushSize();
		this.clone.clear();
		this.fall.clear();
		this.drop.clear();
		this.solid.clear();
		this.sorted = false;
		Block targetBlock = getTargetBlock();
		int yStartingPoint = targetBlock.getY() + snipeData.getCylinderCenter();
		int yEndPoint = targetBlock.getY() + snipeData.getVoxelHeight() + snipeData.getCylinderCenter();
		World world = this.getWorld();
		if (yStartingPoint < 0) {
			yStartingPoint = 0;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
		} else if (yStartingPoint > world.getMaxHeight() - 1) {
			yStartingPoint = world.getMaxHeight() - 1;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
		}
		if (yEndPoint < 0) {
			yEndPoint = 0;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
		} else if (yEndPoint > world.getMaxHeight() - 1) {
			yEndPoint = world.getMaxHeight() - 1;
			snipeData.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
		}
		double bSquared = Math.pow(brushSize, 2);
		for (int z = yStartingPoint; z < yEndPoint; z++) {
			this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX(), z, targetBlock.getZ()), 0, z - yStartingPoint, 0));
			for (int y = 1; y <= brushSize; y++) {
				this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX(), z, targetBlock.getZ() + y), 0, z - yStartingPoint, y));
				this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX(), z, targetBlock.getZ() - y), 0, z - yStartingPoint, -y));
				this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX() + y, z, targetBlock.getZ()), y, z - yStartingPoint, 0));
				this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX() - y, z, targetBlock.getZ()), -y, z - yStartingPoint, 0));
			}
			for (int x = 1; x <= brushSize; x++) {
				double xSquared = Math.pow(x, 2);
				for (int y = 1; y <= brushSize; y++) {
					if ((xSquared + Math.pow(y, 2)) <= bSquared) {
						this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX() + x, z, targetBlock.getZ() + y), x, z - yStartingPoint, y));
						this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX() + x, z, targetBlock.getZ() - y), x, z - yStartingPoint, -y));
						this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX() - x, z, targetBlock.getZ() + y), -x, z - yStartingPoint, y));
						this.clone.add(new StampBrushBlockWrapper(this.clampY(targetBlock.getX() - x, z, targetBlock.getZ() - y), -x, z - yStartingPoint, -y));
					}
				}
			}
		}
		snipeData.sendMessage(ChatColor.GREEN + String.valueOf(this.clone.size()) + ChatColor.AQUA + " blocks copied successfully.");
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.clone(snipeData);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.height();
		message.center();
		switch (this.stamp) {
			case DEFAULT:
				message.brushMessage("Default Stamp");
				break;
			case NO_AIR:
				message.brushMessage("No-Air Stamp");
				break;
			case FILL:
				message.brushMessage("Fill Stamp");
				break;
			default:
				message.custom(ChatColor.DARK_RED + "Error while stamping! Report");
				break;
		}
	}

	@Override
	public final void parameters(String[] parameters, com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		String parameter = parameters[1];
		if (parameter.equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "Clone / Stamp Cylinder brush parameters");
			snipeData.sendMessage(ChatColor.GREEN + "cs f -- Activates Fill mode");
			snipeData.sendMessage(ChatColor.GREEN + "cs a -- Activates No-Air mode");
			snipeData.sendMessage(ChatColor.GREEN + "cs d -- Activates Default mode");
		}
		if (parameter.equalsIgnoreCase("a")) {
			this.setStamp(StampType.NO_AIR);
			this.reSort();
			snipeData.sendMessage(ChatColor.AQUA + "No-Air stamp brush");
		} else if (parameter.equalsIgnoreCase("f")) {
			this.setStamp(StampType.FILL);
			this.reSort();
			snipeData.sendMessage(ChatColor.AQUA + "Fill stamp brush");
		} else if (parameter.equalsIgnoreCase("d")) {
			this.setStamp(StampType.DEFAULT);
			this.reSort();
			snipeData.sendMessage(ChatColor.AQUA + "Default stamp brush");
		} else if (!parameter.isEmpty() && parameter.charAt(0) == 'c') {
			snipeData.setCylinderCenter(Integer.parseInt(parameter.replace("c", "")));
			snipeData.sendMessage(ChatColor.BLUE + "Center set to " + snipeData.getCylinderCenter());
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.clonestamp";
	}
}
