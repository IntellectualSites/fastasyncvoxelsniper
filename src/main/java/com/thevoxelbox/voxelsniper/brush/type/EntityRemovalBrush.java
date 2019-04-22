package com.thevoxelbox.voxelsniper.brush.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

public class EntityRemovalBrush extends AbstractBrush {

	private final List<String> exemptions = new ArrayList<>(3);

	public EntityRemovalBrush() {
		super("Entity Removal");
		this.exemptions.add("org.bukkit.entity.Player");
		this.exemptions.add("org.bukkit.entity.Hanging");
		this.exemptions.add("org.bukkit.entity.NPC");
	}

	private void radialRemoval(ToolkitProperties toolkitProperties) {
		Chunk targetChunk = getTargetBlock().getChunk();
		int entityCount = 0;
		int chunkCount = 0;
		try {
			entityCount += removeEntities(targetChunk);
			int radius = Math.round(toolkitProperties.getBrushSize() / 16);
			for (int x = targetChunk.getX() - radius; x <= targetChunk.getX() + radius; x++) {
				for (int z = targetChunk.getZ() - radius; z <= targetChunk.getZ() + radius; z++) {
					entityCount += removeEntities(getWorld().getChunkAt(x, z));
					chunkCount++;
				}
			}
		} catch (PatternSyntaxException exception) {
			exception.printStackTrace();
			toolkitProperties.sendMessage(ChatColor.RED + "Error in RegEx: " + ChatColor.LIGHT_PURPLE + exception.getPattern());
			toolkitProperties.sendMessage(ChatColor.RED + String.format("%s (Index: %d)", exception.getDescription(), exception.getIndex()));
		}
		toolkitProperties.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + entityCount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + chunkCount + ChatColor.GREEN + (chunkCount == 1 ? " chunk." : " chunks."));
	}

	private int removeEntities(Chunk chunk) throws PatternSyntaxException {
		int entityCount = 0;
		for (Entity entity : chunk.getEntities()) {
			if (isClassInExemptionList(entity.getClass())) {
				continue;
			}
			entity.remove();
			entityCount++;
		}
		return entityCount;
	}

	private boolean isClassInExemptionList(Class<? extends Entity> entityClass) throws PatternSyntaxException {
		// Create a list of superclasses and interfaces implemented by the current entity type
		List<String> entityClassHierarchy = new ArrayList<>();
		Class<?> currentClass = entityClass;
		while (currentClass != null && !currentClass.equals(Object.class)) {
			entityClassHierarchy.add(currentClass.getCanonicalName());
			for (Class<?> intrf : currentClass.getInterfaces()) {
				entityClassHierarchy.add(intrf.getCanonicalName());
			}
			currentClass = currentClass.getSuperclass();
		}
		return this.exemptions.stream()
			.anyMatch(exemptionPattern -> entityClassHierarchy.stream()
				.anyMatch(typeName -> typeName.matches(exemptionPattern)));
	}

	@Override
	public void arrow(ToolkitProperties toolkitProperties) {
		this.radialRemoval(toolkitProperties);
	}

	@Override
	public void powder(ToolkitProperties toolkitProperties) {
		this.radialRemoval(toolkitProperties);
	}

	@Override
	public void info(Messages messages) {
		messages.brushName(getName());
		StringBuilder exemptionsList = new StringBuilder(ChatColor.GREEN + "Exemptions: " + ChatColor.LIGHT_PURPLE);
		for (Iterator<String> it = this.exemptions.iterator(); it.hasNext(); ) {
			exemptionsList.append(it.next());
			if (it.hasNext()) {
				exemptionsList.append(", ");
			}
		}
		messages.custom(exemptionsList.toString());
		messages.size();
	}

	@Override
	public void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (String currentParam : parameters) {
			if (currentParam.startsWith("+") || currentParam.startsWith("-")) {
				boolean isAddOperation = currentParam.startsWith("+");
				// +#/-# will suppress auto-prefixing
				String exemptionPattern = currentParam.startsWith("+#") || currentParam.startsWith("-#") ? currentParam.substring(2) : (currentParam.contains(".") ? currentParam.substring(1) : ".*." + currentParam.substring(1));
				if (isAddOperation) {
					this.exemptions.add(exemptionPattern);
					toolkitProperties.sendMessage(String.format("Added %s to entity exemptions list.", exemptionPattern));
				} else {
					this.exemptions.remove(exemptionPattern);
					toolkitProperties.sendMessage(String.format("Removed %s from entity exemptions list.", exemptionPattern));
				}
			}
			if (currentParam.equalsIgnoreCase("list-exemptions") || currentParam.equalsIgnoreCase("lex")) {
				for (String exemption : this.exemptions) {
					toolkitProperties.sendMessage(ChatColor.LIGHT_PURPLE + exemption);
				}
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.entityremoval";
	}
}
