package com.thevoxelbox.voxelsniper.sniper;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeAction;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
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

	private UUID uuid;
	private boolean enabled = true;
	private int undoCacheSize;
	private Deque<Undo> undoList = new LinkedList<>();
	private Map<String, SniperToolkit> toolkits = new HashMap<>();

	public Sniper(UUID uuid, int undoCacheSize) {
		this.uuid = uuid;
		this.undoCacheSize = undoCacheSize;
		this.toolkits.put("default", createDefaultSniperTool(this));
	}

	private static SniperToolkit createDefaultSniperTool(Sniper sniper) {
		SniperToolkit toolkit = new SniperToolkit("default", sniper);
		toolkit.assignAction(Material.ARROW, SnipeAction.ARROW);
		toolkit.assignAction(Material.GUNPOWDER, SnipeAction.GUNPOWDER);
		return toolkit;
	}

	@Deprecated
	public Sniper(Player player, int undoCacheSize) {
		this.uuid = player.getUniqueId();
		this.undoCacheSize = undoCacheSize;
		SniperToolkit sniperToolkit = new SniperToolkit(this);
		sniperToolkit.assignAction(Material.ARROW, SnipeAction.ARROW);
		sniperToolkit.assignAction(Material.GUNPOWDER, SnipeAction.GUNPOWDER);
		this.toolkits.put("default", sniperToolkit);
	}

	public Map<String, SniperToolkit> getToolkits() {
		return Map.copyOf(this.toolkits);
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
		if (type.isEmpty()) {
			return "default";
		}
		return getToolkitName(type);
	}

	@Nullable
	public String getToolkitName(Material material) {
		return this.toolkits.values()
			.stream()
			.filter(toolkit -> toolkit.hasItemAssigned(material))
			.findFirst()
			.map(SniperToolkit::getToolkitName)
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
		return Bukkit.getPlayer(this.uuid);
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
		String toolId = getToolkitName(itemInHand);
		if (toolId == null) {
			return false;
		}
		SniperToolkit sniperToolkit = this.toolkits.get(toolId);
		switch (action) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				break;
			default:
				return false;
		}
		if (sniperToolkit.hasItemAssigned(itemInHand)) {
			Player player = getPlayer();
			if (player == null) {
				return false;
			}
			Brush currentBrush = sniperToolkit.getCurrentBrush();
			if (currentBrush == null) {
				player.sendMessage("No Brush selected.");
				return true;
			}
			if (!player.hasPermission(currentBrush.getPermissionNode())) {
				player.sendMessage("You are not allowed to use this brush. You're missing the permission node '" + currentBrush.getPermissionNode() + "'");
				return true;
			}
			SnipeData snipeData = sniperToolkit.getSnipeData();
			if (player.isSneaking()) {
				Block targetBlock;
				SnipeAction snipeAction = sniperToolkit.getAssignedAction(itemInHand);
				Messages messages = snipeData.getMessages();
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
						messages.blockDataType();
						return true;
					} else if (snipeAction == SnipeAction.GUNPOWDER) {
						if (targetBlock != null) {
							BlockData blockData = targetBlock.getBlockData();
							snipeData.setBlockData(blockData);
						} else {
							snipeData.resetBlockData();
						}
						messages.blockData();
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
						messages.replaceBlockDataType();
						return true;
					} else if (snipeAction == SnipeAction.GUNPOWDER) {
						if (targetBlock != null) {
							BlockData blockData = targetBlock.getBlockData();
							snipeData.setReplaceBlockData(blockData);
						} else {
							snipeData.resetReplaceBlockData();
						}
						messages.replaceBlockData();
						return true;
					}
				}
			} else {
				SnipeAction snipeAction = sniperToolkit.getAssignedAction(itemInHand);
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
				if (currentBrush instanceof PerformerBrush) {
					PerformerBrush performerBrush = (PerformerBrush) currentBrush;
					performerBrush.initPerformer(snipeData);
				}
				return currentBrush.perform(snipeAction, snipeData, targetBlock, lastBlock);
			}
		}
		return false;
	}

	@Nullable
	public Brush setBrush(String toolId, Class<? extends Brush> brush) {
		if (!this.toolkits.containsKey(toolId)) {
			return null;
		}
		return this.toolkits.get(toolId)
			.setCurrentBrush(brush);
	}

	@Nullable
	public Brush getBrush(String toolId) {
		if (!this.toolkits.containsKey(toolId)) {
			return null;
		}
		return this.toolkits.get(toolId)
			.getCurrentBrush();
	}

	@Nullable
	public Brush previousBrush(String toolId) {
		if (!this.toolkits.containsKey(toolId)) {
			return null;
		}
		return this.toolkits.get(toolId)
			.previousBrush();
	}

	public boolean setTool(String toolId, SnipeAction action, Material itemInHand) {
		for (Map.Entry<String, SniperToolkit> entry : this.toolkits.entrySet()) {
			if (!entry.getKey()
				.equals(toolId) && entry.getValue()
				.hasItemAssigned(itemInHand)) {
				return false;
			}
		}
		if (!this.toolkits.containsKey(toolId)) {
			SniperToolkit tool = new SniperToolkit(this);
			this.toolkits.put(toolId, tool);
		}
		this.toolkits.get(toolId)
			.assignAction(itemInHand, action);
		return true;
	}

	public void removeTool(String toolId, Material itemInHand) {
		if (!this.toolkits.containsKey(toolId)) {
			SniperToolkit tool = new SniperToolkit(this);
			this.toolkits.put(toolId, tool);
		}
		this.toolkits.get(toolId)
			.unassignAction(itemInHand);
	}

	public void removeTool(String toolId) {
		if (toolId == null) {
			return;
		}
		this.toolkits.remove(toolId);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void storeUndo(@Nullable Undo undo) {
		if (this.undoCacheSize <= 0) {
			return;
		}
		if (undo != null && undo.getSize() > 0) {
			while (this.undoList.size() >= this.undoCacheSize) {
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
		SniperToolkit backup = this.toolkits.remove(toolId);
		SniperToolkit newTool = new SniperToolkit(this);
		for (Map.Entry<Material, SnipeAction> entry : backup.getActionTools()
			.entrySet()) {
			newTool.assignAction(entry.getKey(), entry.getValue());
		}
		this.toolkits.put(toolId, newTool);
	}

	@Nullable
	public SnipeData getSnipeData(String toolId) {
		return this.toolkits.containsKey(toolId) ? this.toolkits.get(toolId)
			.getSnipeData() : null;
	}

	public void displayInfo() {
		String currentToolId = getCurrentToolId();
		SniperToolkit sniperToolkit = this.toolkits.get(currentToolId);
		Brush brush = sniperToolkit.getCurrentBrush();
		sendMessage("Current Tool: " + ((currentToolId != null) ? currentToolId : "Default Tool"));
		if (brush == null) {
			sendMessage("No brush selected.");
			return;
		}
		brush.info(sniperToolkit.getMessagesHelper());
		if (brush instanceof PerformerBrush) {
			PerformerBrush performer = (PerformerBrush) brush;
			performer.showInfo(sniperToolkit.getMessagesHelper());
		}
	}

	public SniperToolkit getSniperTool(String toolId) {
		return this.toolkits.get(toolId);
	}

	@Override
	public String toString() {
		return "Sniper{" + "uuid=" + this.uuid + ", enabled=" + this.enabled + ", undoCacheSize=" + this.undoCacheSize + ", undoList=" + this.undoList + ", toolkits=" + this.toolkits + "}";
	}
}
