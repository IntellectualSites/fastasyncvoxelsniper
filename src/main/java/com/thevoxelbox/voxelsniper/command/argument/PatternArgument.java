package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;

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
    public BrushPattern parsePattern(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        return super.parsePattern(commandContext, commandInput);
    }

    @Override
    protected String getPatternResource(String input, Pattern pattern) {
        return input;
    }

}
