package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
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
public class LineBrush extends AbstractPerformerBrush {

	private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
	private Vector originCoords;
	private Vector targetCoords = new Vector();
	private World targetWorld;

	public LineBrush() {
		super("Line");
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		if (parameters[1].equalsIgnoreCase("info")) {
			toolkitProperties.sendMessage(ChatColor.GOLD + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
		}
	}

	private void linePowder(ToolkitProperties v) {
		Vector originClone = this.originCoords.clone()
			.add(HALF_BLOCK_OFFSET);
		Vector targetClone = this.targetCoords.clone()
			.add(HALF_BLOCK_OFFSET);
		Vector direction = targetClone.clone()
			.subtract(originClone);
		double length = this.targetCoords.distance(this.originCoords);
		if (length == 0) {
			this.performer.perform(this.targetCoords.toLocation(this.targetWorld)
				.getBlock());
		} else {
			for (BlockIterator blockIterator = new BlockIterator(this.targetWorld, originClone, direction, 0, NumberConversions.round(length)); blockIterator.hasNext(); ) {
				Block currentBlock = blockIterator.next();
				this.performer.perform(currentBlock);
			}
		}
		v.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.originCoords = this.getTargetBlock()
			.getLocation()
			.toVector();
		this.targetWorld = this.getTargetBlock()
			.getWorld();
		toolkitProperties.getOwner()
			.getPlayer()
			.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		if (this.originCoords == null || !this.getTargetBlock()
			.getWorld()
			.equals(this.targetWorld)) {
			toolkitProperties.getOwner()
				.getPlayer()
				.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
		} else {
			this.targetCoords = this.getTargetBlock()
				.getLocation()
				.toVector();
			this.linePowder(toolkitProperties);
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.line";
	}
}
