package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "performer|perf|p")
@CommandDescription("Performer executor.")
@CommandPermission("voxelsniper.sniper")
public class PerformerExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public PerformerExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("")
    public void onPerformer(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit
    ) {
        Brush brush = toolkit.getCurrentBrush();
        if (!(brush instanceof PerformerBrush)) {
            sniper.print(Caption.of("voxelsniper.command.performer.invalid-brush"));
            return;
        }

        sniper.sendInfo(true);
    }

}
