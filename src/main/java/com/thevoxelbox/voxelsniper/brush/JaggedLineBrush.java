package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Jagged_Line_Brush
 *
 * @author Giltwist
 * @author Monofraps
 */
public class JaggedLineBrush extends PerformBrush {

	private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
	private static int timesUsed;

	private static final int RECURSION_MIN = 1;
	private static final int RECURSION_DEFAULT = 3;
	private static final int RECURSION_MAX = 10;
	private static final int SPREAD_DEFAULT = 3;

	private Random random = new Random();
	private Vector originCoords;
	private Vector targetCoords = new Vector();
	private int recursion = RECURSION_DEFAULT;
	private int spread = SPREAD_DEFAULT;

	/**
	 *
	 */
	public JaggedLineBrush() {
		this.setName("Jagged Line");
	}

	private void jaggedP(SnipeData v) {
		Vector originClone = this.originCoords.clone()
			.add(HALF_BLOCK_OFFSET);
		Vector targetClone = this.targetCoords.clone()
			.add(HALF_BLOCK_OFFSET);
		Vector direction = targetClone.clone()
			.subtract(originClone);
		double length = this.targetCoords.distance(this.originCoords);
		if (length == 0) {
			this.current.perform(this.targetCoords.toLocation(this.getWorld())
				.getBlock());
		} else {
			for (BlockIterator iterator = new BlockIterator(this.getWorld(), originClone, direction, 0, NumberConversions.round(length)); iterator.hasNext(); ) {
				Block block = iterator.next();
				for (int i = 0; i < this.recursion; i++) {
					this.current.perform(this.clampY(Math.round(block.getX() + this.random.nextInt(this.spread * 2) - this.spread), Math.round(block.getY() + this.random.nextInt(this.spread * 2) - this.spread), Math.round(block.getZ() + this.random.nextInt(this.spread * 2) - this.spread)));
				}
			}
		}
		v.owner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData v) {
		if (this.originCoords == null) {
			this.originCoords = new Vector();
		}
		this.originCoords = this.getTargetBlock()
			.getLocation()
			.toVector();
		v.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
	}

	@Override
	public final void powder(SnipeData v) {
		if (this.originCoords == null) {
			v.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
		} else {
			this.targetCoords = this.getTargetBlock()
				.getLocation()
				.toVector();
			this.jaggedP(v);
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom(ChatColor.GRAY + String.format("Recursion set to: %d", this.recursion));
		message.custom(ChatColor.GRAY + String.format("Spread set to: %d", this.spread));
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (String parameter : parameters) {
			try {
				if (parameter.equalsIgnoreCase("info")) {
					snipeData.sendMessage(ChatColor.GOLD + "Jagged Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a jagged line to set the second point.");
					snipeData.sendMessage(ChatColor.AQUA + "/b j r# - sets the number of recursions (default 3, must be 1-10)");
					snipeData.sendMessage(ChatColor.AQUA + "/b j s# - sets the spread (default 3, must be 1-10)");
					return;
				}
				if (parameter.startsWith("r")) {
					int temp = Integer.parseInt(parameter.substring(1));
					if (temp >= RECURSION_MIN && temp <= RECURSION_MAX) {
						this.recursion = temp;
						snipeData.sendMessage(ChatColor.GREEN + "Recursion set to: " + this.recursion);
					} else {
						snipeData.sendMessage(ChatColor.RED + "ERROR: Recursion must be " + RECURSION_MIN + "-" + RECURSION_MAX);
					}
					return;
				} else if (parameter.startsWith("s")) {
					this.spread = Integer.parseInt(parameter.substring(1));
					snipeData.sendMessage(ChatColor.GREEN + "Spread set to: " + this.spread);
				}
			} catch (NumberFormatException exception) {
				snipeData.sendMessage(ChatColor.RED + String.format("Exception while parsing parameter: %s", parameter));
				exception.printStackTrace();
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.jaggedline";
	}
}
