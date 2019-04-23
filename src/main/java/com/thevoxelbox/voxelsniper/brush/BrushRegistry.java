package com.thevoxelbox.voxelsniper.brush;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;

public class BrushRegistry {

	private Map<String, BrushProperties> brushesProperties = new HashMap<>();

	public void register(BrushProperties properties) {
		List<String> aliases = properties.getAliases();
		for (String alias : aliases) {
			this.brushesProperties.put(alias, properties);
		}
	}

	public BrushProperties getBrushProperties(String alias) {
		return this.brushesProperties.get(alias);
	}

	public Map<String, BrushProperties> getBrushesProperties() {
		return Map.copyOf(this.brushesProperties);
	}
}
