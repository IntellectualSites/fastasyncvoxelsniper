package com.thevoxelbox.voxelsniper.sniper.toolkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class Toolkit {

	private String toolkitName;
	@Nullable
	private Class<? extends Brush> currentBrushType;
	@Nullable
	private Class<? extends Brush> previousBrushType;
	private Map<Material, ToolAction> toolActions = new EnumMap<>(Material.class);
	private Map<Class<? extends Brush>, Brush> brushes = new HashMap<>();
	private ToolkitProperties properties = new ToolkitProperties();

	public Toolkit(String toolkitName) {
		this.toolkitName = toolkitName;
	}

	public void reset() {
		this.currentBrushType = null;
		this.previousBrushType = null;
		this.brushes.clear();
	}

	public void addAction(Material itemType, ToolAction action) {
		this.toolActions.put(itemType, action);
	}

	public boolean hasAction(Material itemType) {
		return this.toolActions.containsKey(itemType);
	}

	@Nullable
	public ToolAction getAction(Material itemType) {
		return this.toolActions.get(itemType);
	}

	public void removeAction(Material itemType) {
		this.toolActions.remove(itemType);
	}

	public Brush useBrushType(Class<? extends Brush> brushType) {
		Brush brush = this.brushes.get(brushType);
		if (brush == null) {
			brush = createBrush(brushType);
			this.brushes.put(brushType, brush);
		}
		this.currentBrushType = brushType;
		this.previousBrushType = this.currentBrushType;
		return brush;
	}

	public Brush createBrush(Class<? extends Brush> brushType) {
		try {
			Constructor<? extends Brush> constructor = brushType.getConstructor();
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Nullable
	public Brush getCurrentBrush() {
		if (this.currentBrushType == null) {
			return null;
		}
		return getBrush(this.currentBrushType);
	}

	@Nullable
	public Brush getPreviousBrush() {
		if (this.previousBrushType == null) {
			return null;
		}
		return getBrush(this.previousBrushType);
	}

	@Nullable
	public Brush getBrush(Class<? extends Brush> brushType) {
		return this.brushes.get(brushType);
	}

	public String getToolkitName() {
		return this.toolkitName;
	}

	@Nullable
	public Class<? extends Brush> getCurrentBrushType() {
		return this.currentBrushType;
	}

	@Nullable
	public Class<? extends Brush> getPreviousBrushType() {
		return this.previousBrushType;
	}

	public Map<Material, ToolAction> getToolActions() {
		return Map.copyOf(this.toolActions);
	}

	public Map<Class<? extends Brush>, Brush> getBrushes() {
		return Map.copyOf(this.brushes);
	}

	public ToolkitProperties getProperties() {
		return this.properties;
	}
}
