package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.world.entity.EntityType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;
import java.util.Queue;

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
    public List<String> suggestEntityTypes(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestValues(commandContext, input);
    }

    @Parser(suggestions = "entity-type_suggestions")
    public EntityType parseEntityType(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        return super.parseValue(commandContext, inputQueue);
    }

}
