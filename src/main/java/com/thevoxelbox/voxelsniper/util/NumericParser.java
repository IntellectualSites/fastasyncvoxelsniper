package com.thevoxelbox.voxelsniper.util;

import org.jetbrains.annotations.Nullable;

public final class NumericParser {

	private NumericParser() {
		throw new UnsupportedOperationException("Cannot create instance of utility class");
	}

	@Nullable
	public static Integer parseInteger(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	@Nullable
	public static Double parseDouble(String string) {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}
