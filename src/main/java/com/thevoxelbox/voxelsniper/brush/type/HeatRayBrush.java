package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class HeatRayBrush extends AbstractBrush {

    private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
    private static final double REQUIRED_COBBLE_DENSITY = 0.5;
    private static final double REQUIRED_FIRE_DENSITY = -0.25;
    private static final double REQUIRED_AIR_DENSITY = 0;

    private static final int DEFAULT_OCTAVES = 5;
    private static final double DEFAULT_FREQUENCY = 1;
    private static final double DEFAULT_AMPLITUDE = 0.3;

    private static final MaterialSet FLAMEABLE_BLOCKS = MaterialSet.builder()
            .with(BlockCategories.LOGS)
            .with(BlockCategories.SAPLINGS)
            .with(BlockCategories.PLANKS)
            .with(BlockCategories.LEAVES)
            .with(BlockCategories.WOOL)
            .with(BlockCategories.WOODEN_SLABS)
            .with(BlockCategories.WOODEN_STAIRS)
            .with(BlockCategories.WOODEN_DOORS)
            .with(BlockCategories.WOODEN_TRAPDOORS)
            .with(BlockCategories.WOODEN_PRESSURE_PLATES)
            .with(BlockCategories.ICE)
            .with(BlockCategories.SIGNS)
            .with(BlockCategories.WOODEN_FENCES)
            .with(BlockCategories.FENCE_GATES)
            .with(BlockCategories.SNOW)
            .with(MaterialSets.TORCHES)
            .with(MaterialSets.FLORA)
            .add(BlockTypes.SPONGE)
            .add(BlockTypes.COBWEB)
            .add(BlockTypes.FIRE)
            .add(BlockTypes.LADDER)
            .build();

    private double requiredObsidianDensity;
    private double requiredCobbleDensity;
    private double requiredFireDensity;
    private double requiredAirDensity;

    private int octaves;
    private double frequency;
    private double amplitude;

    @Override
    public void loadProperties() {
        this.requiredObsidianDensity = getDoubleProperty("required-obsidian-density", REQUIRED_OBSIDIAN_DENSITY);
        this.requiredCobbleDensity = getDoubleProperty("required-cobble-density", REQUIRED_COBBLE_DENSITY);
        this.requiredFireDensity = getDoubleProperty("required-fire-density", REQUIRED_FIRE_DENSITY);
        this.requiredAirDensity = getDoubleProperty("required-air-density", REQUIRED_AIR_DENSITY);

        this.octaves = getIntegerProperty("default-octaves", DEFAULT_OCTAVES);
        this.frequency = getDoubleProperty("default-frequency", DEFAULT_FREQUENCY);
        this.amplitude = getDoubleProperty("default-amplitude", DEFAULT_AMPLITUDE);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Heat Ray Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b hr oct [n] -- Sets octave parameter to n for the noise generator.");
            messenger.sendMessage(ChatColor.AQUA + "/b hr amp [n] -- Sets amplitude parameter to n for the noise generator.");
            messenger.sendMessage(ChatColor.AQUA + "/b hr freq [n] -- Sets frequency parameter to n for the noise generator.");
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("oct")) {
                    Integer octaves = NumericParser.parseInteger(parameters[1]);
                    if (octaves != null) {
                        this.octaves = octaves;
                        messenger.sendMessage(ChatColor.GREEN + "Octaves: " + this.octaves);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else if (firstParameter.equalsIgnoreCase("amp")) {
                    Double amplitude = NumericParser.parseDouble(parameters[1]);
                    if (amplitude != null) {
                        this.amplitude = amplitude;
                        messenger.sendMessage(ChatColor.GREEN + "Amplitude: " + this.amplitude);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else if (firstParameter.equalsIgnoreCase("freq")) {
                    Double frequency = NumericParser.parseDouble(parameters[1]);
                    if (frequency != null) {
                        this.frequency = frequency;
                        messenger.sendMessage(ChatColor.GREEN + "Frequency: " + this.frequency);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number.");
                    }
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display parameter " +
                        "info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("oct", "amp", "freq"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        heatRay(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        heatRay(snipe);
    }

    /**
     * Heat Ray executer.
     */
    public void heatRay(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        PerlinNoiseGenerator generator = new PerlinNoiseGenerator(new Random());
        BlockVector3 targetBlock = getTargetBlock();
        Vector targetBlockVector = Vectors.toBukkit(targetBlock);
        Vector currentLocation = new Vector();
        int brushSize = toolkitProperties.getBrushSize();
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                for (int y = brushSize; y >= -brushSize; y--) {
                    currentLocation.setX(targetBlock.getX() + x);
                    currentLocation.setY(targetBlock.getY() + y);
                    currentLocation.setZ(targetBlock.getZ() + z);
                    Vector currentLocationVector = currentLocation.clone();
                    if (currentLocationVector.isInSphere(targetBlockVector, brushSize)) {
                        BlockState currentBlock = getBlock(
                                currentLocation.getBlockX(),
                                currentLocation.getBlockY(),
                                currentLocation.getBlockZ()
                        );
                        BlockType currentBlockType = currentBlock.getBlockType();
                        if (MaterialSets.CHESTS.contains(currentBlockType)) {
                            continue;
                        }
                        if (Materials.isLiquid(currentBlockType)) {
                            setBlockType(
                                    currentLocation.getBlockX(),
                                    currentLocation.getBlockY(),
                                    currentLocation.getBlockZ(),
                                    BlockTypes.AIR
                            );
                            continue;
                        }
                        if (FLAMEABLE_BLOCKS.contains(currentBlockType)) {
                            setBlockType(
                                    currentLocation.getBlockX(),
                                    currentLocation.getBlockY(),
                                    currentLocation.getBlockZ(),
                                    BlockTypes.FIRE
                            );
                            continue;
                        }
                        if (!Materials.isEmpty(currentBlockType)) {
                            double airDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            double fireDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            double cobbleDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            double obsidianDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            if (obsidianDensity >= this.requiredObsidianDensity) {
                                if (currentBlockType != BlockTypes.OBSIDIAN) {
                                    setBlockType(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.OBSIDIAN
                                    );
                                }
                            } else if (cobbleDensity >= this.requiredCobbleDensity) {
                                if (currentBlockType != BlockTypes.COBBLESTONE) {
                                    setBlockType(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.COBBLESTONE
                                    );
                                }
                            } else if (fireDensity >= this.requiredFireDensity) {
                                if (currentBlockType != BlockTypes.FIRE) {
                                    setBlockType(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.FIRE
                                    );
                                }
                            } else if (airDensity >= this.requiredAirDensity) {
                                setBlockType(
                                        currentLocation.getBlockX(),
                                        currentLocation.getBlockY(),
                                        currentLocation.getBlockZ(),
                                        BlockTypes.AIR
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(ChatColor.GREEN + "Octaves: " + this.octaves)
                .message(ChatColor.GREEN + "Amplitude: " + this.amplitude)
                .message(ChatColor.GREEN + "Frequency: " + this.frequency)
                .send();
    }

}
