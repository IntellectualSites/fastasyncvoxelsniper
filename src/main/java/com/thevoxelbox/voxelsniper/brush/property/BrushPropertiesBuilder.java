package com.thevoxelbox.voxelsniper.brush.property;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Brush properties builder.
 */
public class BrushPropertiesBuilder {

    private String name;
    private String permission;
    private final List<String> aliases = new ArrayList<>(1);
    private BrushPatternType brushPatternType;
    private BrushCreator creator;

    /**
     * Sets the name.
     *
     * @param name the name
     * @return the brush properties builder
     */
    public BrushPropertiesBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the permission.
     *
     * @param permission the permission
     * @return the brush properties builder
     */
    public BrushPropertiesBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Adds an alias.
     *
     * @param alias the alias
     * @return the brush properties builder
     */
    public BrushPropertiesBuilder alias(String alias) {
        this.aliases.add(alias);
        return this;
    }

    /**
     * Sets the brush pattern type.
     *
     * @param brushPatternType the brush pattern type
     * @return the brush properties builder
     * @since 2.6.0
     */
    public BrushPropertiesBuilder brushPatternType(BrushPatternType brushPatternType) {
        this.brushPatternType = brushPatternType;
        return this;
    }

    /**
     * Sets the creator.
     *
     * @param creator the creator
     * @return the brush properties builder
     */
    public BrushPropertiesBuilder creator(BrushCreator creator) {
        this.creator = creator;
        return this;
    }

    /**
     * Build the brush properties.
     *
     * @return the brush properties
     */
    public BrushProperties build() {
        if (this.name == null) {
            throw new RuntimeException("Brush name must be specified.");
        }
        if (this.brushPatternType == null) {
            throw new RuntimeException("Brush pattern type must be specified.");
        }
        if (this.creator == null) {
            throw new RuntimeException("Brush creator must be specified.");
        }
        return new BrushProperties(this.name, this.permission, this.aliases, this.brushPatternType, this.creator);
    }

}
