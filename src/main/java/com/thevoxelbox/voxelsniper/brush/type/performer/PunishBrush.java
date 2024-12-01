package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@RequireToolkit
@CommandMethod(value = "brush|b punish|p")
@CommandPermission("voxelsniper.brush.punish")
public class PunishBrush extends AbstractPerformerBrush {

    private static final int TICKS_PER_SECOND = 20;

    private static final int MAX_RANDOM_TELEPORTATION_RANGE = 400;
    private static final int INFINI_PUNISH_SIZE = -3;

    private static final Punishment DEFAULT_PUNISHMENT = Punishment.FIRE;
    private static final int DEFAULT_PUNISH_LEVEL = 10;
    private static final int DEFAULT_PUNISH_DURATION = 60;

    private static final List<String> PUNISHMENTS = Arrays.stream(Punishment.values())
            .map(punishment -> punishment.name().toLowerCase(Locale.ROOT))
            .toList();

    private boolean specificPlayer;
    private String punishPlayerName = "";
    private boolean hypnoAffectLandscape;
    private boolean hitsSelf;

    private int maxRandomTeleportationRange;
    private int infiniPunishSize;

    private Punishment punishment;
    private int punishLevel;
    private int punishDuration;

    @Override
    public void loadProperties() {
        this.maxRandomTeleportationRange = getIntegerProperty("max-random-teleportation-range", MAX_RANDOM_TELEPORTATION_RANGE);
        this.infiniPunishSize = getIntegerProperty("infini-punish-size", INFINI_PUNISH_SIZE);

        this.punishment = (Punishment) getEnumProperty("default-punishment", Punishment.class, DEFAULT_PUNISHMENT);
        this.punishLevel = getIntegerProperty("default-punish-level", DEFAULT_PUNISH_LEVEL);
        this.punishDuration = getIntegerProperty("default-punish-duration", DEFAULT_PUNISH_DURATION);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.punish.info"));
    }

