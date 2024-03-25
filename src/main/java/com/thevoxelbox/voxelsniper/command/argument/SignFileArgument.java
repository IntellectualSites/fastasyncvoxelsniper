package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.io.File;
import java.util.stream.Stream;

public class SignFileArgument extends AbstractFileArgument {

    /**
     * Create a sign file argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public SignFileArgument(VoxelSniperPlugin plugin) {
        super(plugin, plugin.getDataFolder().toPath().resolve("signs/"), ".vsign");
    }

    @Suggestions("sign-file_suggestions")
    public Stream<String> suggestSignFiles(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestFiles(commandContext, input);
    }

    @Parser(name = "sign-file_parser", suggestions = "sign-file_suggestions")
    public File parseSignFile(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        return super.parseFile(commandContext, commandInput);
    }

}
