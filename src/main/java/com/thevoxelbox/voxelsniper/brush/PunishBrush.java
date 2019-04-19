package com.thevoxelbox.voxelsniper.brush;

import java.util.List;
import java.util.Random;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
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
public class PunishBrush extends PerformBrush {

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
		this.setName("Punish");
	}

	@SuppressWarnings("deprecation")
	private void applyPunishment(LivingEntity entity, SnipeData snipeData) {
		switch (this.punishment) {
			case FIRE:
				entity.setFireTicks(TICKS_PER_SECOND * this.punishDuration);
				break;
			case LIGHTNING:
				entity.getWorld()
					.strikeLightning(entity.getLocation());
				break;
			case BLINDNESS:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case DRUNK:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case SLOW:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case JUMP:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case ABSORPTION:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case DAMAGE_RESISTANCE:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case FAST_DIGGING:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case FIRE_RESISTANCE:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case HEAL:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case HEALTH_BOOST:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case HUNGER:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case INCREASE_DAMAGE:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case INVISIBILITY:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case NIGHT_VISION:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case POISON:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case REGENERATION:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case SATURATION:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case SLOW_DIGGING:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case SPEED:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case WATER_BREATHING:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case WEAKNESS:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				break;
			case WITHER:
				entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
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
				entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
				entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, TICKS_PER_SECOND * this.punishDuration, this.punishLevel), true);
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
				double stregth = (1 - (length / snipeData.getBrushSize())) * this.punishLevel;
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
								((Player) entity).sendBlockChange(target, snipeData.getVoxelId(), snipeData.getData());
							}
						}
					}
				}
				break;
			default:
				Bukkit.getLogger()
					.warning("Could not determine the punishment of punish brush!");
				break;
		}
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		if (!snipeData.getOwner()
			.getPlayer()
			.hasPermission("voxelsniper.punish")) {
			snipeData.sendMessage("The server says no!");
			return;
		}
		this.punishDuration = snipeData.getVoxelHeight();
		this.punishLevel = snipeData.getCylinderCenter();
		if (this.specificPlayer) {
			Player punishedPlayer = Bukkit.getPlayer(this.punishPlayerName);
			if (punishedPlayer == null) {
				snipeData.sendMessage("No player " + this.punishPlayerName + " found.");
				return;
			}
			this.applyPunishment(punishedPlayer, snipeData);
			return;
		}
		int brushSizeSquare = snipeData.getBrushSize() * snipeData.getBrushSize();
		Location targetLocation = new Location(snipeData.getWorld(), this.getTargetBlock()
			.getX(), this.getTargetBlock()
			.getY(), this.getTargetBlock()
			.getZ());
		List<LivingEntity> entities = snipeData.getWorld()
			.getLivingEntities();
		int numPunishApps = 0;
		for (LivingEntity entity : entities) {
			if (snipeData.getOwner()
				.getPlayer() != entity || this.hitsSelf) {
				if (snipeData.getBrushSize() >= 0) {
					try {
						if (entity.getLocation()
							.distanceSquared(targetLocation) <= brushSizeSquare) {
							numPunishApps++;
							this.applyPunishment(entity, snipeData);
						}
					} catch (RuntimeException exception) {
						exception.printStackTrace();
						snipeData.sendMessage("An error occured.");
						return;
					}
				} else if (snipeData.getBrushSize() == INFINIPUNISH_SIZE) {
					numPunishApps++;
					this.applyPunishment(entity, snipeData);
				}
			}
		}
		snipeData.sendMessage(ChatColor.DARK_RED + "Punishment applied to " + numPunishApps + " living entities.");
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		if (!snipeData.getOwner()
			.getPlayer()
			.hasPermission("voxelsniper.punish")) {
			snipeData.sendMessage("The server says no!");
			return;
		}
		int brushSizeSquare = snipeData.getBrushSize() * snipeData.getBrushSize();
		Location targetLocation = new Location(snipeData.getWorld(), this.getTargetBlock()
			.getX(), this.getTargetBlock()
			.getY(), this.getTargetBlock()
			.getZ());
		List<LivingEntity> entities = snipeData.getWorld()
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
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom(ChatColor.GREEN + "Punishment: " + this.punishment);
		message.size();
		message.center();
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i].toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Punish Brush Options:");
				snipeData.sendMessage(ChatColor.AQUA + "Punishments can be set via /b p [punishment]");
				snipeData.sendMessage(ChatColor.AQUA + "Punishment level can be set with /vc [level]");
				snipeData.sendMessage(ChatColor.AQUA + "Punishment duration in seconds can be set with /vh [duration]");
				snipeData.sendMessage(ChatColor.AQUA + "Parameter -toggleHypnoLandscape will make Hypno punishment only affect landscape.");
				snipeData.sendMessage(ChatColor.AQUA + "Parameter -toggleSM [playername] will make punishbrush only affect that player.");
				snipeData.sendMessage(ChatColor.AQUA + "Parameter -toggleSelf will toggle whether you get hit as well.");
				snipeData.sendMessage(ChatColor.AQUA + "Available Punishment Options:");
				StringBuilder punishmentOptions = new StringBuilder();
				for (Punishment punishment : Punishment.values()) {
					if (punishmentOptions.length() != 0) {
						punishmentOptions.append(" | ");
					}
					punishmentOptions.append(punishment.name());
				}
				snipeData.sendMessage(ChatColor.GOLD + punishmentOptions.toString());
				return;
			} else if (parameter.equalsIgnoreCase("-toggleSM")) {
				this.specificPlayer = !this.specificPlayer;
				if (this.specificPlayer) {
					try {
						this.punishPlayerName = parameters[++i];
					} catch (IndexOutOfBoundsException exception) {
						snipeData.sendMessage(ChatColor.AQUA + "You have to specify a player name after -toggleSM if you want to turn the specific player feature on.");
					}
				}
			} else if (parameter.equalsIgnoreCase("-toggleSelf")) {
				this.hitsSelf = !this.hitsSelf;
				if (this.hitsSelf) {
					snipeData.sendMessage(ChatColor.AQUA + "Your punishments will now affect you too!");
				} else {
					snipeData.sendMessage(ChatColor.AQUA + "Your punishments will no longer affect you!");
				}
			} else if (parameter.equalsIgnoreCase("-toggleHypnoLandscape")) {
				this.hypnoAffectLandscape = !this.hypnoAffectLandscape;
			} else {
				try {
					this.punishment = Punishment.valueOf(parameter.toUpperCase());
					snipeData.sendMessage(ChatColor.AQUA + this.punishment.name()
						.toLowerCase() + " punishment selected.");
				} catch (IllegalArgumentException exception) {
					snipeData.sendMessage(ChatColor.AQUA + "No such Punishment.");
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
