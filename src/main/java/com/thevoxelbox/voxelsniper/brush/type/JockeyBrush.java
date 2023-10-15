package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.Fawe;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequireToolkit
@CommandMethod(value = "brush|b jockey")
@CommandPermission("voxelsniper.brush.jockey")
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

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.jockey.info"));
    }

    @CommandMethod("t <jockey-type>")
    public void onBrushT(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("jockey-type") JockeyType jockeyType
    ) {
        this.jockeyType = jockeyType;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.jockey.set-mode",
                this.jockeyType.getFullName()
        ));
    }

    @CommandMethod("<player-only> <inverse> <stack>")
    public void onBrushJockeytype(
            final @NotNull Snipe snipe,
            final @Argument("player-only") @Liberal boolean playerOnly,
            final @Argument("inverse") @Liberal boolean inverse,
            final @Argument("stack") @Liberal boolean stack
    ) {
        if (inverse) {
            if (playerOnly) {
                this.onBrushT(snipe, JockeyType.INVERT_PLAYER_ONLY);
            } else {
                this.onBrushT(snipe, JockeyType.INVERT_ALL_ENTITIES);
            }
        } else if (stack) {
            if (playerOnly) {
                this.onBrushT(snipe, JockeyType.STACK_PLAYER_ONLY);
            } else {
                this.onBrushT(snipe, JockeyType.STACK_ALL_ENTITIES);
            }
        } else {
            if (playerOnly) {
                this.onBrushT(snipe, JockeyType.NORMAL_PLAYER_ONLY);
            } else {
                this.onBrushT(snipe, JockeyType.NORMAL_ALL_ENTITIES);
            }
        }
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
        Fawe.instance().getQueueHandler().sync(() -> {
            if (this.jockeyType == JockeyType.INVERT_PLAYER_ONLY || this.jockeyType == JockeyType.INVERT_ALL_ENTITIES) {
                player.eject();
                sniper.print(Caption.of("voxelsniper.brush.jockey.top-ejected"));
            } else {
                if (this.jockeyedEntity != null) {
                    this.jockeyedEntity.eject();
                    this.jockeyedEntity = null;
                    sniper.print(Caption.of("voxelsniper.brush.jockey.ejected"));
                }
            }
        });
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
                    sniper.print(Caption.of("voxelsniper.brush.jockey.sitting", finalClosest.getType().toString()));
                }
            });
        } else {
            sniper.print(Caption.of("voxelsniper.brush.jockey.no-entities"));
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
                sniper.print(Caption.of("voxelsniper.brush.jockey.broke-stack"));
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.brush.jockey.set-mode",
                        this.jockeyType.getFullName()
                ))
                .send();
    }

    /**
     * Available types of jockey modes.
     */
    public enum JockeyType {

        NORMAL_ALL_ENTITIES("normal-all-entities"),
        NORMAL_PLAYER_ONLY("normal-player-only"),
        INVERT_ALL_ENTITIES("invert-all-entities"),
        INVERT_PLAYER_ONLY("invert-player-only"),
        STACK_ALL_ENTITIES("stack-all-entities"),
        STACK_PLAYER_ONLY("stack-player-only");

        private final String name;

        JockeyType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public TranslatableComponent getFullName() {
            return Caption.of("voxelsniper.brush.jockey.mode." + this.name);
        }
    }

}
