package com.thevoxelbox.voxelsniper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author Voxel
 */
public class RangeBlockHelper {

	private static final int MAXIMUM_WORLD_HEIGHT = 255;
	private static final double DEFAULT_PLAYER_VIEW_HEIGHT = 1.65;
	private static final double DEFAULT_LOCATION_VIEW_HEIGHT = 0;
	private static final double DEFAULT_STEP = 0.2;
	private static final int DEFAULT_RANGE = 250;
	private Location playerLocation;
	private double rotationX;
	private double rotationY;
	private double viewHeight;
	private double rotXSin;
	private double rotXCos;
	private double rotYSin;
	private double rotYCos;
	private double length;
	private double hLength;
	private double step;
	private double range;
	private double playerX;
	private double playerY;
	private double playerZ;
	private double xOffset;
	private double yOffset;
	private double zOffset;
	private int lastX;
	private int lastY;
	private int lastZ;
	private int targetX;
	private int targetY;
	private int targetZ;
	private World world;

	/**
	 * Constructor requiring location, uses default values.
	 */
	public RangeBlockHelper(Location location) {
		this.init(location, DEFAULT_RANGE, DEFAULT_STEP, DEFAULT_LOCATION_VIEW_HEIGHT);
	}

	/**
	 * Constructor requiring location, max range, and a stepping value.
	 */
	public RangeBlockHelper(Location location, int range, double step) {
		this.world = location.getWorld();
		this.init(location, range, step, DEFAULT_LOCATION_VIEW_HEIGHT);
	}

	/**
	 * Constructor requiring player, max range, and a stepping value.
	 */
	public RangeBlockHelper(Player player, int range, double step) {
		this.init(player.getLocation(), range, step, DEFAULT_PLAYER_VIEW_HEIGHT);
	}

	/**
	 * Constructor requiring player, uses default values.
	 */
	public RangeBlockHelper(Player player, World world) {
		this.world = world;
		this.init(player.getLocation(), DEFAULT_RANGE, DEFAULT_STEP, DEFAULT_PLAYER_VIEW_HEIGHT);
		// values
	}

	public RangeBlockHelper(Player player, World world, double range) {
		this.world = world;
		this.init(player.getLocation(), range, DEFAULT_STEP, DEFAULT_PLAYER_VIEW_HEIGHT);
		this.fromOffWorld();
	}

