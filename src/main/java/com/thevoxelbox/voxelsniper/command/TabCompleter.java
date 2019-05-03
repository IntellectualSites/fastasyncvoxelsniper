package com.thevoxelbox.voxelsniper.command;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface TabCompleter {

	List<String> complete(CommandSender sender, String[] arguments);
}
