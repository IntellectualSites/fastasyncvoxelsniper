package com.thevoxelbox.voxelsniper.sniper.toolkit;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.thevoxelbox.voxelsniper.BrushRegistrar;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.property.BrushCreator;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class Toolkit {

	private static final BrushProperties DEFAULT_BRUSH_PROPERTIES = BrushRegistrar.DEFAULT_BRUSH_PROPERTIES;

	private String toolkitName;
	private BrushProperties currentBrushProperties;
	private BrushProperties previousBrushProperties;
	private Map<Material, ToolAction> toolActions = new EnumMap<>(Material.class);
	private Map<BrushProperties, Brush> brushes = new HashMap<>();
	private ToolkitProperties properties = new ToolkitProperties();

	public Toolkit(String toolkitName) {
		this.toolkitName = toolkitName;
		this.currentBrushProperties = DEFAULT_BRUSH_PROPERTIES;
		this.previousBrushProperties = DEFAULT_BRUSH_PROPERTIES;
		createBrush(DEFAULT_BRUSH_PROPERTIES);
	}

	public void reset() {
		this.currentBrushProperties = DEFAULT_BRUSH_PROPERTIES;
		this.previousBrushProperties = DEFAULT_BRUSH_PROPERTIES;
		this.brushes.clear();
		this.properties.reset();
		createBrush(DEFAULT_BRUSH_PROPERTIES);
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
		Brush brush = getBrush(properties);
		if (brush == null) {
			brush = createBrush(properties);
		}
		this.previousBrushProperties = this.currentBrushProperties;
		this.currentBrushProperties = properties;
		return brush;
	}

	private Brush createBrush(BrushProperties properties) {
		BrushCreator creator = properties.getCreator();
		Brush brush = creator.create();
		this.brushes.put(properties, brush);
		return brush;
	}

	@Nullable
	public Brush getCurrentBrush() {
		return getBrush(this.currentBrushProperties);
	}

	@Nullable
	public Brush getBrush(BrushProperties properties) {
		return this.brushes.get(properties);
	}

	public String getToolkitName() {
		return this.toolkitName;
	}

	public BrushProperties getCurrentBrushProperties() {
		return this.currentBrushProperties;
	}

	public BrushProperties getPreviousBrushProperties() {
		return this.previousBrushProperties;
	}

	public Map<Material, ToolAction> getToolActions() {
		return Maps.newHashMap(this.toolActions);
	}

	public ToolkitProperties getProperties() {
		return this.properties;
	}
}
