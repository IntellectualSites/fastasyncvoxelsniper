package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;
import java.util.Locale;
import java.util.Queue;

public abstract class AbstractRegistryArgument<T extends Keyed> implements VoxelCommandElement {

    protected final VoxelSniperPlugin plugin;
    protected final NamespacedRegistry<T> registry;
    protected final String parseExceptionCaptionKey;

    /**
     * Create an abstract pattern argument.
     *
     * @param plugin                   the plugin
     * @param registry                 the registry
     * @param parseExceptionCaptionKey the parse exception caption key
     * @since 3.0.0
     */
    public AbstractRegistryArgument(VoxelSniperPlugin plugin, NamespacedRegistry<T> registry, String parseExceptionCaptionKey) {
        this.plugin = plugin;
        this.registry = registry;
        this.parseExceptionCaptionKey = parseExceptionCaptionKey;
    }

    protected List<String> suggestValues(CommandContext<SniperCommander> commandContext, String input) {
        return registry.getSuggestions(input)
                .toList();
    }

    protected T parseValue(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            throw new NoInputProvidedException(AbstractRegistryArgument.class, commandContext);
        }

        T value = registry.get(input.toLowerCase(Locale.ROOT));
        if (value == null) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    parseExceptionCaptionKey,
                    input
            ));
        }

        inputQueue.remove();
        return value;
    }

}
