package com.thevoxelbox.voxelsniper.brush.type.entity;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.List;
import java.util.stream.Collectors;

public class EntityBrush extends AbstractBrush {

    private static final List<String> ENTITIES = EntityType.REGISTRY.values().stream()
            .map(com.sk89q.worldedit.world.entity.EntityType::getId)
            .collect(Collectors.toList());

    private EntityType entityType = EntityTypes.ZOMBIE;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (parameters[0].equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.BLUE + "Entity brush:");
            messenger.sendMessage(ChatColor.AQUA + "/b en entity_type");
            messenger.sendMessage(ChatColor.GOLD + "Currently selected entity type: " + ChatColor.DARK_GREEN + this.entityType.getName());
        } else {
            String entityName = parameters[0];
            EntityType currentEntity = EntityTypes.get(entityName);
            if (currentEntity != null) {
                this.entityType = currentEntity;
                messenger.sendMessage(ChatColor.GREEN + "Entity type set to " + ChatColor.DARK_GREEN + this.entityType.getName());
            } else {
                messenger.sendMessage(ChatColor.RED + "This is not a valid entity!");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(ENTITIES.stream(), parameter, 0);
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

        TaskManager.IMP.sync(() -> {
            try {
                for (int x = 0; x < toolkitProperties.getBrushSize(); x++) {
                    World world = BukkitAdapter.adapt(editSession.getWorld());
                    org.bukkit.entity.Entity bukkitEntity = world.spawn(BukkitAdapter.adapt(world, lastBlock), entityClass);
                    com.sk89q.worldedit.entity.Entity entity = createEntity(lastBlock, bukkitEntity);
                    if (entity == null) {
                        messenger.sendMessage(ChatColor.RED + "Cannot spawn entity!");
                        bukkitEntity.remove();
                        break;
                    }
                }
            } catch (RuntimeException exception) {
                messenger.sendMessage(ChatColor.RED + "Cannot spawn entity!");
            }
            return null;
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .message(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + this.entityType.getId() + ")")
                .brushSizeMessage()
                .send();
    }

}