    @CommandMethod("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                Arrays.asList(Punishment.values()),
                (punishment, punishment2) -> punishment.getName().compareTo(punishment2.getName()),
                Punishment::getFullName,
                punishment -> punishment,
                this.punishment,
                "voxelsniper.performer-brush.punish"
        ));
    }

    @CommandMethod("toggleSelf")
    public void onBrushToggleself(
            final @NotNull Snipe snipe
    ) {
        this.hitsSelf = !this.hitsSelf;

        SnipeMessenger messenger = snipe.createMessenger();
        if (this.hitsSelf) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.punish.hit-self"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.punish.hit-other"));
        }
    }

    @CommandMethod("toggleHypnoLandscape")
    public void onBrushToggleHypnoLandscape(
            final @NotNull Snipe snipe
    ) {
        this.hypnoAffectLandscape = !this.hypnoAffectLandscape;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.punish.hypno-affect-landscape",
                this.hypnoAffectLandscape
        ));
    }

    @CommandMethod("<punishment>")
    public void onBrushPunishment(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("punishment") Punishment punishment
    ) {
        this.punishment = punishment;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.punish.set-punishment",
                this.punishment.getFullName()
        ));
    }

    @CommandMethod("toggleSM <player>")
    public void onBrushTogglesm(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("player") Player player
    ) {
        this.punishPlayerName = player.getName();

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.punish.hit-player",
                this.punishPlayerName
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        this.punishDuration = toolkitProperties.getVoxelHeight();
        this.punishLevel = toolkitProperties.getCylinderCenter();
        if (this.specificPlayer) {
            TaskManager.taskManager().sync(() -> {
                Player punishedPlayer = Bukkit.getPlayer(this.punishPlayerName);
                if (punishedPlayer == null) {
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.command.invalid-player",
                            this.punishPlayerName
                    ));
                    return null;
                }
                applyPunishment(punishedPlayer, snipe);
                return null;
            });
            return;
        }
        int brushSize = toolkitProperties.getBrushSize();
        int brushSizeSquare = brushSize * brushSize;
        BlockVector3 targetBlock = getTargetBlock();
        TaskManager.taskManager().sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            Location targetLocation = new Location(world, targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
            List<LivingEntity> entities = world.getLivingEntities();
            int numPunishApps = 0;
            for (LivingEntity entity : entities) {
                if (player != entity || this.hitsSelf) {
                    if (brushSize >= 0) {
                        try {
                            Location location = entity.getLocation();
                            if (location.distanceSquared(targetLocation) <= brushSizeSquare) {
                                numPunishApps++;
                                applyPunishment(entity, snipe);
                            }
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            messenger.sendMessage(Caption.of("voxelsniper.error.unexpected"));
                            return null;
                        }
                    } else if (brushSize == this.infiniPunishSize) {
                        numPunishApps++;
                        applyPunishment(entity, snipe);
                    }
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.punish.applied", numPunishApps));
            return null;
        });
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int brushSizeSquare = brushSize * brushSize;
        World world = BukkitAdapter.adapt(getEditSession().getWorld());
        BlockVector3 targetBlock = getTargetBlock();
        Location targetLocation = new Location(world, targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        List<LivingEntity> entities = world.getLivingEntities();
        for (LivingEntity entity : entities) {
            Location location = entity.getLocation();
            if (location.distanceSquared(targetLocation) < brushSizeSquare) {
                entity.setFireTicks(0);
                entity.removePotionEffect(Registry.EFFECT.get(NamespacedKey.fromString("blindness")));
                entity.removePotionEffect(Registry.EFFECT.get(NamespacedKey.fromString("nausea")));
                entity.removePotionEffect(Registry.EFFECT.get(NamespacedKey.fromString("slowness")));
                entity.removePotionEffect(Registry.EFFECT.get(NamespacedKey.fromString("jump_boost")));
            }
        }
    }

    private void applyPunishment(LivingEntity entity, Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        switch (this.punishment) {
            case FIRE -> entity.setFireTicks(TICKS_PER_SECOND * this.punishDuration);
            case LIGHTNING -> entity.getWorld()
                    .strikeLightning(entity.getLocation());
            case BLINDNESS -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("blindness"))
            );
            case DRUNK -> addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("nausea")));
            case SLOW -> addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("slowness")));
            case JUMP -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("jump_boost"))
            );
            case ABSORPTION -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("absorption"))
            );
            case DAMAGE_RESISTANCE -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("resistance"))
            );
            case FAST_DIGGING -> addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("haste")));
            case FIRE_RESISTANCE -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("fire_resistance"))
            );
            case HEAL -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("instant_health"))
            );
            case HEALTH_BOOST -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("health_boost"))
            );
            case HUNGER -> addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("hunger")));
            case INCREASE_DAMAGE -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("strenght"))
            );
            case INVISIBILITY -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("invisibility"))
            );
            case NIGHT_VISION -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("night_vision"))
            );
            case POISON -> addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("poison")));
            case REGENERATION -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("regeneration"))
            );
            case SATURATION -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("saturation"))
            );
            case SLOW_DIGGING -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("mining_fatigue"))
            );
            case SPEED -> addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("speed")));
            case WATER_BREATHING -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("water_breathing"))
            );
            case WEAKNESS -> addEffect(
                    entity,
                    Registry.EFFECT.get(NamespacedKey.fromString("weakness"))
            );
            case WITHER -> addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("wither")));
            case KILL -> entity.setHealth(0.0d);
            case RANDOMTP -> {
                Random random = new Random();
                Location targetLocation = entity.getLocation();
                targetLocation.setX(targetLocation.getX() + (random.nextInt(this.maxRandomTeleportationRange) - this.maxRandomTeleportationRange / 2.0));
                targetLocation.setZ(targetLocation.getZ() + (random.nextInt(this.maxRandomTeleportationRange) - this.maxRandomTeleportationRange / 2.0));
                entity.teleport(targetLocation);
            }
            case ALL_POTION -> {
                addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("blindness")));
                addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("nausea")));
                addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("slowness")));
                addEffect(entity, Registry.EFFECT.get(NamespacedKey.fromString("jump_boost")));
            }
            case FORCE -> {
                Vector playerVector = Vectors.toBukkit(getTargetBlock());
                Vector direction = entity.getLocation().toVector().clone();
                direction.subtract(playerVector);
                double length = direction.length();
                double strength = (1 - (length / toolkitProperties.getBrushSize())) * this.punishLevel;
                direction.normalize();
                direction.multiply(strength);
                entity.setVelocity(direction);
            }
            case HYPNO -> {
                if (entity instanceof Player) {
                    Location location = entity.getLocation();
                    Location target = location.clone();
                    for (int z = this.punishLevel; z >= -this.punishLevel; z--) {
                        for (int x = this.punishLevel; x >= -this.punishLevel; x--) {
                            for (int y = this.punishLevel; y >= -this.punishLevel; y--) {
                                target.setX(location.getX() + x);
                                target.setY(location.getY() + y);
                                target.setZ(location.getZ() + z);
                                if (this.hypnoAffectLandscape && Materials.isEmpty(BukkitAdapter.asBlockType(target
                                        .getBlock()
                                        .getType()))) {
                                    continue;
                                }
                                target = location.clone();
                                target.add(x, y, z);
                                ((Player) entity).sendBlockChange(
                                        target,
                                        BukkitAdapter.adapt(toolkitProperties.getPattern().asBlockState())
                                );
                            }
                        }
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + this.punishment);
        }
    }

    private void addEffect(LivingEntity entity, PotionEffectType type) {
        PotionEffect effect = new PotionEffect(type, TICKS_PER_SECOND * this.punishDuration, this.punishLevel);
        entity.addPotionEffect(effect);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.punish.set-punishment",
                        this.punishment.getFullName()
                ))
                .brushSizeMessage()
                .cylinderCenterMessage()
                .send();
    }

    public enum Punishment {
        FIRE("fire"),
        LIGHTNING("lightning"),
        BLINDNESS("blindness"),
        DRUNK("drunk"),
        KILL("kill"),
        RANDOMTP("random-tp"),
        ALL_POTION("all-potion"),
        SLOW("slow"),
        JUMP("jump"),
        ABSORPTION("absorption"),
        DAMAGE_RESISTANCE("damage-resistance"),
        FAST_DIGGING("fast-digging"),
        FIRE_RESISTANCE("fire-resistance"),
        HEAL("heal"),
        HEALTH_BOOST("health-boost"),
        HUNGER("hunger"),
        INCREASE_DAMAGE("increase-damage"),
        INVISIBILITY("invisibility"),
        NIGHT_VISION("night-vision"),
        POISON("poison"),
        REGENERATION("regeneration"),
        SATURATION("saturation"),
        SLOW_DIGGING("slow-digging"),
        SPEED("speed"),
        WATER_BREATHING("water-breathing"),
        WEAKNESS("weakness"),
        WITHER("wither"),
        FORCE("force"),
        HYPNO("hypno");

        private final String name;

        Punishment(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public TranslatableComponent getFullName() {
            return Caption.of("voxelsniper.performer-brush.punish.type." + this.name);
        }
    }

}
