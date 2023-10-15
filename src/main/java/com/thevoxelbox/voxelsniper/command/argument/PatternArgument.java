package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;
import java.util.Queue;

public class PatternArgument extends AbstractPatternArgument<Pattern> {

    /**
     * Create a pattern argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public PatternArgument(VoxelSniperPlugin plugin) {
        super(plugin, plugin.getPatternParser(), "voxelsniper.command.invalid-pattern");
    }

    @Suggestions("pattern_suggestions")
    public List<String> suggestPatterns(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestPatterns(commandContext, input);
    }

    @Parser(name = "pattern_parser", suggestions = "pattern_suggestions")
    public BrushPattern parsePattern(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        return super.parsePattern(commandContext, inputQueue);
    }

    @Override
    protected String getPatternResource(String input, Pattern pattern) {
        return input;
    }

}
