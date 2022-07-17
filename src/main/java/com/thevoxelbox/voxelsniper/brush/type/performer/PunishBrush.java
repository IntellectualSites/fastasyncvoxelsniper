package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Punish Brush Options:");
            messenger.sendMessage(ChatColor.AQUA + "/b p [p] -- Sets Punishment to p.");
            messenger.sendMessage(ChatColor.AQUA + "/vc [n] -- Sets Punishment level to n.");
            messenger.sendMessage(ChatColor.AQUA + "/vh [n] -- Sets Punishment duration to n.");
            messenger.sendMessage(ChatColor.AQUA + "/b p toggleSM [s] -- Makes Punish Brush only affect s player name.");
            messenger.sendMessage(ChatColor.AQUA + "/b p toggleSelf -- Toggles whether you get hit as well.");
            messenger.sendMessage(ChatColor.AQUA + "/b p toggleHypnoLandscape -- Makes Hypno punishment only affect " +
                    "landscape.");
            messenger.sendMessage(ChatColor.AQUA + "/b p list -- Lists all available punishments.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(
                            Arrays.stream(Punishment.values())
                                    .map(punishment -> ((punishment == this.punishment) ? ChatColor.GOLD : ChatColor.GRAY) +
                                            punishment.name())
                                    .collect(Collectors.joining(ChatColor.WHITE + ", ",
                                            ChatColor.AQUA + "Available punishments: ", ""
                                    ))
                    );
                } else if (firstParameter.equalsIgnoreCase("toggleSelf")) {
                    this.hitsSelf = !this.hitsSelf;
                    if (this.hitsSelf) {
                        messenger.sendMessage(ChatColor.AQUA + "Your punishments will now affect you too!");
                    } else {
                        messenger.sendMessage(ChatColor.AQUA + "Your punishments will no longer affect you!");
                    }
                } else if (firstParameter.equalsIgnoreCase("toggleHypnoLandscape")) {
                    this.hypnoAffectLandscape = !this.hypnoAffectLandscape;
                } else {
                    try {
                        this.punishment = Punishment.valueOf(firstParameter.toUpperCase(Locale.ROOT));
                        messenger.sendMessage(ChatColor.AQUA + this.punishment.name()
                                .toLowerCase(Locale.ROOT) + " punishment selected.");
                    } catch (IllegalArgumentException exception) {
                        messenger.sendMessage(ChatColor.RED + "Invalid Punishment: " + firstParameter);
                    }
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("toggleSM")) {
                    String punishPlayerName = parameters[1];
                    if (Bukkit.getPlayer(punishPlayerName) != null) {
                        this.specificPlayer = !this.specificPlayer;
                        if (this.specificPlayer) {
                            this.punishPlayerName = punishPlayerName;
                            messenger.sendMessage(ChatColor.AQUA + "Your punishments will now only affect: " + this.punishPlayerName);
                        }
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid player name: " + punishPlayerName);
                    }
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display " +
                            "parameter info.");
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display " +
                        "parameter info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.concat(
                    PUNISHMENTS.stream(),
                    Stream.of("list", "toggleSM", "toggleSelf", "toggleHypnoLandscape")
            ), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (firstParameter.equalsIgnoreCase("toggleSM")) {
                String parameter = parameters[1];
                Player sniper = snipe.getSniper().getPlayer();
                return super.sortCompletions(Bukkit.getOnlinePlayers().stream()
                        .filter(sniper::canSee)
                        .map(HumanEntity::getName), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        if (!player.hasPermission("voxelsniper.punish")) {
            messenger.sendMessage("The server says no!");
            return;
        }
        this.punishDuration = toolkitProperties.getVoxelHeight();
        this.punishLevel = toolkitProperties.getCylinderCenter();
        if (this.specificPlayer) {
            TaskManager.taskManager().sync(() -> {
                Player punishedPlayer = Bukkit.getPlayer(this.punishPlayerName);
                if (punishedPlayer == null) {
                    messenger.sendMessage("No player " + this.punishPlayerName + " found.");
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
                        } catch (RuntimeException exception) {
                            exception.printStackTrace();
                            messenger.sendMessage("An error occured.");
                            return null;
                        }
                    } else if (brushSize == this.infiniPunishSize) {
                        numPunishApps++;
                        applyPunishment(entity, snipe);
                    }
                }
            }
            messenger.sendMessage(ChatColor.DARK_RED + "Punishment applied to " + numPunishApps + " living entities.");
            return null;
        });
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        if (!player.hasPermission("voxelsniper.punish")) {
            messenger.sendMessage("The server says no!");
            return;
        }
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
                entity.removePotionEffect(PotionEffectType.BLINDNESS);
                entity.removePotionEffect(PotionEffectType.CONFUSION);
                entity.removePotionEffect(PotionEffectType.SLOW);
                entity.removePotionEffect(PotionEffectType.JUMP);
            }
        }
    }

    private void applyPunishment(LivingEntity entity, Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        switch (this.punishment) {
            case FIRE:
                entity.setFireTicks(TICKS_PER_SECOND * this.punishDuration);
                break;
            case LIGHTNING:
                entity.getWorld()
                        .strikeLightning(entity.getLocation());
                break;
            case BLINDNESS:
                addEffect(entity, PotionEffectType.BLINDNESS);
                break;
            case DRUNK:
                addEffect(entity, PotionEffectType.CONFUSION);
                break;
            case SLOW:
                addEffect(entity, PotionEffectType.SLOW);
                break;
            case JUMP:
                addEffect(entity, PotionEffectType.JUMP);
                break;
            case ABSORPTION:
                addEffect(entity, PotionEffectType.ABSORPTION);
                break;
            case DAMAGE_RESISTANCE:
                addEffect(entity, PotionEffectType.DAMAGE_RESISTANCE);
                break;
            case FAST_DIGGING:
                addEffect(entity, PotionEffectType.FAST_DIGGING);
                break;
            case FIRE_RESISTANCE:
                addEffect(entity, PotionEffectType.FIRE_RESISTANCE);
                break;
            case HEAL:
                addEffect(entity, PotionEffectType.HEAL);
                break;
            case HEALTH_BOOST:
                addEffect(entity, PotionEffectType.HEALTH_BOOST);
                break;
            case HUNGER:
                addEffect(entity, PotionEffectType.HUNGER);
                break;
            case INCREASE_DAMAGE:
                addEffect(entity, PotionEffectType.INCREASE_DAMAGE);
                break;
            case INVISIBILITY:
                addEffect(entity, PotionEffectType.INVISIBILITY);
                break;
            case NIGHT_VISION:
                addEffect(entity, PotionEffectType.NIGHT_VISION);
                break;
            case POISON:
                addEffect(entity, PotionEffectType.POISON);
                break;
            case REGENERATION:
                addEffect(entity, PotionEffectType.REGENERATION);
                break;
            case SATURATION:
                addEffect(entity, PotionEffectType.SATURATION);
                break;
            case SLOW_DIGGING:
                addEffect(entity, PotionEffectType.SLOW_DIGGING);
                break;
            case SPEED:
                addEffect(entity, PotionEffectType.SPEED);
                break;
            case WATER_BREATHING:
                addEffect(entity, PotionEffectType.WATER_BREATHING);
                break;
            case WEAKNESS:
                addEffect(entity, PotionEffectType.WEAKNESS);
                break;
            case WITHER:
                addEffect(entity, PotionEffectType.WITHER);
                break;
            case KILL:
                entity.setHealth(0.0d);
                break;
            case RANDOMTP:
                Random random = new Random();
                Location targetLocation = entity.getLocation();
                targetLocation.setX(targetLocation.getX() + (random.nextInt(this.maxRandomTeleportationRange) - this.maxRandomTeleportationRange / 2.0));
                targetLocation.setZ(targetLocation.getZ() + (random.nextInt(this.maxRandomTeleportationRange) - this.maxRandomTeleportationRange / 2.0));
                entity.teleport(targetLocation);
                break;
            case ALL_POTION:
                addEffect(entity, PotionEffectType.BLINDNESS);
                addEffect(entity, PotionEffectType.CONFUSION);
                addEffect(entity, PotionEffectType.SLOW);
                addEffect(entity, PotionEffectType.JUMP);
                break;
            case FORCE:
                Vector playerVector = Vectors.toBukkit(getTargetBlock());
                Vector direction = entity.getLocation().toVector().clone();
                direction.subtract(playerVector);
                double length = direction.length();
                double strength = (1 - (length / toolkitProperties.getBrushSize())) * this.punishLevel;
                direction.normalize();
                direction.multiply(strength);
                entity.setVelocity(direction);
                break;
            case HYPNO:
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
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.punishment);
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
                .message(ChatColor.GREEN + "Punishment: " + this.punishment)
                .brushSizeMessage()
                .cylinderCenterMessage()
                .send();
    }

    private enum Punishment {
        FIRE,
        LIGHTNING,
        BLINDNESS,
        DRUNK,
        KILL,
        RANDOMTP,
        ALL_POTION,
        SLOW,
        JUMP,
        ABSORPTION,
        DAMAGE_RESISTANCE,
        FAST_DIGGING,
        FIRE_RESISTANCE,
        HEAL,
        HEALTH_BOOST,
        HUNGER,
        INCREASE_DAMAGE,
        INVISIBILITY,
        NIGHT_VISION,
        POISON,
        REGENERATION,
        SATURATION,
        SLOW_DIGGING,
        SPEED,
        WATER_BREATHING,
        WEAKNESS,
        WITHER,
        FORCE,
        HYPNO
    }

}
