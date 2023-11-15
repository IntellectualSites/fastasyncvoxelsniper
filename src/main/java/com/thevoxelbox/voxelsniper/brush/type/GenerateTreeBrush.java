package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import cloud.commandframework.annotations.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Proposal: Use /v and /vr for leave and wood material // or two more parameters -- Monofraps
@RequireToolkit
@CommandMethod(value = "brush|b generate_tree|generatetree|gt")
@CommandPermission("voxelsniper.brush.generatetree")
public class GenerateTreeBrush extends AbstractBrush {

    private static final BlockType DEFAULT_LEAF_TYPE = BlockTypes.OAK_LEAVES;
    private static final BlockType DEFAULT_WOOD_TYPE = BlockTypes.OAK_LOG;
    private static final boolean DEFAULT_ROOT_FLOAT = false;
    private static final int DEFAULT_START_HEIGHT = 0;
    private static final int DEFAULT_ROOT_LENGTH = 9;
    private static final int DEFAULT_MIN_ROOTS = 1;
    private static final int DEFAULT_MAX_ROOTS = 2;
    private static final int DEFAULT_THICKNESS = 1;
    private static final int DEFAULT_SLOPE_CHANCE = 40;
    private static final int DEFAULT_HEIGHT_MIN = 14;
    private static final int DEFAULT_HEIGHT_MAX = 18;
    private static final int DEFAULT_BRANCH_LENGTH = 8;
    private static final int DEFAULT_NODE_MIN = 3;
    private static final int DEFAULT_NODE_MAX = 4;

    private static final MaterialSet SOLIDS = MaterialSet.builder()
            .with(BlockCategories.LOGS)
            .with(MaterialSets.AIRS)
            .add(BlockTypes.WATER)
            .add(BlockTypes.SNOW)
            .build();

    // Tree variables.
    private final Random randGenerator = new Random();
    private final List<BlockVector3> branchBlocksLocation = new ArrayList<>();

    private BlockType leafType;
    private BlockType woodType;
    private boolean rootFloat;
    private int startHeight;
    private int rootLength;
    private int minRoots;
    private int maxRoots;
    private int thickness;
    private int slopeChance;
    private int heightMin;
    private int heightMax;
    private int branchLength;
    private int nodeMin;
    private int nodeMax;

    private int blockPositionX;
    private int blockPositionY;
    private int blockPositionZ;

    @Override
    public void loadProperties() {
        resetValues();
    }

    private void resetValues() {
        this.leafType = (BlockType) getRegistryProperty("default-leaf-type", BlockType.REGISTRY, DEFAULT_LEAF_TYPE);
        this.woodType = (BlockType) getRegistryProperty("default-wood-type", BlockType.REGISTRY, DEFAULT_WOOD_TYPE);
        this.rootFloat = getBooleanProperty("default-root-float", DEFAULT_ROOT_FLOAT);
        this.startHeight = getIntegerProperty("default-start-height", DEFAULT_START_HEIGHT);
        this.rootLength = getIntegerProperty("default-root-length", DEFAULT_ROOT_LENGTH);
        this.minRoots = getIntegerProperty("default-min-roots", DEFAULT_MIN_ROOTS);
        this.maxRoots = getIntegerProperty("default-max-roots", DEFAULT_MAX_ROOTS);
        this.thickness = getIntegerProperty("default-thickness", DEFAULT_THICKNESS);
        this.slopeChance = getIntegerProperty("default-slope-chance", DEFAULT_SLOPE_CHANCE);
        this.heightMin = getIntegerProperty("default-height-min", DEFAULT_HEIGHT_MIN);
        this.heightMax = getIntegerProperty("default-height-max", DEFAULT_HEIGHT_MAX);
        this.branchLength = getIntegerProperty("default-branch-length", DEFAULT_BRANCH_LENGTH);
        this.nodeMin = getIntegerProperty("default-node-min", DEFAULT_NODE_MIN);
        this.nodeMax = getIntegerProperty("default-node-max", DEFAULT_NODE_MAX);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.generate-tree.info"));
    }

