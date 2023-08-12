package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@RequireToolkit
@CommandMethod(value = "brush|b heat_ray|heatray|hr")
@CommandPermission("voxelsniper.brush.heatray")
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

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.heat-ray.info"));
    }

    @CommandMethod("oct <octaves>")
    public void onBrushOct(
            final @NotNull Snipe snipe,
            final @Argument("octaves") @Range(min = "0") int octaves
    ) {
        this.octaves = octaves;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.heat-ray.set-octaves",
                this.octaves
        ));
    }

    @CommandMethod("amp <amplitude>")
    public void onBrushAmp(
            final @NotNull Snipe snipe,
            final @Argument("amplitude") double amplitude
    ) {
        this.amplitude = amplitude;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.heat-ray.set-amplitude",
                this.amplitude
        ));
    }

    @CommandMethod("freq <frequency>")
    public void onBrushFreq(
            final @NotNull Snipe snipe,
            final @Argument("frequency") double frequency
    ) {
        this.frequency = frequency;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.heat-ray.set-frequency",
                this.frequency
        ));
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
     * Heat Ray executor.
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
                            setBlock(
                                    currentLocation.getBlockX(),
                                    currentLocation.getBlockY(),
                                    currentLocation.getBlockZ(),
                                    BlockTypes.AIR
                            );
                            continue;
                        }
                        if (FLAMEABLE_BLOCKS.contains(currentBlockType)) {
                            setBlock(
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
                                    setBlock(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.OBSIDIAN
                                    );
                                }
                            } else if (cobbleDensity >= this.requiredCobbleDensity) {
                                if (currentBlockType != BlockTypes.COBBLESTONE) {
                                    setBlock(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.COBBLESTONE
                                    );
                                }
                            } else if (fireDensity >= this.requiredFireDensity) {
                                if (currentBlockType != BlockTypes.FIRE) {
                                    setBlock(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.FIRE
                                    );
                                }
                            } else if (airDensity >= this.requiredAirDensity) {
                                setBlock(
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
                .message(Caption.of(
                        "voxelsniper.brush.heat-ray.set-octaves",
                        this.octaves
                ))
                .message(Caption.of(
                        "voxelsniper.brush.heat-ray.set-amplitude",
                        this.amplitude
                ))
                .message(Caption.of(
                        "voxelsniper.brush.heat-ray.set-frequency",
                        this.frequency
                ))
                .send();
    }

}
