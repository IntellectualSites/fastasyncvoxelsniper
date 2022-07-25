package com.thevoxelbox.voxelsniper.brush.type.entity;

import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.bukkit.World;

import java.util.List;
import java.util.stream.Stream;

public class EntityBrush extends AbstractBrush {

    private static final EntityType DEFAULT_ENTITY_TYPE = EntityTypes.ZOMBIE;

    private static final List<String> ENTITIES = EntityType.REGISTRY.values().stream()
            .map(entityType -> entityType.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH))
            .toList();

    private EntityType entityType;

    @Override
    public void loadProperties() {
        this.entityType = (EntityType) getRegistryProperty("default-entity-type", EntityType.REGISTRY, DEFAULT_ENTITY_TYPE);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.entity.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                            EntityType.REGISTRY.values(),
                            (type, type2) -> type.getId().compareTo(type2.getId()),
                            type -> TextComponent.of(type.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)),
                            type -> type,
                            this.entityType,
                            "voxelsniper.brush.entity"
                    ));
                } else {
                    EntityType currentEntity = EntityTypes.get(firstParameter);

                    if (currentEntity != null) {
                        this.entityType = currentEntity;
                        messenger.sendMessage(Caption.of("voxelsniper.brush.entity.set-entity-type", this.entityType.getName()));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.brush.entity.invalid-entity-type", firstParameter));
                    }
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
            return super.sortCompletions(Stream.concat(
                    ENTITIES.stream(),
                    Stream.of("list")
            ), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        spawn(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        spawn(snipe);
    }

    private void spawn(Snipe snipe) {
        Class<? extends org.bukkit.entity.Entity> entityClass = BukkitAdapter.adapt(entityType).getEntityClass();
        if (entityClass == null) {
            return;
        }

        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        EditSession editSession = getEditSession();
        BlockVector3 lastBlock = getLastBlock();

        TaskManager.taskManager().sync(() -> {
            try {
                for (int x = 0; x < toolkitProperties.getBrushSize(); x++) {
                    World world = BukkitAdapter.adapt(editSession.getWorld());
                    org.bukkit.entity.Entity bukkitEntity = world.spawn(BukkitAdapter.adapt(world, lastBlock), entityClass);
                    com.sk89q.worldedit.entity.Entity entity = createEntity(lastBlock, bukkitEntity);
                    if (entity == null) {
                        messenger.sendMessage(Caption.of("voxelsniper.brush.entity.cannot-spawn"));
                        bukkitEntity.remove();
                        break;
                    }
                }
            } catch (RuntimeException exception) {
                messenger.sendMessage(Caption.of("voxelsniper.brush.entity.cannot-spawn"));
            }
            return null;
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .message(Caption.of("voxelsniper.brush.entity.set-entity-type", this.entityType.getName()))
                .brushSizeMessage()
                .send();
    }

}
