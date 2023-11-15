package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.io.File;
import java.util.List;
import java.util.Queue;

public class StencilListFileArgument extends AbstractFileArgument {

    /**
     * Create a stencil file argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public StencilListFileArgument(VoxelSniperPlugin plugin) {
        super(plugin, plugin.getDataFolder().toPath().resolve("stencilLists/"), ".txt");
    }

    @Suggestions("stencil-list-file_suggestions")
    public List<String> suggestStencilListFiles(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestFiles(commandContext, input);
    }

    @Parser(name = "stencil-list-file_parser", suggestions = "stencil-list-file_suggestions")
    public File parseStencilListFile(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        return super.parseFile(commandContext, inputQueue);
    }

}
