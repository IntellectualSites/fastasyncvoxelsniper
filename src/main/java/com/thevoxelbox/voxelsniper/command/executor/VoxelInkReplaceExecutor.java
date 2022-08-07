package com.thevoxelbox.voxelsniper.command.executor;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VoxelInkReplaceExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public VoxelInkReplaceExecutor(VoxelSniperPlugin plugin) {
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
        VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
        List<String> liteSniperRestrictedPatterns = config.getLitesniperRestrictedMaterials();
        BlockState blockState;
        if (arguments.length == 0) {
            BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
            BlockVector3 targetBlock = blockTracer.getTargetBlock();
            blockState = BukkitAdapter.adapt(player.getWorld().getBlockAt(
                    targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()
            ).getBlockData());
        } else {
            try {
                blockState = BlockState.get(arguments[0]);
            } catch (InputParseException ignored) {
                sniper.print(Caption.of("voxelsniper.command.cannot-parse-input"));
                return;
            }
        }

        if (blockState == null) {
            sniper.print(Caption.of("voxelsniper.command.invalid-block"));
            return;
        }
        if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedPatterns.contains(
                blockState.getBlockType().getResource())) {
            sniper.print(Caption.of("voxelsniper.command.not-allowed", blockState.getAsString()));
            return;
        }

        toolkitProperties.setReplacePattern(new BrushPattern(blockState));
        Messenger messenger = new Messenger(plugin, sender);
        messenger.sendReplacePatternMessage(toolkitProperties.getReplacePattern());
    }

}
