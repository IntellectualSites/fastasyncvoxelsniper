package com.thevoxelbox.voxelsniper.sniper.toolkit;

import com.sk89q.worldedit.world.item.ItemType;
import com.thevoxelbox.voxelsniper.BrushRegistrar;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.property.BrushCreator;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Toolkit {

    private static final BrushProperties DEFAULT_BRUSH_PROPERTIES = BrushRegistrar.DEFAULT_BRUSH_PROPERTIES;

    private final String toolkitName;
    private final Map<ItemType, ToolAction> toolActions = new HashMap<>();
    private final Map<BrushProperties, Brush> brushes = new HashMap<>();
    private final ToolkitProperties properties = new ToolkitProperties();
    private BrushProperties currentBrushProperties;
    private BrushProperties previousBrushProperties;

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

    public void addToolAction(ItemType toolMaterial, ToolAction action) {
        this.toolActions.put(toolMaterial, action);
    }

    public boolean hasToolAction(ItemType toolMaterial) {
        return this.toolActions.containsKey(toolMaterial);
    }

    @Nullable
    public ToolAction getToolAction(ItemType toolMaterial) {
        return this.toolActions.get(toolMaterial);
    }

    public void removeToolAction(ItemType toolMaterial) {
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

    public Map<ItemType, ToolAction> getToolActions() {
        return Collections.unmodifiableMap(this.toolActions);
    }

    public ToolkitProperties getProperties() {
        return this.properties;
    }

}
