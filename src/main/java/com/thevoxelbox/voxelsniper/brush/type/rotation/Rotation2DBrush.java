package com.thevoxelbox.voxelsniper.brush.type.rotation;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b rotation_2d|rotation2d|rot2d|rot2")
@Permission("voxelsniper.brush.rot2d")
public class Rotation2DBrush extends AbstractBrush {

    private static final int DEFAULT_ANGLE = 0;

    private int brushSize;
    private BlockState[][][] snap;

    private double angle = DEFAULT_ANGLE;

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Command("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.rotation-2d.info",
                180, DECIMAL_FORMAT.format(Math.PI)
        ));
    }

    @Command("<degrees-angle>")
    public void onBrushDegreesangle(
            final @NotNull Snipe snipe,
            final @Argument("degrees-angle") @Range(min = "0", max = "360") int degreesAngle
    ) {
        this.angle = Math.toRadians(degreesAngle);

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.rotation-2d.set-angle",
                DECIMAL_FORMAT.format(this.angle),
                DECIMAL_FORMAT.format(degreesAngle)
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.brushSize = toolkitProperties.getBrushSize();
        getMatrix();
        rotate();
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.brushSize = toolkitProperties.getBrushSize();
        getMatrix();
        rotate();
    }

    private void getMatrix() {
        int brushSize = (this.brushSize * 2) + 1;
        this.snap = new BlockState[brushSize][brushSize][brushSize];
        double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
        BlockVector3 targetBlock = this.getTargetBlock();
        int sx = targetBlock.getX() - this.brushSize;
        for (int x = 0; x < this.snap.length; x++) {
            int sz = targetBlock.getZ() - this.brushSize;
            double xSquared = Math.pow(x - this.brushSize, 2);
            for (int y = 0; y < this.snap.length; y++) {
                int sy = targetBlock.getY() - this.brushSize;
                if (xSquared + Math.pow(y - this.brushSize, 2) <= brushSizeSquared) {
                    for (int z = 0; z < this.snap.length; z++) {
                        // why is this not sx + x, sy + y sz + z?
                        this.snap[x][z][y] = getBlock(sx, clampY(sy), sz);
                        setBlock(sx, clampY(sy), sz, BlockTypes.AIR);
                        sy++;
                    }
                }
                sz++;
            }
            sx++;
        }
    }

    private void rotate() {
        double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
        double cos = Math.cos(this.angle);
        double sin = Math.sin(this.angle);
        boolean[][] doNotFill = new boolean[this.snap.length][this.snap.length];
        // I put y in the inside loop, since it doesn't have any power functions, should be much faster.
        // Also, new array keeps track of which x and z coords are being assigned in the rotated space so that we can
        // do a targeted filling of only those columns later that were left out.
        BlockVector3 targetBlock = getTargetBlock();
        for (int x = 0; x < this.snap.length; x++) {
            int xx = x - this.brushSize;
            double xSquared = Math.pow(xx, 2);
            for (int y = 0; y < this.snap.length; y++) {
                int zz = y - this.brushSize;
                if (xSquared + Math.pow(zz, 2) <= brushSizeSquared) {
                    double newX = (xx * cos) - (zz * sin);
                    double newZ = (xx * sin) + (zz * cos);
                    doNotFill[(int) newX + this.brushSize][(int) newZ + this.brushSize] = true;
                    for (int currentY = 0; currentY < this.snap.length; currentY++) {
                        int yy = currentY - this.brushSize;
                        BlockState blockData = this.snap[x][currentY][y];
                        BlockType type = blockData.getBlockType();
                        if (Materials.isEmpty(type)) {
                            continue;
                        }
                        setBlockData(
                                targetBlock.getX() + (int) newX,
                                targetBlock.getY() + yy,
                                targetBlock.getZ() + (int) newZ,
                                blockData
                        );
                    }
                }
            }
        }
        for (int x = 0; x < this.snap.length; x++) {
            double xSquared = Math.pow(x - this.brushSize, 2);
            int fx = x + targetBlock.getX() - this.brushSize;
            for (int z = 0; z < this.snap.length; z++) {
                if (xSquared + Math.pow(z - this.brushSize, 2) <= brushSizeSquared) {
                    int fz = z + targetBlock.getZ() - this.brushSize;
                    if (!doNotFill[x][z]) {
                        // smart fill stuff
                        for (int y = 0; y < this.snap.length; y++) {
                            int fy = y + targetBlock.getY() - this.brushSize;
                            BlockType a = getBlockType(fx + 1, fy, fz);
                            BlockType b = getBlockType(fx, fy, fz - 1);
                            BlockType c = getBlockType(fx, fy, fz + 1);
                            BlockType d = getBlockType(fx - 1, fy, fz);
                            BlockState aData = getBlock(fx + 1, fy, fz);
                            BlockState dData = getBlock(fx - 1, fy, fz);
                            BlockState bData = getBlock(fx, fy, fz - 1);
                            BlockState winner;
                            if (a == b || a == c || a == d) { // I figure that since we are already narrowing it down to ONLY the holes left behind, it
                                // should
                                // be fine to do all 5 checks needed to be legit about it.
                                winner = aData;
                            } else if (b == d || c == d) {
                                winner = dData;
                            } else {
                                winner = bData; // blockPositionY making this default, it will also automatically cover situations where B = C;
                            }
                            setBlockData(fx, fy, fz, winner);
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
                .message(Caption.of(
                        "voxelsniper.brush.rotation-2d.set-angle",
                        DECIMAL_FORMAT.format(this.angle),
                        DECIMAL_FORMAT.format(Math.toDegrees(this.angle))
                ))
                .send();
    }

}
