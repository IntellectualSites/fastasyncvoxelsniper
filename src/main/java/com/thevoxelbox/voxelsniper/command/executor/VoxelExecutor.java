package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequireToolkit
@CommandMethod(value = "voxel|v|voxel_ink|voxelink|vi")
@CommandDescription("Voxel input.")
@CommandPermission("voxelsniper.sniper")
public class VoxelExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;
    private final VoxelSniperConfig config;

    public VoxelExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getVoxelSniperConfig();
    }

    @CommandMethod("[pattern]")
    public void onVoxel(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit,
            final @Nullable @Argument(value = "pattern", parserName = "pattern_parser") BrushPattern brushPattern
    ) {
        Player player = sniper.getPlayer();
        ToolkitProperties toolkitProperties = toolkit.getProperties();

        if (brushPattern == null) {
            BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
            BlockVector3 targetBlock = blockTracer.getTargetBlock();
            if (targetBlock != null) {
                BlockState targetBlockState = BukkitAdapter.adapt(
                        player.getWorld().getBlockAt(
                                targetBlock.x(),
                                targetBlock.y(),
                                targetBlock.z()
                        ).getBlockData());
                if (targetBlockState == null) {
                    sniper.print(Caption.of("voxelsniper.command.invalid-target-block"));
                    return;
                }

                BlockType targetBlockType = targetBlockState.getBlockType();
                if (!player.hasPermission("voxelsniper.ignorelimitations") && config
                        .getLitesniperRestrictedMaterials()
                        .contains(targetBlockType.getResource())) {
                    sniper.print(Caption.of("voxelsniper.command.not-allowed", targetBlockType.id()));
                    return;
                }

                toolkitProperties.setPattern(new BrushPattern(targetBlockState));
            }
        } else {
            toolkitProperties.setPattern(brushPattern);
        }

        Messenger messenger = new Messenger(plugin, player);
        messenger.sendPatternMessage(toolkitProperties.getPattern());
    }

}
