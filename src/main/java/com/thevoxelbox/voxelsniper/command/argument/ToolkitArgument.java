package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;

import java.util.List;
import java.util.Queue;

public class ToolkitArgument implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    /**
     * Create a toolkit argument.
     *
     * @param plugin the plugin
     * @since TODO
     */
    public ToolkitArgument(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Suggestions("toolkit_suggestions")
    public List<String> suggestToolkits(CommandContext<Sniper> commandContext, String input) {
        Sniper sniper = commandContext.getSender();
        return sniper.getToolkits().stream()
                .filter(toolkit -> !toolkit.isDefault())
                .map(Toolkit::getToolkitName)
                .toList();
    }

    @Parser(suggestions = "toolkit_suggestions")
    public Toolkit parseToolkit(CommandContext<Sniper> commandContext, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            throw new NoInputProvidedException(ToolkitArgument.class, commandContext);
        }

        Sniper sniper = commandContext.getSender();
        Toolkit toolkit = sniper.getToolkit(input);
        if (toolkit == null) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    "voxelsniper.command.toolkit.not-found",
                    input
            ));
        }

        inputQueue.remove();
        return toolkit;
    }

}
