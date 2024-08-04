package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;

import java.util.stream.Stream;

public class ToolkitArgument implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    /**
     * Create a toolkit argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public ToolkitArgument(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Suggestions("toolkit_suggestions")
    public Stream<String> suggestToolkits(CommandContext<Sniper> commandContext, String input) {
        Sniper sniper = commandContext.sender();
        return sniper.getToolkits().stream()
                .filter(toolkit -> !toolkit.isDefault())
                .map(Toolkit::getToolkitName);
    }

    @Parser(suggestions = "toolkit_suggestions")
    public Toolkit parseToolkit(CommandContext<Sniper> commandContext, CommandInput commandInput) {
        String input = commandInput.readString();
        Sniper sniper = commandContext.sender();
        Toolkit toolkit = sniper.getToolkit(input);
        if (toolkit == null) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    "voxelsniper.command.toolkit.not-found",
                    input
            ));
        }

        return toolkit;
    }

}
