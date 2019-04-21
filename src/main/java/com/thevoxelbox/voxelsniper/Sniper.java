package com.thevoxelbox.voxelsniper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.perform.BrushPerformer;
import com.thevoxelbox.voxelsniper.brush.perform.PerformBrush;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

public class Sniper {

	private VoxelSniperPlugin plugin;
	private UUID player;
	private boolean enabled = true;
	private LinkedList<Undo> undoList = new LinkedList<>();
	private Map<String, SniperTool> tools = new HashMap<>();

	public Sniper(VoxelSniperPlugin plugin, Player player) {
		this.plugin = plugin;
		this.player = player.getUniqueId();
		SniperTool sniperTool = new SniperTool(this);
		sniperTool.assignAction(Material.ARROW, SnipeAction.ARROW);
		sniperTool.assignAction(Material.GUNPOWDER, SnipeAction.GUNPOWDER);
		this.tools.put(null, sniperTool);
	}

	@Nullable
	public String getCurrentToolId() {
		Player player = getPlayer();
		if (player == null) {
			return null;
		}
		PlayerInventory inventory = player.getInventory();
		ItemStack itemInHand = inventory.getItemInMainHand();
		Material type = itemInHand.getType();
		return getToolId(type);
	}

	@Nullable
	public String getToolId(Material material) {
		return this.tools.entrySet()
			.stream()
			.filter(entry -> {
				SniperTool tool = entry.getValue();
				return tool.hasToolAssigned(material);
			})
			.findFirst()
			.map(Entry::getKey)
			.orElse(null);
	}

	public void sendMessages(String... messages) {
		Player player = getPlayer();
		if (player == null) {
			return;
		}
		for (String message : messages) {
			player.sendMessage(message);
		}
	}

