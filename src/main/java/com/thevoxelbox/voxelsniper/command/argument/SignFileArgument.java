package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.io.File;
import java.util.List;
import java.util.Queue;

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
    public List<String> suggestSignFiles(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestFiles(commandContext, input);
    }

    @Parser(name = "sign-file_parser", suggestions = "sign-file_suggestions")
    public File parseSignFile(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        return super.parseFile(commandContext, inputQueue);
    }

}
