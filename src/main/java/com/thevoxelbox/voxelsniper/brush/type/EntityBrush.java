package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Entity_Brush
 *
 * @author Piotr
 */
public class EntityBrush extends AbstractBrush {

	private EntityType entityType = EntityType.ZOMBIE;

	public EntityBrush() {
		super("Entity");
	}

	private void spawn(ToolkitProperties toolkitProperties) {
		for (int x = 0; x < toolkitProperties.getBrushSize(); x++) {
			try {
				this.getWorld()
					.spawn(this.getLastBlock()
						.getLocation(), this.entityType.getEntityClass());
			} catch (IllegalArgumentException exception) {
				toolkitProperties.sendMessage(ChatColor.RED + "Cannot spawn entity!");
			}
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.spawn(toolkitProperties);
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		this.spawn(toolkitProperties);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushMessage(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + this.entityType.getName() + ")");
		messages.size();
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		if (parameters[1].equalsIgnoreCase("info")) {
			toolkitProperties.sendMessage(ChatColor.BLUE + "The available entity types are as follows:");
			String names = Arrays.stream(EntityType.values())
				.map(currentEntity -> ChatColor.AQUA + " | " + ChatColor.DARK_GREEN + currentEntity.getName())
				.collect(Collectors.joining("", "", ChatColor.AQUA + " |"));
			toolkitProperties.sendMessage(names);
		} else {
			EntityType currentEntity = EntityType.fromName(parameters[1]);
			if (currentEntity != null) {
				this.entityType = currentEntity;
				toolkitProperties.sendMessage(ChatColor.GREEN + "Entity type set to " + this.entityType.getName());
			} else {
				toolkitProperties.sendMessage(ChatColor.RED + "This is not a valid entity!");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.entity";
	}
}
