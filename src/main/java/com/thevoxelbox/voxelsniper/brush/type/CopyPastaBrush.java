package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b copy_pasta|copypasta|cp")
@Permission("voxelsniper.brush.copypasta")
public class CopyPastaBrush extends AbstractBrush {

    private static final int BLOCK_LIMIT = 10000;
    private final int[] pastePoint = new int[3];
    private final int[] minPoint = new int[3];
    private final int[] offsetPoint = new int[3];
    private final int[] arraySize = new int[3];
    private int blockLimit;
    private boolean pasteAir = true; // False = no air, true = air
    private int points; //
    private int numBlocks;
    private int[] firstPoint = new int[3];
    private int[] secondPoint = new int[3];
    private BlockType[] blockArray;
    private BlockState[] dataArray;
    private int pivot; // ccw degrees

    @Override
    public void loadProperties() {
        this.blockLimit = getIntegerProperty("block-limit", BLOCK_LIMIT);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.copy-pasta.info"));
    }

    @Command("air")
    public void onBrushAir(
            final @NotNull Snipe snipe
    ) {
        this.pasteAir = !this.pasteAir;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.copy-pasta.set-paste-air",
                VoxelSniperText.getStatus(this.pasteAir)
        ));
    }

    private void onBrushPivotCommand(Snipe snipe, int pivot) {
        this.pivot = pivot;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.copy-pasta.set-pivot",
                this.pivot
        ));
    }

    @Command("0")
    public void onBrush0(
            final @NotNull Snipe snipe
    ) {
        this.onBrushPivotCommand(snipe, 0);
    }

    @Command("90")
    public void onBrush90(
            final @NotNull Snipe snipe
    ) {
        this.onBrushPivotCommand(snipe, 90);
    }

    @Command("180")
    public void onBrush180(
            final @NotNull Snipe snipe
    ) {
        this.onBrushPivotCommand(snipe, 180);
    }

    @Command("270")
    public void onBrush270(
            final @NotNull Snipe snipe
    ) {
        this.onBrushPivotCommand(snipe, 270);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.points == 0) {
            this.firstPoint[0] = targetBlock.getX();
            this.firstPoint[1] = targetBlock.getY();
            this.firstPoint[2] = targetBlock.getZ();
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
            this.points = 1;
        } else if (this.points == 1) {
            this.secondPoint[0] = targetBlock.getX();
            this.secondPoint[1] = targetBlock.getY();
            this.secondPoint[2] = targetBlock.getZ();
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
            this.points = 2;
        } else {
            this.firstPoint = new int[3];
            this.secondPoint = new int[3];
            this.numBlocks = 0;
            this.blockArray = new BlockType[1];
            this.dataArray = new BlockState[1];
            this.points = 0;
            messenger.sendMessage(Caption.of("voxelsniper.brush.copy-pasta.points-cleared"));
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.points == 2) {
            if (this.numBlocks == 0) {
                doCopy(snipe);
            } else if (this.numBlocks > 0 && this.numBlocks < this.blockLimit) {
                BlockVector3 targetBlock = this.getTargetBlock();
                this.pastePoint[0] = targetBlock.getX();
                this.pastePoint[1] = targetBlock.getY();
                this.pastePoint[2] = targetBlock.getZ();
                doPasta(snipe);
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.unexpected"));
            }
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.copy-pasta.invalid-points"));
        }
    }

    private void doCopy(Snipe snipe) {
        for (int i = 0; i < 3; i++) {
            this.arraySize[i] = Math.abs(this.firstPoint[i] - this.secondPoint[i]) + 1;
            this.minPoint[i] = Math.min(this.firstPoint[i], this.secondPoint[i]);
            this.offsetPoint[i] = this.minPoint[i] - this.firstPoint[i]; // will always be negative or zero
        }
        this.numBlocks = this.arraySize[0] * this.arraySize[1] * this.arraySize[2];
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.numBlocks > 0 && this.numBlocks < this.blockLimit) {
            this.blockArray = new BlockType[this.numBlocks];
            this.dataArray = new BlockState[this.numBlocks];
            for (int i = 0; i < this.arraySize[0]; i++) {
                for (int j = 0; j < this.arraySize[1]; j++) {
                    for (int k = 0; k < this.arraySize[2]; k++) {
                        int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
                        BlockState block = getBlock(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
                        this.blockArray[currentPosition] = block.getBlockType();
                        BlockState clamp = this.clampY(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
                        this.dataArray[currentPosition] = clamp;
                    }
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.copy-pasta.copied", this.numBlocks));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.copy-pasta.invalid-copy", this.numBlocks, this.blockLimit));
        }
    }

    private void doPasta(Snipe snipe) {
        for (int i = 0; i < this.arraySize[0]; i++) {
            for (int j = 0; j < this.arraySize[1]; j++) {
                for (int k = 0; k < this.arraySize[2]; k++) {
                    int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
                    int x;
                    int y;
                    int z;
                    switch (this.pivot) {
                        case 180 -> {
                            x = this.pastePoint[0] - this.offsetPoint[0] - i;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] - this.offsetPoint[2] - k;
                        }
                        case 270 -> {
                            x = this.pastePoint[0] + this.offsetPoint[2] + k;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] - this.offsetPoint[0] - i;
                        }
                        case 90 -> {
                            x = this.pastePoint[0] - this.offsetPoint[2] - k;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] + this.offsetPoint[0] + i;
                        }
                        default -> { // assume no rotation
                            x = this.pastePoint[0] + this.offsetPoint[0] + i;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] + this.offsetPoint[2] + k;
                        }
                    }
                    if (!(Materials.isEmpty(this.blockArray[currentPosition]) && !this.pasteAir)) {
                        setBlockData(x, y, z, this.dataArray[currentPosition]);
                    }
                }
            }
        }
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.copy-pasta.pasted", this.numBlocks));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.brush.copy-pasta.set-paste-air",
                        VoxelSniperText.getStatus(this.pasteAir)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.copy-pasta.set-pivot",
                        this.pivot
                ))
                .send();
    }

}
