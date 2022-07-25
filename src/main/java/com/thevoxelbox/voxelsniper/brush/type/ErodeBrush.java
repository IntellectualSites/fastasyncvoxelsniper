package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ErodeBrush extends AbstractBrush {

    private static final List<Direction> FACES_TO_CHECK = Arrays.asList(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.UP,
            Direction.DOWN,
            Direction.EAST,
            Direction.WEST
    );

    private ErosionPreset currentPreset = Preset.DEFAULT.getPreset();

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.erode.info"));
        } else {
            Preset preset = Preset.getPreset(firstParameter);
            if (preset != null) {
                try {
                    this.currentPreset = preset.getPreset();
                    messenger.sendMessage(Caption.of("voxelsniper.brush.erode.set-preset", preset.getFullName()));
                    return;
                } catch (IllegalArgumentException exception) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.erode.invalid-preset", preset.getFullName()));
                    return;
                }
            }

            ErosionPreset currentPresetBackup = this.currentPreset;
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("f")) {
                    Integer fillFaces = NumericParser.parseInteger(parameters[1]);
                    if (fillFaces != null) {
                        this.currentPreset = new ErosionPreset(
                                this.currentPreset.getErosionFaces(),
                                this.currentPreset.getErosionRecursions(),
                                fillFaces,
                                this.currentPreset.getFillRecursions()
                        );
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                } else if (firstParameter.equalsIgnoreCase("e")) {
                    Integer erosionFaces = NumericParser.parseInteger(parameters[1]);
                    if (erosionFaces != null) {
                        this.currentPreset = new ErosionPreset(
                                erosionFaces,
                                this.currentPreset.getErosionRecursions(),
                                this.currentPreset.getFillFaces(),
                                this.currentPreset.getFillRecursions()
                        );
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                } else if (firstParameter.equalsIgnoreCase("F")) {
                    Integer fillRecursion = NumericParser.parseInteger(parameters[1]);
                    if (fillRecursion != null) {
                        this.currentPreset = new ErosionPreset(
                                this.currentPreset.getErosionFaces(),
                                this.currentPreset.getErosionRecursions(),
                                this.currentPreset.getFillFaces(),
                                fillRecursion
                        );
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                } else if (firstParameter.equalsIgnoreCase("E")) {
                    Integer erosionRecursion = NumericParser.parseInteger(parameters[1]);
                    if (erosionRecursion != null) {
                        this.currentPreset = new ErosionPreset(
                                this.currentPreset.getErosionFaces(),
                                erosionRecursion,
                                this.currentPreset.getFillFaces(),
                                this.currentPreset.getFillRecursions()
                        );
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                }
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
                return;
            }

            if (!this.currentPreset.equals(currentPresetBackup)) {
                if (this.currentPreset.getErosionFaces() != currentPresetBackup.getErosionFaces()) {
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.erode.set-erosion-faces",
                            this.currentPreset.getErosionFaces()
                    ));
                }
                if (this.currentPreset.getFillFaces() != currentPresetBackup.getFillFaces()) {
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.erode.set-fill-faces",
                            this.currentPreset.getFillFaces()
                    ));
                }
                if (this.currentPreset.getErosionRecursions() != currentPresetBackup.getErosionRecursions()) {
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.erode.set-erosion-recursions",
                            this.currentPreset.getErosionRecursions()
                    ));
                }
                if (this.currentPreset.getFillRecursions() != currentPresetBackup.getFillRecursions()) {
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.erode.set-fill-recursions",
                            this.currentPreset.getFillRecursions()
                    ));
                }
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("f", "e", "F", "E", "default", "melt", "fill", "smooth", "lift", "floatclean"),
                    parameter, 0
            );
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        erosion(snipe, this.currentPreset);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        erosion(snipe, this.currentPreset.getInverted());
    }

    private void erosion(Snipe snipe, ErosionPreset erosionPreset) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockVector3 targetBlock = getTargetBlock();
        BlockChangeTracker blockChangeTracker = new BlockChangeTracker(getEditSession());
        Vector targetBlockVector = Vectors.toBukkit(targetBlock);
        for (int i = 0; i < erosionPreset.getErosionRecursions(); ++i) {
            erosionIteration(toolkitProperties, erosionPreset, blockChangeTracker, targetBlockVector);
        }
        for (int i = 0; i < erosionPreset.getFillRecursions(); ++i) {
            fillIteration(toolkitProperties, erosionPreset, blockChangeTracker, targetBlockVector);
        }
        for (BlockWrapper blockWrapper : blockChangeTracker.getAll()) {
            BlockState block = blockWrapper.getBlock();
            if (block != null) {
                BlockState blockData = blockWrapper.getBlockData();
                setBlockData(blockWrapper.getX(), blockWrapper.getY(), blockWrapper.getZ(), blockData);
            }
        }
    }

    private void fillIteration(
            ToolkitProperties toolkitProperties,
            ErosionPreset erosionPreset,
            BlockChangeTracker blockChangeTracker,
            Vector targetBlockVector
    ) {
        int currentIteration = blockChangeTracker.nextIteration();
        BlockVector3 targetBlock = getTargetBlock();
        int brushSize = toolkitProperties.getBrushSize();
        for (int x = targetBlock.getX() - brushSize; x <= targetBlock.getX() + brushSize; ++x) {
            for (int z = targetBlock.getZ() - brushSize; z <= targetBlock.getZ() + brushSize; ++z) {
                for (int y = targetBlock.getY() - brushSize; y <= targetBlock.getY() + brushSize; ++y) {
                    Vector currentPosition = new Vector(x, y, z);
                    if (currentPosition.isInSphere(targetBlockVector, brushSize)) {
                        BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
                        if (!(currentBlock.isEmpty() || currentBlock.isLiquid())) {
                            continue;
                        }
                        int count = 0;
                        Map<BlockWrapper, Integer> blockCount = new HashMap<>();
                        for (Direction direction : FACES_TO_CHECK) {
                            Vector relativePosition = Vectors.toBukkit(
                                    getRelativeBlock(Vectors.of(currentPosition), direction)
                            );
                            BlockWrapper relativeBlock = blockChangeTracker.get(relativePosition, currentIteration);
                            if (!(relativeBlock.isEmpty() || relativeBlock.isLiquid())) {
                                count++;
                                BlockWrapper typeBlock = new BlockWrapper(x, y, z, null, relativeBlock.getBlockData());
                                if (blockCount.containsKey(typeBlock)) {
                                    blockCount.put(typeBlock, blockCount.get(typeBlock) + 1);
                                } else {
                                    blockCount.put(typeBlock, 1);
                                }
                            }
                        }
                        BlockWrapper currentBlockWrapper = new BlockWrapper(x, y, z, null, BlockTypes.AIR.getDefaultState());
                        int amount = 0;
                        for (BlockWrapper wrapper : blockCount.keySet()) {
                            Integer currentCount = blockCount.get(wrapper);
                            if (amount <= currentCount) {
                                currentBlockWrapper = wrapper;
                                amount = currentCount;
                            }
                        }
                        if (count >= erosionPreset.getFillFaces()) {
                            blockChangeTracker.put(
                                    currentPosition,
                                    new BlockWrapper(x, y, z, currentBlock.getBlock(), currentBlockWrapper.getBlockData()),
                                    currentIteration
                            );
                        }
                    }
                }
            }
        }
    }

    private void erosionIteration(
            ToolkitProperties toolkitProperties,
            ErosionPreset erosionPreset,
            BlockChangeTracker blockChangeTracker,
            Vector targetBlockVector
    ) {
        int currentIteration = blockChangeTracker.nextIteration();
        BlockVector3 targetBlock = this.getTargetBlock();
        int brushSize = toolkitProperties.getBrushSize();
        for (int x = targetBlock.getX() - brushSize; x <= targetBlock.getX() + brushSize; ++x) {
            for (int z = targetBlock.getZ() - brushSize; z <= targetBlock.getZ() + brushSize; ++z) {
                for (int y = targetBlock.getY() - brushSize; y <= targetBlock.getY() + brushSize; ++y) {
                    Vector currentPosition = new Vector(x, y, z);
                    if (currentPosition.isInSphere(targetBlockVector, brushSize)) {
                        BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
                        if (currentBlock.isEmpty() || currentBlock.isLiquid()) {
                            continue;
                        }
                        int count = (int) FACES_TO_CHECK.stream()
                                .map(direction -> getRelativeBlock(Vectors.of(currentPosition), direction))
                                .map(Vectors::toBukkit)
                                .map(relativePosition -> blockChangeTracker.get(relativePosition, currentIteration))
                                .filter(relativeBlock -> relativeBlock.isEmpty() || relativeBlock.isLiquid())
                                .count();
                        if (count >= erosionPreset.getErosionFaces()) {
                            blockChangeTracker.put(
                                    currentPosition,
                                    new BlockWrapper(x, y, z, currentBlock.getBlock(), BlockTypes.AIR.getDefaultState()),
                                    currentIteration
                            );
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
                .message(Caption.of("voxelsniper.brush.erode.set-erosion-faces", this.currentPreset.getErosionFaces()))
                .message(Caption.of("voxelsniper.brush.erode.set-fill-faces", this.currentPreset.getFillFaces()))
                .message(Caption.of("voxelsniper.brush.erode.set-erosion-recursions", this.currentPreset.getErosionRecursions()))
                .message(Caption.of("voxelsniper.brush.erode.set-fill-recursions", this.currentPreset.getFillRecursions()))
                .send();
    }

    private enum Preset {

        DEFAULT("default", new ErosionPreset(0, 1, 0, 1)),
        MELT("melt", new ErosionPreset(2, 1, 5, 1)),
        FILL("fill", new ErosionPreset(5, 1, 2, 1)),
        SMOOTH("smooth", new ErosionPreset(3, 1, 3, 1)),
        LIFT("lift", new ErosionPreset(6, 0, 1, 1)),
        FLOAT_CLEAN("floatclean", new ErosionPreset(6, 1, 6, 1));

        private final String name;
        private final ErosionPreset preset;

        Preset(String name, ErosionPreset preset) {
            this.name = name;
            this.preset = preset;
        }

        @Nullable
        public static Preset getPreset(String name) {
            return Arrays.stream(values())
                    .filter(preset -> preset.name.equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }

        public String getName() {
            return this.name;
        }

        public TranslatableComponent getFullName() {
            return Caption.of("voxelsniper.brush.erode.preset." + this.name);
        }

        public ErosionPreset getPreset() {
            return this.preset;
        }
    }

    private static final class BlockChangeTracker {

        private final Map<Integer, Map<Vector, BlockWrapper>> blockChanges;
        private final Map<Vector, BlockWrapper> flatChanges;
        private final EditSession editSession;
        private int nextIterationId;

        private BlockChangeTracker(EditSession editSession) {
            this.blockChanges = new HashMap<>();
            this.flatChanges = new HashMap<>();
            this.editSession = editSession;
        }

        public BlockWrapper get(Vector position, int iteration) {
            for (int i = iteration - 1; i >= 0; --i) {
                if (this.blockChanges.containsKey(i) && this.blockChanges.get(i)
                        .containsKey(position)) {
                    return this.blockChanges.get(i)
                            .get(position);
                }
            }
            return new BlockWrapper(position.getBlockX(), position.getBlockY(), position.getBlockZ(),
                    editSession.getBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ())
            );
        }

        public Collection<BlockWrapper> getAll() {
            return this.flatChanges.values();
        }

        public int nextIteration() {
            int nextIterationId = this.nextIterationId;
            this.nextIterationId++;
            return nextIterationId;
        }

        public void put(Vector position, BlockWrapper changedBlock, int iteration) {
            if (!this.blockChanges.containsKey(iteration)) {
                this.blockChanges.put(iteration, new HashMap<>());
            }
            this.blockChanges.get(iteration)
                    .put(position, changedBlock);
            this.flatChanges.put(position, changedBlock);
        }

    }

    private static final class BlockWrapper {

        private final int x;
        private final int y;
        private final int z;
        @Nullable
        private final BlockState block;
        private final BlockState blockData;

        private BlockWrapper(int x, int y, int z, BlockState block) {
            this(x, y, z, block, block);
        }

        private BlockWrapper(int x, int y, int z, @Nullable BlockState block, BlockState blockData) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = block;
            this.blockData = blockData;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        @Nullable
        public BlockState getBlock() {
            return this.block;
        }

        public BlockState getBlockData() {
            return this.blockData;
        }

        public boolean isEmpty() {
            BlockType type = blockData.getBlockType();
            return Materials.isEmpty(type);
        }

        public boolean isLiquid() {
            BlockType type = blockData.getBlockType();
            return Materials.isLiquid(type);
        }

    }

    private record ErosionPreset(int erosionFaces, int erosionRecursions, int fillFaces, int fillRecursion) implements
            Serializable {

        @Serial
        private static final long serialVersionUID = 8997952776355430411L;

        @Override
        public int hashCode() {
            return Objects.hash(this.erosionFaces, this.erosionRecursions, this.fillFaces, this.fillRecursion);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ErosionPreset other) {
                return this.erosionFaces == other.erosionFaces && this.erosionRecursions == other.erosionRecursions && this.fillFaces == other.fillFaces && this.fillRecursion == other.fillRecursion;
            }
            return false;
        }

        /**
         * Returns the erosion faces
         */
        public int getErosionFaces() {
            return this.erosionFaces;
        }

        /**
         * Returns the erosion recursion
         */
        public int getErosionRecursions() {
            return this.erosionRecursions;
        }

        /**
         * Returns the fill faces
         */
        public int getFillFaces() {
            return this.fillFaces;
        }

        /**
         * Returns the fill recursion
         */
        public int getFillRecursions() {
            return this.fillRecursion;
        }

        public ErosionPreset getInverted() {
            return new ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursions);
        }

    }

}
