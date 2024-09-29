package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.io.File;
import java.util.stream.Stream;

public class StencilFileArgument extends AbstractFileArgument {

    /**
     * Create a stencil file argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public StencilFileArgument(VoxelSniperPlugin plugin) {
        super(plugin, plugin.getDataFolder().toPath().resolve("stencils/"), ".vstencil");
    }

    @Suggestions("stencil-file_suggestions")
    public Stream<String> suggestStencilFiles(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestFiles(commandContext, input);
    }

    @Parser(name = "stencil-file_parser", suggestions = "stencil-file_suggestions")
    public File parseStencilFile(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        return super.parseFile(commandContext, commandInput);
    }

}
