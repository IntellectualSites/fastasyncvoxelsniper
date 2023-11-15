package com.thevoxelbox.voxelsniper.brush.type.stamp;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

/**
 * The CloneStamp class is used to create a collection of blocks in a cylinder shape according to the selection the player has set.
 */
@RequireToolkit
@CommandMethod(value = "brush|b clone_stamp|clonestamp|cs")
@CommandPermission("voxelsniper.brush.clonestamp")
public class CloneStampBrush extends AbstractStampBrush {

    private static final StampType DEFAULT_STAMP_TYPE = StampType.DEFAULT;

    @Override
    public void loadProperties() {
        this.stamp = ((StampType) getEnumProperty("default-stamp-type", StampType.class, DEFAULT_STAMP_TYPE));
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.clone-stamp.info"));
    }

    @CommandMethod("<stamp-type>")
    public void onBrushStamptype(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("stamp-type") StampType stampType
    ) {
        this.stamp = stampType;
        this.reSort();

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.clone-stamp.set-stamp-type",
                StampType.FILL.getFullName()
        ));
    }

    @CommandMethod("a")
    public void onBrushA(
            final @NotNull Snipe snipe
    ) {
        this.onBrushStamptype(snipe, StampType.NO_AIR);
    }

    @CommandMethod("f")
    public void onBrushF(
            final @NotNull Snipe snipe
    ) {
        this.onBrushStamptype(snipe, StampType.FILL);
    }

    @CommandMethod("d")
    public void onBrushD(
            final @NotNull Snipe snipe
    ) {
        this.onBrushStamptype(snipe, StampType.DEFAULT);
    }

    @CommandMethod("c <center>")
    public void onBrushC(
            final @NotNull Snipe snipe,
            final @Argument("center") int center
    ) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        toolkitProperties.setCylinderCenter(center);

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.clone-stamp.set-center",
                toolkitProperties.getCylinderCenter()
        ));
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        clone(snipe);
    }

    /**
     * The clone method is used to grab a snapshot of the selected area dictated blockPositionY targetBlock.x y z v.brushSize v.voxelHeight and v.cCen.
     * x y z -- initial center of the selection v.brushSize -- the radius of the cylinder v.voxelHeight -- the height of the cylinder c.cCen -- the offset on
     * the Y axis of the selection ( bottom of the cylinder ) as blockPositionY: Bottom_Y = targetBlock.y + v.cCen;
     */
    private void clone(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        this.clone.clear();
        this.fall.clear();
        this.drop.clear();
        this.solid.clear();
        this.sorted = false;
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockY = targetBlock.getY();
        int yStartingPoint = targetBlockY + toolkitProperties.getCylinderCenter();
        int yEndPoint = targetBlockY + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
        EditSession editSession = getEditSession();
        int minHeight = editSession.getMinY();
        if (yStartingPoint < minHeight) {
            yStartingPoint = minHeight;
            messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-start"));
        } else {
            int maxHeight = editSession.getMaxY();
            if (yStartingPoint > maxHeight) {
                yStartingPoint = maxHeight;
                messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-start"));
            }
        }
        if (yEndPoint < minHeight) {
            yEndPoint = minHeight;
            messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-end"));
        } else {
            int maxHeight = editSession.getMaxY();
            if (yEndPoint > maxHeight) {
                yEndPoint = maxHeight;
                messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-end"));
            }
        }
        double bSquared = Math.pow(brushSize, 2);
        int targetBlockX = targetBlock.getX();
        int targetBlockZ = targetBlock.getZ();
        for (int z = yStartingPoint; z < yEndPoint; z++) {
            this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ), 0, z - yStartingPoint, 0));
            for (int y = 1; y <= brushSize; y++) {
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ + y), 0, z - yStartingPoint, y));
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX, z, targetBlockZ - y), 0, z - yStartingPoint, -y));
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX + y, z, targetBlockZ), y, z - yStartingPoint, 0));
                this.clone.add(new StampBrushBlockWrapper(clampY(targetBlockX - y, z, targetBlockZ), -y, z - yStartingPoint, 0));
            }
            for (int x = 1; x <= brushSize; x++) {
                double xSquared = Math.pow(x, 2);
                for (int y = 1; y <= brushSize; y++) {
                    if ((xSquared + Math.pow(y, 2)) <= bSquared) {
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX + x, z, targetBlockZ + y),
                                x,
                                z - yStartingPoint,
                                y
                        ));
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX + x, z, targetBlockZ - y),
                                x,
                                z - yStartingPoint,
                                -y
                        ));
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX - x, z, targetBlockZ + y),
                                -x,
                                z - yStartingPoint,
                                y
                        ));
                        this.clone.add(new StampBrushBlockWrapper(
                                clampY(targetBlockX - x, z, targetBlockZ - y),
                                -x,
                                z - yStartingPoint,
                                -y
                        ));
                    }
                }
            }
        }
        messenger.sendMessage(Caption.of("voxelsniper.brush.clone-stamp.copied", this.clone.size()));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .voxelHeightMessage()
                .cylinderCenterMessage()
                .message(Caption.of(
                        "voxelsniper.brush.clone-stamp.set-stamp-type",
                        this.stamp.getFullName()
                ))
                .send();
    }

}
