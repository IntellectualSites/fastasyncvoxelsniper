package com.thevoxelbox.voxelsniper.brush.type.stencil;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This is paste only currently. Assumes files exist, and thus has no usefulness until I add in saving stencils later. Uses sniper-exclusive stencil format: 3
 * shorts for X,Z,Y size of cuboid 3 shorts for X,Z,Y offsets from the -X,-Z,-Y corner. This is the reference point for pasting, corresponding to where you
 * click your brush. 1 long integer saying how many runs of blocks are in the schematic (data is compressed into runs) 1 per run: (1 boolean: true = compressed
 * line ahead, false = locally unique block ahead. This wastes a bit instead of a byte, and overall saves space, as long as at least 1/8 of all RUNS are going
 * to be size 1, which in Minecraft is almost definitely true. IF boolean was true, next unstenciled byte stores the number of consecutive blocks of the same type,
 * up to 256. IF boolean was false, there is no byte here, goes straight to ID and data instead, which applies to just one block. 2 bytes to identify type of
 * block. First byte is ID, second is data. This applies to every one of the line of consecutive blocks if boolean was true.)
 */
@RequireToolkit
@CommandMethod(value = "brush|b stencil|st")
@CommandPermission("voxelsniper.brush.stencil")
public class StencilBrush extends AbstractBrush {

    private static final int DEFAULT_PASTE_OPTION = 1;
    private static final int DEFAULT_MAX_AREA_VOLUME = 5_000_000;
    private static final int DEFAULT_MAX_SAVE_VOLUME = 50_000;

    private final int[] firstPoint = new int[3];
    private final int[] secondPoint = new int[3];
    private final int[] pastePoint = new int[3];
    private File file = null;
    private short x;
    private short z;
    private short y;
    private short xRef;
    private short zRef;
    private short yRef;
    private byte point = 1;

    private byte pasteOption; // 0 = full, 1 = fill, 2 = replace
    private int maxAreaVolume;
    private int maxSaveVolume;

    @Override
    public void loadProperties() {
        this.pasteOption = (byte) getIntegerProperty("default-paste-option", DEFAULT_PASTE_OPTION);
        this.maxAreaVolume = getIntegerProperty("default-max-area-volume", DEFAULT_MAX_AREA_VOLUME);
        this.maxSaveVolume = getIntegerProperty("default-max-save-volume", DEFAULT_MAX_SAVE_VOLUME);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.stencil.info"));
    }

