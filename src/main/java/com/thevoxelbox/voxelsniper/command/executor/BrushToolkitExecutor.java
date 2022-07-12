package com.thevoxelbox.voxelsniper.command.executor;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.command.TabCompleter;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class BrushToolkitExecutor implements CommandExecutor, TabCompleter {

    private final VoxelSniperPlugin plugin;

    public BrushToolkitExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.registerAndGetSniper(player);
        if (sniper == null) {
            sender.sendMessage(ChatColor.RED + "Sniper not found.");
            return;
        }
        int length = arguments.length;
        if (length == 0) {
            sender.sendMessage("/btool assign <arrow|gunpowder> <toolkit name>");
            sender.sendMessage("/btool remove <toolkit name>");
            sender.sendMessage("/btool remove");
            return;
        }
        String firstArgument = arguments[0];
        if (length == 3 && firstArgument.equalsIgnoreCase("assign")) {
            ToolAction action = ToolAction.getToolAction(arguments[1]);
            if (action == null) {
                sender.sendMessage("/btool assign <arrow|gunpowder> <toolkit name>");
                return;
            }
            PlayerInventory inventory = player.getInventory();
            ItemStack itemInHand = inventory.getItemInMainHand();
            ItemType itemType = BukkitAdapter.asItemType(itemInHand.getType());
            if (itemType == ItemTypes.AIR) {
                sender.sendMessage("/btool assign <arrow|gunpowder> <toolkit name>");
                return;
            }
            String toolkitName = arguments[2];
            Toolkit toolkit = sniper.getToolkit(toolkitName);
            if (toolkit == null) {
                toolkit = new Toolkit(toolkitName);
            }
            toolkit.addToolAction(itemType, action);
            sniper.addToolkit(toolkit);
            sender.sendMessage(itemInHand
                    .getType()
                    .name() + " has been assigned to '" + toolkitName + "' as action " + action.name() + ".");
            return;
        }
        if (length == 2 && firstArgument.equalsIgnoreCase("remove")) {
            Toolkit toolkit = sniper.getToolkit(arguments[1]);
            if (toolkit == null) {
                sender.sendMessage(ChatColor.RED + "Toolkit " + arguments[1] + " not found.");
                return;
            }
            sniper.removeToolkit(toolkit);
            return;
        }
        if (length == 1 && firstArgument.equalsIgnoreCase("remove")) {
            PlayerInventory inventory = player.getInventory();
            ItemStack itemInHand = inventory.getItemInMainHand();
            ItemType itemType = BukkitAdapter.asItemType(itemInHand.getType());
            if (itemType == ItemTypes.AIR) {
                sender.sendMessage("Can't unassign empty hands.");
                return;
            }
            Toolkit toolkit = sniper.getCurrentToolkit();
            if (toolkit == null) {
                sender.sendMessage("Can't unassign default tool.");
                return;
            }
            toolkit.removeToolAction(itemType);
        }
    }


    @Override
    public List<String> complete(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.registerAndGetSniper(player);
        if (sniper == null || arguments.length == 0) {
            return Collections.emptyList();
        }

        String firstArgument = arguments[0];
        if (arguments.length == 1) {
            String argumentLowered = firstArgument.toLowerCase(Locale.ROOT);
            return Stream.of("assign", "remove")
                    .filter(subCommand -> subCommand.startsWith(argumentLowered))
                    .toList();
        }
        if (arguments.length == 2 && firstArgument.equalsIgnoreCase("assign")) {
            String argument = arguments[1];
            String argumentLowered = argument.toLowerCase(Locale.ROOT);
            return Stream.of("arrow", "gunpowder")
                    .filter(tool -> tool.startsWith(argumentLowered))
                    .toList();
        }
        if (arguments.length == 2 && firstArgument.equalsIgnoreCase("remove") ||
                arguments.length == 3 && firstArgument.equalsIgnoreCase("assign")) {
            String argument = arguments[arguments.length - 1];
            String argumentLowered = argument.toLowerCase(Locale.ROOT);
            return sniper.getToolkits().stream()
                    .map(Toolkit::getToolkitName)
                    .filter(toolkitName -> toolkitName.startsWith(argumentLowered))
                    .toList();
        }
        return Collections.emptyList();
    }

}
