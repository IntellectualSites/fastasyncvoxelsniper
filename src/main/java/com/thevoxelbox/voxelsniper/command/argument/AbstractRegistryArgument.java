package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.Locale;
import java.util.stream.Stream;

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

    protected Stream<String> suggestValues(CommandContext<SniperCommander> commandContext, String input) {
        return registry.getSuggestions(input);
    }

    protected T parseValue(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        final String input = commandInput.readString();
        T value = registry.get(input.toLowerCase(Locale.ROOT));
        if (value == null) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    parseExceptionCaptionKey,
                    input
            ));
        }

        return value;
    }

}
