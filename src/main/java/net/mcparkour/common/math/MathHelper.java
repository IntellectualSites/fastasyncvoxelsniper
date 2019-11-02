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

package net.mcparkour.common.math;

public final class MathHelper {

    private MathHelper() {
        throw new UnsupportedOperationException("Cannot create an instance of this class");
    }

    public static double circleArea(int radius) {
        return Math.PI * square(radius);
    }

    public static double circleArea(double radius) {
        return Math.PI * square(radius);
    }

    public static double sphereVolume(int radius) {
        return 4.0 / 3.0 * Math.PI * cube(radius);
    }

    public static double sphereVolume(double radius) {
        return 4.0 / 3.0 * Math.PI * cube(radius);
    }

    public static int square(int number) {
        return number * number;
    }

    public static double square(double number) {
        return number * number;
    }

    public static int cube(int number) {
        return number * number * number;
    }

    public static double cube(double number) {
        return number * number * number;
    }
}
