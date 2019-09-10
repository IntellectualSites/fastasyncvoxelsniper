package com.thevoxelbox.voxelsniper.util.math.vector;

import java.util.Objects;

public class Vector3i {

	private int x;
	private int y;
	private int z;

	public Vector3i() {
		this(0, 0, 0);
	}

	public Vector3i(Vector3i vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i plusX(int x) {
		return plus(x, 0, 0);
	}

	public Vector3i plusY(int y) {
		return plus(0, y, 0);
	}

	public Vector3i plusZ(int z) {
		return plus(0, 0, z);
	}

	public Vector3i plus(Vector3i vector) {
		return plus(vector.x, vector.y, vector.z);
	}

	public Vector3i plus(int x, int y, int z) {
		return new Vector3i(this.x + x, this.y + y, this.z + z);
	}

	public Vector3i timesX(int x) {
		return times(x, 1, 1);
	}

	public Vector3i timesY(int y) {
		return times(1, y, 1);
	}

	public Vector3i timesZ(int z) {
		return times(1, 1, z);
	}

	public Vector3i times(Vector3i vector) {
		return times(vector.x, vector.y, vector.z);
	}

	public Vector3i times(int x, int y, int z) {
		return new Vector3i(this.x * x, this.y * y, this.z * z);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		Vector3i vector3i = (Vector3i) object;
		return this.x == vector3i.x &&
			this.y == vector3i.y &&
			this.z == vector3i.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y, this.z);
	}

	@Override
	public String toString() {
		return "Vector3i{" +
			"x=" + this.x +
			", y=" + this.y +
			", z=" + this.z +
			"}";
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
