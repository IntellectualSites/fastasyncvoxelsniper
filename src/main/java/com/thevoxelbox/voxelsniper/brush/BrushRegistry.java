package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BrushRegistry {

    private final Set<BrushProperties> uniqueBrushesProperties = new HashSet<>();
    private final Map<String, BrushProperties> brushesProperties = new HashMap<>();

    public void register(BrushProperties properties) {
        // Registers unique brush properties.
        uniqueBrushesProperties.add(properties);

        // Registers all aliases.
        List<String> aliases = properties.getAliases();
        for (String alias : aliases) {
            this.brushesProperties.put(alias, properties);
        }
    }

    /**
     * Return the unique brushes properties.
     *
     * @return the unique brushes properties
     * @since 3.0.0
     */
    public Set<BrushProperties> getUniqueBrushesProperties() {
        return uniqueBrushesProperties;
    }

    @Nullable
    public BrushProperties getBrushProperties(String alias) {
        return this.brushesProperties.get(alias);
    }

    public Map<String, BrushProperties> getBrushesProperties() {
        return Collections.unmodifiableMap(this.brushesProperties);
    }

}
