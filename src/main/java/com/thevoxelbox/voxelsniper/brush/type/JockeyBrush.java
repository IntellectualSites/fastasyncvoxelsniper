package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class JockeyBrush extends AbstractBrush {

    private static final int ENTITY_STACK_LIMIT = 50;

    private static final JockeyType DEFAULT_JOCKEY_TYPE = JockeyType.NORMAL_ALL_ENTITIES;

    @Nullable
    private Entity jockeyedEntity;

    private int entityStackLimit;

    private JockeyType jockeyType;

    @Override
    public void loadProperties() {
        this.entityStackLimit = getIntegerProperty("entity-stack-limit", ENTITY_STACK_LIMIT);
        this.jockeyType = (JockeyType) getEnumProperty("default-jockey-type", JockeyType.class, DEFAULT_JOCKEY_TYPE);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];
        boolean stack = false;
        boolean playerOnly = false;
        boolean inverse = false;

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Jockey Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b jockey [true|false] [true|false] [true|false] -- Sets whether players only or entities should be " +
                    "affected to n. Sets if entities should be inverted to i. Sets if entities should be stacked to s.");
        } else {
            if (parameters.length == 4) {
                playerOnly = Boolean.parseBoolean(parameters[1]);
                inverse = Boolean.parseBoolean(parameters[2]);
                stack = Boolean.parseBoolean(parameters[3]);
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display " +
                        "parameter info.");
            }
        }
        if (inverse) {
            this.jockeyType = playerOnly ? JockeyType.INVERT_PLAYER_ONLY : JockeyType.INVERT_ALL_ENTITIES;
        } else if (stack) {
            this.jockeyType = playerOnly ? JockeyType.STACK_PLAYER_ONLY : JockeyType.STACK_ALL_ENTITIES;
        } else {
            this.jockeyType = playerOnly ? JockeyType.NORMAL_PLAYER_ONLY : JockeyType.NORMAL_ALL_ENTITIES;
        }
        messenger.sendMessage("Current jockey mode: " + ChatColor.GREEN + this.jockeyType);
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length <= 3) {
            int index = parameters.length - 1;
            String parameter = parameters[index];
            return super.sortCompletions(Stream.of("true", "false"), parameter, index);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        if (this.jockeyType == JockeyType.STACK_ALL_ENTITIES || this.jockeyType == JockeyType.STACK_PLAYER_ONLY) {
            stack(snipe);
        } else {
            sitOn(snipe);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        if (this.jockeyType == JockeyType.INVERT_PLAYER_ONLY || this.jockeyType == JockeyType.INVERT_ALL_ENTITIES) {
            player.eject();
            player.sendMessage(ChatColor.GOLD + "The guy on top of you has been ejected!");
        } else {
            if (this.jockeyedEntity != null) {
                this.jockeyedEntity.eject();
                this.jockeyedEntity = null;
                player.sendMessage(ChatColor.GOLD + "You have been ejected!");
            }
        }
    }

    private void sitOn(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        BlockVector3 targetBlock = getTargetBlock();
        World world = BukkitAdapter.adapt(getEditSession().getWorld());
        int targetChunkX = targetBlock.getX() >> 4;
        int targetChunkZ = targetBlock.getZ() >> 4;
        double range = Double.MAX_VALUE;
        Entity closest = null;
        for (int x = targetChunkX - 1; x <= targetChunkX + 1; x++) {
            for (int y = targetChunkZ - 1; y <= targetChunkZ + 1; y++) {
                if (!world.isChunkLoaded(x, y)) {
                    continue;
                }

                for (Entity entity : world.getChunkAt(x, y).getEntities()) {
                    if (entity.getEntityId() == player.getEntityId()) {
                        continue;
                    }
                    if (this.jockeyType == JockeyType.NORMAL_PLAYER_ONLY || this.jockeyType == JockeyType.INVERT_PLAYER_ONLY) {
                        if (!(entity instanceof Player)) {
                            continue;
                        }
                    }
                    Location entityLocation = entity.getLocation();
                    double entityDistance = entityLocation.distance(player.getLocation());
                    if (entityDistance < range) {
                        range = entityDistance;
                        closest = entity;
                    }
                }
            }
        }
        if (closest != null) {
            Entity finalClosest = closest;
            Fawe.instance().getQueueHandler().sync(() -> {
                PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(
                        player,
                        player.getLocation(),
                        finalClosest.getLocation(),
                        PlayerTeleportEvent.TeleportCause.PLUGIN
                );
                PluginManager pluginManager = Bukkit.getPluginManager();
                pluginManager.callEvent(playerTeleportEvent);
                if (!playerTeleportEvent.isCancelled()) {
                    if (jockeyType == JockeyType.INVERT_PLAYER_ONLY || jockeyType == JockeyType.INVERT_ALL_ENTITIES) {
                        player.addPassenger(finalClosest);
                    } else {
                        finalClosest.addPassenger(player);
                        jockeyedEntity = finalClosest;
                    }
                    player.sendMessage(ChatColor.GREEN + "You are now sitting on entity: " + finalClosest.getEntityId());
                }
            });
        } else {
            player.sendMessage(ChatColor.RED + "Could not find any entities");
        }
    }

    private void stack(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        int brushSizeDoubled = toolkitProperties.getBrushSize() * 2;
        List<Entity> nearbyEntities = player.getNearbyEntities(brushSizeDoubled, brushSizeDoubled, brushSizeDoubled);
        Entity lastEntity = player;
        int stackHeight = 0;
        for (Entity entity : nearbyEntities) {
            if (stackHeight >= entityStackLimit) {
                return;
            }
            if (this.jockeyType == JockeyType.STACK_ALL_ENTITIES) {
                lastEntity.addPassenger(entity);
                lastEntity = entity;
                stackHeight++;
            } else if (this.jockeyType == JockeyType.STACK_PLAYER_ONLY) {
                if (entity instanceof Player) {
                    lastEntity.addPassenger(entity);
                    lastEntity = entity;
                    stackHeight++;
                }
            } else {
                player.sendMessage("You broke stack! :O");
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message("Current jockey mode: " + ChatColor.GREEN + this.jockeyType)
                .send();
    }

    /**
     * Available types of jockey modes.
     */
    private enum JockeyType {

        NORMAL_ALL_ENTITIES("Normal (All)"),
        NORMAL_PLAYER_ONLY("Normal (Player only)"),
        INVERT_ALL_ENTITIES("Invert (All)"),
        INVERT_PLAYER_ONLY("Invert (Player only)"),
        STACK_ALL_ENTITIES("Stack (All)"),
        STACK_PLAYER_ONLY("Stack (Player only)");

        private final String name;

        JockeyType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}
