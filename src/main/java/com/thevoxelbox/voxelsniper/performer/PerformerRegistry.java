package com.thevoxelbox.voxelsniper.performer;

import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PerformerRegistry {

    private final Set<PerformerProperties> uniquePerformerProperties = new HashSet<>();
    private final Map<String, PerformerProperties> performerProperties = new HashMap<>();

    public void register(PerformerProperties properties) {
        // Registers unique performers properties.
        uniquePerformerProperties.add(properties);

        // Registers all aliases.

        List<String> aliases = properties.getAliases();
        for (String alias : aliases) {
            this.performerProperties.put(alias, properties);
        }
    }

    /**
     * Return the unique performer properties.
     *
     * @return the unique performer properties
     * @since TODO
     */
    public Set<PerformerProperties> getUniquePerformerProperties() {
        return uniquePerformerProperties;
    }

    @Nullable
    public PerformerProperties getPerformerProperties(String alias) {
        return this.performerProperties.get(alias);
    }

    public Map<String, PerformerProperties> getPerformerProperties() {
        return Collections.unmodifiableMap(this.performerProperties);
    }

}
