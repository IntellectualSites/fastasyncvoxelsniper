package com.thevoxelbox.voxelsniper.command.executor;

import com.fastasyncworldedit.core.Fawe;
import com.intellectualsites.paster.IncendoPaster;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.command.TabCompleter;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VoxelSniperExecutor implements CommandExecutor, TabCompleter {

    private final VoxelSniperPlugin plugin;

    public VoxelSniperExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Sniper sniper = (sender instanceof Player) ? sniperRegistry.registerAndGetSniper((Player) sender) : null;

        if (arguments.length >= 1) {
            String firstArgument = arguments[0];
            if (firstArgument.equalsIgnoreCase("brushes")) {
                Toolkit toolkit = sniper == null ? null : sniper.getCurrentToolkit();
                BrushProperties brushProperties = toolkit == null ? null : toolkit.getCurrentBrushProperties();
                sender.sendMessage(
                        this.plugin.getBrushRegistry().getBrushesProperties().entrySet().stream()
                                .map(entry -> (entry.getValue() == brushProperties ? ChatColor.GOLD : ChatColor.GRAY)
                                        + entry.getKey())
                                .sorted()
                                .collect(Collectors.joining(ChatColor.WHITE + ", ",
                                        ChatColor.AQUA + "Available brushes: ", ""
                                ))
                );
                return;
            } else if (firstArgument.equalsIgnoreCase("range")) {
                if (sniper == null) {
                    return;
                }
                Toolkit toolkit = sniper.getCurrentToolkit();
                if (toolkit == null) {
                    return;
                }
                ToolkitProperties toolkitProperties = toolkit.getProperties();
                if (toolkitProperties == null) {
                    return;
                }
                if (arguments.length == 2) {
                    Integer range = NumericParser.parseInteger(arguments[1]);
                    if (range != null) {
                        if (range < 1) {
                            sender.sendMessage("Values less than 1 are not allowed.");
                            return;
                        } else {
                            toolkitProperties.setBlockTracerRange(range);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid number.");
                        return;
                    }
                } else {
                    toolkitProperties.setBlockTracerRange(0);
                }
                Integer blockTracerRange = toolkitProperties.getBlockTracerRange();
                sender.sendMessage(ChatColor.GOLD + "Distance Restriction toggled " + ChatColor.DARK_RED + (blockTracerRange == null
                        ? "off"
                        : "on") + ChatColor.GOLD + ". Range is " + ChatColor.LIGHT_PURPLE + blockTracerRange);
                return;
            } else if (firstArgument.equalsIgnoreCase("perf")) {
                sender.sendMessage(
                        this.plugin.getPerformerRegistry().getPerformerProperties().keySet().stream()
                                .map(alias -> ChatColor.GRAY + alias)
                                .sorted()
                                .collect(Collectors.joining(ChatColor.WHITE + ", ",
                                        ChatColor.AQUA + "Available performers (abbreviated): ", ""
                                ))
                );
                return;
            } else if (firstArgument.equalsIgnoreCase("perflong")) {
                sender.sendMessage(
                        this.plugin.getPerformerRegistry().getPerformerProperties().values().stream()
                                .map(properties -> ChatColor.GRAY + properties.getName())
                                .sorted()
                                .collect(Collectors.joining(ChatColor.WHITE + ", ",
                                        ChatColor.AQUA + "Available performers: ", ""
                                ))
                );
                return;
            } else if (firstArgument.equalsIgnoreCase("enable")) {
                if (sniper == null) {
                    return;
                }
                sniper.setEnabled(true);
                sender.sendMessage("FastAsyncVoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return;
            } else if (firstArgument.equalsIgnoreCase("disable")) {
                if (sniper == null) {
                    return;
                }
                sniper.setEnabled(false);
                sender.sendMessage("FastAsyncVoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return;
            } else if (firstArgument.equalsIgnoreCase("toggle")) {
                if (sniper == null) {
                    return;
                }
                sniper.setEnabled(!sniper.isEnabled());
                sender.sendMessage("FastAsyncVoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return;
            } else if (firstArgument.equalsIgnoreCase("info")) {
                sender.sendMessage(plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion());
                sender.sendMessage(plugin.getDescription().getDescription());
                sender.sendMessage("Website: " + plugin.getDescription().getWebsite());
                return;
            } else if (firstArgument.equalsIgnoreCase("reload")) {
                if (sender.hasPermission("voxelsniper.admin")) {
                    plugin.reload();
                    sender.sendMessage(ChatColor.GREEN + "FastAsyncVoxelSniper config reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You are not allowed to use this command. You're missing the permission " +
                            "node 'voxelsniper.admin'");
                }
                return;
            } else if (firstArgument.equalsIgnoreCase("debugpaste")) {
                if (sender.hasPermission("voxelsniper.admin")) {
                    String destination;
                    try {
                        final File logFile = new File("logs/latest.log");
                        final File config = new File(plugin.getDataFolder(), "config.yml");
                        destination = IncendoPaster.debugPaste(logFile, Fawe.imp().getDebugInfo(), config);
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.RED + "Failed to upload debugpaste because of " + e);
                        return;
                    }
                    sender.sendMessage(destination);
                } else {
                    sender.sendMessage(ChatColor.RED + "You are not allowed to use this command. You're missing the permission " +
                            "node 'voxelsniper.admin'");
                }
                return;
            }
        }
        if (sniper != null) {
            sender.sendMessage(ChatColor.DARK_RED + "FastAsyncVoxelSniper - Current Brush Settings:");
            sniper.sendInfo(sender);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] arguments) {
        if (arguments.length == 1) {
            String argument = arguments[0];
            String argumentLowered = argument.toLowerCase(Locale.ROOT);
            return Stream.concat(
                            Stream.of("brushes", "range", "perf", "perflong", "enable", "disable", "toggle", "info"),
                            sender.hasPermission("voxelsniper.admin") ? Stream.of("reload", "debugpaste") : Stream.empty()
                    )
                    .filter(subCommand -> subCommand.startsWith(argumentLowered))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
