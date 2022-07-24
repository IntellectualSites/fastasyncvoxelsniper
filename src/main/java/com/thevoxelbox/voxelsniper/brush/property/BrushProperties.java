package com.thevoxelbox.voxelsniper.brush.property;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BrushProperties {

    private final String name;
    @Nullable
    private final String permission;
    private final List<String> aliases;
    private final BrushPatternType brushPatternType;
    private final BrushCreator creator;

    /**
     * Create a new Brush properties.
     *
     * @param name             the name
     * @param permission       the permission
     * @param aliases          the aliases
     * @param brushPatternType the brush pattern type
     * @param creator          the creator
     */
    BrushProperties(
            String name, @Nullable String permission, List<String> aliases, BrushPatternType brushPatternType,
            BrushCreator creator
    ) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
        this.brushPatternType = brushPatternType;
        this.creator = creator;
    }

    /**
     * Create a builder for brush properties.
     *
     * @return the brush properties builder
     */
    public static BrushPropertiesBuilder builder() {
        return new BrushPropertiesBuilder();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the permission.
     *
     * @return the permission
     */
    @Nullable
    public String getPermission() {
        return this.permission;
    }

    /**
     * Gets the aliases.
     *
     * @return the aliases
     */
    public List<String> getAliases() {
        return this.aliases;
    }

    /**
     * Gets the accepted brush pattern type for this brush.
     *
     * @return the brush pattern type
     * @since 2.6.0
     */
    public BrushPatternType getBrushPatternType() {
        return brushPatternType;
    }


    /**
     * Gets the brush creator.
     *
     * @return the creator
     */
    public BrushCreator getCreator() {
        return this.creator;
    }

}
