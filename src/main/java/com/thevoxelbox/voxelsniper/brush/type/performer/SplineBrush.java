package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * FOR ANY BRUSH THAT USES A SPLINE, EXTEND THAT BRUSH FROM THIS BRUSH!!! That way, the spline calculations are already there. Also, the UI for
 * the splines will be included.
 */
public class SplineBrush extends AbstractPerformerBrush {

    private final List<BlockVector3> endPts = new ArrayList<>();
    private final List<BlockVector3> ctrlPts = new ArrayList<>();
    private final List<Point> spline = new ArrayList<>();
    private boolean set;
    private boolean ctrl;

    @Override
    public void loadProperties() {
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.spline.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("ss")) {
                    if (this.set) {
                        this.set = false;
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.spline.set-endpoint",
                                VoxelSniperText.getStatus(false)
                        ));
                    } else {
                        this.set = true;
                        this.ctrl = false;
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.spline.set-endpoint",
                                VoxelSniperText.getStatus(true)
                        ));
                    }
                } else if (firstParameter.equalsIgnoreCase("sc")) {
                    if (this.ctrl) {
                        this.ctrl = false;
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.spline.set-control-point",
                                VoxelSniperText.getStatus(false)
                        ));
                    } else {
                        this.set = false;
                        this.ctrl = true;
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.spline.set-control-point",
                                VoxelSniperText.getStatus(true)
                        ));
                    }
                } else if (firstParameter.equalsIgnoreCase("clear")) {
                    clear(snipe);
                } else if (firstParameter.equalsIgnoreCase("ren")) {
                    if (this.endPts.size() == 2 && this.ctrlPts.size() == 2) {
                        if (spline(
                                new Point(this.endPts.get(0)),
                                new Point(this.endPts.get(1)),
                                new Point(this.ctrlPts.get(0)),
                                new Point(this.ctrlPts.get(1)),
                                snipe
                        )) {
                            render();
                        }
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.spline.missing-points"));
                    }
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
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
            return super.sortCompletions(Stream.of("ss", "sc", "clear", "ren"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (this.set) {
            removeFromSet(snipe, true, targetBlock);
        } else if (this.ctrl) {
            removeFromSet(snipe, false, targetBlock);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (this.set) {
            addToSet(snipe, true, targetBlock);
        }
        if (this.ctrl) {
            addToSet(snipe, false, targetBlock);
        }
    }

    private void addToSet(Snipe snipe, boolean ep, BlockVector3 targetBlock) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (ep) {
            if (this.endPts.contains(targetBlock) || this.endPts.size() == 2) {
                return;
            }
            this.endPts.add(targetBlock);
            messenger.sendMessage(Caption.of(
                    "voxelsniper.performer-brush.spline.add-endpoint",
                    targetBlock.getX(),
                    targetBlock.getY(),
                    targetBlock.getZ()
            ));
            return;
        }
        if (this.ctrlPts.contains(targetBlock) || this.ctrlPts.size() == 2) {
            return;
        }
        this.ctrlPts.add(targetBlock);
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.spline.add-control-point",
                targetBlock.getX(),
                targetBlock.getY(),
                targetBlock.getZ()
        ));
    }

    private void removeFromSet(Snipe snipe, boolean ep, BlockVector3 targetBlock) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (ep) {
            if (!this.endPts.contains(targetBlock)) {
                messenger.sendMessage(Caption.of("voxelsniper.performer-brush.spline.not-in-endpoint"));
                return;
            }
            this.endPts.add(targetBlock);
            messenger.sendMessage(Caption.of(
                    "voxelsniper.performer-brush.spline.remove-endpoint",
                    targetBlock.getX(),
                    targetBlock.getY(),
                    targetBlock.getZ()
            ));
            return;
        }
        if (!this.ctrlPts.contains(targetBlock)) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.spline.not-in-control-point"));
            return;
        }
        this.ctrlPts.remove(targetBlock);
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.spline.remove-control-point",
                targetBlock.getX(),
                targetBlock.getY(),
                targetBlock.getZ()
        ));
    }

    private boolean spline(Point start, Point end, Point c1, Point c2, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        this.spline.clear();
        try {
            Point c = c1.subtract(start)
                    .multiply(3);
            Point b = c2.subtract(c1)
                    .multiply(3)
                    .subtract(c);
            Point a = end.subtract(start)
                    .subtract(c)
                    .subtract(b);
            for (double t = 0.0; t < 1.0; t += 0.01) {
                int px = (int) Math.round(a.getX() * (t * t * t) + b.getX() * (t * t) + c.getX() * t + this.endPts.get(0)
                        .getX());
                int py = (int) Math.round(a.getY() * (t * t * t) + b.getY() * (t * t) + c.getY() * t + this.endPts.get(0)
                        .getY());
                int pz = (int) Math.round(a.getZ() * (t * t * t) + b.getZ() * (t * t) + c.getZ() * t + this.endPts.get(0)
                        .getZ());
                Point point = new Point(px, py, pz);
                if (!this.spline.contains(point)) {
                    this.spline.add(point);
                }
            }
            return true;
        } catch (RuntimeException exception) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.spline.not-enough-points", this.endPts.size(),
                    this.ctrlPts.size()
            ));
            return false;
        }
    }

    private void render() {
        if (this.spline.isEmpty()) {
            return;
        }
        for (Point point : this.spline) {
            this.performer.perform(
                    getEditSession(),
                    point.getX(),
                    clampY(point.getY()),
                    point.getZ(),
                    clampY(point.getX(), point.getY(), point.getZ())
            );
        }
    }

    private void clear(Snipe snipe) {
        this.spline.clear();
        this.ctrlPts.clear();
        this.endPts.clear();
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.spline.cleared"));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.spline.set-endpoint",
                        VoxelSniperText.getStatus(this.set)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.spline.set-control-point",
                        VoxelSniperText.getStatus(this.ctrl)
                ))
                .send();
    }

    // Vector class for splines
    private static final class Point {

        private int x;
        private int y;
        private int z;

        private Point(BlockVector3 block) {
            this.x = block.getX();
            this.y = block.getY();
            this.z = block.getZ();
        }

        private Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point add(Point point) {
            return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
        }

        public Point multiply(int scalar) {
            return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
        }

        public Point subtract(Point point) {
            return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
        }

        public int getX() {
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return this.z;
        }

        public void setZ(int z) {
            this.z = z;
        }

    }

}
