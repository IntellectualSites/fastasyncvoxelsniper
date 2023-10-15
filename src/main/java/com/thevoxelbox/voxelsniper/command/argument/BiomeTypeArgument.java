package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;
import java.util.Queue;

public class BiomeTypeArgument extends AbstractRegistryArgument<BiomeType> {

    /**
     * Create a biome type argument.
     *
     * @param plugin the plugin
     * @since TODO
     */
    public BiomeTypeArgument(VoxelSniperPlugin plugin) {
        super(plugin, BiomeType.REGISTRY, "voxelsniper.command.invalid-biome");
    }

    @Suggestions("biome-type_suggestions")
    public List<String> suggestBiomeTypes(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestValues(commandContext, input);
    }

    @Parser(suggestions = "biome-type_suggestions")
    public BiomeType parseBiomeType(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        return super.parseValue(commandContext, inputQueue);
    }

}
