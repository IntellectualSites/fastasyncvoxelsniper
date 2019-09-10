package com.thevoxelbox.voxelsniper.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

public class CommandRegistry {

	private Plugin plugin;

	public CommandRegistry(Plugin plugin) {
		this.plugin = plugin;
	}

	public void register(CommandProperties properties, CommandExecutor executor) {
		Command command = new Command(properties, executor);
		register(command);
	}

	public void register(Command command) {
		Server server = this.plugin.getServer();
		CommandMap commandMap = getCommandMap(server);
		commandMap.register("voxel_sniper", command);
	}

	@SuppressWarnings("JavaReflectionMemberAccess")
	private CommandMap getCommandMap(Server server) {
		try {
			Method method = Server.class.getDeclaredMethod("getCommandMap");
			return (CommandMap) method.invoke(server);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
			throw new RuntimeException(exception);
		}
	}
}
