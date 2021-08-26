package com.thevoxelbox.voxelsniper.config;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Configuration storage defining global configurations for VoxelSniper.
 */
public class VoxelSniperConfigLoader {

    private static final String MESSAGE_ON_LOGIN_ENABLED = "message-on-login-enabled";
    private static final String DEFAULT_BLOCK_MATERIAL = "default-block-material";
    private static final String DEFAULT_REPLACE_BLOCK_MATERIAL = "default-replace-block-material";
    private static final String DEFAULT_BRUSH_SIZE = "default-brush-size";
    private static final String LITESNIPER_MAX_BRUSH_SIZE = "litesniper-max-brush-size";
    private static final String LITESNIPER_RESTRICTED_MATERIALS = "litesniper-restricted-materials";
    private static final String BRUSH_SIZE_WARNING_THRESHOLD = "brush-size-warning-threshold";
    private static final String DEFAULT_VOXEL_HEIGHT = "default-voxel-height";
    private static final String DEFAULT_CYLINDER_CENTER = "default-cylinder-center";

    private static final boolean DEFAULT_MESSAGE_ON_LOGIN_ENABLED = false;
    private static final BlockType DEFAULT_BLOCK_MATERIAL_VALUE = BlockTypes.AIR;
    private static final BlockType DEFAULT_REPLACE_BLOCK_MATERIAL_VALUE = BlockTypes.AIR;
    private static final int DEFAULT_BRUSH_SIZE_VALUE = 3;
    private static final int DEFAULT_LITESNIPER_MAX_BRUSH_SIZE = 5;
    private static final int DEFAULT_BRUSH_SIZE_WARNING_THRESHOLD = 20;
    private static final int DEFAULT_VOXEL_HEIGHT_VALUE = 1;
    private static final int DEFAULT_CYLINDER_CENTER_VALUE = 0;

    private final FileConfiguration config;

    /**
     * @param config Configuration that is going to be used.
     */
    public VoxelSniperConfigLoader(FileConfiguration config) {
        this.config = config;
    }

    /**
     * Return if the login message is enabled.
     *
     * @return true if message on login is enabled, false otherwise.
     */
    public boolean isMessageOnLoginEnabled() {
        return this.config.getBoolean(MESSAGE_ON_LOGIN_ENABLED, DEFAULT_MESSAGE_ON_LOGIN_ENABLED);
    }

    /**
     * Set the message on login to be enabled or disabled.
     *
     * @param enabled Messages on Login enabled
     */
    public void setMessageOnLoginEnabled(boolean enabled) {
        this.config.set(MESSAGE_ON_LOGIN_ENABLED, enabled);
    }

    /**
     * Return default block material.
     *
     * @return default type
     */
    public BlockType getDefaultBlockMaterial() {
        return BlockTypes.get(this.config.getString(DEFAULT_BLOCK_MATERIAL, DEFAULT_BLOCK_MATERIAL_VALUE.getId()));
    }

    /**
     * Set default block material
     *
     * @param blockType default type
     */
    public void setDefaultBlockMaterial(BlockType blockType) {
        this.config.set(DEFAULT_BLOCK_MATERIAL, blockType.getId());
    }

    /**
     * Return default replace block material.
     *
     * @return default type
     */
    public BlockType getDefaultReplaceBlockMaterial() {
        return BlockTypes.get(this.config.getString(
                DEFAULT_REPLACE_BLOCK_MATERIAL,
                DEFAULT_REPLACE_BLOCK_MATERIAL_VALUE.getId()
        ));
    }

    /**
     * Set default replace block material
     *
     * @param blockType default type
     */
    public void setDefaultReplaceBlockMaterial(BlockType blockType) {
        this.config.set(DEFAULT_REPLACE_BLOCK_MATERIAL, blockType.getId());
    }

    /**
     * Return default brush size.
     *
     * @return default size
     */
    public int getDefaultBrushSize() {
        return this.config.getInt(DEFAULT_BRUSH_SIZE, DEFAULT_BRUSH_SIZE_VALUE);
    }

    /**
     * Set default brush size.
     *
     * @param size default size
     */
    public void setDefaultBrushSize(int size) {
        this.config.set(DEFAULT_BRUSH_SIZE, size);
    }

    /**
     * Return maximum size of brushes that LiteSnipers can use.
     *
     * @return maximum size
     */
    public int getLitesniperMaxBrushSize() {
        return this.config.getInt(LITESNIPER_MAX_BRUSH_SIZE, DEFAULT_LITESNIPER_MAX_BRUSH_SIZE);
    }

    /**
     * Set maximum size of brushes that LiteSnipers can use.
     *
     * @param size maximum size
     */
    public void setLitesniperMaxBrushSize(int size) {
        this.config.set(LITESNIPER_MAX_BRUSH_SIZE, size);
    }

    /**
     * Return List of restricted Litesniper Materials.
     *
     * @return List of restricted Litesniper Materials
     */
    public List<BlockType> getLitesniperRestrictedMaterials() {
        return this.config.getStringList(LITESNIPER_RESTRICTED_MATERIALS).stream()
                .map(key -> key.toLowerCase(Locale.ROOT))
                .map(BlockTypes::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Set new list of restricted Litesniper Materials.
     *
     * @param restrictedMaterials List of restricted Litesniper Materials
     */
    public void setLitesniperRestrictedMaterials(List<BlockType> restrictedMaterials) {
        this.config.set(LITESNIPER_RESTRICTED_MATERIALS, restrictedMaterials.stream()
                .map(BlockType::getId));
    }

    /**
     * Return maximum reasonable brush size before sending a warning.
     *
     * @return maximum size
     */
    public int getBrushSizeWarningThreshold() {
        return this.config.getInt(BRUSH_SIZE_WARNING_THRESHOLD, DEFAULT_BRUSH_SIZE_WARNING_THRESHOLD);
    }

    /**
     * Set maximum reasonable brush size before sending a warning.
     *
     * @param size maximum size
     */
    public void setBrushSizeWarningThreshold(int size) {
        this.config.set(BRUSH_SIZE_WARNING_THRESHOLD, size);
    }

    /**
     * Return default voxel height.
     *
     * @return default height
     */
    public int getDefaultVoxelHeight() {
        return this.config.getInt(DEFAULT_VOXEL_HEIGHT, DEFAULT_VOXEL_HEIGHT_VALUE);
    }

    /**
     * Set default voxel height.
     *
     * @param height default height
     */
    public void setDefaultVoxelHeight(int height) {
        this.config.set(DEFAULT_VOXEL_HEIGHT, height);
    }

    /**
     * Return default cylinder center.
     *
     * @return default center
     */
    public int getDefaultCylinderCenter() {
        return this.config.getInt(DEFAULT_CYLINDER_CENTER, DEFAULT_CYLINDER_CENTER_VALUE);
    }

    /**
     * Set default cylinder center.
     *
     * @param center default center
     */
    public void setDefaultCylinderCenter(int center) {
        this.config.set(DEFAULT_CYLINDER_CENTER, center);
    }

}
