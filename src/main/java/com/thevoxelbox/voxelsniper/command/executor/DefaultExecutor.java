package com.thevoxelbox.voxelsniper.command.executor;

import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "default|d")
@CommandDescription("VoxelSniper Default.")
@Permission("voxelsniper.sniper")
public class DefaultExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public DefaultExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("")
    public void onDefault(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit
    ) {
        toolkit.reset();
        sniper.print(Caption.of("voxelsniper.command.default.reset"));
    }

}
