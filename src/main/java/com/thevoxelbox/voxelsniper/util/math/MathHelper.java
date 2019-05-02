package com.thevoxelbox.voxelsniper.util.math;

public final class MathHelper {

	private MathHelper() {
		throw new UnsupportedOperationException("Cannot create instance of utility class");
	}

	public static int square(int number) {
		return number * number;
	}

	public static int cube(int number) {
		return number * number * number;
	}

	public static double square(double number) {
		return number * number;
	}

	public static double cube(double number) {
		return number * number * number;
	}
}
