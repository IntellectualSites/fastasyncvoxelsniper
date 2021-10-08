package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractBrush implements Brush {

    protected static final VoxelSniperPlugin PLUGIN = VoxelSniperPlugin.plugin;
    protected static final VoxelSniperConfig CONFIG = PLUGIN.getVoxelSniperConfig();
    protected static final File PLUGIN_DATA_FOLDER = PLUGIN.getDataFolder();
    protected static final int CHUNK_SIZE = 16;
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".##");
    private static final Logger LOGGER = LogManager.getLogger("FastAsyncVoxelSniper/" + AbstractBrush.class.getSimpleName());
    private BrushProperties properties;

    private EditSession editSession;
    private BlockVector3 targetBlock;
    private BlockVector3 lastBlock;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        player.sendMessage(ChatColor.RED + "This brush does not accept additional parameters.");
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return sortCompletions(Stream.empty(), parameter, 0);
        }
        return Collections.emptyList();
    }

    /**
     * Sort and return all possible completions that match given parameter.
     *
     * @param completions Completions
     * @param parameter   Given parameter
     * @param index       Parameter index
     * @return Sorted completions.
     */
    public List<String> sortCompletions(Stream<String> completions, String parameter, int index) {
        // The first brush parameter may be info.
        // Removing MINECRAFT_IDENTIFIER permits completing whether minecraft:XXXX or XXXX.
        String parameterLowered = (parameter.startsWith(Identifiers.MINECRAFT_IDENTIFIER)
                ? parameter.substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)
                : parameter)
                .toLowerCase(Locale.ROOT);
        return (index == 0 ? Stream.concat(completions, Stream.of("info")) : completions)
                .filter(completion -> completion.toLowerCase(Locale.ROOT).startsWith(parameterLowered))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public void perform(
            Snipe snipe,
            ToolAction action,
            EditSession editSession,
            BlockVector3 targetBlock,
            BlockVector3 lastBlock
    ) {
        this.editSession = editSession;
        this.targetBlock = targetBlock;
        this.lastBlock = lastBlock;
        if (action == ToolAction.ARROW) {
            handleArrowAction(snipe);
        } else if (action == ToolAction.GUNPOWDER) {
            handleGunpowderAction(snipe);
        }
    }

    public int clampY(int y) {
        int clampedY = y;
        int minHeight = editSession.getMinY();
        if (clampedY <= minHeight) {
            clampedY = minHeight;
        } else {
            int maxHeight = editSession.getMaxY();
            if (clampedY > maxHeight) {
                clampedY = maxHeight;
            }
        }
        return clampedY;
    }

    public BlockState clampY(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return clampY(x, y, z);
    }

    public BlockState clampY(int x, int y, int z) {
        return getBlock(x, clampY(y), z);
    }

    public void setBiome(int x, int y, int z, BiomeType biomeType) {
        try {
            editSession.setBiome(x, y, z, biomeType);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHighestTerrainBlock(int x, int z, int minY, int maxY) {
        try {
            return editSession.getHighestTerrainBlock(x, z, minY, maxY);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean regenerateChunk(int chunkX, int chunkZ, BiomeType biomeType) {
        try {
            World world = BukkitAdapter.adapt(editSession.getWorld());
            int minX = chunkX << 4;
            int minZ = chunkZ << 4;
            RegenOptions regenOptions = RegenOptions.builder()
                    .seed(world.getSeed())
                    .regenBiomes(true)
                    .biomeType(biomeType)
                    .build();
            return editSession.getWorld().regenerate(
                    new CuboidRegion(
                            editSession.getWorld(),
                            BlockVector3.at(minX, editSession.getMinY(), minZ),
                            BlockVector3.at(minX + 15, editSession.getMaxY(), minZ + 15)
                    ),
                    editSession,
                    regenOptions
            );
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshChunk(int chunkX, int chunkZ) {
        try {
            editSession.getWorld().refreshChunk(chunkX, chunkZ);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean generateTree(BlockVector3 location, TreeGenerator.TreeType treeType) {
        try {
            return treeType.generate(editSession, location);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public Entity createEntity(BlockVector3 location, org.bukkit.entity.Entity bukkitEntity) {
        return editSession.createEntity(
                new Location(editSession, location.getX(), location.getY(), location.getZ()),
                BukkitAdapter.adapt(bukkitEntity).getState()
        );
    }

    public Direction getDirection(BlockVector3 first, BlockVector3 second) {
        for (Direction direction : Direction.values()) {
            if (first.getX() + direction.getX() == second.getX()
                    && first.getY() + direction.getY() == second.getY()
                    && first.getZ() + direction.getZ() == second.getZ()) {
                return direction;
            }
        }
        return null;
    }

    public BlockVector3 getRelativeBlock(BlockVector3 origin, Direction direction) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        return getRelativeBlock(x, y, z, direction);
    }

    public BlockVector3 getRelativeBlock(int x, int y, int z, Direction direction) {
        return direction.toBlockVector().add(x, y, z);
    }

    public BlockType getBlockType(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return getBlockType(x, y, z);
    }

    public BlockType getBlockType(int x, int y, int z) {
        BlockState block = getBlock(x, y, z);
        return block.getBlockType();
    }

    public void setBlockType(BlockVector3 position, BlockType type) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        setBlockType(x, y, z, type);
    }

    public void setBlockType(int x, int y, int z, BlockType type) {
        setBlockData(x, y, z, type.getDefaultState());
    }

    public void setBlockData(BlockVector3 position, BlockState blockState) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        setBlockData(x, y, z, blockState);
    }

    public void setBlockData(int x, int y, int z, BlockState blockState) {
        editSession.setBlock(x, y, z, blockState);
        if (blockState.getMaterial().isTile()) {
            try {
                editSession.setTile(x, y, z, blockState.getMaterial().getDefaultTile());
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public BaseBlock getFullBlock(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return getFullBlock(x, y, z);
    }

    public BaseBlock getFullBlock(int x, int y, int z) {
        return editSession.getFullBlock(x, y, z);
    }

    public BlockState getBlock(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return getBlock(x, y, z);
    }

    public BlockState getBlock(int x, int y, int z) {
        return editSession.getBlock(x, y, z);
    }

    public void setBlock(BlockVector3 position, BaseBlock block) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        setBlock(x, y, z, block);
    }

    public void setBlock(int x, int y, int z, BaseBlock block) {
        editSession.setBlock(x, y, z, block);
    }

    @Override
    public BrushProperties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(BrushProperties properties) {
        this.properties = properties;
    }

    /**
     * Return a config property associated to a brush, if exists. Otherwise, set the default value and return it.
     *
     * @param propertyName the name of the property
     * @param defaultValue the default value to set and return
     * @return the associated proprorty, or the default value
     */
    public Object getProperty(String propertyName, Object defaultValue) {
        return getProperty(propertyName, defaultValue, defaultValue);
    }

    /**
     * Return a config property associated to a brush, if exists. Otherwise, set the default value and return it.
     *
     * @param propertyName       the name of the property
     * @param defaultValue       the default value to set and return
     * @param defaultConfigValue the default config value to set, objets such as Enum and Registry values are not serializable
     * @return the associated property, or the default value
     */
    public Object getProperty(String propertyName, Object defaultValue, Object defaultConfigValue) {
        String brush = this.properties.getName();
        Object currentPropertyValue = CONFIG.getBrushProperties()
                .computeIfAbsent(brush, brushName -> new HashMap<>())
                .putIfAbsent(propertyName, defaultValue);
        if (currentPropertyValue == null) {
            CONFIG.saveBrushPropertyToConfig(brush, propertyName, defaultConfigValue);
        }
        return currentPropertyValue;
    }

    public String getStringProperty(String propertyName, String defaultValue) {
        Object propertyValue = this.getProperty(propertyName, defaultValue);

        if (propertyValue instanceof String) {
            return (String) propertyValue;
        }

        LOGGER.warn("Invalid or missing String property '{}' value for '{}' brush! Setting up the default one...",
                propertyName, this.properties.getName()
        );
        return defaultValue;
    }

    public boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        Object propertyValue = this.getProperty(propertyName, defaultValue);

        if (propertyValue instanceof Boolean) {
            return (boolean) propertyValue;
        }

        LOGGER.warn("Invalid or missing Boolean property '{}' value for '{}' brush! Setting up the default one...",
                propertyName, this.properties.getName()
        );
        return defaultValue;
    }

    public int getIntegerProperty(String propertyName, int defaultValue) {
        Object propertyValue = this.getProperty(propertyName, defaultValue);

        if (propertyValue instanceof Integer) {
            return (int) propertyValue;
        }

        LOGGER.warn("Invalid or Integer String property '{}' value for '{}' brush! Setting up the default one...",
                propertyName, this.properties.getName()
        );
        return defaultValue;
    }

    public double getDoubleProperty(String propertyName, double defaultValue) {
        Object propertyValue = this.getProperty(propertyName, defaultValue);

        if (propertyValue instanceof Double) {
            return (double) propertyValue;
        }

        LOGGER.warn("Invalid or missing Double property '{}' value for '{}' brush! Setting up the default one...",
                propertyName, this.properties.getName()
        );
        return defaultValue;
    }

    public List<?> getListProperty(String propertyName, List<?> defaultValue) {
        Object propertyValue = this.getProperty(propertyName, defaultValue);

        if (propertyValue instanceof List) {
            return (List<?>) propertyValue;
        }

        LOGGER.warn("Invalid or missing List property '{}' value for '{}' brush! Setting up the default one...",
                propertyName, this.properties.getName()
        );
        return defaultValue;
    }

    public Object getRegistryProperty(
            String propertyName, NamespacedRegistry<? extends Keyed> registry, Keyed
            defaultValue
    ) {
        Object propertyValue = this.getProperty(propertyName, defaultValue, defaultValue.getId());

        if (propertyValue instanceof String) {
            Object registryValue = registry.get(((String) propertyValue).toLowerCase(Locale.ROOT));
            if (registryValue != null) {
                return registryValue;
            }
        }

        LOGGER.warn("Invalid or missing '{}' Registry property '{}' value for '{}' brush! " +
                "Setting up the default one...", registry.getName(), propertyName, this.properties.getName());
        return defaultValue;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Enum<?> getEnumProperty(String propertyName, Class<?> enumClass, Enum<?> defaultValue) {
        Object propertyValue = this.getProperty(propertyName, defaultValue, defaultValue.name());

        if (propertyValue instanceof String) {
            try {
                return Enum.valueOf((Class<Enum>) enumClass, ((String) propertyValue).toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
            }
        }

        LOGGER.warn("Invalid or missing '{}' Enum property '{}' value for '{}' brush! Setting up the default one...",
                enumClass.getSimpleName(), propertyName, this.properties.getName()
        );
        return defaultValue;
    }

    @Override
    public void loadProperties() {
    }

    public EditSession getEditSession() {
        return editSession;
    }

    public BlockVector3 getTargetBlock() {
        return this.targetBlock;
    }

    public void setTargetBlock(BlockVector3 targetBlock) {
        this.targetBlock = targetBlock;
    }

    /**
     * @return Block before target Block.
     */
    public BlockVector3 getLastBlock() {
        return this.lastBlock;
    }

    public void setLastBlock(BlockVector3 lastBlock) {
        this.lastBlock = lastBlock;
    }

}
