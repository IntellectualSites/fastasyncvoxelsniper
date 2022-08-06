package com.thevoxelbox.voxelsniper.util.message;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Messenger {

    private final VoxelSniperPlugin plugin;
    private final CommandSender sender;
    private int sent;

    public Messenger(VoxelSniperPlugin plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        this.sent = 0;
    }

    public void sendBrushNameMessage(String brushName) {
        sendMessage(Caption.of("voxelsniper.messenger.brush-name", brushName));
    }

    public void sendPerformerNameMessage(String performerName) {
        sendMessage(Caption.of("voxelsniper.messenger.performer-name", performerName));
    }

    public void sendPatternMessage(BrushPattern brushPattern) {
        sendMessage(Caption.of("voxelsniper.messenger.pattern", brushPattern.getName()));
    }

    public void sendReplacePatternMessage(BrushPattern replaceBrushPattern) {
        sendMessage(Caption.of("voxelsniper.messenger.replace-pattern", replaceBrushPattern.getName()));
    }

    public void sendBrushSizeMessage(int brushSize) {
        sendMessage(Caption.of("voxelsniper.messenger.brush-size", brushSize));
        if (brushSize >= this.plugin.getVoxelSniperConfig().getBrushSizeWarningThreshold()) {
            sendMessage(Caption.of("voxelsniper.messenger.large-brush-size"));
        }
    }

    public void sendCylinderCenterMessage(int cylinderCenter) {
        sendMessage(Caption.of("voxelsniper.messenger.cylinder-center", cylinderCenter));
    }

    public void sendVoxelHeightMessage(int voxelHeight) {
        sendMessage(Caption.of("voxelsniper.messenger.voxel-height", voxelHeight));
    }

    public void sendVoxelListMessage(List<? extends BlockState> voxelList) {
        if (voxelList.isEmpty()) {
            sendMessage(Caption.of("voxelsniper.messenger.voxel.list-empty"));
            return;
        }

        sendMessage(VoxelSniperText.formatList(
                voxelList,
                (state, state2) -> state.getAsString().compareTo(state2.getAsString()),
                state -> TextComponent.of(state.getAsString()),
                "voxelsniper.messenger.voxel"
        ));
    }

    public void sendMessage(Component component) {
        VoxelSniperText.print(this.sender, component, this.sent++ == 0);
    }

}
