package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b fill_down|filldown|fd")
@CommandPermission("voxelsniper.brush.filldown")
public class FillDownBrush extends AbstractPerformerBrush {

    private boolean trueCircle;
    private boolean fillLiquid = true;
    private boolean fromExisting;
    private int minY;

    @Override
    public void loadProperties() {
        if (getEditSession() != null) {
            this.minY = getEditSession().getMinY();
        }
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
                "voxelsniper.performer-brush.fill-down.info",
                this.getEditSession().getMinY()
        ));
    }

    @CommandMethod("<true-circle>")
    public void onBrushTruecircle(
            final @NotNull Snipe snipe,
            final @Argument("true-circle") @Liberal boolean trueCircle
    ) {
        this.trueCircle = trueCircle;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.parameter.true-circle",
                VoxelSniperText.getStatus(this.trueCircle)
        ));
    }

    @CommandMethod("all")
    public void onBrushAll(
            final @NotNull Snipe snipe
    ) {
        this.fillLiquid = true;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.fill-down.set-fill-all",
                VoxelSniperText.getStatus(true)
        ));
    }

    @CommandMethod("some")
    public void onBrushSome(
            final @NotNull Snipe snipe
    ) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.fillLiquid = false;
        toolkitProperties.resetReplacePattern();

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.fill-down.set-fill-some",
                VoxelSniperText.getStatus(true)
        ));
    }

    @CommandMethod("e")
    public void onBrushE(
            final @NotNull Snipe snipe
    ) {
        this.fromExisting = !this.fromExisting;

        SnipeMessenger messenger = snipe.createMessenger();
        if (this.fromExisting) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.performer-brush.fill-down.set-fill-down-existing",
                    VoxelSniperText.getStatus(true)
            ));
        } else {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.performer-brush.fill-down.set-fill-down-all",
                    VoxelSniperText.getStatus(true)
            ));
        }
    }

    @CommandMethod("y <min-y>")
    public void onBrushY(
            final @NotNull Snipe snipe,
            final @Argument("min-y") int minY
    ) {
        int minYMin = this.getEditSession().getMinY();
        int minYMax = this.getEditSession().getMaxY();
        this.minY = minY < minYMin ? minYMin : Math.min(minY, minYMax);

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.fill-down.set-min-y",
                this.minY
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        fillDown(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        fillDown(snipe);
    }

    private void fillDown(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        BlockVector3 targetBlock = this.getTargetBlock();
        for (int x = -brushSize; x <= brushSize; x++) {
            double currentXSquared = Math.pow(x, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if (currentXSquared + Math.pow(z, 2) <= brushSizeSquared) {
                    int y = 0;
                    if (this.fromExisting) {
                        boolean found = false;
                        for (y = -toolkitProperties.getVoxelHeight(); y < toolkitProperties.getVoxelHeight(); y++) {
                            BlockType currentBlockType = getBlockType(
                                    targetBlock.getX() + x,
                                    targetBlock.getY() + y,
                                    targetBlock.getZ() + z
                            );
                            if (!Materials.isEmpty(currentBlockType)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            continue;
                        }
                        y--;
                    }
                    for (; y >= -(targetBlock.getY() - minY); --y) {
                        BlockState currentBlockState = getBlock(
                                targetBlock.getX() + x,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + z
                        );
                        if (Materials.isEmpty(currentBlockState.getBlockType())
                                || (this.fillLiquid && Materials.isLiquid(currentBlockState.getBlockType()))) {
                            this.performer.perform(
                                    getEditSession(),
                                    targetBlock.getX() + x,
                                    targetBlock.getY() + y,
                                    targetBlock.getZ() + z,
                                    currentBlockState
                            );
                        } else {
                            break;
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
                .brushSizeMessage()
                .message(Caption.of(
                        "voxelsniper.brush.parameter.true-circle",
                        VoxelSniperText.getStatus(this.trueCircle)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-fill-all",
                        VoxelSniperText.getStatus(fillLiquid)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-fill-some",
                        VoxelSniperText.getStatus(!fillLiquid)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-fill-down-existing",
                        VoxelSniperText.getStatus(fromExisting)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-fill-down-all",
                        VoxelSniperText.getStatus(!fromExisting)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.fill-down.set-min-y",
                        this.minY
                ))
                .send();
    }

}
