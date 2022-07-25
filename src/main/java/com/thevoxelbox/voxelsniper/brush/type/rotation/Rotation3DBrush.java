package com.thevoxelbox.voxelsniper.brush.type.rotation;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class Rotation3DBrush extends AbstractBrush {

    private static final int DEFAULT_SE_YAW = 0;
    private static final int DEFAULT_SE_PITCH = 0;
    private static final int DEFAULT_SE_ROLL = 0;

    private int brushSize;
    private BlockState[][][] snap;

    private double seYaw = DEFAULT_SE_YAW;
    private double sePitch = DEFAULT_SE_PITCH;
    private double seRoll = DEFAULT_SE_ROLL;

    // after all rotations, compare snapshot to new state of world?
    // --> agreed. Do what erode does and store one snapshot with Block pointers and int id of what the block started with, afterwards simply go thru that
    // matrix and compare Block.getId with 'id'
    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        // which way is clockwise is less obvious for roll and pitch... should probably fix that / make it clear
        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.rotation-3d.info", 180, DECIMAL_FORMAT.format(Math.PI)));
        } else {
            if (parameters.length == 2) {
                Double degreesAngle = NumericParser.parseDouble(parameters[1]);

                if (degreesAngle != null && degreesAngle >= 0 && degreesAngle <= 359) {
                    if (firstParameter.equalsIgnoreCase("p")) {
                        this.sePitch = Math.toRadians(degreesAngle);
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.brush.rotation-3d.set-z-angle",
                                DECIMAL_FORMAT.format(this.sePitch),
                                DECIMAL_FORMAT.format(degreesAngle)
                        ));
                    } else if (firstParameter.equalsIgnoreCase("r")) {
                        this.seRoll = Math.toRadians(degreesAngle);
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.brush.rotation-3d.set-x-angle",
                                DECIMAL_FORMAT.format(this.seRoll),
                                DECIMAL_FORMAT.format(degreesAngle)
                        ));
                    } else if (firstParameter.equalsIgnoreCase("y")) {
                        this.seYaw = Math.toRadians(degreesAngle);
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.brush.rotation-3d.set-y-angle",
                                DECIMAL_FORMAT.format(this.seYaw),
                                DECIMAL_FORMAT.format(degreesAngle)
                        ));
                    }
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number-between", firstParameter, 1, 359));
                }
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("p", "r", "y"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
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

    private void getMatrix() { // only need to do once. But y needs to change + sphere
        double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
        int brushSize = (this.brushSize * 2) + 1;
        this.snap = new BlockState[brushSize][brushSize][brushSize];
        BlockVector3 targetBlock = this.getTargetBlock();
        int sx = targetBlock.getX() - this.brushSize;
        //int sy = this.getTargetBlock().getY() - this.brushSize; Not used
        for (int x = 0; x < this.snap.length; x++) {
            double xSquared = Math.pow(x - this.brushSize, 2);
            for (int z = 0; z < this.snap.length; z++) {
                double zSquared = Math.pow(z - this.brushSize, 2);
                int sz = targetBlock.getZ() - this.brushSize;
                for (int y = 0; y < this.snap.length; y++) {
                    if (xSquared + zSquared + Math.pow(y - this.brushSize, 2) <= brushSizeSquared) {
                        this.snap[x][y][z] = getBlock(sx, clampY(sz), sz);
                        setBlock(sx, clampY(sz), sz, BlockTypes.AIR);
                        sz++;
                    }
                }
            }
            sx++;
        }
    }

    private void rotate() {
        // basically 1) make it a sphere we are rotating in, not a cylinder
        // 2) do three rotations in a row, one in each dimension, unless some dimensions are set to zero or undefined or
        // whatever, then skip those.
        // --> Why not utilize Sniper'world new opportunities and have arrow rotate all 3, gunpowder rotate x, goldsisc y, otherdisc z. Or something like that. Or
        // we
        // could just use arrow and gunpowder and just differentiate between left and right click that gis 4 different situations
        // --> Well, there would be 7 different possibilities... X, Y, Z, XY, XZ, YZ, XYZ, and different numbers of parameters for each, so I think each having
        // and item is too confusing. How about this: arrow = rotate one dimension, based on the face you click, and takes 1 param... gunpowder: rotates all three
        // at once, and takes 3 params.
        double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
        double cosYaw = Math.cos(this.seYaw);
        double sinYaw = Math.sin(this.seYaw);
        double cosPitch = Math.cos(this.sePitch);
        double sinPitch = Math.sin(this.sePitch);
        double cosRoll = Math.cos(this.seRoll);
        double sinRoll = Math.sin(this.seRoll);
        boolean[][][] doNotFill = new boolean[this.snap.length][this.snap.length][this.snap.length];
        BlockVector3 targetBlock = this.getTargetBlock();
        for (int x = 0; x < this.snap.length; x++) {
            int xx = x - this.brushSize;
            double xSquared = Math.pow(xx, 2);
            for (int z = 0; z < this.snap.length; z++) {
                int zz = z - this.brushSize;
                double zSquared = Math.pow(zz, 2);
                double newxzX = (xx * cosYaw) - (zz * sinYaw);
                double newxzZ = (xx * sinYaw) + (zz * cosYaw);
                for (int y = 0; y < this.snap.length; y++) {
                    int yy = y - this.brushSize;
                    if (xSquared + zSquared + Math.pow(yy, 2) <= brushSizeSquared) {
                        double newxyX = (newxzX * cosPitch) - (yy * sinPitch);
                        double newxyY = (newxzX * sinPitch) + (yy * cosPitch); // calculates all three in succession in precise math space
                        double newyzY = (newxyY * cosRoll) - (newxzZ * sinRoll);
                        double newyzZ = (newxyY * sinRoll) + (newxzZ * cosRoll);
                        doNotFill[(int) newxyX + this.brushSize][(int) newyzY + this.brushSize][(int) newyzZ + this.brushSize] = true; // only rounds off to nearest
                        // block
                        // after all three, though.
                        BlockState blockData = this.snap[x][y][z];
                        BlockType type = blockData.getBlockType();
                        if (Materials.isEmpty(type)) {
                            continue;
                        }
                        this.setBlockData(
                                targetBlock.getX() + (int) newxyX,
                                targetBlock.getY() + (int) newyzY,
                                targetBlock.getZ() + (int) newyzZ,
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
                double zSquared = Math.pow(z - this.brushSize, 2);
                int fz = z + targetBlock.getZ() - this.brushSize;
                for (int y = 0; y < this.snap.length; y++) {
                    if (xSquared + zSquared + Math.pow(y - this.brushSize, 2) <= brushSizeSquared) {
                        if (!doNotFill[x][y][z]) {
                            // smart fill stuff
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
                        "voxelsniper.brush.rotation-3d.set-z-angle",
                        DECIMAL_FORMAT.format(this.sePitch),
                        DECIMAL_FORMAT.format(Math.toDegrees(this.sePitch))
                ))
                .message(Caption.of(
                        "voxelsniper.brush.rotation-3d.set-x-angle",
                        DECIMAL_FORMAT.format(this.seRoll),
                        DECIMAL_FORMAT.format(Math.toDegrees(this.seRoll))
                ))
                .message(Caption.of(
                        "voxelsniper.brush.rotation-3d.set-y-angle",
                        DECIMAL_FORMAT.format(this.seYaw),
                        DECIMAL_FORMAT.format(Math.toDegrees(this.seYaw))
                ))
                .send();
    }

}
