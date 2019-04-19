package com.thevoxelbox.voxelsniper;

import java.util.HashMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import com.thevoxelbox.voxelsniper.command.VoxelBrushCommand;
import com.thevoxelbox.voxelsniper.command.VoxelBrushToolCommand;
import com.thevoxelbox.voxelsniper.command.VoxelCenterCommand;
import com.thevoxelbox.voxelsniper.command.VoxelChunkCommand;
import com.thevoxelbox.voxelsniper.command.VoxelDefaultCommand;
import com.thevoxelbox.voxelsniper.command.VoxelGoToCommand;
import com.thevoxelbox.voxelsniper.command.VoxelHeightCommand;
import com.thevoxelbox.voxelsniper.command.VoxelInkCommand;
import com.thevoxelbox.voxelsniper.command.VoxelInkReplaceCommand;
import com.thevoxelbox.voxelsniper.command.VoxelListCommand;
import com.thevoxelbox.voxelsniper.command.VoxelPaintCommand;
import com.thevoxelbox.voxelsniper.command.VoxelPerformerCommand;
import com.thevoxelbox.voxelsniper.command.VoxelReplaceCommand;
import com.thevoxelbox.voxelsniper.command.VoxelSniperCommand;
import com.thevoxelbox.voxelsniper.command.VoxelUndoCommand;
import com.thevoxelbox.voxelsniper.command.VoxelUndoUserCommand;
import com.thevoxelbox.voxelsniper.command.VoxelVoxelCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Voxel
 */
public class VoxelSniperListener implements Listener {

	private static final String SNIPER_PERMISSION = "voxelsniper.sniper";
	private final VoxelSniper plugin;
	private Map<String, VoxelCommand> commands = new HashMap<>();

	/**
	 *
	 */
	public VoxelSniperListener(VoxelSniper plugin) {
		this.plugin = plugin;
		MetricsManager.setSnipeCounterInitTimeStamp(System.currentTimeMillis());
		addCommand(new VoxelBrushCommand(plugin));
		addCommand(new VoxelBrushToolCommand(plugin));
		addCommand(new VoxelCenterCommand(plugin));
		addCommand(new VoxelChunkCommand(plugin));
		addCommand(new VoxelDefaultCommand(plugin));
		addCommand(new VoxelGoToCommand(plugin));
		addCommand(new VoxelHeightCommand(plugin));
		addCommand(new VoxelInkCommand(plugin));
		addCommand(new VoxelInkReplaceCommand(plugin));
		addCommand(new VoxelListCommand(plugin));
		addCommand(new VoxelPaintCommand(plugin));
		addCommand(new VoxelPerformerCommand(plugin));
		addCommand(new VoxelReplaceCommand(plugin));
		addCommand(new VoxelSniperCommand(plugin));
		addCommand(new VoxelUndoCommand(plugin));
		addCommand(new VoxelUndoUserCommand(plugin));
		addCommand(new VoxelVoxelCommand(plugin));
	}

	private void addCommand(VoxelCommand command) {
		this.commands.put(command.getIdentifier()
			.toLowerCase(), command);
	}

	/**
	 * @return boolean Success.
	 */
	public boolean onCommand(Player player, String[] split, String command) {
		VoxelCommand found = this.commands.get(command.toLowerCase());
		if (found == null) {
			return false;
		}
		if (!hasPermission(found, player)) {
			player.sendMessage(ChatColor.RED + "Insufficient Permissions.");
			return true;
		}
		return found.onCommand(player, split);
	}

	private boolean hasPermission(VoxelCommand command, Player player) {
		if (command == null || player == null) {
			// Just a usual check for nulls
			return false;
		} else if (command.getPermission() == null || command.getPermission()
			.isEmpty()) {
			// This is for commands that do not require a permission node to be executed
			return true;
		} else {
			// Should utilize Vault for permission checks if available
			return player.hasPermission(command.getPermission());
		}
	}

	/**
	 *
	 */
	@EventHandler
	public final void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission(SNIPER_PERMISSION)) {
			return;
		}
		try {
			Sniper sniper = this.plugin.getSniperManager()
				.getSniperForPlayer(player);
			if (sniper.isEnabled() && sniper.snipe(event.getAction(), event.getMaterial(), event.getClickedBlock(), event.getBlockFace())) {
				MetricsManager.increaseSnipeCounter();
				event.setCancelled(true);
			}
		} catch (RuntimeException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 *
	 */
	@EventHandler
	public final void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Sniper sniper = this.plugin.getSniperManager()
			.getSniperForPlayer(player);
		if (player.hasPermission(SNIPER_PERMISSION) && this.plugin.getVoxelSniperConfiguration()
			.isMessageOnLoginEnabled()) {
			sniper.displayInfo();
		}
	}
}
