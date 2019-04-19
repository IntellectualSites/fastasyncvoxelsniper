package com.thevoxelbox.voxelsniper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.MutableClassToInstanceMap;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.perform.BrushPerformer;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import com.thevoxelbox.voxelsniper.event.SniperMaterialChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperReplaceMaterialChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class Sniper {

	private VoxelSniper plugin;
	private final UUID player;
	private boolean enabled = true;
	private LinkedList<Undo> undoList = new LinkedList<>();
	private Map<String, SniperTool> tools = Maps.newHashMap();

	public Sniper(VoxelSniper plugin, Player player) {
		this.plugin = plugin;
		this.player = player.getUniqueId();
		SniperTool sniperTool = new SniperTool(this);
		sniperTool.assignAction(SnipeAction.ARROW, Material.ARROW);
		sniperTool.assignAction(SnipeAction.GUNPOWDER, Material.GUNPOWDER);
		this.tools.put(null, sniperTool);
	}

	@Nullable
	public String getCurrentToolId() {
		return getToolId((getPlayer().getItemInHand() != null) ? getPlayer().getItemInHand()
			.getType() : null);
	}

	@Nullable
	public String getToolId(Material itemInHand) {
		if (itemInHand == null) {
			return null;
		}
		return this.tools.entrySet()
			.stream()
			.filter(entry -> entry.getValue()
				.hasToolAssigned(itemInHand))
			.findFirst()
			.map(Entry::getKey)
			.orElse(null);
	}

	@Nullable
	public Player getPlayer() {
		return Bukkit.getPlayer(this.player);
	}

	/**
	 * Sniper execution call.
	 *
	 * @param action Action player performed
	 * @param itemInHand Item in hand of player
	 * @param clickedBlock Block that the player targeted/interacted with
	 * @param clickedFace Face of that targeted Block
	 * @return true if command visibly processed, false otherwise.
	 */
	public boolean snipe(Action action, Material itemInHand, Block clickedBlock, BlockFace clickedFace) {
		String toolId = getToolId(itemInHand);
		SniperTool sniperTool = this.tools.get(toolId);
		switch (action) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				break;
			default:
				return false;
		}
		if (sniperTool.hasToolAssigned(itemInHand)) {
			if (sniperTool.getCurrentBrush() == null) {
				getPlayer().sendMessage("No Brush selected.");
				return true;
			}
			if (!getPlayer().hasPermission(sniperTool.getCurrentBrush()
				.getPermissionNode())) {
				getPlayer().sendMessage("You are not allowed to use this brush. You're missing the permission node '" + sniperTool.getCurrentBrush()
					.getPermissionNode() + "'");
				return true;
			}
			SnipeData snipeData = sniperTool.getSnipeData();
			if (getPlayer().isSneaking()) {
				Block targetBlock;
				SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand);
				switch (action) {
					case LEFT_CLICK_BLOCK:
					case LEFT_CLICK_AIR:
						if (clickedBlock != null) {
							targetBlock = clickedBlock;
						} else {
							RangeBlockHelper rangeBlockHelper = snipeData.isRanged() ? new RangeBlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new RangeBlockHelper(getPlayer(), getPlayer().getWorld());
							targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
						}
						switch (snipeAction) {
							case ARROW:
								if (targetBlock != null) {
									BlockData originalVoxel = snipeData.getVoxelData();
									BlockData blockData = targetBlock.getBlockData();
									snipeData.setVoxelData(blockData);
									SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, originalVoxel, blockData);
									Bukkit.getPluginManager()
										.callEvent(event);
								} else {
									int originalVoxel = snipeData.getVoxelId();
									snipeData.setVoxelId(0);
									SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, new MaterialData(originalVoxel, snipeData.getData()), new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
									Bukkit.getPluginManager()
										.callEvent(event);
								}
								snipeData.getVoxelMessage()
									.voxel();
								return true;
							case GUNPOWDER:
								if (targetBlock != null) {
									byte originalData = snipeData.getData();
									snipeData.setData(targetBlock.getData());
									SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getVoxelId(), originalData), new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
									Bukkit.getPluginManager()
										.callEvent(event);
									snipeData.getVoxelMessage()
										.data();
									return true;
								} else {
									byte originalData = snipeData.getData();
									snipeData.setData((byte) 0);
									SniperMaterialChangedEvent event = new SniperMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getVoxelId(), originalData), new MaterialData(snipeData.getVoxelId(), snipeData.getData()));
									Bukkit.getPluginManager()
										.callEvent(event);
									snipeData.getVoxelMessage()
										.data();
									return true;
								}
							default:
								break;
						}
						break;
					case RIGHT_CLICK_AIR:
					case RIGHT_CLICK_BLOCK:
						if (clickedBlock != null) {
							targetBlock = clickedBlock;
						} else {
							RangeBlockHelper rangeBlockHelper = snipeData.isRanged() ? new RangeBlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new RangeBlockHelper(getPlayer(), getPlayer().getWorld());
							targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
						}
						switch (snipeAction) {
							case ARROW:
								if (targetBlock != null) {
									int originalId = snipeData.getReplaceId();
									snipeData.setReplaceId(targetBlock.getTypeId());
									SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(originalId, snipeData.getReplaceData()), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
									Bukkit.getPluginManager()
										.callEvent(event);
									snipeData.getVoxelMessage()
										.replace();
									return true;
								} else {
									int originalId = snipeData.getReplaceId();
									snipeData.setReplaceId(0);
									SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(originalId, snipeData.getReplaceData()), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
									Bukkit.getPluginManager()
										.callEvent(event);
									snipeData.getVoxelMessage()
										.replace();
									return true;
								}
							case GUNPOWDER:
								if (targetBlock != null) {
									byte originalData = snipeData.getReplaceData();
									snipeData.setReplaceData(targetBlock.getData());
									SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getReplaceId(), originalData), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
									Bukkit.getPluginManager()
										.callEvent(event);
									snipeData.getVoxelMessage()
										.replaceData();
									return true;
								} else {
									byte originalData = snipeData.getReplaceData();
									snipeData.setReplaceData((byte) 0);
									SniperReplaceMaterialChangedEvent event = new SniperReplaceMaterialChangedEvent(this, toolId, new MaterialData(snipeData.getReplaceId(), originalData), new MaterialData(snipeData.getReplaceId(), snipeData.getReplaceData()));
									Bukkit.getPluginManager()
										.callEvent(event);
									snipeData.getVoxelMessage()
										.replaceData();
									return true;
								}
							default:
								break;
						}
						break;
					default:
						return false;
				}
			} else {
				SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand);
				switch (action) {
					case RIGHT_CLICK_AIR:
					case RIGHT_CLICK_BLOCK:
						break;
					default:
						return false;
				}
				Block lastBlock;
				Block targetBlock;
				if (clickedBlock != null) {
					targetBlock = clickedBlock;
					lastBlock = clickedBlock.getRelative(clickedFace);
					if (lastBlock == null) {
						getPlayer().sendMessage(ChatColor.RED + "Snipe target block must be visible.");
						return true;
					}
				} else {
					RangeBlockHelper rangeBlockHelper = snipeData.isRanged() ? new RangeBlockHelper(getPlayer(), getPlayer().getWorld(), snipeData.getRange()) : new RangeBlockHelper(getPlayer(), getPlayer().getWorld());
					targetBlock = snipeData.isRanged() ? rangeBlockHelper.getRangeBlock() : rangeBlockHelper.getTargetBlock();
					lastBlock = rangeBlockHelper.getLastBlock();
					if (targetBlock == null || lastBlock == null) {
						getPlayer().sendMessage(ChatColor.RED + "Snipe target block must be visible.");
						return true;
					}
				}
				if (sniperTool.getCurrentBrush() instanceof PerformBrush) {
					PerformBrush performerBrush = (PerformBrush) sniperTool.getCurrentBrush();
					performerBrush.initPerformer(snipeData);
				}
				return sniperTool.getCurrentBrush()
					.perform(snipeAction, snipeData, targetBlock, lastBlock);
			}
		}
		return false;
	}

	public Brush setBrush(String toolId, Class<? extends Brush> brush) {
		if (!this.tools.containsKey(toolId)) {
			return null;
		}
		return this.tools.get(toolId)
			.setCurrentBrush(brush);
	}

	public Brush getBrush(String toolId) {
		if (!this.tools.containsKey(toolId)) {
			return null;
		}
		return this.tools.get(toolId)
			.getCurrentBrush();
	}

	public Brush previousBrush(String toolId) {
		if (!this.tools.containsKey(toolId)) {
			return null;
		}
		return this.tools.get(toolId)
			.previousBrush();
	}

	public boolean setTool(String toolId, SnipeAction action, Material itemInHand) {
		for (Map.Entry<String, SniperTool> entry : this.tools.entrySet()) {
			if (!entry.getKey()
				.equals(toolId) && entry.getValue()
				.hasToolAssigned(itemInHand)) {
				return false;
			}
		}
		if (!this.tools.containsKey(toolId)) {
			SniperTool tool = new SniperTool(this);
			this.tools.put(toolId, tool);
		}
		this.tools.get(toolId)
			.assignAction(action, itemInHand);
		return true;
	}

	public void removeTool(String toolId, Material itemInHand) {
		if (!this.tools.containsKey(toolId)) {
			SniperTool tool = new SniperTool(this);
			this.tools.put(toolId, tool);
		}
		this.tools.get(toolId)
			.unassignAction(itemInHand);
	}

	public void removeTool(String toolId) {
		if (toolId == null) {
			return;
		}
		this.tools.remove(toolId);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void storeUndo(@Nullable Undo undo) {
		if (VoxelSniper.getInstance()
			.getVoxelSniperConfig()
			.getUndoCacheSize() <= 0) {
			return;
		}
		if (undo != null && undo.getSize() > 0) {
			while (this.undoList.size() >= this.plugin.getVoxelSniperConfig()
				.getUndoCacheSize()) {
				this.undoList.pollLast();
			}
			this.undoList.push(undo);
		}
	}

	public void undo() {
		undo(1);
	}

	public void undo(int amount) {
		if (this.undoList.isEmpty()) {
			getPlayer().sendMessage(ChatColor.GREEN + "There's nothing to undo.");
		} else {
			int sum = 0;
			for (int x = 0; x < amount && !this.undoList.isEmpty(); x++) {
				Undo undo = this.undoList.pop();
				if (undo != null) {
					undo.undo();
					sum += undo.getSize();
				} else {
					break;
				}
			}
			getPlayer().sendMessage(ChatColor.GREEN + "Undo successful:  " + ChatColor.RED + sum + ChatColor.GREEN + " blocks have been replaced.");
		}
	}

	public void reset(String toolId) {
		SniperTool backup = this.tools.remove(toolId);
		SniperTool newTool = new SniperTool(this);
		for (Map.Entry<SnipeAction, Material> entry : backup.getActionTools()
			.entrySet()) {
			newTool.assignAction(entry.getKey(), entry.getValue());
		}
		this.tools.put(toolId, newTool);
	}

	public SnipeData getSnipeData(String toolId) {
		return this.tools.containsKey(toolId) ? this.tools.get(toolId)
			.getSnipeData() : null;
	}

	public void displayInfo() {
		String currentToolId = getCurrentToolId();
		SniperTool sniperTool = this.tools.get(currentToolId);
		Brush brush = sniperTool.getCurrentBrush();
		getPlayer().sendMessage("Current Tool: " + ((currentToolId != null) ? currentToolId : "Default Tool"));
		if (brush == null) {
			getPlayer().sendMessage("No brush selected.");
			return;
		}
		brush.info(sniperTool.getMessageHelper());
		if (brush instanceof BrushPerformer) {
			((BrushPerformer) brush).showInfo(sniperTool.getMessageHelper());
		}
	}

	public SniperTool getSniperTool(String toolId) {
		return this.tools.get(toolId);
	}

	public static class SniperTool {

		private BiMap<SnipeAction, Material> actionTools = HashBiMap.create();
		private ClassToInstanceMap<Brush> brushes = MutableClassToInstanceMap.create();
		private Class<? extends Brush> currentBrush;
		private Class<? extends Brush> previousBrush;
		private SnipeData snipeData;
		private Message messageHelper;

		private SniperTool(Sniper owner) {
			this(SnipeBrush.class, new SnipeData(owner));
		}

		private SniperTool(Class<? extends Brush> currentBrush, SnipeData snipeData) {
			this.snipeData = snipeData;
			this.messageHelper = new Message(snipeData);
			snipeData.setVoxelMessage(this.messageHelper);
			Brush newBrushInstance = instanciateBrush(currentBrush);
			if (snipeData.owner()
				.getPlayer()
				.hasPermission(newBrushInstance.getPermissionNode())) {
				this.brushes.put(currentBrush, newBrushInstance);
				this.currentBrush = currentBrush;
			}
		}

		public boolean hasToolAssigned(Material material) {
			return this.actionTools.containsValue(material);
		}

		public SnipeAction getActionAssigned(Material itemInHand) {
			return this.actionTools.inverse()
				.get(itemInHand);
		}

		public Material getToolAssigned(SnipeAction action) {
			return this.actionTools.get(action);
		}

		public void assignAction(SnipeAction action, Material itemInHand) {
			this.actionTools.forcePut(action, itemInHand);
		}

		public void unassignAction(Material itemInHand) {
			this.actionTools.inverse()
				.remove(itemInHand);
		}

		public BiMap<SnipeAction, Material> getActionTools() {
			return ImmutableBiMap.copyOf(this.actionTools);
		}

		public SnipeData getSnipeData() {
			return this.snipeData;
		}

		public Message getMessageHelper() {
			return this.messageHelper;
		}

		public Brush getCurrentBrush() {
			if (this.currentBrush == null) {
				return null;
			}
			return this.brushes.getInstance(this.currentBrush);
		}

		public Brush setCurrentBrush(Class<? extends Brush> brush) {
			Preconditions.checkNotNull(brush, "Can't set brush to null.");
			Brush brushInstance = this.brushes.get(brush);
			if (brushInstance == null) {
				brushInstance = instanciateBrush(brush);
				Preconditions.checkNotNull(brushInstance, "Could not instanciate brush class.");
				if (this.snipeData.owner()
					.getPlayer()
					.hasPermission(brushInstance.getPermissionNode())) {
					this.brushes.put(brush, brushInstance);
					this.previousBrush = this.currentBrush;
					this.currentBrush = brush;
					return brushInstance;
				}
			}
			if (this.snipeData.owner()
				.getPlayer()
				.hasPermission(brushInstance.getPermissionNode())) {
				this.previousBrush = this.currentBrush;
				this.currentBrush = brush;
				return brushInstance;
			}
			return null;
		}

		@Nullable
		public Brush previousBrush() {
			if (this.previousBrush == null) {
				return null;
			}
			return setCurrentBrush(this.previousBrush);
		}

		@Nullable
		private Brush instanciateBrush(Class<? extends Brush> brush) {
			try {
				Constructor<? extends Brush> constructor = brush.getConstructor();
				return constructor.newInstance();
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
				return null;
			}
		}
	}
}
