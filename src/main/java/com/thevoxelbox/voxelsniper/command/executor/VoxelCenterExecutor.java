package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
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
@CommandMethod(value = "voxel_center|voxelcenter|vc")
@CommandDescription("VoxelCenter & VoxelCentroid input.")
@CommandPermission("voxelsniper.sniper")
public class VoxelCenterExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public VoxelCenterExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("<center>")
    public void onVoxelCenter(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit,
            final @Argument("center") int center
    ) {
        Player player = sniper.getPlayer();
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        toolkitProperties.setCylinderCenter(center);

        Messenger messenger = new Messenger(plugin, player);
        messenger.sendCylinderCenterMessage(center);
    }

}
