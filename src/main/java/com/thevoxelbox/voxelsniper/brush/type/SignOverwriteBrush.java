package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SignOverwriteBrush extends AbstractBrush {

    private static final int MAX_SIGN_LINE_LENGTH = 15;
    private static final int NUM_SIGN_LINES = 4;

    private final String[] signTextLines = new String[NUM_SIGN_LINES];
    private final boolean[] signLinesEnabled = new boolean[]{true, true, true, true};
    private boolean rangedMode;

    public SignOverwriteBrush() {
        clearBuffer();
        resetStates();
    }

    @Override
    public void loadProperties() {
        File dataFolder = new File(PLUGIN_DATA_FOLDER, "/signs");
        dataFolder.mkdirs();
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        String firstParameter = parameters[0];
        boolean textChanged = false;

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("clear") || firstParameter.equalsIgnoreCase("c")) {
                    clearBuffer();
                    messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.cleared"));
                } else if (firstParameter.equalsIgnoreCase("clearall") || firstParameter.equalsIgnoreCase("ca")) {
                    clearBuffer();
                    resetStates();
                    messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.cleared-reset"));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else {
                if (Stream.of("1", "2", "3", "4")
                        .anyMatch(firstParameter::equalsIgnoreCase)) {
                    String secondParameter = parameters[1];
                    Integer lineNumber = NumericParser.parseInteger(firstParameter);
                    if (lineNumber == null) {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", firstParameter));
                        return;
                    }
                    int lineIndex = lineNumber - 1;

                    if (secondParameter.equalsIgnoreCase("set")) {
                        if (parameters.length < 3) {
                            messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
                            return;
                        }

                        StringBuilder newTextBuilder = new StringBuilder();
                        // Go through the end of the array.
                        for (int index = 2; index < parameters.length; index++) {
                            String word = parameters[index];
                            newTextBuilder.append(word);

                            if (index < parameters.length - 1) {
                                newTextBuilder.append(" ");
                            }
                        }

                        // Check the line length and cut the text if needed.
                        if (newTextBuilder.length() > MAX_SIGN_LINE_LENGTH) {
                            messenger.sendMessage(Caption.of(
                                    "voxelsniper.brush.sign-overwrite.invalid-length",
                                    lineNumber,
                                    MAX_SIGN_LINE_LENGTH
                            ));
                            newTextBuilder = new StringBuilder(newTextBuilder.substring(0, MAX_SIGN_LINE_LENGTH));
                        }
                        String formattedText = ChatColor.translateAlternateColorCodes('&', newTextBuilder.toString());
                        this.signTextLines[lineIndex] = formattedText;
                        messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.line-set", lineNumber, formattedText));
                    } else if (secondParameter.equalsIgnoreCase("toggle")) {
                        this.signLinesEnabled[lineIndex] = !this.signLinesEnabled[lineIndex];
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.brush.sign-overwrite.line-status",
                                VoxelSniperText.getStatus(this.signLinesEnabled[lineIndex])
                        ));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                    }
                } else if (firstParameter.equalsIgnoreCase("multiple") || firstParameter.equalsIgnoreCase("m")) {
                    this.rangedMode = Boolean.parseBoolean(parameters[1]);

                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.sign-overwrite.set-ranged-mode",
                            VoxelSniperText.getStatus(this.rangedMode)
                    ));
                    if (this.rangedMode) {
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.brush.sign-overwrite.brush-size",
                                toolkitProperties.getBrushSize()
                        ));
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.brush.sign-overwrite.brush-height",
                                toolkitProperties.getVoxelHeight()
                        ));
                    }
                } else if (firstParameter.equalsIgnoreCase("save") || firstParameter.equalsIgnoreCase("s")) {
                    String fileName = parameters[1];
                    saveBufferToFile(snipe, fileName);
                } else if (firstParameter.equalsIgnoreCase("open") || firstParameter.equalsIgnoreCase("o")) {
                    String fileName = parameters[1];
                    textChanged = loadBufferFromFile(snipe, fileName);
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            }
        }

        if (textChanged) {
            displayBuffer(snipe);
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of(
                    "1", "2", "3", "4",
                    "clear", "c", "clearall", "ca",
                    "multiple", "m", "save", "s", "open", "o"
            ), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (Stream.of("1", "2", "3", "4")
                    .anyMatch(firstParameter::equalsIgnoreCase)) {
                String parameter = parameters[1];
                return super.sortCompletions(Stream.of("set", "toggle"), parameter, 1);
            } else if (firstParameter.equalsIgnoreCase("multiple")) {
                String parameter = parameters[1];
                return super.sortCompletions(Stream.of("on", "off"), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        if (this.rangedMode) {
            setRanged(snipe);
        } else {
            setSingle(snipe);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BaseBlock block = getFullBlock(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        if (DeprecationUtil.isSign(block.getBlockType())) {
            CompoundBinaryTag tag = block.getNbt();
            if (tag == null) {
                return;
            }
            for (int i = 0; i < this.signTextLines.length; i++) {
                if (this.signLinesEnabled[i]) {
                    this.signTextLines[i] = tag.getString("Text" + (i + 1));
                }
            }
            displayBuffer(snipe);
        } else {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.invalid-block"));
        }
    }

    /**
     * Sets the text of a given sign.
     */
    private void setSignText(int x, int y, int z, BaseBlock block) {
        CompoundBinaryTag tag = block.getNbt();
        if (tag == null) {
            return;
        }

        for (int i = 0; i < this.signTextLines.length; i++) {
            if (this.signLinesEnabled[i]) {
                tag = tag.putString("Text" + (i + 1), toJson(this.signTextLines[i]));
            }
        }
        block = block.toBaseBlock(tag);
        setBlock(x, y, z, block);
    }

    /**
     * Sets the text of the target sign if the target block is a sign.
     */
    private void setSingle(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BaseBlock block = getFullBlock(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        if (DeprecationUtil.isSign(block.getBlockType())) {
            setSignText(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), block);
        } else {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.invalid-block"));
        }
    }

    /**
     * Sets all signs in a range of box{x=z=brushSize*2+1 ; z=voxelHeight*2+1}.
     */
    private void setRanged(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockVector3 targetBlock = getTargetBlock();
        int brushSize = toolkitProperties.getBrushSize();
        int voxelHeight = toolkitProperties.getVoxelHeight();
        int minX = targetBlock.getX() - brushSize;
        int maxX = targetBlock.getX() + brushSize;
        int minY = targetBlock.getY() - voxelHeight;
        int maxY = targetBlock.getY() + voxelHeight;
        int minZ = targetBlock.getZ() - brushSize;
        int maxZ = targetBlock.getZ() + brushSize;
        boolean signFound = false; // indicates whether or not a sign was set
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BaseBlock block = getFullBlock(x, y, z);
                    if (DeprecationUtil.isSign(block.getBlockType())) {
                        setSignText(x, y, z, block);
                        signFound = true;
                    }
                }
            }
        }
        if (!signFound) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.no-sign"));
        }
    }

    private void displayBuffer(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.set-buffer"));
        for (int index = 0; index < this.signTextLines.length; index++) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.sign-overwrite.buffer-line",
                    index + 1,
                    VoxelSniperText.getStatus(this.signLinesEnabled[index]),
                    this.signTextLines[index]
            ));
        }
    }

    /**
     * Saves the buffer to file.
     */
    private void saveBufferToFile(Snipe snipe, String fileName) {
        SnipeMessenger messenger = snipe.createMessenger();
        File store = new File(PLUGIN_DATA_FOLDER, "/signs/" + fileName + ".vsign");
        if (store.exists()) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-exists"));
            return;
        }
        try {
            store.createNewFile();
            FileWriter outFile = new FileWriter(store);
            BufferedWriter outStream = new BufferedWriter(outFile);
            for (int i = 0; i < this.signTextLines.length; i++) {
                outStream.write(this.signLinesEnabled[i] + "\n");
                outStream.write(this.signTextLines[i] + "\n");
            }
            outStream.close();
            outFile.close();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-saved"));
        } catch (IOException exception) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-save-failed", exception.getMessage()));
            exception.printStackTrace();
        }
    }

    /**
     * Loads a buffer from a file.
     *
     * @return {@code true} if file has been loaded successfully, {@code false} otherwise
     */
    private boolean loadBufferFromFile(Snipe snipe, String fileName) {
        SnipeMessenger messenger = snipe.createMessenger();
        File store = new File(PLUGIN_DATA_FOLDER, "/signs/" + fileName + ".vsign");
        if (!store.exists()) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-missing"));
            return false;
        }
        try {
            FileReader inFile = new FileReader(store);
            BufferedReader inStream = new BufferedReader(inFile);
            for (int i = 0; i < this.signTextLines.length; i++) {
                this.signLinesEnabled[i] = Boolean.parseBoolean(inStream.readLine());
                this.signTextLines[i] = inStream.readLine();
            }
            inStream.close();
            inFile.close();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-loaded"));
            return true;
        } catch (IOException exception) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-load-failed", exception.getMessage()));
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Clears the internal text buffer. (Sets it to empty strings)
     */
    private void clearBuffer() {
        Arrays.fill(this.signTextLines, "");
    }

    /**
     * Resets line enabled states to enabled.
     */
    private void resetStates() {
        Arrays.fill(this.signLinesEnabled, true);
    }

    private String toJson(String oldInput) {
        if (oldInput == null || oldInput.isEmpty()) {
            return "";
        }
        return LegacyComponentSerializer.legacy().serialize(TextComponent.of(oldInput));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.set-buffer"));
        for (int index = 0; index < this.signTextLines.length; index++) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.sign-overwrite.buffer-line",
                    index + 1,
                    VoxelSniperText.getStatus(this.signLinesEnabled[index]),
                    this.signTextLines[index]
            ));
        }
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.sign-overwrite.set-ranged-mode",
                VoxelSniperText.getStatus(this.rangedMode)
        ));
        if (this.rangedMode) {
            messenger.sendBrushSizeMessage();
            messenger.sendVoxelHeightMessage();
        }
    }

}
