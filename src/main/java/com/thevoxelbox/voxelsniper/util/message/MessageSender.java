package com.thevoxelbox.voxelsniper.util.message;

import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageSender {

    private final VoxelSniperPlugin plugin;
    private final CommandSender sender;
    private final List<String> messages = new ArrayList<>(0);

    public MessageSender(CommandSender sender) {
        this.plugin = VoxelSniperPlugin.plugin;
        this.sender = sender;
    }

    public MessageSender brushNameMessage(String brushName) {
        this.messages.add(ChatColor.AQUA + "Brush Type: " + ChatColor.LIGHT_PURPLE + brushName);
        return this;
    }

    public MessageSender performerNameMessage(String performerName) {
        this.messages.add(ChatColor.DARK_PURPLE + "Performer: " + ChatColor.DARK_GREEN + performerName);
        return this;
    }

    public MessageSender patternMessage(BrushPattern brushPattern) {
        this.messages.add(ChatColor.GOLD + "Voxel: " + ChatColor.RED + brushPattern.getName());
        return this;
    }

    public MessageSender replacePatternMessage(BrushPattern replaceBrushPattern) {
        this.messages.add(ChatColor.AQUA + "Replace: " + ChatColor.RED + replaceBrushPattern.getName());
        return this;
    }

    public MessageSender brushSizeMessage(int brushSize) {
        this.messages.add(ChatColor.GREEN + "Brush Size: " + ChatColor.DARK_RED + brushSize);
        if (brushSize >= this.plugin.getVoxelSniperConfig().getBrushSizeWarningThreshold()) {
            this.messages.add(ChatColor.RED + "WARNING: Large brush size selected!");
        }
        return this;
    }

    public MessageSender cylinderCenterMessage(int cylinderCenter) {
        this.messages.add(ChatColor.BLUE + "Brush Center: " + ChatColor.DARK_RED + cylinderCenter);
        return this;
    }

    public MessageSender voxelHeightMessage(int voxelHeight) {
        this.messages.add(ChatColor.DARK_AQUA + "Brush Height: " + ChatColor.DARK_RED + voxelHeight);
        return this;
    }

    public MessageSender voxelListMessage(List<? extends BlockState> voxelList) {
        if (voxelList.isEmpty()) {
            this.messages.add(ChatColor.DARK_GREEN + "No blocks selected!");
        }
        String message = voxelList.stream()
                .map(state -> ChatColor.AQUA + state.getAsString())
                .collect(Collectors.joining(ChatColor.WHITE + ", ",
                        ChatColor.DARK_GREEN + "Block Types Selected: ", ""
                ));
        this.messages.add(message);
        return this;
    }

    public MessageSender message(String message) {
        this.messages.add(message);
        return this;
    }

    public void send() {
        this.messages.forEach(this.sender::sendMessage);
    }

}
