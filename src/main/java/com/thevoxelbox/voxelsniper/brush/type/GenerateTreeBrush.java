package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

// Proposal: Use /v and /vr for leave and wood material // or two more parameters -- Monofraps
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

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "This brush takes the following parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b gt default -- Restores default params.");
            messenger.sendMessage(ChatColor.AQUA + "/b gt lt [t] -- Sets leaf type to t. (e.g. oak_leaves)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt wt [t] -- Sets wood type to t. (e.g. oak_log)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt tt [n] -- Sets tree thickness to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt rf [true|false] -- Sets root float.");
            messenger.sendMessage(ChatColor.AQUA + "/b gt sh [n] -- Sets starting height to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt rl [n] -- Sets root length to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt ts [n] -- Sets trunk slope chance to n. (0-100)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt bl [n] -- Sets branch length to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt minr [n] -- Sets minimum roots to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt maxr [n] -- Sets maximum roots to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt minh [n] -- Sets minimum height to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt maxh [n] -- Sets maximum height to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt minl [n] -- Sets minimum leaf node size to n. (whole number)");
            messenger.sendMessage(ChatColor.AQUA + "/b gt maxl [n] -- Sets maximum leaf node size to n. (whole number)");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("default")) { // Default settings.
                    // -------
                    // Presets
                    // -------
                    resetValues();
                    messenger.sendMessage(ChatColor.GOLD + "Brush reset to default parameters.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter " +
                            "info.");
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("lt")) { // Leaf Type
                    BlockType leafType = BlockTypes.get(parameters[1]);
                    if (leafType != null) {
                        this.leafType = leafType;
                        messenger.sendMessage(ChatColor.BLUE + "Leaf Type set to: " + this.leafType.getId());
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid leaf type: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("wt")) { // Wood Type
                    BlockType woodType = BlockTypes.get(parameters[1]);
                    if (woodType != null) {
                        this.woodType = woodType;
                        messenger.sendMessage(ChatColor.BLUE + "Wood Type set to: " + this.woodType.getId());
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid wood type: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("tt")) { // Tree Thickness
                    Integer thickness = NumericParser.parseInteger(parameters[1]);
                    if (thickness != null) {
                        this.thickness = thickness;
                        messenger.sendMessage(ChatColor.BLUE + "Thickness set to: " + this.thickness);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("rf")) { // Root Float
                    this.rootFloat = Boolean.parseBoolean(parameters[1]);
                    messenger.sendMessage(ChatColor.BLUE + "Floating Roots set to: " + this.rootFloat);
                } else if (firstParameter.equalsIgnoreCase("sh")) { // Starting Height
                    Integer startHeight = NumericParser.parseInteger(parameters[1]);
                    if (startHeight != null) {
                        this.startHeight = startHeight;
                        messenger.sendMessage(ChatColor.BLUE + "Starting Height set to: " + this.startHeight);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("rl")) { // Root Length
                    Integer rootLength = NumericParser.parseInteger(parameters[1]);
                    if (rootLength != null) {
                        this.rootLength = rootLength;
                        messenger.sendMessage(ChatColor.BLUE + "Root Length set to: " + this.rootLength);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("ts")) { // Trunk Slope Chance
                    Integer slopeChance = NumericParser.parseInteger(parameters[1]);
                    if (slopeChance != null && slopeChance >= 0 && slopeChance <= 100) {
                        this.slopeChance = slopeChance;
                        messenger.sendMessage(ChatColor.BLUE + "Trunk Slope set to: " + this.slopeChance);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("bl")) { // Branch Length
                    Integer branchLenght = NumericParser.parseInteger(parameters[1]);
                    if (branchLenght != null) {
                        this.branchLength = branchLenght;
                        messenger.sendMessage(ChatColor.BLUE + "Branch Length set to: " + this.branchLength);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("minr")) { // Minimum Roots
                    Integer minRoots = NumericParser.parseInteger(parameters[1]);
                    if (minRoots != null) {
                        this.minRoots = minRoots;
                        if (this.minRoots > this.maxRoots) {
                            this.minRoots = this.maxRoots;
                            messenger.sendMessage(ChatColor.RED + "Minimum Roots can't exceed Maximum Roots, has been set to: " + this.minRoots + " instead!");
                        } else {
                            messenger.sendMessage(ChatColor.BLUE + "Minimum Roots set to: " + this.minRoots);
                        }
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("maxr")) { // Maximum Roots
                    Integer maxRoots = NumericParser.parseInteger(parameters[1]);
                    if (maxRoots != null) {
                        this.maxRoots = maxRoots;
                        if (this.minRoots > this.maxRoots) {
                            this.maxRoots = this.minRoots;
                            messenger.sendMessage(ChatColor.RED + "Maximum Roots can't be lower than Minimum Roots, has been set to: " + this.minRoots + " Instead!");
                        } else {
                            messenger.sendMessage(ChatColor.BLUE + "Maximum Roots set to: " + this.maxRoots);
                        }
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("minh")) { // Height Minimum
                    Integer heightMinimum = NumericParser.parseInteger(parameters[1]);
                    if (heightMinimum != null) {
                        this.heightMin = heightMinimum;
                        if (this.heightMin > this.heightMax) {
                            this.heightMin = this.heightMax;
                            messenger.sendMessage(ChatColor.RED + "Minimum Height exceed than Maximum Height, has been set to: " + this.heightMin + " Instead!");
                        } else {
                            messenger.sendMessage(ChatColor.BLUE + "Minimum Height set to: " + this.heightMin);
                        }
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("maxh")) { // Height Maximum
                    Integer heightMaximum = NumericParser.parseInteger(parameters[1]);
                    if (heightMaximum != null) {
                        this.heightMax = heightMaximum;
                        if (this.heightMin > this.heightMax) {
                            this.heightMax = this.heightMin;
                            messenger.sendMessage(ChatColor.RED + "Maximum Height can't be lower than Minimum Height, has been set to: " + this.heightMax + " Instead!");
                        } else {
                            messenger.sendMessage(ChatColor.BLUE + "Maximum Roots set to: " + this.heightMax);
                        }
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("minl")) { // Leaf Node Min Size
                    Integer nodeMin = NumericParser.parseInteger(parameters[1]);
                    if (nodeMin != null) {
                        this.nodeMin = nodeMin;
                        messenger.sendMessage(ChatColor.BLUE + "Leaf Min Thickness set to: " + this.nodeMin);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
                    }
                } else if (firstParameter.equalsIgnoreCase("maxl")) { // Leaf Node Max Size
                    Integer nodeMax = NumericParser.parseInteger(parameters[1]);
                    if (nodeMax != null) {
                        this.nodeMax = nodeMax;
                        messenger.sendMessage(ChatColor.BLUE + "Leaf Max Thickness set to: " + this.nodeMax);
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid number: " + parameters[1]);
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
            return super.sortCompletions(Stream.of(
                    "lt", "wt", "tt", "rf", "sh", "rl", "ts", "bl",
                    "minr", "maxr", "minh", "maxh", "minl", "maxl", "default"
            ), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (firstParameter.equalsIgnoreCase("rf")) {
                String parameter = parameters[1];
                return super.sortCompletions(Stream.of("true", "false"), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
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
                setBlock(x, clampY(y), z, this.leafType);
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
                .send();
    }

}
