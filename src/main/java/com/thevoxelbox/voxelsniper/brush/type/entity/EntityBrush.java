package com.thevoxelbox.voxelsniper.brush.type.entity;

import java.util.Arrays;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityBrush extends AbstractBrush {

	private EntityType entityType = EntityType.ZOMBIE;

	@SuppressWarnings("deprecation")
	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(ChatColor.BLUE + "The available entity types are as follows:");
			String names = Arrays.stream(EntityType.values())
				.map(currentEntity -> ChatColor.AQUA + " | " + ChatColor.DARK_GREEN + currentEntity.getName())
				.collect(Collectors.joining("", "", ChatColor.AQUA + " |"));
			messenger.sendMessage(names);
		} else {
			EntityType currentEntity = EntityType.fromName(parameters[1]);
			if (currentEntity != null) {
				this.entityType = currentEntity;
				messenger.sendMessage(ChatColor.GREEN + "Entity type set to " + this.entityType.getName());
			} else {
				messenger.sendMessage(ChatColor.RED + "This is not a valid entity!");
			}
		}
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
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		SnipeMessenger messenger = snipe.createMessenger();
		for (int x = 0; x < toolkitProperties.getBrushSize(); x++) {
			try {
				World world = getWorld();
				Block lastBlock = getLastBlock();
				Class<? extends Entity> entityClass = this.entityType.getEntityClass();
				if (entityClass == null) {
					return;
				}
				world.spawn(lastBlock.getLocation(), entityClass);
			} catch (IllegalArgumentException exception) {
				messenger.sendMessage(ChatColor.RED + "Cannot spawn entity!");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.message(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + this.entityType.getName() + ")")
			.brushSizeMessage()
			.send();
	}
}
