package com.thevoxelbox.voxelsniper.util.message;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MessageSender {

    private final VoxelSniperPlugin plugin;
    private final CommandSender sender;
    private final List<Component> messages = new ArrayList<>(0);

    public MessageSender(CommandSender sender) {
        this.plugin = VoxelSniperPlugin.plugin;
        this.sender = sender;
    }

    public MessageSender brushNameMessage(String brushName) {
        this.messages.add(Caption.of("voxelsniper.messenger.brush-name", brushName));
        return this;
    }

    public MessageSender performerNameMessage(String performerName) {
        this.messages.add(Caption.of("voxelsniper.messenger.performer-name", performerName));
        return this;
    }

    public MessageSender patternMessage(BrushPattern brushPattern) {
        this.messages.add(Caption.of("voxelsniper.messenger.pattern", brushPattern.getName()));
        return this;
    }

    public MessageSender replacePatternMessage(BrushPattern replaceBrushPattern) {
        this.messages.add(Caption.of("voxelsniper.messenger.replace-pattern", replaceBrushPattern.getName()));
        return this;
    }

    public MessageSender brushSizeMessage(int brushSize) {
        this.messages.add(Caption.of("voxelsniper.messenger.brush-size", brushSize));
        if (brushSize >= this.plugin.getVoxelSniperConfig().getBrushSizeWarningThreshold()) {
            this.messages.add(Caption.of("voxelsniper.messenger.large-brush-size"));
        }
        return this;
    }

    public MessageSender cylinderCenterMessage(int cylinderCenter) {
        this.messages.add(Caption.of("voxelsniper.messenger.cylinder-center", cylinderCenter));
        return this;
    }

    public MessageSender voxelHeightMessage(int voxelHeight) {
        this.messages.add(Caption.of("voxelsniper.messenger.voxel-height", voxelHeight));
        return this;
    }

    public MessageSender voxelListMessage(List<? extends BlockState> voxelList) {
        if (voxelList.isEmpty()) {
            this.messages.add(Caption.of("voxelsniper.messenger.voxel.list-empty"));
            return this;
        }

        this.messages.add(VoxelSniperText.formatList(
                voxelList,
                (state, state2) -> state.getAsString().compareTo(state2.getAsString()),
                state -> TextComponent.of(state.getAsString()),
                "voxelsniper.messenger.voxel"
        ));
        return this;
    }

    public MessageSender message(Component component) {
        this.messages.add(component);
        return this;
    }

    public void send() {
        for (int i = 0; i < this.messages.size(); i++) {
            VoxelSniperText.print(this.sender, messages.get(i), i == 0);
        }
    }

}
