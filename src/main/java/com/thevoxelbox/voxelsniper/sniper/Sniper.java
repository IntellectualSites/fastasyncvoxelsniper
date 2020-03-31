package com.thevoxelbox.voxelsniper.sniper;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.beta.implementation.queue.QueueHandler;
import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.command.HistoryCommands;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.session.request.Request;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sniper {

	private static final String DEFAULT_TOOLKIT_NAME = "default";

	private UUID uuid;
	private boolean enabled = true;
	private int undoCacheSize;
//	private Deque<Undo> undoList = new LinkedList<>(); //FAWE Removed
	private List<Toolkit> toolkits = new ArrayList<>();

	public Sniper(UUID uuid, int undoCacheSize) {
		this.uuid = uuid;
		this.undoCacheSize = undoCacheSize;
		Toolkit defaultToolkit = createDefaultToolkit();
		this.toolkits.add(defaultToolkit);
	}

	private Toolkit createDefaultToolkit() {
		Toolkit toolkit = new Toolkit("default");
		toolkit.addToolAction(Material.ARROW, ToolAction.ARROW);
		toolkit.addToolAction(Material.GUNPOWDER, ToolAction.GUNPOWDER);
		return toolkit;
	}

	public Player getPlayer() {
		Player player = Bukkit.getPlayer(this.uuid);
		if (player == null) {
			throw new UnknownSniperPlayerException();
		}
		return player;
	}

	@Nullable
	public Toolkit getCurrentToolkit() {
		Player player = getPlayer();
		PlayerInventory inventory = player.getInventory();
		ItemStack itemInHand = inventory.getItemInMainHand();
		Material itemType = itemInHand.getType();
		if (Materials.isEmpty(itemType)) {
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
			.filter(toolkit -> toolkit.hasToolAction(itemType))
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
	 * @param usedItem Item in hand of player
	 * @param clickedBlock Block that the player targeted/interacted with
	 * @param clickedBlockFace Face of that targeted Block
	 * @return true if command visibly processed, false otherwise.
	 */
	//FAWE MODIFIED
	public boolean snipe(Player player, Action action, Material usedItem, @Nullable Block clickedBlock, BlockFace clickedBlockFace) {
		{ //FAWE ADDED
			switch (action) {
				case LEFT_CLICK_AIR:
				case LEFT_CLICK_BLOCK:
				case RIGHT_CLICK_AIR:
				case RIGHT_CLICK_BLOCK:
					break;
				default:
					return false;
			}
			if (toolkits.isEmpty()) {
				return false;
			}
		}
		Toolkit toolkit = getToolkit(usedItem);
		if (toolkit == null) {
			return false;
		}
		ToolAction toolAction = toolkit.getToolAction(usedItem);
		if (toolAction == null) {
			return false;
		}
		BrushProperties currentBrushProperties = toolkit.getCurrentBrushProperties();
		String permission = currentBrushProperties.getPermission();
		if (permission != null && !player.hasPermission(permission)) {
			player.sendMessage("You are not allowed to use this brush. You're missing the permission node '" + permission + "'");
			return false;
		}
		{ //FAWE ADDED
			BukkitPlayer wePlayer = BukkitAdapter.adapt(player);
			LocalSession session = wePlayer.getSession();
			QueueHandler queue = Fawe.get().getQueueHandler();
			queue.async(() -> {
				synchronized (session) {
					if (!player.isValid()) return;
					snipeOnCurrentThread(wePlayer, player, action, usedItem, clickedBlock, clickedBlockFace, toolkit, toolAction, currentBrushProperties);
				}
			});
		}
		return true;
	}

	private AsyncWorld tmpWorld;

	public AsyncWorld getWorld() {
		return tmpWorld;
	}

	public synchronized boolean snipeOnCurrentThread(com.sk89q.worldedit.entity.Player fp, Player player, Action action, Material usedItem, @Nullable Block clickedBlock, BlockFace clickedBlockFace, Toolkit toolkit, ToolAction toolAction, BrushProperties currentBrushProperties) {
		LocalSession session = fp.getSession(); //FAWE add
		synchronized (session) {//FAWE add
		EditSession editSession = session.createEditSession(fp); //FAWE add
		World world = BukkitAdapter.adapt(editSession.getWorld()); //FAWE add
		AsyncWorld asyncWorld = new AsyncWorld(world, editSession); //FAWE add
		this.tmpWorld = asyncWorld;

		if (clickedBlock != null) {
			clickedBlock = asyncWorld.getBlockAt(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());
		}
		try {//FAWE ADD
		ToolkitProperties toolkitProperties = toolkit.getProperties();
		BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
		{//FAWE add
			Request.reset();
			Request.request().setExtent(editSession);
			if (clickedBlock == null) {
				@NotNull Location loc = player.getLocation();
				int distance = toolkitProperties.getBlockTracerRange() == null ? Math.max(Bukkit.getViewDistance(), 3) * 16 - toolkitProperties.getBrushSize() : toolkitProperties.getBlockTracerRange();
				BlockIterator iterator = new BlockIterator(asyncWorld, loc.toVector(), loc.getDirection(), player.getEyeHeight(), distance);
				outer:
				while (iterator.hasNext()) {
					clickedBlock = iterator.next();
					@NotNull Material type = clickedBlock.getType();
					switch (type) {
						case AIR:
						case CAVE_AIR:
						case VOID_AIR:
							break;
						default:
							break outer;
					}
				}
			}
		}
		Block targetBlock = clickedBlock == null ? blockTracer.getTargetBlock() : clickedBlock;
		if (player.isSneaking()) {
			SnipeMessenger messenger = new SnipeMessenger(toolkitProperties, currentBrushProperties, player);
			if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
				if (toolAction == ToolAction.ARROW) {
					if (Materials.isEmpty(targetBlock.getType())) {
						toolkitProperties.resetBlockData();
					} else {
						Material type = targetBlock.getType();
						toolkitProperties.setBlockType(type);
					}
					messenger.sendBlockTypeMessage();
					return true;
				} else if (toolAction == ToolAction.GUNPOWDER) {
					if (Materials.isEmpty(targetBlock.getType())) {
						toolkitProperties.resetBlockData();
					} else {
						BlockData blockData = targetBlock.getBlockData();
						toolkitProperties.setBlockData(blockData);
					}
					messenger.sendBlockDataMessage();
					return true;
				}
				return false;
			} else if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				if (toolAction == ToolAction.ARROW) {
					if (targetBlock == null) {
						toolkitProperties.resetReplaceBlockData();
					} else {
						Material type = targetBlock.getType();
						toolkitProperties.setReplaceBlockType(type);
					}
					messenger.sendReplaceBlockTypeMessage();
					return true;
				} else if (toolAction == ToolAction.GUNPOWDER) {
					if (targetBlock == null) {
						toolkitProperties.resetReplaceBlockData();
					} else {
						BlockData blockData = targetBlock.getBlockData();
						toolkitProperties.setReplaceBlockData(blockData);
					}
					messenger.sendReplaceBlockDataMessage();
					return true;
				}
				return false;
			}
			return false;
		} else {
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				if (Materials.isEmpty(targetBlock.getType())) {
					player.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
					return true;
				}
				Brush currentBrush = toolkit.getCurrentBrush();
				if (currentBrush == null) {
					return false;
				}
				Snipe snipe = new Snipe(this, toolkit, toolkitProperties, currentBrushProperties, currentBrush);
				if (currentBrush instanceof PerformerBrush) {
					PerformerBrush performerBrush = (PerformerBrush) currentBrush;
					performerBrush.initialize(snipe);
				}
				Block lastBlock = clickedBlock == null ? blockTracer.getLastBlock() : clickedBlock.getRelative(clickedBlockFace);
				currentBrush.perform(snipe, toolAction, targetBlock, lastBlock);
				return true;
			}
		}
		return false;
		}
		finally { //FAWE ADD
			tmpWorld = null;
			session.remember(editSession);
			editSession.flushQueue();
			WorldEdit.getInstance().flushBlockBag(fp, editSession);
		}
		}
	}

	//FAWE Modified
	public void storeUndo(Undo undo) {
		/* //FAWE removed
		if (this.undoCacheSize <= 0) {
			return;
		}
		if (undo.isEmpty()) {
			return;
		}
		while (this.undoList.size() >= this.undoCacheSize) {
			this.undoList.pollLast();
		}
		this.undoList.push(undo);
		*/
	}

	//FAWE  Modified
	public void undo(CommandSender sender, int amount) {
		{ //FAWE add
			com.sk89q.worldedit.entity.Player actor = (com.sk89q.worldedit.entity.Player) WorldEditPlugin.getInstance().wrapCommandSender(sender);
			LocalSession session = actor.getSession();
			new HistoryCommands(WorldEdit.getInstance()).undo(actor, session, amount, null);
		}
		/* //FAWE modified
		if (this.undoList.isEmpty()) {
			sender.sendMessage(ChatColor.GREEN + "There's nothing to undo.");
			return;
		}
		int sum = 0;
		for (int index = 0; index < amount && !this.undoList.isEmpty(); index++) {
			Undo undo = this.undoList.pop();
			undo.undo();
			sum += undo.getSize();
		}
		sender.sendMessage(ChatColor.GREEN + "Undo successful:  " + ChatColor.RED + sum + ChatColor.GREEN + " blocks have been replaced.");
		*/
	}

	public void sendInfo(CommandSender sender) {
		Toolkit toolkit = getCurrentToolkit();
		if (toolkit == null) {
			sender.sendMessage("Current toolkit: none");
			return;
		}
		sender.sendMessage("Current toolkit: " + toolkit.getToolkitName());
		BrushProperties brushProperties = toolkit.getCurrentBrushProperties();
		Brush brush = toolkit.getCurrentBrush();
		if (brush == null) {
			sender.sendMessage("No brush selected.");
			return;
		}
		ToolkitProperties toolkitProperties = toolkit.getProperties();
		Snipe snipe = new Snipe(this, toolkit, toolkitProperties, brushProperties, brush);
		brush.sendInfo(snipe);
		if (brush instanceof PerformerBrush) {
			PerformerBrush performer = (PerformerBrush) brush;
			performer.sendPerformerInfo(snipe);
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
