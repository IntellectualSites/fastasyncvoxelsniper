package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
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
            messenger.sendMessage(ChatColor.DARK_AQUA + "The arrow writes the internal line buffer to the tearget " +
                    "sign.");
            messenger.sendMessage(ChatColor.DARK_AQUA + "The gunpowder reads the text of the target sign into the " +
                    "internal buffer.");
            messenger.sendMessage(ChatColor.DARK_AQUA + "Colors can be used using the \"&\" symbol.");
            messenger.sendMessage(ChatColor.GOLD + "Sign Overwrite Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b sio 1 [set|toggle] (...) -- Sets the text of the first sign line. " +
                    "(e.g. /b sio 1 set Blah Blah | /b sio 1 toggle)");
            messenger.sendMessage(ChatColor.AQUA + "/b sio 2 [set|toggle] (...) -- Sets the text of the second sign line. " +
                    "(e.g. /b sio 2 set Blah Blah | /b sio 2 toggle");
            messenger.sendMessage(ChatColor.AQUA + "/b sio 3 [set|toggle] (...) -- Sets the text of the third sign line. " +
                    "(e.g. /b sio 3 set Blah Blah | /b sio 3 toggle");
            messenger.sendMessage(ChatColor.AQUA + "/b sio 4 [set|toggle] (...) -- Sets the text of the fourth sign line. " +
                    "(e.g. /b sio 4 set Blah Blah | /b sio 4 toggle");
            messenger.sendMessage(ChatColor.AQUA + "/b sio clear -- Clears the line buffer. (Alias: /b sio c)");
            messenger.sendMessage(ChatColor.AQUA + "/b sio clearall -- Clears the line buffer and sets all lines back to " +
                    "enabled (Alias: /b sio ca)");
            messenger.sendMessage(ChatColor.AQUA + "/b sio multiple [true|false] -- Enables or disables ranged mode. (see Wiki " +
                    "for more information) (Alias: /b sio m [true|false])");
            messenger.sendMessage(ChatColor.AQUA + "/b sio save [n] -- Save you buffer to a file named n. (Alias: /b sio s [n])");
            messenger.sendMessage(ChatColor.AQUA + "/b sio open [n] -- Loads a buffer from a file named n. (Alias: /b sio o [n])");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("clear") || firstParameter.equalsIgnoreCase("c")) {
                    clearBuffer();
                    messenger.sendMessage(ChatColor.BLUE + "Internal text buffer cleard.");
                } else if (firstParameter.equalsIgnoreCase("clearall") || firstParameter.equalsIgnoreCase("ca")) {
                    clearBuffer();
                    resetStates();
                    messenger.sendMessage(ChatColor.BLUE + "Internal text buffer cleard and states back to enabled.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else {
                if (Stream.of("1", "2", "3", "4")
                        .anyMatch(firstParameter::equalsIgnoreCase)) {
                    String secondParameter = parameters[1];
                    Integer lineNumber = NumericParser.parseInteger(firstParameter);
                    int lineIndex = lineNumber - 1;

                    if (secondParameter.equalsIgnoreCase("set")) {
                        if (parameters.length < 3) {
                            messenger.sendMessage(ChatColor.RED + "Missing arguments, this command expects more.");
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
                            messenger.sendMessage(ChatColor.RED + "Warning: Text on line " + lineNumber + " exceeds the maximum " +
                                    "line length of " + MAX_SIGN_LINE_LENGTH + " characters. Your text will be cut.");
                            newTextBuilder = new StringBuilder(newTextBuilder.substring(0, MAX_SIGN_LINE_LENGTH));
                        }
                        String formattedText = ChatColor.translateAlternateColorCodes('&', newTextBuilder.toString());
                        this.signTextLines[lineIndex] = formattedText;
                        messenger.sendMessage(ChatColor.AQUA + "Line " + lineNumber + " set to: " + ChatColor.RESET + formattedText);
                    } else if (secondParameter.equalsIgnoreCase("toggle")) {
                        this.signLinesEnabled[lineIndex] = !this.signLinesEnabled[lineIndex];
                        messenger.sendMessage(ChatColor.BLUE + "Line " + firstParameter + " is " + ChatColor.GREEN +
                                (this.signLinesEnabled[lineIndex] ? "enabled" : "disabled") + ChatColor.BLUE + ".");
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                    }
                } else if (firstParameter.equalsIgnoreCase("multiple") || firstParameter.equalsIgnoreCase("m")) {
                    this.rangedMode = Boolean.parseBoolean(parameters[1]);
                    messenger.sendMessage(ChatColor.BLUE + "Ranged mode is " + ChatColor.GREEN + (this.rangedMode ? "enabled" :
                            "disabled") + ChatColor.BLUE + ".");
                    if (this.rangedMode) {
                        messenger.sendMessage(ChatColor.GREEN + "Brush size set to: " + ChatColor.RED + toolkitProperties.getBrushSize());
                        messenger.sendMessage(ChatColor.AQUA + "Brush height set to: " + ChatColor.RED + toolkitProperties.getVoxelHeight());
                    }
                } else if (firstParameter.equalsIgnoreCase("save") || firstParameter.equalsIgnoreCase("s")) {
                    String fileName = parameters[1];
                    saveBufferToFile(snipe, fileName);
                } else if (firstParameter.equalsIgnoreCase("open") || firstParameter.equalsIgnoreCase("o")) {
                    String fileName = parameters[1];
                    textChanged = loadBufferFromFile(snipe, fileName);
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
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
            messenger.sendMessage(ChatColor.RED + "Target block is not a sign.");
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
            messenger.sendMessage(ChatColor.RED + "Target block is not a sign.");
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
            messenger.sendMessage(ChatColor.RED + "Did not found any sign in selection box.");
        }
    }

    private void displayBuffer(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(ChatColor.BLUE + "Buffer text set to: ");
        for (int index = 0; index < this.signTextLines.length; index++) {
            messenger.sendMessage((this.signLinesEnabled[index] ? ChatColor.GREEN + "(E): " : ChatColor.RED + "(D): ")
                    + ChatColor.GRAY + this.signTextLines[index]);
        }
    }

    /**
     * Saves the buffer to file.
     */
    private void saveBufferToFile(Snipe snipe, String fileName) {
        SnipeMessenger messenger = snipe.createMessenger();
        File store = new File(PLUGIN_DATA_FOLDER, "/signs/" + fileName + ".vsign");
        if (store.exists()) {
            messenger.sendMessage(ChatColor.RED + "This file already exists.");
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
            messenger.sendMessage(ChatColor.BLUE + "File saved successfully.");
        } catch (IOException exception) {
            messenger.sendMessage(ChatColor.RED + "Failed to save file. " + exception.getMessage());
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
            messenger.sendMessage(ChatColor.RED + "This file does not exist.");
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
            messenger.sendMessage(ChatColor.BLUE + "File loaded successfully.");
            return true;
        } catch (IOException exception) {
            messenger.sendMessage(ChatColor.RED + "Failed to load file. " + exception.getMessage());
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
        messenger.sendMessage(ChatColor.BLUE + "Buffer text: ");
        for (int index = 0; index < this.signTextLines.length; index++) {
            messenger.sendMessage((this.signLinesEnabled[index]
                    ? ChatColor.GREEN + "(E): "
                    : ChatColor.RED + "(D): ") + ChatColor.GRAY + this.signTextLines[index]);
        }
        messenger.sendMessage(ChatColor.BLUE + String.format(
                "Ranged mode is %s",
                ChatColor.GREEN + (this.rangedMode ? "enabled" : "disabled")
        ));
        if (this.rangedMode) {
            messenger.sendBrushSizeMessage();
            messenger.sendVoxelHeightMessage();
        }
    }

}
