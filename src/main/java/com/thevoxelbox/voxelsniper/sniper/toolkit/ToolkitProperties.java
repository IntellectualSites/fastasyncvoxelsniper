package com.thevoxelbox.voxelsniper.sniper.toolkit;

import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ToolkitProperties {

    private static final VoxelSniperPlugin plugin = VoxelSniperPlugin.plugin;

    private final Set<BlockState> voxelList = new HashSet<>();
    private BrushPattern pattern;
    private BrushPattern replacePattern;
    private int brushSize;
    private int voxelHeight;
    private int cylinderCenter;
    @Nullable
    private Integer blockTracerRange;
    private boolean lightningEnabled;

    public ToolkitProperties() {
        VoxelSniperConfig config = plugin.getVoxelSniperConfig();

        this.pattern = new BrushPattern(config.getDefaultBlockMaterial().getDefaultState());
        this.replacePattern = new BrushPattern(config.getDefaultReplaceBlockMaterial().getDefaultState());
        this.brushSize = config.getDefaultBrushSize();
        this.voxelHeight = config.getDefaultVoxelHeight();
        this.cylinderCenter = config.getDefaultCylinderCenter();
    }

    public void reset() {
        VoxelSniperConfig config = plugin.getVoxelSniperConfig();

        resetPattern();
        resetReplacePattern();
        this.brushSize = config.getDefaultBrushSize();
        this.voxelHeight = config.getDefaultVoxelHeight();
        this.cylinderCenter = config.getDefaultCylinderCenter();
        this.blockTracerRange = null;
        this.lightningEnabled = false;
        this.voxelList.clear();
    }

    /**
     * Resets the pattern.
     *
     * @since 2.6.0
     */
    public void resetPattern() {
        this.pattern = new BrushPattern(plugin.getVoxelSniperConfig().getDefaultBlockMaterial().getDefaultState());
    }

    /**
     * Resets the replace pattern.
     *
     * @since 2.6.0
     */
    public void resetReplacePattern() {
        this.replacePattern = new BrushPattern(plugin.getVoxelSniperConfig().getDefaultReplaceBlockMaterial().getDefaultState());
    }

    public BlockTracer createBlockTracer(Player player) {
        int distance = this.blockTracerRange == null
                ? Math.max(Bukkit.getViewDistance(), 3) * 16 - this.brushSize
                : this.blockTracerRange;
        return new BlockTracer(player, distance);
    }

    public void addToVoxelList(BlockState blockData) {
        this.voxelList.add(blockData);
    }

    public void removeFromVoxelList(BlockState blockData) {
        this.voxelList.remove(blockData);
    }

    public void clearVoxelList() {
        this.voxelList.clear();
    }

    public boolean isVoxelListContains(BlockState blockData) {
        return this.voxelList.contains(blockData);
    }

    /**
     * Returns the pattern.
     *
     * @return the pattern
     * @since 2.6.0
     */
    public BrushPattern getPattern() {
        return this.pattern;
    }

    /**
     * Sets the pattern.
     *
     * @param brushPattern the new pattern
     * @since 2.6.0
     */
    public void setPattern(BrushPattern brushPattern) {
        this.pattern = brushPattern;
    }

    /**
     * Returns the replace pattern.
     *
     * @return the replace pattern
     * @since 2.6.0
     */
    public BrushPattern getReplacePattern() {
        return this.replacePattern;
    }

    /**
     * Sets the replace pattern.
     *
     * @param replaceBrushPattern the new replace pattern
     * @since 2.6.0
     */
    public void setReplacePattern(BrushPattern replaceBrushPattern) {
        this.replacePattern = replaceBrushPattern;
    }

    public int getBrushSize() {
        return this.brushSize;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public int getVoxelHeight() {
        return this.voxelHeight;
    }

    public void setVoxelHeight(int voxelHeight) {
        this.voxelHeight = voxelHeight;
    }

    public int getCylinderCenter() {
        return this.cylinderCenter;
    }

    public void setCylinderCenter(int cylinderCenter) {
        this.cylinderCenter = cylinderCenter;
    }

    @Nullable
    public Integer getBlockTracerRange() {
        return this.blockTracerRange;
    }

    public void setBlockTracerRange(@Nullable Integer blockTracerRange) {
        this.blockTracerRange = blockTracerRange;
    }

    public boolean isLightningEnabled() {
        return this.lightningEnabled;
    }

    public void setLightningEnabled(boolean lightningEnabled) {
        this.lightningEnabled = lightningEnabled;
    }

    /**
     * Return the voxel list.
     *
     * @return the voxel list
     * @since 3.0.0
     */
    public Collection<BlockState> getVoxelList() {
        return Collections.unmodifiableCollection(this.voxelList);
    }

}