	public void sendMessage(String message) {
		Player player = getPlayer();
		if (player == null) {
			return;
		}
		player.sendMessage(message);
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
	public boolean snipe(Action action, Material itemInHand, @Nullable Block clickedBlock, BlockFace clickedFace) {
		String toolId = getToolId(itemInHand);
		if (toolId == null) {
			return false;
		}
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
			Player player = getPlayer();
			if (player == null) {
				return false;
			}
			if (sniperTool.getCurrentBrush() == null) {
				player.sendMessage("No Brush selected.");
				return true;
			}
			if (!player.hasPermission(sniperTool.getCurrentBrush()
				.getPermissionNode())) {
				player.sendMessage("You are not allowed to use this brush. You're missing the permission node '" + sniperTool.getCurrentBrush()
					.getPermissionNode() + "'");
				return true;
			}
			SnipeData snipeData = sniperTool.getSnipeData();
			if (player.isSneaking()) {
				Block targetBlock;
				SnipeAction snipeAction = sniperTool.getActionAssigned(itemInHand);
				Message message = snipeData.getMessage();
				if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
					if (clickedBlock != null) {
						targetBlock = clickedBlock;
					} else {
						targetBlock = snipeData.isRanged() ? player.getTargetBlock(snipeData.getRange()) : player.getTargetBlock(250);
					}
					if (snipeAction == SnipeAction.ARROW) {
						if (targetBlock != null) {
							Material type = targetBlock.getType();
							snipeData.setBlockDataType(type);
						} else {
							snipeData.resetBlockData();
						}
						message.blockDataType();
						return true;
					} else if (snipeAction == SnipeAction.GUNPOWDER) {
						if (targetBlock != null) {
							BlockData blockData = targetBlock.getBlockData();
							snipeData.setBlockData(blockData);
						} else {
							snipeData.resetBlockData();
						}
						message.blockData();
						return true;
					}
				} else {
					if (clickedBlock != null) {
						targetBlock = clickedBlock;
					} else {
						targetBlock = snipeData.isRanged() ? player.getTargetBlock(snipeData.getRange()) : player.getTargetBlock(250);
					}
					if (snipeAction == SnipeAction.ARROW) {
						if (targetBlock != null) {
							Material type = targetBlock.getType();
							snipeData.setReplaceBlockDataType(type);
						} else {
							snipeData.resetReplaceBlockData();
						}
						message.replaceBlockDataType();
						return true;
					} else if (snipeAction == SnipeAction.GUNPOWDER) {
						if (targetBlock != null) {
							BlockData blockData = targetBlock.getBlockData();
							snipeData.setReplaceBlockData(blockData);
						} else {
							snipeData.resetReplaceBlockData();
						}
						message.replaceBlockData();
						return true;
					}
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
					if (lastBlock.isEmpty()) {
						player.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
						return true;
					}
				} else {
					targetBlock = snipeData.isRanged() ? player.getTargetBlock(snipeData.getRange()) : player.getTargetBlock(250);
					lastBlock = player.getTargetBlock(250);
					if (targetBlock == null || targetBlock.isEmpty() || lastBlock == null || lastBlock.isEmpty()) {
						player.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
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

	@Nullable
	public Brush setBrush(String toolId, Class<? extends Brush> brush) {
		if (!this.tools.containsKey(toolId)) {
			return null;
		}
		return this.tools.get(toolId)
			.setCurrentBrush(brush);
	}

	@Nullable
	public Brush getBrush(String toolId) {
		if (!this.tools.containsKey(toolId)) {
			return null;
		}
		return this.tools.get(toolId)
			.getCurrentBrush();
	}

	@Nullable
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
			.assignAction(itemInHand, action);
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
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		if (config.getUndoCacheSize() <= 0) {
			return;
		}
		if (undo != null && undo.getSize() > 0) {
			while (this.undoList.size() >= config.getUndoCacheSize()) {
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
			sendMessage(ChatColor.GREEN + "There's nothing to undo.");
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
			sendMessage(ChatColor.GREEN + "Undo successful:  " + ChatColor.RED + sum + ChatColor.GREEN + " blocks have been replaced.");
		}
	}

	public void reset(String toolId) {
		SniperTool backup = this.tools.remove(toolId);
		SniperTool newTool = new SniperTool(this);
		for (Map.Entry<Material, SnipeAction> entry : backup.getActionTools()
			.entrySet()) {
			newTool.assignAction(entry.getKey(), entry.getValue());
		}
		this.tools.put(toolId, newTool);
	}

	@Nullable
	public SnipeData getSnipeData(String toolId) {
		return this.tools.containsKey(toolId) ? this.tools.get(toolId)
			.getSnipeData() : null;
	}

	public void displayInfo() {
		String currentToolId = getCurrentToolId();
		SniperTool sniperTool = this.tools.get(currentToolId);
		Brush brush = sniperTool.getCurrentBrush();
		sendMessage("Current Tool: " + ((currentToolId != null) ? currentToolId : "Default Tool"));
		if (brush == null) {
			sendMessage("No brush selected.");
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

	public static final class SniperTool {

		private Map<Material, SnipeAction> actionTools = new HashMap<>();
		private Map<Class<? extends Brush>, Brush> brushes = new HashMap<>();
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
			snipeData.setMessage(this.messageHelper);
			Brush newBrushInstance = createBrushInstance(currentBrush);
			Sniper owner = snipeData.getOwner();
			Player player = owner.getPlayer();
			if (player != null && player.hasPermission(newBrushInstance.getPermissionNode())) {
				this.brushes.put(currentBrush, newBrushInstance);
				this.currentBrush = currentBrush;
			}
		}

		public boolean hasToolAssigned(Material material) {
			return this.actionTools.containsKey(material);
		}

		public SnipeAction getActionAssigned(Material itemInHand) {
			return this.actionTools.get(itemInHand);
		}

		public void assignAction(Material itemInHand, SnipeAction action) {
			this.actionTools.put(itemInHand, action);
		}

		public void unassignAction(Material itemInHand) {
			this.actionTools.remove(itemInHand);
		}

		public Map<Material, SnipeAction> getActionTools() {
			return Map.copyOf(this.actionTools);
		}

		public SnipeData getSnipeData() {
			return this.snipeData;
		}

		public Message getMessageHelper() {
			return this.messageHelper;
		}

		@Nullable
		public Brush getCurrentBrush() {
			if (this.currentBrush == null) {
				return null;
			}
			return this.brushes.get(this.currentBrush);
		}

		@Nullable
		public Brush setCurrentBrush(Class<? extends Brush> brush) {
			Brush brushInstance = this.brushes.get(brush);
			Sniper owner = this.snipeData.getOwner();
			Player player = owner.getPlayer();
			if (brushInstance == null) {
				brushInstance = createBrushInstance(brush);
				if (player != null && player.hasPermission(brushInstance.getPermissionNode())) {
					this.brushes.put(brush, brushInstance);
					this.previousBrush = this.currentBrush;
					this.currentBrush = brush;
					return brushInstance;
				}
			}
			if (player != null && player.hasPermission(brushInstance.getPermissionNode())) {
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

		private Brush createBrushInstance(Class<? extends Brush> brush) {
			try {
				Constructor<? extends Brush> constructor = brush.getConstructor();
				return constructor.newInstance();
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
				throw new RuntimeException(exception);
			}
		}
	}
}
