package com.thevoxelbox.voxelsniper.brush.type.performer;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@RequireToolkit
@Command(value = "brush|b three_point_circle|threepointcircle|tpc")
@Permission("voxelsniper.brush.threepointcircle")
public class ThreePointCircleBrush extends AbstractPerformerBrush {

    private static final Tolerance DEFAULT_TOLERANCE = Tolerance.DEFAULT;

    @Nullable
    private Vector coordinatesOne;
    @Nullable
    private Vector coordinatesTwo;
    @Nullable
    private Vector coordinatesThree;

    private Tolerance tolerance;

    @Override
    public void loadProperties() {
        this.tolerance = (Tolerance) getEnumProperty("default-tolerance", Tolerance.class, DEFAULT_TOLERANCE);
    }

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.three-point-circle.info"));
    }

    @Command("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                Arrays.stream(Tolerance.values()).toList(),
                (tolerance, tolerance2) -> tolerance.getName().compareTo(tolerance2.getName()),
                Tolerance::getFullName,
                tolerance -> tolerance,
                this.tolerance,
                "voxelsniper.brush.three-point-circle"
        ));
    }

    @Command("<tolerance>")
    public void onBrushTolerance(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("tolerance") Tolerance tolerance
    ) {
        this.tolerance = tolerance;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.three-point-circle.set-tolerance",
                this.tolerance.getFullName()
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.coordinatesOne == null) {
            this.coordinatesOne = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-corner"));
        } else if (this.coordinatesTwo == null) {
            this.coordinatesTwo = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-corner"));
        } else if (this.coordinatesThree == null) {
            this.coordinatesThree = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.third-corner"));
        } else {
            this.coordinatesOne = Vectors.toBukkit(targetBlock);
            this.coordinatesTwo = null;
            this.coordinatesThree = null;
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-corner"));
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        if (this.coordinatesOne == null || this.coordinatesTwo == null || this.coordinatesThree == null) {
            return;
        }
        // Calculate triangle defining vectors
        Vector vectorOne = this.coordinatesTwo.clone();
        vectorOne.subtract(this.coordinatesOne);
        Vector vectorTwo = this.coordinatesThree.clone();
        vectorTwo.subtract(this.coordinatesOne);
        Vector vectorThree = this.coordinatesThree.clone();
        vectorThree.subtract(vectorTwo);
        SnipeMessenger messenger = snipe.createMessenger();
        // Redundant data check
        if (vectorOne.length() == 0 || vectorTwo.length() == 0 || vectorThree.length() == 0 || vectorOne.angle(vectorTwo) == 0 || vectorOne
                .angle(vectorThree) == 0 || vectorThree.angle(vectorTwo) == 0) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.three-point-circle.invalid-points",
                    this.coordinatesOne, this.coordinatesTwo, this.coordinatesThree
            ));
            this.coordinatesOne = null;
            this.coordinatesTwo = null;
            this.coordinatesThree = null;
            return;
        }
        // Calculate normal vector of the plane.
        Vector normalVector = vectorOne.clone();
        normalVector.crossProduct(vectorTwo);
        // Calculate constant term of the plane.
        double planeConstant = normalVector.getX() * this.coordinatesOne.getX() + normalVector.getY() * this.coordinatesOne.getY() + normalVector
                .getZ() * this.coordinatesOne.getZ();
        Vector midpointOne = this.coordinatesOne.getMidpoint(this.coordinatesTwo);
        Vector midpointTwo = this.coordinatesOne.getMidpoint(this.coordinatesThree);
        // Find perpendicular vectors to two sides in the plane
        Vector perpendicularOne = normalVector.clone();
        perpendicularOne.crossProduct(vectorOne);
        Vector perpendicularTwo = normalVector.clone();
        perpendicularTwo.crossProduct(vectorTwo);
        // determine value of parametric variable at intersection of two perpendicular bisectors
        Vector tNumerator = midpointTwo.clone();
        tNumerator.subtract(midpointOne);
        tNumerator.crossProduct(perpendicularTwo);
        Vector tDenominator = perpendicularOne.clone();
        tDenominator.crossProduct(perpendicularTwo);
        double t = tNumerator.length() / tDenominator.length();
        // Calculate Circumcenter and Brushcenter.
        Vector circumcenter = new Vector();
        circumcenter.copy(perpendicularOne);
        circumcenter.multiply(t);
        circumcenter.add(midpointOne);
        Vector brushCenter = new Vector(
                Math.round(circumcenter.getX()),
                Math.round(circumcenter.getY()),
                Math.round(circumcenter.getZ())
        );
        // Calculate radius of circumcircle and determine brushsize
        double radius = circumcenter.distance(new Vector(
                this.coordinatesOne.getX(),
                this.coordinatesOne.getY(),
                this.coordinatesOne.getZ()
        ));
        int brushSize = NumberConversions.ceil(radius) + 1;
        for (int x = -brushSize; x <= brushSize; x++) {
            for (int y = -brushSize; y <= brushSize; y++) {
                for (int z = -brushSize; z <= brushSize; z++) {
                    // Calculate distance from center
                    double tempDistance = Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), 0.5);
                    // gets corner-on blocks
                    double cornerConstant = normalVector.getX() * (circumcenter.getX() + x) + normalVector.getY() * (circumcenter.getY() + y) + normalVector
                            .getZ() * (circumcenter.getZ() + z);
                    // gets center-on blocks
                    double centerConstant = normalVector.getX() * (circumcenter.getX() + x + 0.5) + normalVector.getY() * (circumcenter
                            .getY() + y + 0.5) + normalVector.getZ() * (circumcenter.getZ() + z + 0.5);
                    // Check if point is within sphere and on plane (some tolerance given)
                    if (tempDistance <= radius && (Math.abs(cornerConstant - planeConstant) < this.tolerance.getValue() || Math.abs(
                            centerConstant - planeConstant) < this.tolerance.getValue())) {
                        this.performer.perform(
                                getEditSession(),
                                brushCenter.getBlockX() + x,
                                clampY(brushCenter.getBlockY() + y),
                                brushCenter.getBlockZ() + z,
                                this.clampY(brushCenter.getBlockX() + x, brushCenter.getBlockY() + y, brushCenter.getBlockZ() + z)
                        );
                    }
                }
            }
        }
        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.three-point-circle.done"));
        // Reset Brush
        this.coordinatesOne = null;
        this.coordinatesTwo = null;
        this.coordinatesThree = null;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.three-point-circle.set-tolerance",
                        this.tolerance.getFullName()
                ))
                .send();
    }

    public enum Tolerance {

        DEFAULT("default", 1000),
        ACCURATE("accurate", 10),
        SMOOTH("smooth", 2000);

        private final String name;
        private final int value;

        Tolerance(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return this.value;
        }

        public TranslatableComponent getFullName() {
            return Caption.of("voxelsniper.performer-brush.three-point-circle.tolerance." + this.name);
        }
    }

}
