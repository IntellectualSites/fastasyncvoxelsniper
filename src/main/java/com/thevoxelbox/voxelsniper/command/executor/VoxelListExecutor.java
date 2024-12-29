package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.VoxelListBlocksArgument;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@RequireToolkit
@CommandMethod(value = "voxel_list|voxellist|vl")
@CommandDescription("Voxel block exclusions list.")
@CommandPermission("voxelsniper.sniper")
public class VoxelListExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public VoxelListExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("")
    public void onVoxelList(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit
    ) {
        Player player = sniper.getPlayer();
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
        BlockVector3 targetBlock = blockTracer.getTargetBlock();
        if (targetBlock == null) {
            sniper.print(Caption.of("voxelsniper.command.invalid-target-block"));
            return;
        }

        BlockState blockState = BukkitAdapter.adapt(player.getWorld().getBlockAt(
                targetBlock.x(), targetBlock.y(), targetBlock.z()
        ).getBlockData());
        toolkitProperties.addToVoxelList(blockState);

        Messenger messenger = new Messenger(plugin, player);
        Collection<BlockState> voxelList = toolkitProperties.getVoxelList();
        messenger.sendVoxelListMessage(voxelList);
    }

    @CommandMethod("clear")
    public void onVoxelListClear(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit
    ) {
        Player player = sniper.getPlayer();
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        toolkitProperties.clearVoxelList();

        Messenger messenger = new Messenger(plugin, player);
        Collection<BlockState> voxelList = toolkitProperties.getVoxelList();
        messenger.sendVoxelListMessage(voxelList);
    }

    @CommandMethod("<blocks>")
    public void onVoxelListBlocks(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit,
            final @NotNull @Argument("blocks") VoxelListBlocksArgument.BlockWrapper[] blockWrappers
    ) {
        Player player = sniper.getPlayer();
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        for (VoxelListBlocksArgument.BlockWrapper blockWrapper : blockWrappers) {
            if (blockWrapper.remove()) {
                toolkitProperties.removeFromVoxelList(blockWrapper.block());
            } else {
                toolkitProperties.addToVoxelList(blockWrapper.block());
            }
        }

        Messenger messenger = new Messenger(plugin, player);
        Collection<BlockState> voxelList = toolkitProperties.getVoxelList();
        messenger.sendVoxelListMessage(voxelList);
    }

}
