package com.thevoxelbox.voxelsniper.util;

import java.lang.reflect.Field;

public class ReflectionsUtils {

    public static Field getField(Class<?> clazz, String name) {
        do {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        } while (clazz.getSuperclass() != Object.class && ((clazz = clazz.getSuperclass()) != null));
        return null;
    }

}
