package com.thevoxelbox.voxelsniper.util.math;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Vector3i {

	private int x;
	private int y;
	private int z;

	public Vector3i(Block block) {
		this(block.getX(), block.getY(), block.getZ());
	}

	public Vector3i(Location location) {
		this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public Vector3i(Vector vector) {
		this(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	public Vector3i(Vector3i vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i addX(int x) {
		return add(x, 0, 0);
	}

	public Vector3i addY(int y) {
		return add(0, y, 0);
	}

	public Vector3i addZ(int z) {
		return add(0, 0, z);
	}

	public Vector3i add(Vector3i vector) {
		return add(vector.x, vector.y, vector.z);
	}

	public Vector3i add(int x, int y, int z) {
		return new Vector3i(this.x + x, this.y + y, this.z + z);
	}

	public Vector3i multiplyX(int x) {
		return multiply(x, 0, 0);
	}

	public Vector3i multiplyY(int y) {
		return multiply(0, y, 0);
	}

	public Vector3i multiplyZ(int z) {
		return multiply(0, 0, z);
	}

	public Vector3i multiply(Vector3i vector) {
		return multiply(vector.x, vector.y, vector.z);
	}

	public Vector3i multiply(int x, int y, int z) {
		return new Vector3i(this.x * x, this.y * y, this.z * z);
	}

	public Vector toBukkit() {
		return new Vector(this.x, this.y, this.z);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}
}
