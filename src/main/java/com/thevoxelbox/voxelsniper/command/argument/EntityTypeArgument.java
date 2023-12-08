package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.sk89q.worldedit.world.entity.EntityType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.stream.Stream;

public class EntityTypeArgument extends AbstractRegistryArgument<EntityType> {

    /**
     * Create an entity type argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public EntityTypeArgument(VoxelSniperPlugin plugin) {
        super(plugin, EntityType.REGISTRY, "voxelsniper.command.invalid-entity");
    }

    @Suggestions("entity-type_suggestions")
    public Stream<String> suggestEntityTypes(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestValues(commandContext, input);
    }

    @Parser(suggestions = "entity-type_suggestions")
    public EntityType parseEntityType(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        return super.parseValue(commandContext, commandInput);
    }

}
