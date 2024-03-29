package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class EntityClassArgument implements VoxelCommandElement {

    public static final List<String> ENTITY_CLASSES = Arrays.stream(EntityType.values())
            .map(EntityType::getEntityClass)
            .flatMap(entityClass -> getEntityClassHierarchy(entityClass).stream())
            .distinct()
            .map(Class::getCanonicalName)
            .toList();

    public static List<Class<?>> getEntityClassHierarchy(Class<? extends Entity> entityClass) {
        List<Class<?>> entityClassHierarchy = new ArrayList<>(10);
        entityClassHierarchy.add(Entity.class);
        Class<?> currentClass = entityClass;

        while (currentClass != null && !currentClass.equals(Entity.class)) {
            entityClassHierarchy.add(currentClass);
            entityClassHierarchy.addAll(Arrays.asList(currentClass.getInterfaces()));

            currentClass = currentClass.getSuperclass();
        }
        return entityClassHierarchy;
    }

    private final VoxelSniperPlugin plugin;

    /**
     * Create a toolkit argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public EntityClassArgument(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Suggestions("entity-class_suggestions")
    public List<String> suggestEntityClasses(CommandContext<Sniper> commandContext, String input) {
        return ENTITY_CLASSES;
    }

    @Parser(name = "entity-class_parser", suggestions = "entity-class_suggestions")
    public Class<? extends Entity> parseEntityClass(CommandContext<Sniper> commandContext, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            throw new NoInputProvidedException(EntityClassArgument.class, commandContext);
        }

        Class<? extends Entity> clazz = getEntityClass(input);
        if (clazz == null) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    "voxelsniper.command.invalid-entity-class",
                    input
            ));
        }

        inputQueue.remove();
        return clazz;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Entity> getEntityClass(String path) {
        try {
            Class<?> clazz = Class.forName(path);
            return Entity.class.isAssignableFrom(clazz) ? (Class<? extends Entity>) clazz : null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
