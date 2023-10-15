package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendBallBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b erode_blend|erode_blend_ball|erodeblend|erodeblendball|eb")
@CommandPermission("voxelsniper.brush.erodeblend")
public class ErodeBlendBrush extends AbstractBrush {

    private final BlendBallBrush blendBall;
    private final ErodeBrush erode;

    public ErodeBlendBrush() {
        this.blendBall = new BlendBallBrush();
        this.erode = new ErodeBrush();
    }

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        this.blendBall.onBrush(snipe);
        this.erode.onBrush(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        this.blendBall.onBrushInfo(snipe);
        this.erode.onBrushInfo(snipe);
    }

    @CommandMethod("water")
    public void onBrushWater(
            final @NotNull Snipe snipe
    ) {
        this.blendBall.onBrushWater(snipe);
    }

    @CommandMethod("<preset>")
    public void onBrushPreset(
            final @NotNull Snipe snipe,

            final @Argument("preset") ErodeBrush.Preset preset
    ) {
        this.erode.onBrushPreset(snipe, preset);
    }

    @CommandMethod("e <erosion-faces>")
    public void onBrushErosionfaces(
            final @NotNull Snipe snipe,
            final @Argument("erosion-faces") @Range(min = "0") int erosionFaces
    ) {
        this.erode.onBrushErosionfaces(snipe, erosionFaces);
    }

    @CommandMethod("E <erosion-recursions>")
    public void onBrushErosionrecursion(
            final @NotNull Snipe snipe,
            final @Argument("erosion-recursions") @Range(min = "0") int erosionRecursions
    ) {
        this.erode.onBrushErosionrecursion(snipe, erosionRecursions);
    }

    @CommandMethod("f <fill-faces>")
    public void onBrushFillfaces(
            final @NotNull Snipe snipe,
            final @Argument("fill-faces") @Range(min = "0") int fillFaces
    ) {
        this.erode.onBrushFillfaces(snipe, fillFaces);
    }

    @CommandMethod("F <fill-recursions>")
    public void onBrushFillrecursion(
            final @NotNull Snipe snipe,
            final @Argument("fill-recursions") @Range(min = "0") int fillRecursions
    ) {
        this.erode.onBrushFillrecursion(snipe, fillRecursions);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();

        this.erode.perform(snipe, ToolAction.ARROW, editSession, targetBlock, lastBlock);
        this.blendBall.setAirExcluded(false);
        this.blendBall.perform(snipe, ToolAction.ARROW, editSession, targetBlock, lastBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();

        this.erode.perform(snipe, ToolAction.GUNPOWDER, editSession, targetBlock, lastBlock);
        this.blendBall.setAirExcluded(false);
        this.blendBall.perform(snipe, ToolAction.GUNPOWDER, editSession, targetBlock, lastBlock);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        this.blendBall.sendInfo(snipe);
        this.erode.sendInfo(snipe);
    }

}
