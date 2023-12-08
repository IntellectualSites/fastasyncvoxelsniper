package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;

public class BrushPropertiesArgument implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;
    private final BrushRegistry brushRegistry;

    /**
     * Create a brush properties argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public BrushPropertiesArgument(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
        this.brushRegistry = plugin.getBrushRegistry();
    }

    @Suggestions("brush-properties_suggestions")
    public Stream<String> suggestBrushProperties(CommandContext<SniperCommander> commandContext, String input) {
        SniperCommander commander = commandContext.sender();
        CommandSender sender = commander.getCommandSender();
        return brushRegistry.getBrushesProperties().entrySet().stream()
                .filter(entry -> {
                    String permission = entry.getValue().getPermission();
                    return permission == null || sender.hasPermission(permission);
                })
                .map(Map.Entry::getKey);
    }

    @Parser(name = "brush-properties_parser", suggestions = "brush-properties_suggestions")
    public BrushProperties parseBrushProperties(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        SniperCommander commander = commandContext.sender();
        String input = commandInput.readString();
        BrushProperties properties = brushRegistry.getBrushProperties(input);
        if (properties == null) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    "voxelsniper.command.brush.no-alias",
                    input
            ));
        }
        CommandSender sender = commander.getCommandSender();
        String permission = properties.getPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    "voxelsniper.command.missing-permission",
                    permission
            ));
        }

        return properties;
    }

}
