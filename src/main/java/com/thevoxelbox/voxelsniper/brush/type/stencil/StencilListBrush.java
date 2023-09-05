package com.thevoxelbox.voxelsniper.brush.type.stencil;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@RequireToolkit
@CommandMethod(value = "brush|b stencil_list|stencillist|sl")
@CommandPermission("voxelsniper.brush.stencillist")
public class StencilListBrush extends AbstractBrush {

    private static final String NO_FILE_LOADED = "NoFileLoaded";
    private static final int DEFAULT_PASTE_OPTION = 1;

    private final Map<Integer, String> stencilList = new HashMap<>();
    private File file = null;
    private short x;
    private short z;
    private short y;
    private short xRef;
    private short zRef;
    private short yRef;

    private byte pasteOption; // 0 = full, 1 = fill, 2 = replace

    @Override
    public void loadProperties() {
        this.pasteOption = (byte) getIntegerProperty("default-paste-option", DEFAULT_PASTE_OPTION);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.stencil-list.info"));
    }

    private void onBrushPasteCommand(Snipe snipe, byte pasteOption, File stencilList) {
        this.pasteOption = pasteOption;
        this.file = stencilList;

        SnipeMessenger messenger = snipe.createMessenger();
        if (stencilList.exists()) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil-list.loaded",
                    this.file.getName()
            ));
            readStencilList();
        } else {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.stencil-list.missing",
                    this.file.getName()
            ));
            this.file = null;
        }
    }

    @CommandMethod("full <stencil-list>")
    public void onBrushFull(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil-list", parserName = "stencil-list-file_parser") File stencilList
    ) {
        this.onBrushPasteCommand(snipe, (byte) 0, stencilList);
    }

    @CommandMethod("fill <stencil-list>")
    public void onBrushFill(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil-list", parserName = "stencil-list-file_parser") File stencilList
    ) {
        this.onBrushPasteCommand(snipe, (byte) 1, stencilList);
    }

    @CommandMethod("replace <stencil-list>")
    public void onBrushReplace(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil-list", parserName = "stencil-list-file_parser") File stencilList
    ) {
        this.onBrushPasteCommand(snipe, (byte) 2, stencilList);
    }

    @CommandMethod("<stencil-list>")
    public void onBrushStencillist(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "stencil-list", parserName = "stencil-list-file_parser") File stencilList
    ) {
        this.onBrushFill(snipe, stencilList);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        stencilPaste(snipe, StencilReader.BlockDataReader.BLOCK_DATA);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        stencilPasteRotation(snipe);
    }

    private String readRandomStencil() {
        double rand = Math.random() * this.stencilList.size();
        int choice = (int) rand;
        return this.stencilList.get(choice);
    }

    private void readStencilList() {
        stencilList.clear();
        if (file.exists()) {
            try {
                Scanner scanner = new Scanner(file);
                int counter = 0;
                while (scanner.hasNext()) {
                    this.stencilList.put(counter, scanner.nextLine());
                    counter++;
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void stencilPaste(Snipe snipe, StencilReader.BlockDataReader blockDataReader) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.file == null) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.missing-file"));
            return;
        }
        String stencilName = this.readRandomStencil();
        messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.stencil-loaded", stencilName));
        File stencil = new File(PLUGIN_DATA_FOLDER, "/stencils/" + stencilName + ".vstencil");
        if (stencil.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(stencil)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                int currX = -this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = -this.zRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = blockDataReader.readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
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
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
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
                                if (!Materials.isEmpty(blockData.getBlockType()) && clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
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
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
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
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
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
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
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
                        stencilName,
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
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.invalid-stencil-list-name"));
        }
    }

    private void stencilPaste180(Snipe snipe, StencilReader.BlockDataReader blockDataReader) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.file == null) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.missing-file"));
            return;
        }
        String stencilName = this.readRandomStencil();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.stencil-list.stencil-loaded",
                stencilName
        ));
        File stencil = new File(PLUGIN_DATA_FOLDER, "/stencils/" + stencilName + ".vstencil");
        if (stencil.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(stencil)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                int currX = this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = this.zRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = this.getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = blockDataReader.readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                                currX--;
                                if (currX == -this.x + this.xRef) {
                                    currX = this.xRef;
                                    currZ--;
                                    if (currZ == -this.z + this.zRef) {
                                        currZ = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
                                    blockDataReader.readBlockData(in)
                            );
                            currX--;
                            if (currX == -this.x + this.xRef) {
                                currX = this.xRef;
                                currZ--;
                                if (currZ == -this.z + this.zRef) {
                                    currZ = this.zRef;
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
                                if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currX--;
                                if (currX == -this.x + this.xRef) {
                                    currX = this.xRef;
                                    currZ--;
                                    if (currZ == -this.z + this.zRef) {
                                        currZ = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currX--;
                            if (currX == -this.x + this.xRef) {
                                currX = this.xRef;
                                currZ--;
                                if (currZ == -this.z + this.zRef) {
                                    currZ = this.zRef;
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
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currX--;
                                if (currX == -this.x + this.xRef) {
                                    currX = this.xRef;
                                    currZ--;
                                    if (currZ == -this.z + this.zRef) {
                                        currZ = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currX--;
                            if (currX == -this.x + this.xRef) {
                                currX = this.xRef;
                                currZ--;
                                if (currZ == -this.z + this.zRef) {
                                    currZ = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                messenger.sendMessage(Caption.of(
                        "voxelsniper.brush.stencil.pasted",
                        stencilName,
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
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.invalid-stencil-list-name"));
        }
    }

    private void stencilPaste270(Snipe snipe, StencilReader.BlockDataReader blockDataReader) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.file == null) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.missing-file"));
            return;
        }
        String stencilName = this.readRandomStencil();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.stencil-list.stencil-loaded",
                stencilName
        ));
        File file = new File(PLUGIN_DATA_FOLDER, "/stencils/" + stencilName + ".vstencil");
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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                int currX = this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = -this.xRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = this.getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = blockDataReader.readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                                currZ++;
                                if (currZ == this.x - this.xRef) {
                                    currZ = -this.xRef;
                                    currX--;
                                    if (currX == -this.z + this.zRef) {
                                        currX = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
                                    blockDataReader.readBlockData(in)
                            );
                            currZ++;
                            currZ++;
                            if (currZ == this.x - this.xRef) {
                                currZ = -this.xRef;
                                currX--;
                                if (currX == -this.z + this.zRef) {
                                    currX = this.zRef;
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
                                if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) { // no reason to paste air over
                                    // air, and it prevents us
                                    // most of the time from
                                    // having to even check the
                                    // block.
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ++;
                                if (currZ == this.x - this.xRef) {
                                    currZ = -this.xRef;
                                    currX--;
                                    if (currX == -this.z + this.zRef) {
                                        currX = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) { // no reason to paste air over
                                // air, and it prevents us most of
                                // the time from having to even
                                // check the block.
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ++;
                            if (currZ == this.x - this.xRef) {
                                currZ = -this.xRef;
                                currX--;
                                if (currX == -this.z + this.zRef) {
                                    currX = this.zRef;
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
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ++;
                                if (currZ == this.x - this.xRef) {
                                    currZ = -this.xRef;
                                    currX--;
                                    if (currX == -this.z + this.zRef) {
                                        currX = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ++;
                            if (currZ == this.x - this.xRef) {
                                currZ = -this.xRef;
                                currX--;
                                if (currX == -this.z + this.zRef) {
                                    currX = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                messenger.sendMessage(Caption.of(
                        "voxelsniper.brush.stencil.pasted",
                        stencilName,
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
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.invalid-stencil-list-name"));
        }
    }

    private void stencilPaste90(Snipe snipe, StencilReader.BlockDataReader blockDataReader) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.file == null) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.missing-file"));
            return;
        }
        String stencilName = this.readRandomStencil();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.stencil-list.stencil-loaded",
                stencilName
        ));
        File file = new File(PLUGIN_DATA_FOLDER, "/stencils/" + stencilName + ".vstencil");
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
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                int currX = -this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = this.xRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = this.getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = blockDataReader.readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                                currZ--;
                                if (currZ == -this.x + this.xRef) {
                                    currZ = this.xRef;
                                    currX++;
                                    if (currX == this.z - this.zRef) {
                                        currX = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
                                    blockDataReader.readBlockData(in)
                            );
                            currZ--;
                            if (currZ == -this.x + this.xRef) {
                                currZ = this.xRef;
                                currX++;
                                if (currX == this.z - this.zRef) {
                                    currX = -this.zRef;
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
                                if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ--;
                                if (currZ == -this.x + this.xRef) {
                                    currZ = this.xRef;
                                    currX++;
                                    if (currX == this.z - this.zRef) {
                                        currX = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ--;
                            if (currZ == -this.x + this.xRef) {
                                currZ = this.xRef;
                                currX++;
                                if (currX == this.z - this.zRef) {
                                    currX = -this.zRef;
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
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ--;
                                if (currZ == -this.x + this.xRef) {
                                    currZ = this.xRef;
                                    currX++;
                                    if (currX == this.z - this.zRef) {
                                        currX = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = blockDataReader.readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ--;
                            if (currZ == -this.x + this.xRef) {
                                currZ = this.xRef;
                                currX++;
                                if (currX == this.z - this.zRef) {
                                    currX = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                messenger.sendMessage(Caption.of(
                        "voxelsniper.brush.stencil.pasted",
                        stencilName,
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
            messenger.sendMessage(Caption.of("voxelsniper.brush.stencil-list.invalid-stencil-list-name"));
        }
    }

    private void stencilPasteRotation(Snipe snipe) {
        // just randomly chooses a rotation and then calls stencilPaste.
        this.readStencilList();
        double random = Math.random();
        if (random < 0.26) {
            this.stencilPaste(snipe, StencilReader.BlockDataReader.BLOCK_DATA);
        } else if (random < 0.51) {
            this.stencilPaste90(snipe, StencilReader.BlockDataReader.BLOCK_DATA);
        } else if (random < 0.76) {
            this.stencilPaste180(snipe, StencilReader.BlockDataReader.BLOCK_DATA);
        } else {
            this.stencilPaste270(snipe, StencilReader.BlockDataReader.BLOCK_DATA);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(this.file == null
                        ? Caption.of("voxelsniper.brush.stencil-list.missing-file")
                        : Caption.of(
                                "voxelsniper.brush.stencil-list.loaded",
                                this.file.getName()
                        ))
                .send();
    }

}
