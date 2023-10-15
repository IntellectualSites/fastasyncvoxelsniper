package com.thevoxelbox.voxelsniper.command.argument.annotation;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify min and max field values of numerical.
 *
 * @since 3.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DynamicRange {

    /**
     * Return the minimum field value accepted by the parser.
     *
     * @return minimum field value
     * @since 3.0.0
     */
    @NonNull String min() default "";

    /**
     * Return the maximum field value accepted by the parser.
     *
     * @return maximum field value
     * @since 3.0.0
     */
    @NonNull String max() default "";

}