	public final void fromOffWorld() {
		if (this.targetY > MAXIMUM_WORLD_HEIGHT) {
			while (this.targetY > MAXIMUM_WORLD_HEIGHT && this.length <= this.range) {
				this.lastX = this.targetX;
				this.lastY = this.targetY;
				this.lastZ = this.targetZ;
				do {
					this.length += this.step;
					this.hLength = (this.length * this.rotYCos);
					this.yOffset = (this.length * this.rotYSin);
					this.xOffset = (this.hLength * this.rotXCos);
					this.zOffset = (this.hLength * this.rotXSin);
					this.targetX = (int) Math.floor(this.xOffset + this.playerX);
					this.targetY = (int) Math.floor(this.yOffset + this.playerY);
					this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);
				} while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
			}
		} else if (this.targetY < 0) {
			while (this.targetY < 0 && this.length <= this.range) {
				this.lastX = this.targetX;
				this.lastY = this.targetY;
				this.lastZ = this.targetZ;
				do {
					this.length += this.step;
					this.hLength = (this.length * this.rotYCos);
					this.yOffset = (this.length * this.rotYSin);
					this.xOffset = (this.hLength * this.rotXCos);
					this.zOffset = (this.hLength * this.rotXSin);
					this.targetX = (int) Math.floor(this.xOffset + this.playerX);
					this.targetY = (int) Math.floor(this.yOffset + this.playerY);
					this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);
				} while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
			}
		}
	}

	/**
	 * Returns the current block along the line of vision.
	 *
	 * @return Block
	 */
	@Nullable
	public final Block getCurrentBlock() {
		if (this.length > this.range || this.targetY > MAXIMUM_WORLD_HEIGHT || this.targetY < 0) {
			return null;
		}
		return this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
	}

	/**
	 * Returns the block attached to the face at the cursor, or null if out of range.
	 *
	 * @return Block
	 */

	@Nullable
	public final Block getFaceBlock() {
		while (this.getNextBlock() != null && getCurrentBlock() != null && getCurrentBlock().getType() == Material.AIR) {
		}
		if (getCurrentBlock() == null) {
			return null;
		}
		return getLastBlock();
	}

	/**
	 * Returns the previous block along the line of vision.
	 *
	 * @return Block
	 */
	@Nullable
	public final Block getLastBlock() {
		if (this.lastY > MAXIMUM_WORLD_HEIGHT || this.lastY < 0) {
			return null;
		}
		return this.world.getBlockAt(this.lastX, this.lastY, this.lastZ);
	}

	/**
	 * Returns STEPS forward along line of vision and returns block.
	 *
	 * @return Block
	 */
	@Nullable
	public final Block getNextBlock() {
		this.lastX = this.targetX;
		this.lastY = this.targetY;
		this.lastZ = this.targetZ;
		do {
			this.length += this.step;
			this.hLength = (this.length * this.rotYCos);
			this.yOffset = (this.length * this.rotYSin);
			this.xOffset = (this.hLength * this.rotXCos);
			this.zOffset = (this.hLength * this.rotXSin);
			this.targetX = (int) Math.floor(this.xOffset + this.playerX);
			this.targetY = (int) Math.floor(this.yOffset + this.playerY);
			this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);
		} while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
		if (this.length > this.range || this.targetY > MAXIMUM_WORLD_HEIGHT || this.targetY < 0) {
			return null;
		}
		return this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
	}

	/**
	 * @return Block
	 */
	@Nullable
	public final Block getRangeBlock() {
		this.fromOffWorld();
		if (this.length > this.range) {
			return null;
		}
		return getRange();
	}

	/**
	 * Returns the block at the cursor, or null if out of range.
	 *
	 * @return Block
	 */

	@Nullable
	public final Block getTargetBlock() {
		fromOffWorld();
		while (getNextBlock() != null && getCurrentBlock() != null && getCurrentBlock().getType() == Material.AIR) {
		}
		return getCurrentBlock();
	}

	/**
	 * Sets current block type id.
	 */

	public final void setCurrentBlock(Material type) {
		if (this.getCurrentBlock() != null) {
			Block block = this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
			block.setType(type);
		}
	}

	/**
	 * Sets the type of the block attached to the face at the cursor.
	 */
	public final void setFaceBlock(Material type) {
		while (getNextBlock() != null && getCurrentBlock() != null && getCurrentBlock().getType() == Material.AIR) {
		}
		if (this.getCurrentBlock() != null) {
			Block block = this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
			block.setType(type);
		}
	}

	/**
	 * Sets previous block type id.
	 */
	public final void setLastBlock(Material type) {
		if (this.getLastBlock() != null) {
			Block block = this.world.getBlockAt(this.lastX, this.lastY, this.lastZ);
			block.setType(type);
		}
	}

	/**
	 * Sets the type of the block at the cursor.
	 */
	public final void setTargetBlock(Material type) {
		while (getNextBlock() != null && getCurrentBlock() != null && getCurrentBlock().getType() == Material.AIR) {
		}
		if (getCurrentBlock() != null) {
			Block block = this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
			block.setType(type);
		}
	}

	private Block getRange() {
		while (true) {
			this.lastX = this.targetX;
			this.lastY = this.targetY;
			this.lastZ = this.targetZ;
			do {
				this.length += this.step;
				this.hLength = (this.length * this.rotYCos);
				this.yOffset = (this.length * this.rotYSin);
				this.xOffset = (this.hLength * this.rotXCos);
				this.zOffset = (this.hLength * this.rotXSin);
				this.targetX = (int) Math.floor(this.xOffset + this.playerX);
				this.targetY = (int) Math.floor(this.yOffset + this.playerY);
				this.targetZ = (int) Math.floor(this.zOffset + this.playerZ);
			} while ((this.length <= this.range) && ((this.targetX == this.lastX) && (this.targetY == this.lastY) && (this.targetZ == this.lastZ)));
			if (!this.world.getBlockAt(this.targetX, this.targetY, this.targetZ)
				.isEmpty()) {
				return this.world.getBlockAt(this.targetX, this.targetY, this.targetZ);
			}
			if (this.length > this.range || this.targetY > MAXIMUM_WORLD_HEIGHT || this.targetY < 0) {
				return this.world.getBlockAt(this.lastX, this.lastY, this.lastZ);
			}
		}
	}

	private void init(Location location, double range, double step, double viewHeight) {
		this.playerLocation = location;
		this.viewHeight = viewHeight;
		this.playerX = this.playerLocation.getX();
		this.playerY = this.playerLocation.getY() + this.viewHeight;
		this.playerZ = this.playerLocation.getZ();
		this.range = range;
		this.step = step;
		this.length = 0;
		this.rotationX = (this.playerLocation.getYaw() + 90) % 360;
		this.rotationY = this.playerLocation.getPitch() * -1;
		this.rotYCos = Math.cos(Math.toRadians(this.rotationY));
		this.rotYSin = Math.sin(Math.toRadians(this.rotationY));
		this.rotXCos = Math.cos(Math.toRadians(this.rotationX));
		this.rotXSin = Math.sin(Math.toRadians(this.rotationX));
		this.targetX = (int) Math.floor(this.playerLocation.getX());
		this.targetY = (int) Math.floor(this.playerLocation.getY() + this.viewHeight);
		this.targetZ = (int) Math.floor(this.playerLocation.getZ());
		this.lastX = this.targetX;
		this.lastY = this.targetY;
		this.lastZ = this.targetZ;
	}
}
