package com.thevoxelbox.voxelsniper.brush.type.entity;

import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EntityRemovalBrush extends AbstractBrush {

    private static final List<String> DEFAULT_EXEMPTIONS = Arrays.asList("org.bukkit.entity.Player",
            "org.bukkit.entity.Hanging", "org.bukkit.entity.NPC"
    );

    private static final List<String> ENTITY_CLASSES = Arrays.stream(EntityType.values())
            .map(EntityType::getEntityClass)
            .flatMap(entityClass -> getEntityClassHierarchy(entityClass).stream())
            .distinct()
            .map(Class::getCanonicalName)
            .toList();

    private List<String> exemptions;

    private static List<Class<?>> getEntityClassHierarchy(Class<? extends Entity> entityClass) {
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

    @SuppressWarnings("unchecked")
    @Override
    public void loadProperties() {
        this.exemptions = new ArrayList<>(
                (List<String>) getListProperty("default-exemptions", DEFAULT_EXEMPTIONS)
        );
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.entity.removal"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(VoxelSniperText.formatList(
                            ENTITY_CLASSES,
                            String::compareTo,
                            TextComponent::of,
                            "voxelsniper.brush.entity-removal"
                    ));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("+")) {
                    String exemptionToAdd = parameters[1];
                    if (isEntityClass(exemptionToAdd)) {
                        this.exemptions.add(exemptionToAdd);
                        messenger.sendMessage(Caption.of("voxelsniper.brush.entity-removal.add-entity-class", exemptionToAdd));
                    } else {
                        messenger.sendMessage(Caption.of(
                                "voxelsniper.brush.entity-removal.invalid-entity-class",
                                exemptionToAdd
                        ));
                    }
                } else if (firstParameter.equalsIgnoreCase("-")) {
                    String exemptionToRemove = parameters[1];
                    this.exemptions.remove(exemptionToRemove);
                    messenger.sendMessage(Caption.of("voxelsniper.brush.entity-removal.remove-entity-class", exemptionToRemove));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("+", "-", "list"), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (firstParameter.equalsIgnoreCase("+")) {
                String parameter = parameters[1];
                return super.sortCompletions(ENTITY_CLASSES.stream(), parameter, 1);
            } else if (firstParameter.equalsIgnoreCase("-")) {
                String parameter = parameters[1];
                return super.sortCompletions(this.exemptions.stream(), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        radialRemoval(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        radialRemoval(snipe);
    }

    private void radialRemoval(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        int entityCount = 0;
        int chunkCount = 0;
        int radius = Math.round(toolkitProperties.getBrushSize() / 16.0F);
        for (int x = chunkX - radius; x <= chunkX + radius; x++) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                entityCount += removeEntities(x, z);
                chunkCount++;
            }
        }
        messenger.sendMessage(Caption.of("voxelsniper.brush.entity-removal.removed", entityCount, chunkCount));
    }

    private int removeEntities(int chunkX, int chunkZ) {
        return TaskManager.taskManager().sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                return 0;
            }

            int entityCount = 0;
            for (Entity entity : world.getChunkAt(chunkX, chunkZ).getEntities()) {
                if (!isEntityClassInExemptionList(entity.getClass())) {
                    entity.remove();
                    entityCount++;
                }
            }
            return entityCount;
        });
    }

    private boolean isEntityClass(String path) {
        try {
            Class<?> clazz = Class.forName(path);
            return Entity.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean isEntityClassInExemptionList(Class<? extends Entity> entityClass) {
        // Create a list of superclasses and interfaces implemented by the current entity type
        List<Class<?>> entityClassHierarchy = getEntityClassHierarchy(entityClass);
        return this.exemptions.stream()
                .anyMatch(exemption -> entityClassHierarchy.stream()
                        .map(Class::getCanonicalName)
                        .anyMatch(typeName -> typeName.equals(exemption)));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(VoxelSniperText.formatList(
                        exemptions,
                        String::compareTo,
                        TextComponent::of,
                        "voxelsniper.brush.entity-removal"
                ))
                .brushSizeMessage()
                .send();
    }

}
