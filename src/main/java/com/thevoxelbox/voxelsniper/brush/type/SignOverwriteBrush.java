package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotation.specifier.Liberal;
import org.incendo.cloud.annotation.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.internal.Constants;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag;
import com.sk89q.worldedit.util.nbt.ListBinaryTag;
import com.sk89q.worldedit.util.nbt.StringBinaryTag;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequireToolkit
@Command(value = "brush|b sign_overwrite|signoverwrite|sio")
@Permission("voxelsniper.brush.signoverwrite")
public class SignOverwriteBrush extends AbstractBrush {

    private static final Side DEFAULT_SIDE = Side.FRONT;

    private static final List<String> SIDES = Arrays.stream(Side.values())
            .map(Side::getName)
            .toList();

    private static final int MAX_SIGN_LINE_LENGTH = 15;
    private static final int NUM_SIGN_LINES = 4;
    private static final char LEGACY_AMPERSAND = '&';

    private final Component[] signTextLines = new Component[NUM_SIGN_LINES];
    private final boolean[] signLinesEnabled = new boolean[]{true, true, true, true};
    private Side side;
    private boolean rangedMode;

    public SignOverwriteBrush() {
        clearBuffer();
        resetStates();
    }

    @Override
    public void loadProperties() {
        this.side = (Side) getEnumProperty("default-side", Side.class, DEFAULT_SIDE);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.sign-overwrite.info"));
    }

