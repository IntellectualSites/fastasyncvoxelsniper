package com.thevoxelbox.voxelsniper.brush.type.stencil;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.Bukkit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * This is paste only currently. Assumes files exist, and thus has no usefulness until I add in saving stencils later. Uses sniper-exclusive stencil format: 3
 * shorts for X,Z,Y size of cuboid 3 shorts for X,Z,Y offsets from the -X,-Z,-Y corner. This is the reference point for pasting, corresponding to where you
 * click your brush. 1 long integer saying how many runs of blocks are in the schematic (data is compressed into runs) 1 per run: ( 1 boolean: true = compressed
 * line ahead, false = locally unique block ahead. This wastes a bit instead of a byte, and overall saves space, as long as at least 1/8 of all RUNS are going
 * to be size 1, which in Minecraft is almost definitely true. IF boolean was true, next unsigned byte stores the number of consecutive blocks of the same type,
 * up to 256. IF boolean was false, there is no byte here, goes straight to ID and data instead, which applies to just one block. 2 bytes to identify type of
 * block. First byte is ID, second is data. This applies to every one of the line of consecutive blocks if boolean was true. )
 */
public class StencilBrush extends AbstractBrush {

    private static final String NO_FILE_LOADED = "NoFileLoaded";
    private static final int DEFAULT_PASTE_OPTION = 1;
    private static final int DEFAULT_MAX_AREA_VOLUME = 5_000_000;
    private static final int DEFAULT_MAX_SAVE_VOLUME = 50_000;

    private final int[] firstPoint = new int[3];
    private final int[] secondPoint = new int[3];
    private final int[] pastePoint = new int[3];
    private String filename = NO_FILE_LOADED;
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

        File dataFolder = new File(PLUGIN_DATA_FOLDER, "/stencils");
        dataFolder.mkdirs();
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.info"));
        } else {
            byte pasteOption;
            byte pasteParam;
            if (firstParameter.equalsIgnoreCase("full")) {
                pasteOption = 0;
                pasteParam = 1;
            } else if (firstParameter.equalsIgnoreCase("fill")) {
                pasteOption = 1;
                pasteParam = 1;
            } else if (firstParameter.equalsIgnoreCase("replace")) {
                pasteOption = 2;
                pasteParam = 1;
            } else {
                // Reset to [s] parameter expected.
                pasteOption = 1;
                pasteParam = 0;
            }
            if (parameters.length != 1 + pasteParam) {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
                return;
            }

            this.pasteOption = pasteOption;
            try {
                this.filename = parameters[pasteParam];
                File file = new File(PLUGIN_DATA_FOLDER, "/stencils/" + this.filename + ".vstencil");
                if (file.exists()) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.loaded", this.filename));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.missing", this.filename));
                }
            } catch (RuntimeException exception) {
                messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.wrong-stencil-name"));
                exception.printStackTrace();
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("full", "fill", "replace"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) { // will be used to copy/save later on?
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.point == 1) {
            this.firstPoint[0] = targetBlock.getX();
            this.firstPoint[1] = targetBlock.getZ();
            this.firstPoint[2] = targetBlock.getY();
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.coordinates", this.firstPoint[0], this.firstPoint[1],
                    this.firstPoint[2]
            ));
            this.point = 2;
        } else if (this.point == 2) {
            this.secondPoint[0] = targetBlock.getX();
            this.secondPoint[1] = targetBlock.getZ();
            this.secondPoint[2] = targetBlock.getY();
            if ((Math.abs(this.firstPoint[0] - this.secondPoint[0]) * Math.abs(this.firstPoint[1] - this.secondPoint[1]) * Math.abs(
                    this.firstPoint[2] - this.secondPoint[2])) > this.maxAreaVolume) {
                messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.area-too-large", this.maxAreaVolume));
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
            this.pastePoint[0] = targetBlock.getX();
            this.pastePoint[1] = targetBlock.getZ();
            this.pastePoint[2] = targetBlock.getY();
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.paste-point"));
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.coordinates", this.pastePoint[0], this.pastePoint[1],
                    this.pastePoint[2]
            ));
            this.point = 1;
            this.stencilSave(snipe);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) { // will be used to paste later on
        this.stencilPaste(snipe);
    }

    private void stencilPaste(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.filename.matches(NO_FILE_LOADED)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.missing-file"));
            return;
        }
        File file = new File(PLUGIN_DATA_FOLDER, "/stencils/" + this.filename + ".vstencil");
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
                int blockPositionX = targetBlock.getX();
                int blockPositionY = targetBlock.getY();
                int blockPositionZ = targetBlock.getZ();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
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
                                    readBlockData(in)
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
                            blockData = readBlockData(in);
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
                            blockData = readBlockData(in);
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
                            blockData = readBlockData(in);
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
                            blockData = readBlockData(in);
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
                messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.pasted", this.filename, volume));
                in.close();
            } catch (IOException exception) {
                messenger.sendMessage(Caption.of("voxelsniper.error.unexpected"));
                exception.printStackTrace();
            }
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.wrong-stencil-name"));
        }
    }

    private BlockState readBlockData(DataInputStream in) throws IOException {
        String blockDataString = in.readUTF();
        return BukkitAdapter.adapt(Bukkit.createBlockData(blockDataString));
    }

    private void stencilSave(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        File file = new File(PLUGIN_DATA_FOLDER, "/stencils/" + this.filename + ".vstencil");
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
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.volume-coordinates", this.x * this.z * this.y,
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
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil.saved", this.filename, volume));
            out.close();
        } catch (IOException exception) {
            messenger.sendMessage(Caption.of("voxelsniper.error.unexpected"));
            exception.printStackTrace();
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
                .message(Caption.of("voxelsniper.brush.stencil.loaded", this.filename))
                .send();
    }

}
