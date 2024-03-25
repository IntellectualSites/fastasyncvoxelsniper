package com.thevoxelbox.voxelsniper.command.executor;

import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "voxel_height|voxelheight|vh")
@CommandDescription("VoxelHeight input.")
@Permission("voxelsniper.sniper")
public class VoxelHeightExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public VoxelHeightExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("<height>")
    public void onVoxelHeight(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit,
            final int height
    ) {
        Player player = sniper.getPlayer();
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        toolkitProperties.setVoxelHeight(height);

        Messenger messenger = new Messenger(plugin, player);
        messenger.sendVoxelHeightMessage(height);
    }

}