    private void onBrushPasteCommand(Snipe snipe, byte pasteOption, File stencil) {
        this.pasteOption = pasteOption;
        this.file = stencil;

        SnipeMessenger messenger = snipe.createMessenger();
        if (stencil.exists()) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil.loaded",
                    this.file.getName()
            ));
        } else {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil.missing",
                    this.file.getName()
            ));
        }
    }

    @CommandMethod("full <stencil>")
    public void onBrushFull(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil", parserName = "stencil-file_parser") File stencil
    ) {
        this.onBrushPasteCommand(snipe, (byte) 0, stencil);
    }

    @CommandMethod("fill <stencil>")
    public void onBrushFill(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil", parserName = "stencil-file_parser") File stencil
    ) {
        this.onBrushPasteCommand(snipe, (byte) 1, stencil);
    }

    @CommandMethod("replace <stencil>")
    public void onBrushReplace(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil", parserName = "stencil-file_parser") File stencil
    ) {
        this.onBrushPasteCommand(snipe, (byte) 2, stencil);
    }

    @CommandMethod("<stencil>")
    public void onBrushStencil(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil", parserName = "stencil-file_parser") File stencil
    ) {
        this.onBrushFill(snipe, stencil);
    }

    @Override
    public void handleArrowAction(Snipe snipe) { // will be used to copy/save later on?
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.point == 1) {
            this.firstPoint[0] = targetBlock.x();
            this.firstPoint[1] = targetBlock.z();
            this.firstPoint[2] = targetBlock.y();
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil.coordinates",
                    this.firstPoint[0], this.firstPoint[1], this.firstPoint[2]
            ));
            this.point = 2;
        } else if (this.point == 2) {
            this.secondPoint[0] = targetBlock.x();
            this.secondPoint[1] = targetBlock.z();
            this.secondPoint[2] = targetBlock.y();
            if ((Math.abs(this.firstPoint[0] - this.secondPoint[0]) * Math.abs(this.firstPoint[1] - this.secondPoint[1]) * Math.abs(
                    this.firstPoint[2] - this.secondPoint[2])) > this.maxAreaVolume) {
                messenger.sendMessage(Caption.of(
                        "voxelsniper.brush.stencil.area-too-large",
                        this.maxAreaVolume
                ));
                this.point = 1;
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
                messenger.sendMessage(Caption.of(
                        "voxelsniper.brush.stencil.coordinates",
                        this.secondPoint[0],
                        this.secondPoint[1],
                        this.secondPoint[2]
                ));
                this.point = 3;
            }
        } else if (this.point == 3) {
            this.pastePoint[0] = targetBlock.x();
            this.pastePoint[1] = targetBlock.z();
            this.pastePoint[2] = targetBlock.y();
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.paste-point"));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil.coordinates",
                    this.pastePoint[0], this.pastePoint[1], this.pastePoint[2]
            ));
            this.point = 1;
            this.stencilSave(snipe);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) { // will be used to paste later on
        this.stencilPaste(snipe, StencilReader.BlockDataReader.BLOCK_DATA);
    }

    private void stencilPaste(Snipe snipe, StencilReader.BlockDataReader blockDataReader) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.file == null) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.missing-file"));
            return;
        }
        if (file.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                int volume = this.x * this.y * this.z;
                int currX = -this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = -this.zRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = getTargetBlock();
                int blockPositionX = targetBlock.x();
                int blockPositionY = targetBlock.y();
                int blockPositionZ = targetBlock.z();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = blockDataReader.readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        blockPositionX + currX,
                                        clampY(blockPositionY + currY),
                                        blockPositionZ + currZ,
                                        blockData
                                );
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    blockPositionX + currX,
                                    clampY(blockPositionY + currY),
                                    blockPositionZ + currZ,
                                    blockDataReader.readBlockData(in)
                            );
                            currX++;
                            if (currX == this.x - this.xRef) {
                                currX = -this.xRef;
                                currZ++;
                                if (currZ == this.z - this.zRef) {
                                    currZ = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = blockDataReader.readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                BlockType type = blockData.getBlockType();
                                if (!Materials.isEmpty(type) && clampY(
                                        blockPositionX + currX,
                                        blockPositionY + currY,
                                        blockPositionZ + currZ
                                ).isAir()) {
                                    setBlockData(
                                            blockPositionX + currX,
                                            clampY(blockPositionY + currY),
                                            blockPositionZ + currZ,
                                            blockData
                                    );
                                }
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            BlockType type = blockData.getBlockType();
                            if (!Materials.isEmpty(type) && clampY(
                                    blockPositionX + currX,
                                    blockPositionY + currY,
                                    blockPositionZ + currZ
                            ).isAir()) {
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                setBlockData(
                                        blockPositionX + currX,
                                        clampY(blockPositionY + currY),
                                        blockPositionZ + currZ,
                                        blockData
                                );
                            }
                            currX++;
                            if (currX == this.x - this.xRef) {
                                currX = -this.xRef;
                                currZ++;
                                if (currZ == this.z - this.zRef) {
                                    currZ = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = blockDataReader.readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                BlockType type = blockData.getBlockType();
                                if (!Materials.isEmpty(type)) {
                                    setBlockData(
                                            blockPositionX + currX,
                                            clampY(blockPositionY + currY),
                                            blockPositionZ + currZ,
                                            blockData
                                    );
                                }
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            BlockType type = blockData.getBlockType();
                            if (!Materials.isEmpty(type)) {
                                setBlockData(
                                        blockPositionX + currX,
                                        clampY(blockPositionY + currY),
                                        blockPositionZ + currZ,
                                        blockData
                                );
                            }
                            currX++;
                            if (currX == this.x) {
                                currX = 0;
                                currZ++;
                                if (currZ == this.z) {
                                    currZ = 0;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                messenger.sendMessage(Caption.of(
                        "voxelsniper.brush.stencil.pasted",
                        this.file.getName(),
                        volume
                ));
                in.close();
            } catch (IOException e) {
                if (blockDataReader == StencilReader.BlockDataReader.BLOCK_DATA) {
                    // Tries with some legacy handling for legacy stencils.
                    stencilPaste(snipe, StencilReader.BlockDataReader.LEGACY_IDS);
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.unexpected"));
                    e.printStackTrace();
                }
            }
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.invalid-stencil-name"));
        }
    }

    private void stencilSave(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        try {
            this.x = (short) (Math.abs((this.firstPoint[0] - this.secondPoint[0])) + 1);
            this.z = (short) (Math.abs((this.firstPoint[1] - this.secondPoint[1])) + 1);
            this.y = (short) (Math.abs((this.firstPoint[2] - this.secondPoint[2])) + 1);
            int volume = this.x * this.y * this.z;
            this.xRef = (short) ((this.firstPoint[0] > this.secondPoint[0])
                    ? (this.pastePoint[0] - this.secondPoint[0])
                    : (this.pastePoint[0] - this.firstPoint[0]));
            this.zRef = (short) ((this.firstPoint[1] > this.secondPoint[1])
                    ? (this.pastePoint[1] - this.secondPoint[1])
                    : (this.pastePoint[1] - this.firstPoint[1]));
            this.yRef = (short) ((this.firstPoint[2] > this.secondPoint[2])
                    ? (this.pastePoint[2] - this.secondPoint[2])
                    : (this.pastePoint[2] - this.firstPoint[2]));
            if ((this.x * this.y * this.z) > maxSaveVolume) {
                messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.area-too-large", this.maxSaveVolume));
                return;
            }
            createParentDirs(file);
            file.createNewFile();
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            int blockPositionX = Math.min(this.firstPoint[0], this.secondPoint[0]);
            int blockPositionZ = Math.min(this.firstPoint[1], this.secondPoint[1]);
            int blockPositionY = Math.min(this.firstPoint[2], this.secondPoint[2]);
            out.writeShort(this.x);
            out.writeShort(this.z);
            out.writeShort(this.y);
            out.writeShort(this.xRef);
            out.writeShort(this.zRef);
            out.writeShort(this.yRef);
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil.volume-coordinates",
                    this.x * this.z * this.y,
                    blockPositionX, blockPositionZ, blockPositionY
            ));
            BlockState[] blockDataArray = new BlockState[this.x * this.z * this.y];
            byte[] runSizeArray = new byte[this.x * this.z * this.y];
            BlockState lastBlockData = getBlock(blockPositionX, blockPositionY, blockPositionZ);
            int counter = 0;
            int arrayIndex = 0;
            for (int y = 0; y < this.y; y++) {
                for (int z = 0; z < this.z; z++) {
                    for (int x = 0; x < this.x; x++) {
                        BlockState thisBlockData = getBlock(blockPositionX + x, blockPositionY + y, blockPositionZ + z);
                        if (!thisBlockData.equals(lastBlockData) || counter == 255) {
                            blockDataArray[arrayIndex] = lastBlockData;
                            runSizeArray[arrayIndex] = (byte) (counter - 128);
                            arrayIndex++;
                            counter = 1;
                        } else {
                            counter++;
                        }
                        lastBlockData = thisBlockData;
                    }
                }
            }
            blockDataArray[arrayIndex] = lastBlockData; // saving last run, which will always be left over.
            runSizeArray[arrayIndex] = (byte) (counter - 128);
            out.writeInt(arrayIndex + 1);
            // v.sendMessage("number of runs = " + arrayIndex);
            for (int i = 0; i < arrayIndex + 1; i++) {
                if (runSizeArray[i] > -127) {
                    out.writeBoolean(true);
                    out.writeByte(runSizeArray[i]);
                } else {
                    out.writeBoolean(false);
                }
                out.writeUTF(blockDataArray[i].getAsString());
            }
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil.saved",
                    this.file.getName(),
                    volume
            ));
            out.close();
        } catch (IOException e) {
            messenger.sendMessage(Caption.of("voxelsniper.error.unexpected"));
            e.printStackTrace();
        }
    }

    private void createParentDirs(File file) throws IOException {
        File canonicalFile = file.getCanonicalFile();
        File parent = canonicalFile.getParentFile();
        if (parent == null) {
            return;
        }
        parent.mkdirs();
        if (!parent.isDirectory()) {
            throw new IOException("Unable to create parent directories of " + file);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(this.file == null
                        ? Caption.of("voxelsniper.brush.stencil-list.missing-file")
                        : Caption.of(
                                "voxelsniper.brush.stencil.loaded",
                                this.file.getName()
                        ))
                .send();
    }

}
