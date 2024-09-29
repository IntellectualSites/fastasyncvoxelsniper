package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.stream.Stream;

public class BiomeTypeArgument extends AbstractRegistryArgument<BiomeType> {

    /**
     * Create a biome type argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public BiomeTypeArgument(VoxelSniperPlugin plugin) {
        super(plugin, BiomeType.REGISTRY, "voxelsniper.command.invalid-biome");
    }

    @Suggestions("biome-type_suggestions")
    public Stream<String> suggestBiomeTypes(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestValues(commandContext, input);
    }

    @Parser(suggestions = "biome-type_suggestions")
    public BiomeType parseBiomeType(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        return super.parseValue(commandContext, commandInput);
    }

}