    @Command("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                Arrays.stream(Side.values()).toList(),
                (side, side2) -> side.getName().compareTo(side2.getName()),
                Side::getFullName,
                side -> side,
                this.side,
                "voxelsniper.brush.sign-overwrite"
        ));
    }

    @Command("clear|c")
    public void onBrushClear(
            final @NotNull Snipe snipe
    ) {
        this.clearBuffer();

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.cleared"));
    }

    @Command("clearall|ca")
    public void onBrushClearall(
            final @NotNull Snipe snipe
    ) {
        this.clearBuffer();
        this.resetStates();

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.cleared-reset"));
    }

    @Command("side <side>")
    public void onBrushSide(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("side") Side side
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (!isHangingSignsSupported()) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.legacy-side"));
            return;
        }
        this.side = side;

        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.sign-overwrite.set-side",
                this.side.getFullName()
        ));
    }

    @SuppressWarnings("deprecation") // Paper deprecation
    @Command("<line> set <text>")
    public void onBrushLineSet(
            final @NotNull Snipe snipe,
            final @Argument("line") @Range(min = "1", max = "4") int line,
            final @NotNull @Argument("text") @Greedy String text
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        TextComponent formattedText = LegacyComponentSerializer.legacy().deserialize(text, LEGACY_AMPERSAND);
        int lineIndex = line - 1;

        // Checks the line length and cut the text if needed.
        // There is no plain text serializer available yet, rely on legacy chat color stripping for now.
        if (ChatColor.stripColor(toLegacyText(formattedText)).length() > MAX_SIGN_LINE_LENGTH) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.sign-overwrite.invalid-length",
                    line,
                    MAX_SIGN_LINE_LENGTH
            ));
            formattedText = LegacyComponentSerializer.legacy()
                    .deserialize(text.substring(0, MAX_SIGN_LINE_LENGTH), LEGACY_AMPERSAND);
        }

        this.signTextLines[lineIndex] = formattedText;
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.sign-overwrite.set-line",
                line,
                formattedText
        ));
    }

    @Command("<line> toggle")
    public void onBrushLineToggle(
            final @NotNull Snipe snipe,
            final @Argument("line") @Range(min = "1", max = "4") int line
    ) {
        int lineIndex = line - 1;
        this.signLinesEnabled[lineIndex] = !this.signLinesEnabled[lineIndex];

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.sign-overwrite.line-status",
                line,
                VoxelSniperText.getStatus(this.signLinesEnabled[lineIndex])
        ));
    }

    @Command("multiple|m <ranged-mode>")
    public void onBrushMultiple(
            final @NotNull Snipe snipe,
            final @Argument("ranged-mode") @Liberal boolean rangedMode
    ) {
        this.rangedMode = rangedMode;

        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
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
    }

    @Command("save|s <sign>")
    public void onBrushSave(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "sign", parserName = "sign-file_parser") File sign
    ) {
        this.saveBufferToFile(snipe, sign);
    }

    @Command("open|o <sign>")
    public void onBrushOpen(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "sign", parserName = "sign-file_parser") File sign
    ) {
        if (this.loadBufferFromFile(snipe, sign)) {
            this.displayBuffer(snipe);
        }
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
        if (isSign(block.getBlockType())) {
            CompoundBinaryTag tag = block.getNbt();
            if (tag == null) {
                return;
            }

            if (isHangingSignsSupported()) {
                // 1.20+ behavior, with two-side text.
                CompoundBinaryTag text = tag.getCompound(side.getTagName());
                ListBinaryTag messages = text.getList("messages");
                for (int i = 0; i < this.signTextLines.length; i++) {
                    if (this.signLinesEnabled[i]) {
                        this.signTextLines[i] = fromJson(messages.getString(i));
                    }
                }
            } else {
                for (int i = 0; i < this.signTextLines.length; i++) {
                    if (this.signLinesEnabled[i]) {
                        this.signTextLines[i] = fromJson(tag.getString("Text" + (i + 1)));
                    }
                }
            }
            displayBuffer(snipe);
        } else {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.invalid-block"));
        }
    }

    private boolean isHangingSignsSupported() {
        int dataVersion = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataVersion();
        return dataVersion >= Constants.DATA_VERSION_MC_1_20;
    }

    private boolean isSign(BlockType blockType) {
        return DeprecationUtil.isSign(blockType) || BlockCategories.ALL_HANGING_SIGNS.contains(blockType);
    }

    /**
     * Sets the text of a given sign.
     */
    private void setSignText(int x, int y, int z, BaseBlock block) {
        CompoundBinaryTag tag = block.getNbt();
        if (tag == null) {
            return;
        }

        if (isHangingSignsSupported()) {
            // 1.20+ behavior, with two-sided text.
            CompoundBinaryTag text = tag.getCompound(side.getTagName());
            ListBinaryTag messages = text.getList("messages");
            for (int i = 0; i < this.signTextLines.length; i++) {
                if (this.signLinesEnabled[i]) {
                    messages = messages.set(i, StringBinaryTag.stringBinaryTag(toJson(this.signTextLines[i])), ignored -> {
                    });
                }
            }
            text = text.put("messages", messages);
            tag = tag.put(side.getTagName(), text);
        } else {
            // Legacy behavior with only one-sided text.
            for (int i = 0; i < this.signTextLines.length; i++) {
                if (this.signLinesEnabled[i]) {
                    tag = tag.putString("Text" + (i + 1), toJson(this.signTextLines[i]));
                }
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
        if (isSign(block.getBlockType())) {
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
                    if (isSign(block.getBlockType())) {
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
    private void saveBufferToFile(Snipe snipe, File store) {
        SnipeMessenger messenger = snipe.createMessenger();
        try {
            store.createNewFile();
            FileWriter outFile = new FileWriter(store);
            BufferedWriter outStream = new BufferedWriter(outFile);
            for (int i = 0; i < this.signTextLines.length; i++) {
                outStream.write(this.signLinesEnabled[i] + "\n");
                outStream.write(toLegacyText(this.signTextLines[i]) + "\n");
            }
            outStream.close();
            outFile.close();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-saved"));
        } catch (IOException e) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-save-failed", e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Loads a buffer from a file.
     *
     * @return {@code true} if file has been loaded successfully, {@code false} otherwise
     */
    private boolean loadBufferFromFile(Snipe snipe, File store) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (!store.exists()) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-missing"));
            return false;
        }
        try {
            FileReader inFile = new FileReader(store);
            BufferedReader inStream = new BufferedReader(inFile);
            for (int i = 0; i < this.signTextLines.length; i++) {
                this.signLinesEnabled[i] = Boolean.parseBoolean(inStream.readLine());
                this.signTextLines[i] = fromLegacyText(inStream.readLine());
            }
            inStream.close();
            inFile.close();
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-loaded"));
            return true;
        } catch (IOException e) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.sign-overwrite.file-load-failed", e.getMessage()));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clears the internal text buffer. (Sets it to empty strings)
     */
    private void clearBuffer() {
        Arrays.fill(this.signTextLines, TextComponent.empty());
    }

    /**
     * Resets line enabled states to enabled.
     */
    private void resetStates() {
        Arrays.fill(this.signLinesEnabled, true);
    }

    private String toJson(Component input) {
        return GsonComponentSerializer.INSTANCE.serialize(input == null
                ? TextComponent.empty() : input);
    }

    private Component fromJson(String input) {
        return GsonComponentSerializer.INSTANCE.deserialize(input);
    }

    private String toLegacyText(Component input) {
        return LegacyComponentSerializer.legacy().serialize(input);
    }

    private TextComponent fromLegacyText(String input) {
        return LegacyComponentSerializer.legacy().deserialize(input);
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
                "voxelsniper.brush.sign-overwrite.set-side",
                side.getFullName()
        ));
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.sign-overwrite.set-ranged-mode",
                VoxelSniperText.getStatus(this.rangedMode)
        ));
        if (this.rangedMode) {
            messenger.sendBrushSizeMessage();
            messenger.sendVoxelHeightMessage();
        }
    }

    /**
     * Available types of sides.
     */
    public enum Side {

        FRONT("front", "front_text"),
        BACK("back", "back_text");

        private final String name;
        private final String tagName;

        Side(String name, String tagName) {
            this.name = name;
            this.tagName = tagName;
        }

        public String getName() {
            return name;
        }

        public String getTagName() {
            return tagName;
        }

        public TranslatableComponent getFullName() {
            return Caption.of("voxelsniper.brush.sign-overwrite.side." + this.name);
        }
    }

}
