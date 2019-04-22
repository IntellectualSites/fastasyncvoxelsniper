package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.List;
import java.util.Random;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Punish_Brush
 *
 * @author Monofraps
 * @author Deamon
 * @author MikeMatrix
 */
public class PunishBrush extends AbstractPerformerBrush {

	private static final int MAX_RANDOM_TELEPORTATION_RANGE = 400;
	private static final int TICKS_PER_SECOND = 20;
	private static final int INFINIPUNISH_SIZE = -3;
	private static final int DEFAULT_PUNISH_LEVEL = 10;
	private static final int DEFAULT_PUNISH_DURATION = 60;

	private Punishment punishment = Punishment.FIRE;
	private int punishLevel = DEFAULT_PUNISH_LEVEL;
	private int punishDuration = DEFAULT_PUNISH_DURATION;
	private boolean specificPlayer;
	private String punishPlayerName = "";
	private boolean hypnoAffectLandscape;
	private boolean hitsSelf;

	/**
	 * Default Constructor.
	 */
	public PunishBrush() {
		super("Punish");
	}

	private void applyPunishment(LivingEntity entity, ToolkitProperties toolkitProperties) {
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
				targetLocation.setX(targetLocation.getX() + (random.nextInt(MAX_RANDOM_TELEPORTATION_RANGE) - MAX_RANDOM_TELEPORTATION_RANGE / 2.0));
				targetLocation.setZ(targetLocation.getZ() + (random.nextInt(MAX_RANDOM_TELEPORTATION_RANGE) - MAX_RANDOM_TELEPORTATION_RANGE / 2.0));
				entity.teleport(targetLocation);
				break;
			case ALL_POTION:
				addEffect(entity, PotionEffectType.BLINDNESS);
				addEffect(entity, PotionEffectType.CONFUSION);
				addEffect(entity, PotionEffectType.SLOW);
				addEffect(entity, PotionEffectType.JUMP);
				break;
			case FORCE:
				Vector playerVector = this.getTargetBlock()
					.getLocation()
					.toVector();
				Vector direction = entity.getLocation()
					.toVector()
					.clone();
				direction.subtract(playerVector);
				double length = direction.length();
				double stregth = (1 - (length / toolkitProperties.getBrushSize())) * this.punishLevel;
				direction.normalize();
				direction.multiply(stregth);
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
								if (this.hypnoAffectLandscape && target.getBlock()
									.getType() == Material.AIR) {
									continue;
								}
								target = location.clone();
								target.add(x, y, z);
								((Player) entity).sendBlockChange(target, toolkitProperties.getBlockData());
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
		entity.addPotionEffect(effect, true);
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		if (!toolkitProperties.getOwner()
			.getPlayer()
			.hasPermission("voxelsniper.punish")) {
			toolkitProperties.sendMessage("The server says no!");
			return;
		}
		this.punishDuration = toolkitProperties.getVoxelHeight();
		this.punishLevel = toolkitProperties.getCylinderCenter();
		if (this.specificPlayer) {
			Player punishedPlayer = Bukkit.getPlayer(this.punishPlayerName);
			if (punishedPlayer == null) {
				toolkitProperties.sendMessage("No player " + this.punishPlayerName + " found.");
				return;
			}
			this.applyPunishment(punishedPlayer, toolkitProperties);
			return;
		}
		int brushSizeSquare = toolkitProperties.getBrushSize() * toolkitProperties.getBrushSize();
		Location targetLocation = new Location(toolkitProperties.getWorld(), this.getTargetBlock()
			.getX(), this.getTargetBlock()
			.getY(), this.getTargetBlock()
			.getZ());
		List<LivingEntity> entities = toolkitProperties.getWorld()
			.getLivingEntities();
		int numPunishApps = 0;
		for (LivingEntity entity : entities) {
			if (toolkitProperties.getOwner()
				.getPlayer() != entity || this.hitsSelf) {
				if (toolkitProperties.getBrushSize() >= 0) {
					try {
						if (entity.getLocation()
							.distanceSquared(targetLocation) <= brushSizeSquare) {
							numPunishApps++;
							this.applyPunishment(entity, toolkitProperties);
						}
					} catch (RuntimeException exception) {
						exception.printStackTrace();
						toolkitProperties.sendMessage("An error occured.");
						return;
					}
				} else if (toolkitProperties.getBrushSize() == INFINIPUNISH_SIZE) {
					numPunishApps++;
					this.applyPunishment(entity, toolkitProperties);
				}
			}
		}
		toolkitProperties.sendMessage(ChatColor.DARK_RED + "Punishment applied to " + numPunishApps + " living entities.");
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		if (!toolkitProperties.getOwner()
			.getPlayer()
			.hasPermission("voxelsniper.punish")) {
			toolkitProperties.sendMessage("The server says no!");
			return;
		}
		int brushSizeSquare = toolkitProperties.getBrushSize() * toolkitProperties.getBrushSize();
		Location targetLocation = new Location(toolkitProperties.getWorld(), this.getTargetBlock()
			.getX(), this.getTargetBlock()
			.getY(), this.getTargetBlock()
			.getZ());
		List<LivingEntity> entities = toolkitProperties.getWorld()
			.getLivingEntities();
		for (LivingEntity entity : entities) {
			if (entity.getLocation()
				.distanceSquared(targetLocation) < brushSizeSquare) {
				entity.setFireTicks(0);
				entity.removePotionEffect(PotionEffectType.BLINDNESS);
				entity.removePotionEffect(PotionEffectType.CONFUSION);
				entity.removePotionEffect(PotionEffectType.SLOW);
				entity.removePotionEffect(PotionEffectType.JUMP);
			}
		}
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.custom(ChatColor.GREEN + "Punishment: " + this.punishment);
		messages.size();
		messages.center();
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i].toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				toolkitProperties.sendMessage(ChatColor.GOLD + "Punish Brush Options:");
				toolkitProperties.sendMessage(ChatColor.AQUA + "Punishments can be set via /b p [punishment]");
				toolkitProperties.sendMessage(ChatColor.AQUA + "Punishment level can be set with /vc [level]");
				toolkitProperties.sendMessage(ChatColor.AQUA + "Punishment duration in seconds can be set with /vh [duration]");
				toolkitProperties.sendMessage(ChatColor.AQUA + "Parameter -toggleHypnoLandscape will make Hypno punishment only affect landscape.");
				toolkitProperties.sendMessage(ChatColor.AQUA + "Parameter -toggleSM [playername] will make punishbrush only affect that player.");
				toolkitProperties.sendMessage(ChatColor.AQUA + "Parameter -toggleSelf will toggle whether you get hit as well.");
				toolkitProperties.sendMessage(ChatColor.AQUA + "Available Punishment Options:");
				StringBuilder punishmentOptions = new StringBuilder();
				for (Punishment punishment : Punishment.values()) {
					if (punishmentOptions.length() != 0) {
						punishmentOptions.append(" | ");
					}
					punishmentOptions.append(punishment.name());
				}
				toolkitProperties.sendMessage(ChatColor.GOLD + punishmentOptions.toString());
				return;
			} else if (parameter.equalsIgnoreCase("-toggleSM")) {
				this.specificPlayer = !this.specificPlayer;
				if (this.specificPlayer) {
					try {
						this.punishPlayerName = parameters[++i];
					} catch (IndexOutOfBoundsException exception) {
						toolkitProperties.sendMessage(ChatColor.AQUA + "You have to specify a player name after -toggleSM if you want to turn the specific player feature on.");
					}
				}
			} else if (parameter.equalsIgnoreCase("-toggleSelf")) {
				this.hitsSelf = !this.hitsSelf;
				if (this.hitsSelf) {
					toolkitProperties.sendMessage(ChatColor.AQUA + "Your punishments will now affect you too!");
				} else {
					toolkitProperties.sendMessage(ChatColor.AQUA + "Your punishments will no longer affect you!");
				}
			} else if (parameter.equalsIgnoreCase("-toggleHypnoLandscape")) {
				this.hypnoAffectLandscape = !this.hypnoAffectLandscape;
			} else {
				try {
					this.punishment = Punishment.valueOf(parameter.toUpperCase());
					toolkitProperties.sendMessage(ChatColor.AQUA + this.punishment.name()
						.toLowerCase() + " punishment selected.");
				} catch (IllegalArgumentException exception) {
					toolkitProperties.sendMessage(ChatColor.AQUA + "No such Punishment.");
				}
			}
		}
	}

	/**
	 * @author Monofraps
	 */
	private enum Punishment {
		// Monofraps
		FIRE,
		LIGHTNING,
		BLINDNESS,
		DRUNK,
		KILL,
		RANDOMTP,
		ALL_POTION,
		// Deamon
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
		// MikeMatrix
		FORCE,
		HYPNO
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.punish";
	}
}
