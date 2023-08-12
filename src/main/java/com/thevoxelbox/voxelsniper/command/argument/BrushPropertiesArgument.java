package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
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

public class BrushPropertiesArgument implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;
    private final BrushRegistry brushRegistry;

    /**
     * Create a brush properties argument.
     *
     * @param plugin the plugin
     * @since TODO
     */
    public BrushPropertiesArgument(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
        this.brushRegistry = plugin.getBrushRegistry();
    }

    @Suggestions("brush-properties_suggestions")
    public List<String> suggestBrushProperties(CommandContext<SniperCommander> commandContext, String input) {
        SniperCommander commander = commandContext.getSender();
        CommandSender sender = commander.getCommandSender();
        return brushRegistry.getBrushesProperties().entrySet().stream()
                .filter(entry -> {
                    String permission = entry.getValue().getPermission();
                    return permission == null || sender.hasPermission(permission);
                })
                .map(Map.Entry::getKey)
                .toList();
    }

    @Parser(name = "brush-properties_parser", suggestions = "brush-properties_suggestions")
    public BrushProperties parseBrushProperties(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            throw new NoInputProvidedException(BrushPropertiesArgument.class, commandContext);
        }

        SniperCommander commander = commandContext.getSender();
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

        inputQueue.remove();
        return properties;
    }

}
