package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b")
@CommandDescription("Brush executor.")
@CommandPermission("voxelsniper.sniper")
public class BrushExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;
    private final VoxelSniperConfig config;

    public BrushExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
        this.config = this.plugin.getVoxelSniperConfig();
    }

    @CommandMethod("")
    public void onBrush(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit
    ) {
        Player player = sniper.getPlayer();
        BrushProperties previousBrushProperties = toolkit.getPreviousBrushProperties();
        String permission = previousBrushProperties.getPermission();
        if (permission != null && !player.hasPermission(permission)) {
            sniper.print(Caption.of("voxelsniper.command.missing-permission", permission));
            return;
        }

        toolkit.useBrush(previousBrushProperties);
        sniper.sendInfo(player, true);
    }

    @CommandMethod("<size>")
    public void onBrushSize(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit,
            final @Argument("size") @Range(min = "0") int size
    ) {
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        Player player = sniper.getPlayer();

        int litesniperMaxBrushSize = config.getLitesniperMaxBrushSize();
        Messenger messenger = new Messenger(plugin, player);
        if (!player.hasPermission("voxelsniper.ignorelimitations") && size > litesniperMaxBrushSize) {
            sniper.print(Caption.of("voxelsniper.command.brush.restricted-size", litesniperMaxBrushSize));
            toolkitProperties.setBrushSize(litesniperMaxBrushSize);
            messenger.sendBrushSizeMessage(litesniperMaxBrushSize);
        } else {
            toolkitProperties.setBrushSize(size);
            messenger.sendBrushSizeMessage(size);
        }
    }

}
