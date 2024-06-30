package com.thevoxelbox.voxelsniper.command.executor;

import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
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
@Command(value = "performer|perf|p")
@CommandDescription("Performer executor.")
@Permission("voxelsniper.sniper")
public class PerformerExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public PerformerExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("")
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
