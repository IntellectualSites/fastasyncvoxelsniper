package com.thevoxelbox.voxelsniper.command.executor;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class VoxelListExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public VoxelListExecutor(VoxelSniperPlugin plugin) {
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
        if (arguments.length == 0) {
            BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
            BlockVector3 targetBlock = blockTracer.getTargetBlock();
            if (targetBlock == null) {
                return;
            }
            BlockState blockState = BukkitAdapter.adapt(player.getWorld().getBlockAt(
                    targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()
            ).getBlockData());
            toolkitProperties.addToVoxelList(blockState);
            List<BlockState> voxelList = toolkitProperties.getVoxelList();
            messenger.sendVoxelListMessage(voxelList);
            return;
        } else {
            if (arguments[0].equalsIgnoreCase("clear")) {
                toolkitProperties.clearVoxelList();
                List<BlockState> voxelList = toolkitProperties.getVoxelList();
                messenger.sendVoxelListMessage(voxelList);
                return;
            }
        }
        boolean remove = false;
        for (String string : arguments) {
            String materialString;
            if (!string.isEmpty() && string.charAt(0) == '-') {
                remove = true;
                materialString = string.replaceAll("-", "");
            } else {
                materialString = string;
            }
            BlockType blockType = BlockTypes.get(materialString.toLowerCase(Locale.ROOT));
            if (blockType != null) {
                BlockState blockState = blockType.getDefaultState();
                if (remove) {
                    toolkitProperties.removeFromVoxelList(blockState);
                } else {
                    toolkitProperties.addToVoxelList(blockState);
                }
                List<BlockState> voxelList = toolkitProperties.getVoxelList();
                messenger.sendVoxelListMessage(voxelList);
            }
        }
    }

}
