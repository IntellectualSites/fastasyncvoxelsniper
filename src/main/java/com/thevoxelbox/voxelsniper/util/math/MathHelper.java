package com.thevoxelbox.voxelsniper.util.math;

import org.apache.commons.lang3.ClassUtils;

public final class MathHelper {

    private MathHelper() {
        throw new UnsupportedOperationException("Cannot create an instance of this class");
    }

    public static double circleArea(int radius) {
        return Math.PI * square(radius);
    }

    public static double sphereVolume(int radius) {
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

    /**
     * Return the min number for a given type.
     *
     * @param clazz the class
     * @return the min number or NaN
     * @since 3.0.0
     */
    public static Number minNumberType(Class<?> clazz) {
        clazz = ClassUtils.primitiveToWrapper(clazz);

        if (clazz == Byte.class) {
            return Byte.MIN_VALUE;
        } else if (clazz == Short.class) {
            return Short.MIN_VALUE;
        } else if (clazz == Integer.class) {
            return Integer.MIN_VALUE;
        } else if (clazz == Long.class) {
            return Long.MIN_VALUE;
        } else if (clazz == Float.class) {
            return Float.MIN_VALUE;
        } else if (clazz == Double.class) {
            return Double.MIN_VALUE;
        }
        return Double.NaN;
    }

    /**
     * Return the max number for a given type.
     *
     * @param clazz the class
     * @return the max number or NaN
     * @since 3.0.0
     */
    public static Number maxNumberType(Class<?> clazz) {
        clazz = ClassUtils.primitiveToWrapper(clazz);

        if (clazz == Byte.class) {
            return Byte.MAX_VALUE;
        } else if (clazz == Short.class) {
            return Short.MAX_VALUE;
        } else if (clazz == Integer.class) {
            return Integer.MAX_VALUE;
        } else if (clazz == Long.class) {
            return Long.MAX_VALUE;
        } else if (clazz == Float.class) {
            return Float.MAX_VALUE;
        } else if (clazz == Double.class) {
            return Double.MAX_VALUE;
        }
        return Double.NaN;
    }

}
