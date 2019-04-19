package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Line_Brush
 *
 * @author Gavjenks
 * @author giltwist
 * @author MikeMatrix
 */
public class LineBrush extends PerformBrush {

	private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
	private Vector originCoords;
	private Vector targetCoords = new Vector();
	private World targetWorld;

	/**
	 *
	 */
	public LineBrush() {
		this.setName("Line");
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		if (parameters[1].equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
		}
	}

	private void linePowder(SnipeData v) {
		Vector originClone = this.originCoords.clone()
			.add(HALF_BLOCK_OFFSET);
		Vector targetClone = this.targetCoords.clone()
			.add(HALF_BLOCK_OFFSET);
		Vector direction = targetClone.clone()
			.subtract(originClone);
		double length = this.targetCoords.distance(this.originCoords);
		if (length == 0) {
			this.current.perform(this.targetCoords.toLocation(this.targetWorld)
				.getBlock());
		} else {
			for (BlockIterator blockIterator = new BlockIterator(this.targetWorld, originClone, direction, 0, NumberConversions.round(length)); blockIterator.hasNext(); ) {
				Block currentBlock = blockIterator.next();
				this.current.perform(currentBlock);
			}
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.originCoords = this.getTargetBlock()
			.getLocation()
			.toVector();
		this.targetWorld = this.getTargetBlock()
			.getWorld();
		snipeData.getOwner()
			.getPlayer()
			.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		if (this.originCoords == null || !this.getTargetBlock()
			.getWorld()
			.equals(this.targetWorld)) {
			snipeData.getOwner()
				.getPlayer()
				.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
		} else {
			this.targetCoords = this.getTargetBlock()
				.getLocation()
				.toVector();
			this.linePowder(snipeData);
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.line";
	}
}
