package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.Vectors;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@RequireToolkit
@CommandMethod(value = "brush|b jagged_line|jaggedline|jagged|j")
@CommandPermission("voxelsniper.brush.jaggedline")
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
        super.onBrushInfoCommand(snipe, Caption.of(
                "voxelsniper.performer-brush.jagged-line.info",
                this.getIntegerProperty("default-recursion", DEFAULT_RECURSION),
                this.recursionMin,
                this.recursionMax,
                this.getIntegerProperty("default-spread", DEFAULT_SPREAD)
        ));
    }

    @CommandMethod("r <recursions>")
    public void onBrushR(
            final @NotNull Snipe snipe,
            final @Argument("recursions") @DynamicRange(min = "recursionMin", max = "recursionMax") int recursions
    ) {
        this.recursions = recursions;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.jagged-line.set-recursions",
                this.recursions
        ));
    }

    @CommandMethod("s <spread>")
    public void onBrushS(
            final @NotNull Snipe snipe,
            final @Argument("spread") @Range(min = "0") int spread
    ) {
        this.spread = spread;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.jagged-line.set-spread",
                this.spread
        ));
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
                    int x = block.getX() + this.random.nextInt(this.spread * 2) - this.spread;
                    int y = block.getY() + this.random.nextInt(this.spread * 2) - this.spread;
                    int z = block.getZ() + this.random.nextInt(this.spread * 2) - this.spread;
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
