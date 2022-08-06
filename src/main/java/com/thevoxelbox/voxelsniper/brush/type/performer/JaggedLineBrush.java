package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class JaggedLineBrush extends AbstractPerformerBrush {

    private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);

    private static final int RECURSION_MIN = 1;
    private static final int RECURSION_MAX = 10;

    private static final int DEFAULT_RECURSION = 3;
    private static final int DEFAULT_SPREAD = 3;

    private final Random random = new Random();
    private Vector originCoordinates;
    private Vector targetCoordinates = new Vector();

    private int recursionMin;
    private int recursionMax;

    private int recursions;
    private int spread;

    @Override
    public void loadProperties() {
        this.recursionMin = getIntegerProperty("recursion-min", RECURSION_MIN);
        this.recursionMax = getIntegerProperty("recursion-max", RECURSION_MAX);

        this.recursions = getIntegerProperty("default-recursion", DEFAULT_RECURSION);
        this.spread = getIntegerProperty("default-spread", DEFAULT_SPREAD);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.performer-brush.jagged-line.info",
                    getIntegerProperty("default-recursion", DEFAULT_RECURSION),
                    this.recursionMin,
                    this.recursionMax,
                    getIntegerProperty("default-spread", DEFAULT_SPREAD)
            ));
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("r")) {
                    Integer recursions = NumericParser.parseInteger(parameters[1]);
                    if (recursions != null && recursions >= this.recursionMin && recursions <= this.recursionMax) {
                        this.recursions = recursions;
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.jagged-line.set-recursions",
                                this.recursions
                        ));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number-between", parameters[1],
                                recursionMin, recursionMax
                        ));
                    }
                } else if (firstParameter.equalsIgnoreCase("s")) {
                    Integer spread = NumericParser.parseInteger(parameters[1]);
                    if (spread != null) {
                        this.spread = spread;
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.performer-brush.jagged-line.set-spread",
                                this.spread
                        ));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
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
            return super.sortCompletions(Stream.of("r", "s"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        if (this.originCoordinates == null) {
            this.originCoordinates = new Vector();
        }
        BlockVector3 targetBlock = getTargetBlock();
        this.originCoordinates = Vectors.toBukkit(targetBlock);
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        if (this.originCoordinates == null) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.warning.brush.first-coordinate"));
        } else {
            BlockVector3 targetBlock = getTargetBlock();
            this.targetCoordinates = Vectors.toBukkit(targetBlock);
            jaggedP();
        }
    }

    private void jaggedP() {
        Vector originClone = new Vector().
                copy(this.originCoordinates)
                .add(HALF_BLOCK_OFFSET);
        Vector targetClone = new Vector().
                copy(this.targetCoordinates)
                .add(HALF_BLOCK_OFFSET);
        Vector direction = new Vector().
                copy(targetClone)
                .subtract(originClone);
        double length = this.targetCoordinates.distance(this.originCoordinates);
        if (length == 0) {
            this.performer.perform(
                    getEditSession(),
                    targetCoordinates.getBlockX(),
                    targetCoordinates.getBlockY(),
                    targetCoordinates.getBlockZ(),
                    getBlock(targetCoordinates.getBlockX(), targetCoordinates.getBlockY(), targetCoordinates.getBlockZ())
            );
        } else {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            BlockIterator iterator = new BlockIterator(world, originClone, direction, 0, NumberConversions.round(length));
            while (iterator.hasNext()) {
                Block block = iterator.next();
                for (int i = 0; i < this.recursions; i++) {
                    int x = Math.round(block.getX() + this.random.nextInt(this.spread * 2) - this.spread);
                    int y = Math.round(block.getY() + this.random.nextInt(this.spread * 2) - this.spread);
                    int z = Math.round(block.getZ() + this.random.nextInt(this.spread * 2) - this.spread);
                    this.performer.perform(getEditSession(), x, clampY(y), z, clampY(x, y, z));
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.jagged-line.set-recursions",
                        this.recursions
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.jagged-line.set-spread",
                        this.spread
                ))
                .send();
    }

}
