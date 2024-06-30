package com.thevoxelbox.voxelsniper.brush.type.entity;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b entity|en")
@Permission("voxelsniper.brush.entity")
public class EntityBrush extends AbstractBrush {

    private static final EntityType DEFAULT_ENTITY_TYPE = EntityTypes.ZOMBIE;

    private EntityType entityType;

    @Override
    public void loadProperties() {
        this.entityType = (EntityType) getRegistryProperty("default-entity-type", EntityType.REGISTRY, DEFAULT_ENTITY_TYPE);
    }

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Command("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.entity.info"));
    }

    @Command("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                EntityType.REGISTRY.values(),
                (type, type2) -> type.getId().compareTo(type2.getId()),
                type -> TextComponent.of(type.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)),
                type -> type,
                this.entityType,
                "voxelsniper.brush.entity"
        ));
    }

    @Command("<entity-type>")
    public void onBrushEntitytype(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("entity-type") EntityType entityType
    ) {
        this.entityType = entityType;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.entity.set-entity-type",
                this.entityType.getName()
        ));
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
            } catch (RuntimeException e) {
                messenger.sendMessage(Caption.of("voxelsniper.brush.entity.cannot-spawn"));
            }
            return null;
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .message(Caption.of(
                        "voxelsniper.brush.entity.set-entity-type",
                        this.entityType.getName()
                ))
                .brushSizeMessage()
                .send();
    }

}
