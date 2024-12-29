package com.thevoxelbox.voxelsniper.brush.type.rotation;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
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

// The X Y and Z variable names in this file do NOT MAKE ANY SENSE. Do not attempt to actually figure out what on earth is going on here. Just go to the
// original 2d horizontal brush if you wish to make anything similar to this, and start there. I didn't bother renaming everything.
@RequireToolkit
@CommandMethod(value = "brush|b rotation_2d_vert|rotation2dvert|rot2dv|rot2v")
@CommandPermission("voxelsniper.brush.rot2dvert")
public class Rotation2DVerticalBrush extends AbstractBrush {

    private static final int DEFAULT_ANGLE = 0;

    private int brushSize;
    private BlockState[][][] snap;

    private double angle = DEFAULT_ANGLE;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.rotation-2d-vertical.info",
                180, DECIMAL_FORMAT.format(Math.PI)
        ));
    }

    @CommandMethod("<degrees-angle>")
    public void onBrushDegreesangle(
            final @NotNull Snipe snipe,
            final @Argument("degrees-angle") @Range(min = "0", max = "360") int degreesAngle
    ) {
        this.angle = Math.toRadians(degreesAngle);

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.rotation-2d-vertical.set-angle",
                DECIMAL_FORMAT.format(this.angle),
                DECIMAL_FORMAT.format(degreesAngle)
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.brushSize = toolkitProperties.getBrushSize();
        this.getMatrix();
        this.rotate();
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.brushSize = toolkitProperties.getBrushSize();
        this.getMatrix();
        this.rotate();
    }

    private void getMatrix() {
        int brushSize = (this.brushSize * 2) + 1;
        this.snap = new BlockState[brushSize][brushSize][brushSize];
        BlockVector3 targetBlock = this.getTargetBlock();
        int sx = targetBlock.x() - this.brushSize;
        for (int x = 0; x < this.snap.length; x++) {
            int sz = targetBlock.z() - this.brushSize;
            for (int z = 0; z < this.snap.length; z++) {
                int sy = targetBlock.y() - this.brushSize;
                for (int y = 0; y < this.snap.length; y++) {
                    // why is this not sx + x, sy + y sz + z?
                    this.snap[x][z][y] = getBlock(sx, clampY(sy), sz);
                    setBlock(sx, clampY(sy), sz, BlockTypes.AIR);
                    sy++;
                    sy++;
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
        BlockVector3 targetBlock = this.getTargetBlock();
        for (int x = 0; x < this.snap.length; x++) {
            int xx = x - this.brushSize;
            double xSquared = Math.pow(xx, 2);
            for (int z = 0; z < this.snap.length; z++) {
                int zz = z - this.brushSize;
                if (xSquared + Math.pow(zz, 2) <= brushSizeSquared) {
                    double newX = (xx * cos) - (zz * sin);
                    double newZ = (xx * sin) + (zz * cos);
                    doNotFill[(int) newX + this.brushSize][(int) newZ + this.brushSize] = true;
                    for (int y = 0; y < this.snap.length; y++) {
                        int yy = y - this.brushSize;
                        BlockState blockData = this.snap[y][x][z];
                        BlockType type = blockData.getBlockType();
                        if (Materials.isEmpty(type)) {
                            continue;
                        }
                        setBlockData(
                                targetBlock.x() + yy,
                                targetBlock.y() + (int) newX,
                                targetBlock.z() + (int) newZ,
                                blockData
                        );
                    }
                }
            }
        }
        for (int x = 0; x < this.snap.length; x++) {
            double xSquared = Math.pow(x - this.brushSize, 2);
            int fx = x + targetBlock.x() - this.brushSize;
            for (int z = 0; z < this.snap.length; z++) {
                if (xSquared + Math.pow(z - this.brushSize, 2) <= brushSizeSquared) {
                    int fz = z + targetBlock.z() - this.brushSize;
                    if (!doNotFill[x][z]) {
                        // smart fill stuff
                        for (int y = 0; y < this.snap.length; y++) {
                            int fy = y + targetBlock.y() - this.brushSize;
                            BlockType a = getBlockType(fy, fx + 1, fz);
                            BlockType b = getBlockType(fy, fx, fz - 1);
                            BlockType c = getBlockType(fy, fx, fz + 1);
                            BlockType d = getBlockType(fy, fx - 1, fz);
                            BlockState aData = getBlock(fy, fx + 1, fz);
                            BlockState bData = getBlock(fy, fx, fz - 1);
                            BlockState dData = getBlock(fy, fx - 1, fz);
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
                            this.setBlockData(fy, fx, fz, winner);
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
                        "voxelsniper.brush.rotation-2d-vertical.set-angle",
                        DECIMAL_FORMAT.format(this.angle),
                        DECIMAL_FORMAT.format(Math.toDegrees(this.angle))
                ))
                .send();
    }

}
