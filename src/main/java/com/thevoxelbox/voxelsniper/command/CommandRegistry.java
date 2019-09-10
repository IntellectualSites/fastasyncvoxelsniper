package com.thevoxelbox.voxelsniper.command;

import java.lang.reflect.Method;
import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import net.mcparkour.common.reflection.Reflections;
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
		Method method = Reflections.getMethod(Server.class, "getCommandMap");
		CommandMap commandMap = (CommandMap) Reflections.invokeMethod(method, server);
		commandMap.register("voxel_sniper", command);
	}
}
