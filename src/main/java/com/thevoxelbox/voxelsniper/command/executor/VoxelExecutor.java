package com.thevoxelbox.voxelsniper.command.executor;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.command.TabCompleter;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class VoxelExecutor implements CommandExecutor, TabCompleter {

    private static final List<String> BLOCKS = BlockType.REGISTRY.values().stream()
            .map(blockType -> blockType.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH))
            .toList();

    private final VoxelSniperPlugin plugin;

    public VoxelExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.registerAndGetSniper(player);
        if (sniper == null) {
            return;
        }
        Toolkit toolkit = sniper.getCurrentToolkit();
        if (toolkit == null) {
            return;
        }
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        if (toolkitProperties == null) {
            return;
        }
        Messenger messenger = new Messenger(plugin, sender);
        VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
        List<BlockType> liteSniperRestrictedMaterials = config.getLitesniperRestrictedMaterials();
        if (arguments.length == 0) {
            BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
            BlockVector3 targetBlock = blockTracer.getTargetBlock();
            if (targetBlock != null) {
                BlockType targetBlockType = BukkitAdapter.asBlockType(
                        player.getWorld().getBlockAt(
                                targetBlock.getX(),
                                targetBlock.getY(),
                                targetBlock.getZ()
                        ).getType());
                if (targetBlockType == null) {
                    return;
                }
                if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(
                        targetBlockType)) {
                    sender.sendMessage(ChatColor.RED + "You are not allowed to use " + targetBlockType.getId() + ".");
                    return;
                }
                toolkitProperties.setBlockType(targetBlockType);
                messenger.sendBlockTypeMessage(targetBlockType);
            }
            return;
        }
        BlockType blockType = BlockTypes.get(arguments[0].toLowerCase(Locale.ROOT));
        if (blockType != null) {
            if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(blockType)) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to use " + blockType.getId() + ".");
                return;
            }
            toolkitProperties.setBlockType(blockType);
            messenger.sendBlockTypeMessage(blockType);
        } else {
            sender.sendMessage(ChatColor.RED + "You have entered an invalid item name: " + arguments[0]);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] arguments) {
        if (arguments.length == 1) {
            String argument = arguments[0];
            String argumentLowered = (argument.startsWith(Identifiers.MINECRAFT_IDENTIFIER)
                    ? argument.substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)
                    : argument)
                    .toLowerCase(Locale.ROOT);
            return BLOCKS.stream()
                    .filter(id -> id.startsWith(argumentLowered))
                    .toList();
        }
        return Collections.emptyList();
    }

}