    @CommandMethod("default")
    public void onBrushDefault(
            final @NotNull Snipe snipe
    ) {
        this.resetValues();

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.reset"));
    }

    @CommandMethod("lt <leaf-type>")
    public void onBrushLt(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("leaf-type") BlockType leafType
    ) {
        this.leafType = leafType;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-leaf-type",
                this.leafType.getId()
        ));
    }

    @CommandMethod("wt <wood-type>")
    public void onBrushWt(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("wood-type") BlockType woodType
    ) {
        this.woodType = woodType;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-wood-type",
                this.woodType.getId()
        ));
    }

    @CommandMethod("tt <thickness>")
    public void onBrushTt(
            final @NotNull Snipe snipe,
            final @Argument("thickness") @Range(min = "0") int thickness
    ) {
        this.thickness = thickness;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-thickness",
                this.thickness
        ));
    }

    @CommandMethod("rf <root-float>")
    public void onBrushRf(
            final @NotNull Snipe snipe,
            final @Argument("root-float") @Liberal boolean rootFloat
    ) {
        this.rootFloat = rootFloat;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-floating-roots",
                VoxelSniperText.getStatus(this.rootFloat)
        ));
    }

    @CommandMethod("sh <start-height>")
    public void onBrushSh(
            final @NotNull Snipe snipe,
            final @Argument("start-height") int startHeight
    ) {
        this.startHeight = startHeight;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-start-height",
                this.startHeight
        ));
    }

    @CommandMethod("rl <root-length>")
    public void onBrushRl(
            final @NotNull Snipe snipe,
            final @Argument("root-length") @Range(min = "0") int rootLength
    ) {
        this.rootLength = rootLength;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-root-length",
                this.rootLength
        ));
    }

    @CommandMethod("ts <slope-chance>")
    public void onBrushTs(
            final @NotNull Snipe snipe,
            final @Argument("slope-chance") @Range(min = "0", max = "100") int slopeChance
    ) {
        this.slopeChance = slopeChance;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-trunk-slope",
                this.slopeChance
        ));
    }

    @CommandMethod("bl <branch-length>")
    public void onBrushBl(
            final @NotNull Snipe snipe,
            final @Argument("branch-length") @Range(min = "0") int branchLength
    ) {
        this.branchLength = branchLength;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-branch-length",
                this.branchLength
        ));
    }

    @CommandMethod("minr <min-roots>")
    public void onBrushMinr(
            final @NotNull Snipe snipe,
            final @Argument("min-roots") @DynamicRange(min = "0", max = "maxRoots") int minRoots
    ) {
        this.minRoots = minRoots;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-minimum-roots",
                this.minRoots
        ));
    }

    @CommandMethod("maxr <max-roots>")
    public void onBrushMaxr(
            final @NotNull Snipe snipe,
            final @Argument("max-roots") @DynamicRange(min = "minRoots") int maxRoots
    ) {
        this.maxRoots = maxRoots;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-maximum-roots",
                this.maxRoots
        ));
    }

    @CommandMethod("minh <height-min>")
    public void onBrushMinh(
            final @NotNull Snipe snipe,
            final @Argument("height-min") @DynamicRange(min = "0", max = "heightMax") int heightMin
    ) {
        this.heightMin = heightMin;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-minimum-height",
                this.heightMin
        ));
    }

    @CommandMethod("maxh <height-max>")
    public void onBrushMaxh(
            final @NotNull Snipe snipe,
            final @Argument("height-max") @DynamicRange(min = "heightMin") int heightMax
    ) {
        this.heightMax = heightMax;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-maximum-height",
                this.heightMax
        ));
    }

    @CommandMethod("minl <node-min>")
    public void onBrushMinl(
            final @NotNull Snipe snipe,
            final @Argument("node-min") @DynamicRange(min = "0", max = "nodeMax") int nodeMin
    ) {
        this.nodeMin = nodeMin;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-minimum-leaf-thickness",
                this.nodeMin
        ));
    }

    @CommandMethod("maxl <node-max>")
    public void onBrushMaxl(
            final @NotNull Snipe snipe,
            final @Argument("node-max") @DynamicRange(min = "nodeMin") int nodeMax
    ) {
        this.nodeMax = nodeMax;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.generate-tree.set-maximum-leaf-thickness",
                this.nodeMax
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        this.branchBlocksLocation.clear();
        // Sets the location variables.
        BlockVector3 targetBlock = this.getTargetBlock();
        this.blockPositionX = targetBlock.getX();
        this.blockPositionY = targetBlock.getY() + this.startHeight;
        this.blockPositionZ = targetBlock.getZ();
        // Generates the roots.
        rootGen();
        // Generates the trunk, which also generates branches.
        generateTrunk();
        // Each branch block was saved in an array. This is now fed through an array.
        // This array takes each branch block and constructs a leaf node around it.
        for (BlockVector3 blockLocation : this.branchBlocksLocation) {
            this.blockPositionX = blockLocation.getX();
            this.blockPositionY = blockLocation.getY();
            this.blockPositionZ = blockLocation.getZ();
            this.createLeafNode();
        }
    }
    // The gunpowder currently does nothing extra.

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        handleArrowAction(snipe);
    }
    // Branch Creation based on direction chosen from the parameters passed.

    private void createBranch(int xDirection, int zDirection) {
        // Sets branch origin.
        int originX = this.blockPositionX;
        int originY = this.blockPositionY;
        int originZ = this.blockPositionZ;
        // Sets direction preference.
        int xPreference = this.randGenerator.nextInt(60) + 20;
        int zPreference = this.randGenerator.nextInt(60) + 20;
        // Iterates according to branch length.
        for (int r = 0; r < this.branchLength; r++) {
            // Alters direction according to preferences.
            if (this.randGenerator.nextInt(100) < xPreference) {
                this.blockPositionX += xDirection;
            }
            if (this.randGenerator.nextInt(100) < zPreference) {
                this.blockPositionZ += zDirection;
            }
            // 50% chance to increase elevation every second block.
            if (Math.abs(r % 2) == 1) {
                this.blockPositionY += this.randGenerator.nextInt(2);
            }
            // Creates a branch block.
            setBlock(this.blockPositionX, clampY(this.blockPositionY), this.blockPositionZ, woodType);
            this.branchBlocksLocation.add(BlockVector3.at(this.blockPositionX, clampY(this.blockPositionY), this.blockPositionZ));
        }
        // Resets the origin
        this.blockPositionX = originX;
        this.blockPositionY = originY;
        this.blockPositionZ = originZ;
    }

    private void createLeafNode() {
        // Generates the node size.
        int nodeRadius = this.randGenerator.nextInt(this.nodeMax - this.nodeMin + 1) + this.nodeMin;
        // Lowers the current block in order to start at the bottom of the node.
        this.blockPositionY -= 2;
        double bSquared = Math.pow(nodeRadius + 0.5, 2);
        for (int z = nodeRadius; z >= 0; z--) {
            double zSquared = Math.pow(z, 2);
            for (int x = nodeRadius; x >= 0; x--) {
                double xSquared = Math.pow(x, 2);
                for (int y = nodeRadius; y >= 0; y--) {
                    if ((xSquared + Math.pow(y, 2) + zSquared) <= bSquared) {
                        // Chance to skip creation of a block.
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY + y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY + y, this.blockPositionZ - z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY + y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY + y, this.blockPositionZ - z);
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY - y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY - y, this.blockPositionZ - z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY - y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY - y, this.blockPositionZ - z);
                    }
                }
            }
        }
    }

    private void generateLeafNodeBlock(int x, int y, int z) {
        if (this.randGenerator.nextInt(100) >= 30) {
            // If block is Air, create a leaf block.
            BlockState block = getBlock(x, y, z);
            if (block.isAir()) {
                // Creates block.
                setBlockData(x, clampY(y), z, this.leafType
                        .getDefaultState()
                        .with(PropertyKey.PERSISTENT, true));
            }
        }
    }

    /**
     * Code Concerning Root Generation.
     */

    private void createRoot(int xDirection, int zDirection) {
        // Sets Origin.
        int originX = this.blockPositionX;
        int originY = this.blockPositionY;
        int originZ = this.blockPositionZ;
        // Generates the number of roots to create.
        int roots = this.randGenerator.nextInt(this.maxRoots - this.minRoots + 1) + this.minRoots;
        // A roots preference to move along the X and Y axis.
        // Loops for each root to be created.
        for (int i = 0; i < roots; i++) {
            // Pushes the root'world starting point out from the center of the tree.
            for (int t = 0; t < this.thickness - 1; t++) {
                this.blockPositionX += xDirection;
                this.blockPositionZ += zDirection;
            }
            // Generate directional preference between 30% and 70%
            int xPreference = this.randGenerator.nextInt(30) + 40;
            int zPreference = this.randGenerator.nextInt(30) + 40;
            for (int j = 0; j < this.rootLength; j++) {
                // For the purposes of this algorithm, logs aren't considered solid.
                // If not solid then...
                if (BlockCategories.LOGS.contains(getBlockType(this.blockPositionX, this.blockPositionY, this.blockPositionZ))) {
                    // If solid then...
                    // End loop
                    break;
                } else {
                    // Place log block.
                    setBlock(this.blockPositionX, clampY(this.blockPositionY), this.blockPositionZ, this.woodType);
                }
                // Checks is block below is solid
                if (SOLIDS.contains(clampY(this.blockPositionX, this.blockPositionY - 1, this.blockPositionZ))) {
                    // Mos down if solid.
                    this.blockPositionY -= 1;
                    if (this.rootFloat) {
                        if (this.randGenerator.nextInt(100) < xPreference) {
                            this.blockPositionX += xDirection;
                        }
                        if (this.randGenerator.nextInt(100) < zPreference) {
                            this.blockPositionZ += zDirection;
                        }
                    }
                } else {
                    // If solid then move.
                    if (this.randGenerator.nextInt(100) < xPreference) {
                        this.blockPositionX += xDirection;
                    }
                    if (this.randGenerator.nextInt(100) < zPreference) {
                        this.blockPositionZ += zDirection;
                    }
                    // Checks if new location is solid, if not then move down.
                    if (SOLIDS.contains(clampY(this.blockPositionX, this.blockPositionY - 1, this.blockPositionZ))) {
                        this.blockPositionY -= 1;
                    }
                }
            }
            // Reset origin.
            this.blockPositionX = originX;
            this.blockPositionY = originY;
            this.blockPositionZ = originZ;
        }
    }

    private void rootGen() {
        // Quadrant 1
        this.createRoot(1, 1);
        // Quadrant 2
        this.createRoot(-1, 1);
        // Quadrant 3
        this.createRoot(1, -1);
        // Quadrant 4
        this.createRoot(-1, -1);
    }

    private void createTrunk() {
        // Creates true circle discs of the set size using the wood type selected.
        double bSquared = Math.pow(this.thickness + 0.5, 2);
        for (int x = this.thickness; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int z = this.thickness; z >= 0; z--) {
                if ((xSquared + Math.pow(z, 2)) <= bSquared) {
                    // If block is air, then create a block.
                    generateTrunkBlock(this.blockPositionX + x, this.blockPositionZ + z);
                    generateTrunkBlock(this.blockPositionX + x, this.blockPositionZ - z);
                    generateTrunkBlock(this.blockPositionX - x, this.blockPositionZ + z);
                    generateTrunkBlock(this.blockPositionX - x, this.blockPositionZ - z);
                }
            }
        }
    }

    private void generateTrunkBlock(int x, int y) {
        BlockState block = getBlock(x, this.blockPositionY, y);
        if (block.isAir()) {
            // Creates block.
            setBlock(x, clampY(this.blockPositionY), y, this.woodType);
        }
    }

    private void generateTrunk() {
        // Sets Origin
        int originX = this.blockPositionX;
        int originY = this.blockPositionY;
        int originZ = this.blockPositionZ;
        // ----------
        // Main Trunk
        // ----------
        // Sets directional preferences.
        int xPreference = this.randGenerator.nextInt(this.slopeChance);
        int zPreference = this.randGenerator.nextInt(this.slopeChance);
        // Sets direction.
        int xDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            xDirection = -1;
        }
        int zDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            zDirection = -1;
        }
        // Generates a height for trunk.
        int height = this.randGenerator.nextInt(this.heightMax - this.heightMin + 1) + this.heightMin;
        // This is a hidden value not available through Parameters. Otherwise messy.
        int twistChance = 5;
        for (int p = 0; p < height; p++) {
            if (p > 3) {
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    xDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    zDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < xPreference) {
                    this.blockPositionX += xDirection;
                }
                if (this.randGenerator.nextInt(100) < zPreference) {
                    this.blockPositionZ += zDirection;
                }
            }
            // Creates trunk section
            this.createTrunk();
            // Mos up for next section
            this.blockPositionY += 1;
        }
        // Generates branches at top of trunk for each quadrant.
        this.createBranch(1, 1);
        this.createBranch(-1, 1);
        this.createBranch(1, -1);
        this.createBranch(-1, -1);
        // Reset Origin for next trunk.
        this.blockPositionX = originX;
        this.blockPositionY = originY + 4;
        this.blockPositionZ = originZ;
        // ---------------
        // Secondary Trunk
        // ---------------
        // Sets directional preferences.
        int nextXPreference = this.randGenerator.nextInt(this.slopeChance);
        int nextZPreference = this.randGenerator.nextInt(this.slopeChance);
        // Sets direction.
        int nextXDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            nextXDirection = -1;
        }
        int nextZDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            nextZDirection = -1;
        }
        // Generates a height for trunk.
        int nextHeight = this.randGenerator.nextInt(this.heightMax - this.heightMin + 1) + this.heightMin;
        if (nextHeight > 4) {
            for (int p = 0; p < nextHeight; p++) {
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    nextXDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    nextZDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < nextXPreference) {
                    this.blockPositionX += nextXDirection;
                }
                if (this.randGenerator.nextInt(100) < nextZPreference) {
                    this.blockPositionZ += nextZDirection;
                }
                // Creates a trunk section
                this.createTrunk();
                // Mos up for next section
                this.blockPositionY += 1;
            }
            // Generates branches at top of trunk for each quadrant.
            createBranch(1, 1);
            createBranch(-1, 1);
            createBranch(1, -1);
            createBranch(-1, -1);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-leaf-type",
                        this.leafType.getId()
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-wood-type",
                        this.woodType.getId()
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-thickness",
                        this.thickness
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-floating-roots",
                        this.rootFloat
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-start-height",
                        this.startHeight
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-root-length",
                        this.rootLength
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-trunk-slope",
                        this.slopeChance
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-branch-length",
                        this.branchLength
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-minimum-roots",
                        this.minRoots
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-maximum-roots",
                        this.maxRoots
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-minimum-height",
                        this.heightMin
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-maximum-height",
                        this.heightMax
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-minimum-leaf-thickness",
                        this.nodeMin
                ))
                .message(Caption.of(
                        "voxelsniper.brush.generate-tree.set-maximum-leaf-thickness",
                        this.nodeMax
                ))
                .send();
    }

}
