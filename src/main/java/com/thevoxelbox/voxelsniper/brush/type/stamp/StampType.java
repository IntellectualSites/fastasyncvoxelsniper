package com.thevoxelbox.voxelsniper.brush.type.stamp;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public enum StampType {

    NO_AIR("no-air"),
    FILL("fill"),
    DEFAULT("default");

    private final String name;

    StampType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public TranslatableComponent getFullName() {
        return Caption.of("voxelsniper.brush.stamp.type." + this.name);
    }
}
