package com.thevoxelbox.voxelsniper.util.message;

import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class Messenger {

    private final VoxelSniperPlugin plugin;
    private final CommandSender sender;

    public Messenger(VoxelSniperPlugin plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    public void sendBrushNameMessage(String brushName) {
        sendMessage(ChatColor.AQUA + "Brush Type: " + ChatColor.LIGHT_PURPLE + brushName);
    }

    public void sendPerformerNameMessage(String performerName) {
        sendMessage(ChatColor.DARK_PURPLE + "Performer: " + ChatColor.DARK_GREEN + performerName);
    }

    public void sendPatternMessage(BrushPattern brushPattern) {
        sendMessage(ChatColor.GOLD + "Voxel: " + ChatColor.RED + brushPattern.getName());
    }

    public void sendReplacePatternMessage(BrushPattern replaceBrushPattern) {
        sendMessage(ChatColor.AQUA + "Replace: " + ChatColor.RED + replaceBrushPattern.getName());
    }

    public void sendBrushSizeMessage(int brushSize) {
        sendMessage(ChatColor.GREEN + "Brush Size: " + ChatColor.DARK_RED + brushSize);
        if (brushSize >= this.plugin.getVoxelSniperConfig().getBrushSizeWarningThreshold()) {
            sendMessage(ChatColor.RED + "WARNING: Large brush size selected!");
        }
    }

    public void sendCylinderCenterMessage(int cylinderCenter) {
        sendMessage(ChatColor.BLUE + "Brush Center: " + ChatColor.DARK_RED + cylinderCenter);
    }

    public void sendVoxelHeightMessage(int voxelHeight) {
        sendMessage(ChatColor.DARK_AQUA + "Brush Height: " + ChatColor.DARK_RED + voxelHeight);
    }

    public void sendVoxelListMessage(List<? extends BlockState> voxelList) {
        if (voxelList.isEmpty()) {
            sendMessage(ChatColor.DARK_GREEN + "No blocks selected!");
        }
        String message = voxelList.stream()
                .map(state -> ChatColor.AQUA + state.getAsString())
                .collect(Collectors.joining(ChatColor.WHITE + ", ",
                        ChatColor.DARK_GREEN + "Block Types Selected: ", ""
                ));
        sendMessage(message);
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

}
