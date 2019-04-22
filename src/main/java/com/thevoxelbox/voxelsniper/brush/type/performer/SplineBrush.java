package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

/**
 * FOR ANY BRUSH THAT USES A SPLINE, EXTEND THAT BRUSH FROM THIS BRUSH!!! That way, the spline calculations are already there. Also, the UI for
 * the splines will be included.
 *
 * @author psanker
 */
public class SplineBrush extends AbstractPerformerBrush {

	private final List<Block> endPts = new ArrayList<>();
	private final List<Block> ctrlPts = new ArrayList<>();
	protected List<Point> spline = new ArrayList<>();
	protected boolean set;
	protected boolean ctrl;
	protected String[] sparams = {"ss", "sc", "clear"};

	public SplineBrush() {
		super("Spline");
	}

	public final void addToSet(SnipeData snipeData, boolean ep, Block targetBlock) {
		if (ep) {
			if (this.endPts.contains(targetBlock) || this.endPts.size() == 2) {
				return;
			}
			this.endPts.add(targetBlock);
			snipeData.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + ChatColor.GRAY + "to endpoint selection");
			return;
		}
		if (this.ctrlPts.contains(targetBlock) || this.ctrlPts.size() == 2) {
			return;
		}
		this.ctrlPts.add(targetBlock);
		snipeData.sendMessage(ChatColor.GRAY + "Added block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + ChatColor.GRAY + "to control point selection");
	}

	public final void removeFromSet(SnipeData v, boolean ep, Block targetBlock) {
		if (ep) {
			if (!this.endPts.contains(targetBlock)) {
				v.sendMessage(ChatColor.RED + "That block is not in the endpoint selection set.");
				return;
			}
			this.endPts.add(targetBlock);
			v.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + ChatColor.GRAY + "from endpoint selection");
			return;
		}
		if (!this.ctrlPts.contains(targetBlock)) {
			v.sendMessage(ChatColor.RED + "That block is not in the control point selection set.");
			return;
		}
		this.ctrlPts.remove(targetBlock);
		v.sendMessage(ChatColor.GRAY + "Removed block " + ChatColor.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + ChatColor.GRAY + "from control point selection");
	}

	public final boolean spline(Point start, Point end, Point c1, Point c2, SnipeData v) {
		this.spline.clear();
		try {
			Point c = (c1.subtract(start)).multiply(3);
			Point b = ((c2.subtract(c1)).multiply(3)).subtract(c);
			Point a = ((end.subtract(start)).subtract(c)).subtract(b);
			for (double t = 0.0; t < 1.0; t += 0.01) {
				int px = (int) Math.round((a.getX() * (t * t * t)) + (b.getX() * (t * t)) + (c.getX() * t) + this.endPts.get(0)
					.getX());
				int py = (int) Math.round((a.getY() * (t * t * t)) + (b.getY() * (t * t)) + (c.getY() * t) + this.endPts.get(0)
					.getY());
				int pz = (int) Math.round((a.getZ() * (t * t * t)) + (b.getZ() * (t * t)) + (c.getZ() * t) + this.endPts.get(0)
					.getZ());
				if (!this.spline.contains(new Point(px, py, pz))) {
					this.spline.add(new Point(px, py, pz));
				}
			}
			return true;
		} catch (RuntimeException exception) {
			v.sendMessage(ChatColor.RED + "Not enough points selected; " + this.endPts.size() + " endpoints, " + this.ctrlPts.size() + " control points");
			return false;
		}
	}

	protected final void render(SnipeData v) {
		if (this.spline.isEmpty()) {
			return;
		}
		for (Point point : this.spline) {
			this.current.perform(this.clampY(point.getX(), point.getY(), point.getZ()));
		}
		v.getOwner()
			.storeUndo(this.current.getUndo());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		if (this.set) {
			this.removeFromSet(snipeData, true, this.getTargetBlock());
		} else if (this.ctrl) {
			this.removeFromSet(snipeData, false, this.getTargetBlock());
		}
	}

	protected final void clear(SnipeData v) {
		this.spline.clear();
		this.ctrlPts.clear();
		this.endPts.clear();
		v.sendMessage(ChatColor.GRAY + "Bezier curve cleared.");
	}

	@Override
	public final void powder(SnipeData snipeData) {
		if (this.set) {
			this.addToSet(snipeData, true, this.getTargetBlock());
		}
		if (this.ctrl) {
			this.addToSet(snipeData, false, this.getTargetBlock());
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		if (this.set) {
			message.custom(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
		} else if (this.ctrl) {
			message.custom(ChatColor.GRAY + "Control point selection mode ENABLED.");
		} else {
			message.custom(ChatColor.AQUA + "No selection mode enabled.");
		}
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			if (parameters[i].equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Spline brush parameters");
				snipeData.sendMessage(ChatColor.AQUA + "ss: Enable endpoint selection mode for desired curve");
				snipeData.sendMessage(ChatColor.AQUA + "sc: Enable control point selection mode for desired curve");
				snipeData.sendMessage(ChatColor.AQUA + "clear: Clear out the curve selection");
				snipeData.sendMessage(ChatColor.AQUA + "ren: Render curve from control points");
				return;
			}
			if (parameters[i].equalsIgnoreCase("sc")) {
				if (!this.ctrl) {
					this.set = false;
					this.ctrl = true;
					snipeData.sendMessage(ChatColor.GRAY + "Control point selection mode ENABLED.");
				} else {
					this.ctrl = false;
					snipeData.sendMessage(ChatColor.AQUA + "Control point selection mode disabled.");
				}
			} else if (parameters[i].equalsIgnoreCase("ss")) {
				if (!this.set) {
					this.set = true;
					this.ctrl = false;
					snipeData.sendMessage(ChatColor.GRAY + "Endpoint selection mode ENABLED.");
				} else {
					this.set = false;
					snipeData.sendMessage(ChatColor.AQUA + "Endpoint selection mode disabled.");
				}
			} else if (parameters[i].equalsIgnoreCase("clear")) {
				this.clear(snipeData);
			} else if (parameters[i].equalsIgnoreCase("ren")) {
				if (this.spline(new Point(this.endPts.get(0)), new Point(this.endPts.get(1)), new Point(this.ctrlPts.get(0)), new Point(this.ctrlPts.get(1)), snipeData)) {
					this.render(snipeData);
				}
			} else {
				snipeData.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	// Vector class for splines
	protected static class Point {

		private int x;
		private int y;
		private int z;

		public Point(Block block) {
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
		}

		public Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final Point add(Point point) {
			return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
		}

		public final Point multiply(int scalar) {
			return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
		}

		public final Point subtract(Point point) {
			return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
		}

		public int getX() {
			return this.x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return this.y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getZ() {
			return this.z;
		}

		public void setZ(int z) {
			this.z = z;
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.spline";
	}
}
