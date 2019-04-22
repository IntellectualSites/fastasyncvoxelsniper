package com.thevoxelbox.voxelsniper.sniper;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
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

	private static final String DEFAULT_TOOLKIT_NAME = "default";

	private UUID uuid;
	private boolean enabled = true;
	private int undoCacheSize;
	private Deque<Undo> undoList = new LinkedList<>();
	private List<Toolkit> toolkits = new ArrayList<>();

	public Sniper(UUID uuid, int undoCacheSize) {
		this.uuid = uuid;
		this.undoCacheSize = undoCacheSize;
		Toolkit defaultToolkit = createDefaultToolkit();
		ToolkitProperties properties = defaultToolkit.getProperties();
		properties.setOwner(this);
		this.toolkits.add(defaultToolkit);
	}

	private Toolkit createDefaultToolkit() {
		Toolkit toolkit = new Toolkit("default");
		toolkit.addAction(Material.ARROW, ToolAction.ARROW);
		toolkit.addAction(Material.GUNPOWDER, ToolAction.GUNPOWDER);
		return toolkit;
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

	@Nullable
	public Toolkit getCurrentToolkit() {
		Player player = getPlayer();
		if (player == null) {
			return null;
		}
		PlayerInventory inventory = player.getInventory();
		ItemStack itemInHand = inventory.getItemInMainHand();
		Material itemType = itemInHand.getType();
		if (itemType.isEmpty()) {
			return getToolkit(DEFAULT_TOOLKIT_NAME);
		}
		return getToolkit(itemType);
	}

	public void addToolkit(Toolkit toolkit) {
		this.toolkits.add(toolkit);
	}

	@Nullable
	public Toolkit getToolkit(Material itemType) {
		return this.toolkits.stream()
			.filter(toolkit -> toolkit.hasAction(itemType))
			.findFirst()
			.orElse(null);
	}

	@Nullable
	public Toolkit getToolkit(String toolkitName) {
		return this.toolkits.stream()
			.filter(toolkit -> toolkitName.equals(toolkit.getToolkitName()))
			.findFirst()
			.orElse(null);
	}

	public void removeToolkit(Toolkit toolkit) {
		this.toolkits.remove(toolkit);
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
		Toolkit toolkit = getToolkit(itemInHand);
		if (toolkit == null) {
			return false;
		}
		switch (action) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				break;
			default:
				return false;
		}
		if (toolkit.hasAction(itemInHand)) {
			Player player = getPlayer();
			if (player == null) {
				return false;
			}
			Brush currentBrush = toolkit.getCurrentBrush();
			if (currentBrush == null) {
				player.sendMessage("No Brush selected.");
				return true;
			}
			if (!player.hasPermission(currentBrush.getPermissionNode())) {
				player.sendMessage("You are not allowed to use this brush. You're missing the permission node '" + currentBrush.getPermissionNode() + "'");
				return true;
			}
			ToolkitProperties toolkitProperties = toolkit.getProperties();
			if (player.isSneaking()) {
				Block targetBlock;
				ToolAction toolAction = toolkit.getAction(itemInHand);
				Messages messages = toolkitProperties.getMessages();
				if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
					if (clickedBlock != null) {
						targetBlock = clickedBlock;
					} else {
						targetBlock = toolkitProperties.isRanged() ? player.getTargetBlock(toolkitProperties.getRange()) : player.getTargetBlock(250);
					}
					if (toolAction == ToolAction.ARROW) {
						if (targetBlock != null) {
							Material type = targetBlock.getType();
							toolkitProperties.setBlockDataType(type);
						} else {
							toolkitProperties.resetBlockData();
						}
						messages.blockDataType();
						return true;
					} else if (toolAction == ToolAction.GUNPOWDER) {
						if (targetBlock != null) {
							BlockData blockData = targetBlock.getBlockData();
							toolkitProperties.setBlockData(blockData);
						} else {
							toolkitProperties.resetBlockData();
						}
						messages.blockData();
						return true;
					}
				} else {
					if (clickedBlock != null) {
						targetBlock = clickedBlock;
					} else {
						targetBlock = toolkitProperties.isRanged() ? player.getTargetBlock(toolkitProperties.getRange()) : player.getTargetBlock(250);
					}
					if (toolAction == ToolAction.ARROW) {
						if (targetBlock != null) {
							Material type = targetBlock.getType();
							toolkitProperties.setReplaceBlockDataType(type);
						} else {
							toolkitProperties.resetReplaceBlockData();
						}
						messages.replaceBlockDataType();
						return true;
					} else if (toolAction == ToolAction.GUNPOWDER) {
						if (targetBlock != null) {
							BlockData blockData = targetBlock.getBlockData();
							toolkitProperties.setReplaceBlockData(blockData);
						} else {
							toolkitProperties.resetReplaceBlockData();
						}
						messages.replaceBlockData();
						return true;
					}
				}
			} else {
				ToolAction toolAction = toolkit.getAction(itemInHand);
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
					targetBlock = toolkitProperties.isRanged() ? player.getTargetBlock(toolkitProperties.getRange()) : player.getTargetBlock(120);
					lastBlock = player.getTargetBlock(120);
					if (targetBlock == null || targetBlock.isEmpty() || lastBlock == null || lastBlock.isEmpty()) {
						player.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
						return true;
					}
				}
				if (currentBrush instanceof PerformerBrush) {
					PerformerBrush performerBrush = (PerformerBrush) currentBrush;
					performerBrush.initPerformer(toolkitProperties);
				}
				return currentBrush.perform(toolAction, toolkitProperties, targetBlock, lastBlock);
			}
		}
		return false;
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

	public void displayInfo() {
		Toolkit toolkit = getCurrentToolkit();
		if (toolkit == null) {
			sendMessage("Current toolkit: none");
			return;
		}
		sendMessage("Current toolkit: " + toolkit.getToolkitName());
		Brush brush = toolkit.getCurrentBrush();
		if (brush == null) {
			sendMessage("No brush selected.");
			return;
		}
		ToolkitProperties properties = toolkit.getProperties();
		Messages messages = properties.getMessages();
		brush.info(messages);
		if (brush instanceof PerformerBrush) {
			PerformerBrush performer = (PerformerBrush) brush;
			performer.showInfo(messages);
		}
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
