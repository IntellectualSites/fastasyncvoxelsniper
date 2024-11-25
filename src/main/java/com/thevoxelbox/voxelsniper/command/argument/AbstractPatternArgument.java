package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.registry.AbstractFactory;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.Queue;

public abstract class AbstractPatternArgument<T extends Pattern> implements VoxelCommandElement {

    protected final VoxelSniperPlugin plugin;
    protected final VoxelSniperConfig config;
    protected final AbstractFactory<T> factory;
    protected final String parseExceptionCaptionKey;

    /**
     * Create an abstract pattern argument.
     *
     * @param plugin                   the plugin
     * @param factory                  the factory
     * @param parseExceptionCaptionKey the parse exception caption key
     * @since 3.0.0
     */
    public AbstractPatternArgument(VoxelSniperPlugin plugin, AbstractFactory<T> factory, String parseExceptionCaptionKey) {
        this.plugin = plugin;
        this.config = plugin.getVoxelSniperConfig();
        this.factory = factory;
        this.parseExceptionCaptionKey = parseExceptionCaptionKey;
    }

    protected List<String> suggestPatterns(CommandContext<SniperCommander> commandContext, String input) {
        return factory.getSuggestions(input, new ParserContext());
    }

    protected BrushPattern parsePattern(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            throw new NoInputProvidedException(AbstractPatternArgument.class, commandContext);
        }

        SniperCommander commander = commandContext.getSender();
        ParserContext parserContext = commander.createParserContext();
        try {
            T pattern = factory.parseFromInput(
                    input.toLowerCase(Locale.ROOT),
                    parserContext
            );
            CommandSender sender = commander.getCommandSender();
            if (!sender.hasPermission("voxelsniper.ignorelimitations")
                    && config.getLitesniperRestrictedMaterials().contains(getPatternResource(input, pattern))) {
                throw new VoxelCommandElementParseException(input, Caption.of(
                        "voxelsniper.command.not-allowed",
                        input
                ));
            }

            inputQueue.remove();
            return new BrushPattern(pattern, input);
        } catch (InputParseException e) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    parseExceptionCaptionKey,
                    input
            ));
        }
    }

    protected abstract String getPatternResource(String input, T pattern);

}
