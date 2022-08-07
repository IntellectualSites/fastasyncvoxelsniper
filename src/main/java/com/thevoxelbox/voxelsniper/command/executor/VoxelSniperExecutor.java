package com.thevoxelbox.voxelsniper.command.executor;

import com.fastasyncworldedit.core.Fawe;
import com.fastasyncworldedit.core.configuration.Caption;
import com.intellectualsites.paster.IncendoPaster;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.command.TabCompleter;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

                VoxelSniperText.print(
                        sender,
                        VoxelSniperText.formatListWithCurrent(
                                this.plugin.getBrushRegistry().getBrushesProperties().entrySet(),
                                (entry, entry2) -> entry.getKey().compareTo(entry2.getKey()),
                                entry -> TextComponent.of(entry.getKey()),
                                Map.Entry::getValue,
                                brushProperties,
                                "voxelsniper.command.voxel-sniper.brush"
                        )
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
                            sniper.print(Caption.of("voxelsniper.command.voxel-sniper.invalid-range"));
                            return;
                        } else {
                            toolkitProperties.setBlockTracerRange(range);
                        }
                    } else {
                        sniper.print(Caption.of("voxelsniper.error.invalid-number", arguments[1]));
                        return;
                    }
                } else {
                    toolkitProperties.setBlockTracerRange(0);
                }
                Integer blockTracerRange = toolkitProperties.getBlockTracerRange();
                sniper.print(Caption.of("voxelsniper.command.voxel-sniper.distance-restriction",
                        VoxelSniperText.getStatus(blockTracerRange != null), blockTracerRange == null ? -1 : blockTracerRange
                ));
                return;
            } else if (firstArgument.equalsIgnoreCase("perf")) {
                Toolkit toolkit = sniper == null ? null : sniper.getCurrentToolkit();
                PerformerProperties performerProperties = toolkit == null ? null :
                        toolkit.getCurrentBrush() instanceof Performer performer ? performer.getProperties() : null;

                VoxelSniperText.print(
                        sender,
                        VoxelSniperText.formatListWithCurrent(
                                this.plugin.getPerformerRegistry().getPerformerProperties().keySet(),
                                String::compareTo,
                                TextComponent::of,
                                name -> name,
                                performerProperties == null ? null : performerProperties.getName(),
                                "voxelsniper.command.voxel-sniper.performer"
                        )
                );
                return;
            } else if (firstArgument.equalsIgnoreCase("perflong")) {
                Toolkit toolkit = sniper == null ? null : sniper.getCurrentToolkit();
                PerformerProperties performerProperties = toolkit == null ? null :
                        toolkit.getCurrentBrush() instanceof Performer performer ? performer.getProperties() : null;

                VoxelSniperText.print(
                        sender,
                        VoxelSniperText.formatListWithCurrent(
                                this.plugin.getPerformerRegistry().getPerformerProperties().values(),
                                (properties, properties2) -> properties.getName().compareTo(properties2.getName()),
                                properties -> TextComponent.of(properties.getName()),
                                properties -> properties,
                                performerProperties == null ? null : performerProperties.getName(),
                                "voxelsniper.command.voxel-sniper.performer-long"
                        )
                );
                return;
            } else if (firstArgument.equalsIgnoreCase("enable")) {
                if (sniper == null) {
                    return;
                }
                sniper.setEnabled(true);
                sniper.print(Caption.of(
                        "voxelsniper.command.voxel-sniper.toggle",
                        VoxelSniperText.getStatus(sniper.isEnabled())
                ));
                return;
            } else if (firstArgument.equalsIgnoreCase("disable")) {
                if (sniper == null) {
                    return;
                }
                sniper.setEnabled(false);
                sniper.print(Caption.of(
                        "voxelsniper.command.voxel-sniper.toggle",
                        VoxelSniperText.getStatus(sniper.isEnabled())
                ));

                return;
            } else if (firstArgument.equalsIgnoreCase("toggle")) {
                if (sniper == null) {
                    return;
                }
                sniper.setEnabled(!sniper.isEnabled());
                sniper.print(Caption.of(
                        "voxelsniper.command.voxel-sniper.toggle",
                        VoxelSniperText.getStatus(sniper.isEnabled())
                ));
                return;
            } else if (firstArgument.equalsIgnoreCase("info")) {
                PluginDescriptionFile description = plugin.getDescription();
                VoxelSniperText.print(sender, Caption.of("voxelsniper.command.voxel-sniper.admin-info",
                        description.getName(), description.getVersion(), description.getDescription(),
                        description.getWebsite(), "https://voxelsniper.fandom.com/wiki/VoxelSniper_Wiki",
                        "https://discord.gg/intellectualsites"
                ));
                return;
            } else if (firstArgument.equalsIgnoreCase("reload")) {
                if (sender.hasPermission("voxelsniper.admin")) {
                    plugin.reload();
                    VoxelSniperText.print(sender, Caption.of("voxelsniper.command.voxel-sniper.config-reload"));
                } else {
                    VoxelSniperText.print(sender, Caption.of(
                            "voxelsniper.command.missing-permission",
                            "voxelsniper.admin"
                    ));
                }
                return;
            } else if (firstArgument.equalsIgnoreCase("debugpaste")) {
                if (sender.hasPermission("voxelsniper.admin")) {
                    String destination;
                    try {
                        final File logFile = new File("logs/latest.log");
                        final File config = new File(plugin.getDataFolder(), "config.yml");
                        destination = IncendoPaster.debugPaste(logFile, Fawe.platform().getDebugInfo(), config);
                    } catch (IOException e) {
                        VoxelSniperText.print(sender, Caption.of("voxelsniper.command.voxel-sniper.debugpaste-fail", e));
                        return;
                    }
                    sender.sendMessage(destination);
                } else {
                    VoxelSniperText.print(sender, Caption.of(
                            "voxelsniper.command.missing-permission",
                            "voxelsniper.admin"
                    ));
                }
                return;
            }
        }
        if (sniper != null) {
            sniper.print(Caption.of("voxelsniper.command.voxel-sniper.info"));
            sniper.sendInfo(sender, false);
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
                    .toList();
        }
        return Collections.emptyList();
    }

}
