package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.io.File;
import java.util.List;
import java.util.Queue;

public class StencilFileArgument extends AbstractFileArgument {

    /**
     * Create a stencil file argument.
     *
     * @param plugin the plugin
     * @since TODO
     */
    public StencilFileArgument(VoxelSniperPlugin plugin) {
        super(plugin, plugin.getDataFolder().toPath().resolve("stencils/"), ".vstencil");
    }

    @Suggestions("stencil-file_suggestions")
    public List<String> suggestStencilFiles(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestFiles(commandContext, input);
    }

    @Parser(name = "stencil-file_parser", suggestions = "stencil-file_suggestions")
    public File parseStencilFile(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        return super.parseFile(commandContext, inputQueue);
    }

}
