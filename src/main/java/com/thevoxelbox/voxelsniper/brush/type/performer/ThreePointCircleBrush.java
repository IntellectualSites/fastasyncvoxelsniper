package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.Vectors;
import org.bukkit.ChatColor;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreePointCircleBrush extends AbstractPerformerBrush {

    private static final Tolerance DEFAULT_TOLERANCE = Tolerance.DEFAULT;

    private static final List<String> TOLERANCES = Arrays.stream(Tolerance.values())
            .map(Tolerance::getName)
            .toList();

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

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.DARK_AQUA + "3-Point Circle Brush instructions: Select three corners with the arrow " +
                    "brush, then generate the Circle with the gunpowder brush.");
            messenger.sendMessage(ChatColor.GOLD + "3-Point Circle Parameters");
            messenger.sendMessage(ChatColor.AQUA + "/b tpc [t] -- Sets the calculations to emphasize accuracy or smoothness " +
                    "to t.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(
                            Arrays.stream(Tolerance.values())
                                    .map(tolerance -> ((tolerance == this.tolerance) ? ChatColor.GOLD : ChatColor.GRAY) +
                                            tolerance.name().toLowerCase(Locale.ROOT))
                                    .collect(Collectors.joining(ChatColor.WHITE + ", ",
                                            ChatColor.AQUA + "Available tolerances: ", ""
                                    ))
                    );
                } else {
                    try {
                        this.tolerance = Tolerance.valueOf(firstParameter.toUpperCase(Locale.ROOT));
                        messenger.sendMessage(ChatColor.AQUA + "Brush set to: " + this.tolerance.getName()
                                .toLowerCase(Locale.ROOT) + " tolerance.");
                    } catch (IllegalArgumentException exception) {
                        messenger.sendMessage(ChatColor.RED + "Invalid tolerance:" + firstParameter);
                    }
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display " +
                        "parameter info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(
                    Stream.concat(
                            TOLERANCES.stream(),
                            Stream.of("list")
                    ), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.coordinatesOne == null) {
            this.coordinatesOne = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "First Corner set.");
        } else if (this.coordinatesTwo == null) {
            this.coordinatesTwo = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "Second Corner set.");
        } else if (this.coordinatesThree == null) {
            this.coordinatesThree = Vectors.toBukkit(targetBlock);
            messenger.sendMessage(ChatColor.GRAY + "Third Corner set.");
        } else {
            this.coordinatesOne = Vectors.toBukkit(targetBlock);
            this.coordinatesTwo = null;
            this.coordinatesThree = null;
            messenger.sendMessage(ChatColor.GRAY + "First Corner set.");
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
            messenger.sendMessage(ChatColor.RED + "Invalid points: " + this.coordinatesOne + ", " + this.coordinatesTwo + ", " + this.coordinatesThree);
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
        messenger.sendMessage(ChatColor.GREEN + "Done.");
        // Reset Brush
        this.coordinatesOne = null;
        this.coordinatesTwo = null;
        this.coordinatesThree = null;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GOLD + "Mode: " + this.tolerance.getName())
                .send();
    }

    private enum Tolerance {

        DEFAULT("Default", 1000),
        ACCURATE("Accurate", 10),
        SMOOTH("Smooth", 2000);

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
    }

}
