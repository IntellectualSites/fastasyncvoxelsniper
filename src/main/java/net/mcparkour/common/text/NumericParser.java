/*
 * MIT License
 *
 * Copyright (c) 2019 MCParkour
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.mcparkour.common.text;

import org.jetbrains.annotations.Nullable;

public final class NumericParser {

	private NumericParser() {
		throw new UnsupportedOperationException("Cannot create an instance of this class");
	}

	@Nullable
	public static Byte parseByte(String byteString) {
		try {
			return Byte.parseByte(byteString);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	@Nullable
	public static Short parseShort(String shortString) {
		try {
			return Short.parseShort(shortString);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	@Nullable
	public static Integer parseInteger(String integerString) {
		try {
			return Integer.parseInt(integerString);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	@Nullable
	public static Long parseLong(String longString) {
		try {
			return Long.parseLong(longString);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	@Nullable
	public static Float parseFloat(String floatString) {
		try {
			return Float.parseFloat(floatString);
		} catch (NumberFormatException exception) {
			return null;
		}
	}

	@Nullable
	public static Double parseDouble(String doubleString) {
		try {
			return Double.parseDouble(doubleString);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}
