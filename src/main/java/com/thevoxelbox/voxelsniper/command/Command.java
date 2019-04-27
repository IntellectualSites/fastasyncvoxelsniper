package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class Command extends org.bukkit.command.Command {

	private CommandProperties properties;
	private CommandExecutor executor;

	public Command(CommandProperties properties, CommandExecutor executor) {
		super(properties.getName(), properties.getDescriptionOrDefault(), properties.getUsage(), properties.getAliases());
		setupPermission(properties);
		this.properties = properties;
		this.executor = executor;
	}

	private void setupPermission(CommandProperties properties) {
		String permission = properties.getPermission();
		setPermission(permission);
		setPermissionMessage(ChatColor.RED + "Insufficient permissions.");
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		Class<? extends CommandSender> senderType = this.properties.getSenderTypeOrDefault();
		if (!senderType.isInstance(sender)) {
			sender.sendMessage(ChatColor.RED + "Only " + senderType.getSimpleName() + " can execute this command.");
			return true;
		}
		String permission = this.properties.getPermission();
		if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
			return true;
		}
		this.executor.executeCommand(sender, args);
		return true;
	}
}
