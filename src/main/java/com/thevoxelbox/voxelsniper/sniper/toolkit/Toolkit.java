package com.thevoxelbox.voxelsniper.sniper.toolkit;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.property.BrushCreator;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class Toolkit {

	private String toolkitName;
	@Nullable
	private BrushProperties currentBrushProperties;
	@Nullable
	private BrushProperties previousBrushProperties;
	private Map<Material, ToolAction> toolActions = new EnumMap<>(Material.class);
	private Map<BrushProperties, Brush> brushes = new HashMap<>();
	private ToolkitProperties properties = new ToolkitProperties();

	public Toolkit(String toolkitName) {
		this.toolkitName = toolkitName;
	}

	public void reset() {
		this.currentBrushProperties = null;
		this.previousBrushProperties = null;
		this.brushes.clear();
		this.properties.reset();
	}

	public void addToolAction(Material toolMaterial, ToolAction action) {
		this.toolActions.put(toolMaterial, action);
	}

	public boolean hasToolAction(Material toolMaterial) {
		return this.toolActions.containsKey(toolMaterial);
	}

	@Nullable
	public ToolAction getToolAction(Material toolMaterial) {
		return this.toolActions.get(toolMaterial);
	}

	public void removeToolAction(Material toolMaterial) {
		this.toolActions.remove(toolMaterial);
	}

	public Brush useBrush(BrushProperties properties) {
		Brush brush = this.brushes.get(properties);
		if (brush == null) {
			BrushCreator creator = properties.getCreator();
			brush = creator.create();
			this.brushes.put(properties, brush);
		}
		this.previousBrushProperties = this.currentBrushProperties;
		this.currentBrushProperties = properties;
		return brush;
	}

	@Nullable
	public Brush getCurrentBrush() {
		if (this.currentBrushProperties == null) {
			return null;
		}
		return getBrush(this.currentBrushProperties);
	}

	@Nullable
	public Brush getBrush(BrushProperties properties) {
		return this.brushes.get(properties);
	}

	public String getToolkitName() {
		return this.toolkitName;
	}

	@Nullable
	public BrushProperties getCurrentBrushProperties() {
		return this.currentBrushProperties;
	}

	@Nullable
	public BrushProperties getPreviousBrushProperties() {
		return this.previousBrushProperties;
	}

	public Map<Material, ToolAction> getToolActions() {
		return Map.copyOf(this.toolActions);
	}

	public ToolkitProperties getProperties() {
		return this.properties;
	}
}
