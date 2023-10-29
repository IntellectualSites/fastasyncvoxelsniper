package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "default|d")
@CommandDescription("VoxelSniper Default.")
@CommandPermission("voxelsniper.sniper")
public class DefaultExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public DefaultExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("")
    public void onDefault(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit
    ) {
        toolkit.reset();
        sniper.print(Caption.of("voxelsniper.command.default.reset"));
    }

}
