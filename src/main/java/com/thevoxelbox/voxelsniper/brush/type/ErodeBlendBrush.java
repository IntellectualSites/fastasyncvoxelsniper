package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Range;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendBallBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b erode_blend|erode_blend_ball|erodeblend|erodeblendball|eb")
@Permission("voxelsniper.brush.erodeblend")
public class ErodeBlendBrush extends AbstractBrush {

    private final BlendBallBrush blendBall;
    private final ErodeBrush erode;

    public ErodeBlendBrush() {
        this.blendBall = new BlendBallBrush();
        this.erode = new ErodeBrush();
    }

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        this.blendBall.onBrush(snipe);
        this.erode.onBrush(snipe);
    }

    @Command("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        this.blendBall.onBrushInfo(snipe);
        this.erode.onBrushInfo(snipe);
    }

    @Command("water")
    public void onBrushWater(
            final @NotNull Snipe snipe
    ) {
        this.blendBall.onBrushWater(snipe);
    }

    @Command("<preset>")
    public void onBrushPreset(
            final @NotNull Snipe snipe,

            final @Argument("preset") ErodeBrush.Preset preset
    ) {
        this.erode.onBrushPreset(snipe, preset);
    }

    @Command("e <erosion-faces>")
    public void onBrushErosionfaces(
            final @NotNull Snipe snipe,
            final @Argument("erosion-faces") @Range(min = "0") int erosionFaces
    ) {
        this.erode.onBrushErosionfaces(snipe, erosionFaces);
    }

    @Command("E <erosion-recursions>")
    public void onBrushErosionrecursion(
            final @NotNull Snipe snipe,
            final @Argument("erosion-recursions") @Range(min = "0") int erosionRecursions
    ) {
        this.erode.onBrushErosionrecursion(snipe, erosionRecursions);
    }

    @Command("f <fill-faces>")
    public void onBrushFillfaces(
            final @NotNull Snipe snipe,
            final @Argument("fill-faces") @Range(min = "0") int fillFaces
    ) {
        this.erode.onBrushFillfaces(snipe, fillFaces);
    }

    @Command("F <fill-recursions>")
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
