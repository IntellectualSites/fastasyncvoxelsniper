package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@RequireToolkit
@CommandMethod(value = "brush|b pull")
@CommandPermission("voxelsniper.brush.pull")
public class PullBrush extends AbstractBrush {

    private static final int DEFAULT_PINCH = 1;
    private static final int DEFAULT_BUBBLE = 0;

    private final Set<PullBrushBlockWrapper> surface = new HashSet<>();
    private int voxelHeight;

    private double pinch;
    private double bubble;

    @Override
    public void loadProperties() {
        this.pinch = getIntegerProperty("default-pinch", DEFAULT_PINCH);
        this.bubble = getIntegerProperty("default-bubble", DEFAULT_BUBBLE);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.pull.info"));
    }

    @CommandMethod("<pinch> <bubble>")
    public void onBrushPinchbubble(
            final @NotNull Snipe snipe,
            final @Argument("pinch") double pinch,
            final @Argument("bubble") double bubble
    ) {
        this.pinch = 1 - pinch;
        this.bubble = bubble;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.pull.set-pinch",
                this.pinch
        ));
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.pull.set-bubble",
                this.bubble
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.voxelHeight = toolkitProperties.getVoxelHeight();
        getSurface(toolkitProperties);
        if (this.voxelHeight > 0) {
            for (PullBrushBlockWrapper block : this.surface) {
                setBlock(block);
            }
        } else if (this.voxelHeight < 0) {
            for (PullBrushBlockWrapper block : this.surface) {
                setBlockDown(block);
            }
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.voxelHeight = toolkitProperties.getVoxelHeight();
        this.surface.clear();
        int lastY;
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        // Are we pulling up ?
        BlockVector3 targetBlock = getTargetBlock();
        if (this.voxelHeight > 0) {
            // Z - Axis
            for (int z = -brushSize; z <= brushSize; z++) {
                int zSquared = z * z;
                int actualZ = targetBlock.z() + z;
                // X - Axis
                for (int x = -brushSize; x <= brushSize; x++) {
                    int xSquared = x * x;
                    int actualX = targetBlock.x() + x;
                    // Down the Y - Axis
                    for (int y = brushSize; y >= -brushSize; y--) {
                        double volume = zSquared + xSquared + (y * y);
                        // Is this in the range of the brush?
                        if (volume <= brushSizeSquared && !getBlock(actualX, targetBlock.y() + y, actualZ).isAir()) {
                            int actualY = targetBlock.y() + y;
                            // Starting strength and new Position
                            double str = this.getStr(volume / brushSizeSquared);
                            int lastStr = (int) (this.voxelHeight * str);
                            lastY = actualY + lastStr;
                            setBlock(actualX, clampY(lastY), actualZ,
                                    getBlockType(actualX, actualY, actualZ)
                            );
                            if (Double.compare(str, 1.0) == 0) {
                                str = 0.8;
                            }
                            while (lastStr > 0) {
                                if (actualY < targetBlock.y()) {
                                    str *= str;
                                }
                                lastStr = (int) (this.voxelHeight * str);
                                int newY = actualY + lastStr;
                                BlockType blockType = getBlockType(actualX, actualY, actualZ);
                                for (int i = newY; i < lastY; i++) {
                                    setBlock(actualX, clampY(i), actualZ, blockType);
                                }
                                lastY = newY;
                                actualY--;
                            }
                            break;
                        }
                    }
                }
            }
        } else {
            for (int z = -brushSize; z <= brushSize; z++) {
                double zSquared = Math.pow(z, 2);
                int actualZ = targetBlock.z() + z;
                for (int x = -brushSize; x <= brushSize; x++) {
                    double xSquared = Math.pow(x, 2);
                    int actualX = targetBlock.x() + x;
                    for (int y = -brushSize; y <= brushSize; y++) {
                        double volume = (xSquared + Math.pow(y, 2) + zSquared);
                        if (volume <= brushSizeSquared && !getBlock(actualX, targetBlock.y() + y, actualZ).isAir()) {
                            int actualY = targetBlock.y() + y;
                            lastY = actualY + (int) (this.voxelHeight * this.getStr(volume / brushSizeSquared));
                            setBlock(actualX, clampY(lastY), actualZ,
                                    getBlockType(actualX, actualY, actualZ)
                            );
                            y++;
                            double volume2 = (xSquared + Math.pow(y, 2) + zSquared);
                            while (volume2 <= brushSizeSquared) {
                                int blockY = targetBlock.y() + y + (int) (this.voxelHeight * this.getStr(volume2 / brushSizeSquared));
                                BlockType blockType = getBlockType(actualX, targetBlock.y() + y, actualZ);
                                for (int i = blockY; i < lastY; i++) {
                                    setBlock(actualX, clampY(i), actualZ, blockType);
                                }
                                lastY = blockY;
                                y++;
                                volume2 = (xSquared + Math.pow(y, 2) + zSquared);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private double getStr(double t) {
        double lt = 1 - t;
        return (lt * lt * lt) + 3 * (lt * lt) * t * this.pinch + 3 * lt * (t * t) * this.bubble; // My + (t * ((By + (t * ((c2 + (t * (0 - c2))) - By))) - My));
    }

    private void getSurface(ToolkitProperties toolkitProperties) {
        this.surface.clear();
        int brushSize = toolkitProperties.getBrushSize();
        double bSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = -brushSize; z <= brushSize; z++) {
            double zSquared = Math.pow(z, 2);
            BlockVector3 targetBlock = getTargetBlock();
            int actualZ = targetBlock.z() + z;
            for (int x = -brushSize; x <= brushSize; x++) {
                double xSquared = Math.pow(x, 2);
                int actualX = targetBlock.x() + x;
                for (int y = -brushSize; y <= brushSize; y++) {
                    double volume = (xSquared + Math.pow(y, 2) + zSquared);
                    if (volume <= bSquared) {
                        if (this.isSurface(actualX, targetBlock.y() + y, actualZ)) {
                            this.surface.add(new PullBrushBlockWrapper(
                                    actualX,
                                    clampY(targetBlock.y() + y),
                                    actualZ,
                                    this.clampY(actualX, targetBlock.y() + y, actualZ),
                                    this.getStr(volume / bSquared)
                            ));
                        }
                    }
                }
            }
        }
    }

    private boolean isSurface(int x, int y, int z) {
        return !isEmpty(x, y, z) && (isEmpty(x, y - 1, z) || isEmpty(x, y + 1, z) || isEmpty(x + 1, y, z) || isEmpty(
                x - 1,
                y,
                z
        ) || isEmpty(x, y, z + 1) || isEmpty(x, y, z - 1));
    }

    private boolean isEmpty(int x, int y, int i) {
        return getBlock(x, y, i).isAir();
    }

    private void setBlock(PullBrushBlockWrapper block) {
        int blockY = clampY(block.getY() + (int) (this.voxelHeight * block.getStr()));
        if (Materials.isEmpty(getBlockType(block.getX(), block.getY() - 1, block.getZ()))) {
            setBlockData(block.getX(), blockY, block.getZ(), block.getBlockData());
            for (int y = block.getY(); y < blockY; y++) {
                setBlock(block.getX(), y, block.getZ(), BlockTypes.AIR);
            }
        } else {
            setBlockData(block.getX(), blockY, block.getZ(), block.getBlockData());
            for (int y = block.getY() - 1; y < blockY; y++) {
                setBlockData(block.getX(), clampY(y), block.getZ(), block.getBlockData());
            }
        }
    }

    private void setBlockDown(PullBrushBlockWrapper block) {
        int blockY = clampY(block.getY() + (int) (this.voxelHeight * block.getStr()));
        setBlockData(block.getX(), blockY, block.getZ(), block.getBlockData());
        for (int y = block.getY(); y > blockY; y--) {
            this.setBlock(block.getX(), y, block.getZ(), BlockTypes.AIR);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .voxelHeightMessage()
                .message(Caption.of(
                        "voxelsniper.brush.pull.set-pinch",
                        -this.pinch + 1
                ))
                .message(Caption.of(
                        "voxelsniper.brush.pull.set-bubble",
                        this.bubble
                ))
                .send();
    }

    private static final class PullBrushBlockWrapper {

        private final int x;
        private final int y;
        private final int z;
        private final BlockState blockData;
        private final double str;

        private PullBrushBlockWrapper(int x, int y, int z, BlockState block, double str) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockData = block;
            this.str = str;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public BlockState getBlockData() {
            return this.blockData;
        }

        public double getStr() {
            return this.str;
        }

    }

}
