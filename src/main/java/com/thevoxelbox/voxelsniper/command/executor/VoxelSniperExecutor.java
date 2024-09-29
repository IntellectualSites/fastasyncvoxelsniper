package com.thevoxelbox.voxelsniper.command.executor;

import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Range;
import com.fastasyncworldedit.core.Fawe;
import com.fastasyncworldedit.core.configuration.Caption;
import com.intellectualsites.paster.IncendoPaster;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Command("voxel_sniper|voxelsniper|vs|favs|fastasyncvoxelsniper")
@CommandDescription("FastAsyncVoxelSniper Settings.")
@Permission("voxelsniper.sniper")
public class VoxelSniperExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public VoxelSniperExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(value = "")
    public void onVoxelSniper(
            final @NotNull Sniper sniper
    ) {
        sniper.print(Caption.of("voxelsniper.command.voxel-sniper.info"));
        sniper.sendInfo(false);
    }

    @Command("brushes")
    public void onVoxelSniperBrushes(
            final @NotNull SniperCommander commander
    ) {
        Toolkit toolkit = commander instanceof Sniper sniper ? sniper.getCurrentToolkit() : null;
        BrushProperties brushProperties = toolkit == null ? null : toolkit.getCurrentBrushProperties();

        commander.print(VoxelSniperText.formatListWithCurrent(
                this.plugin.getBrushRegistry().getBrushesProperties().entrySet(),
                (entry, entry2) -> entry.getKey().compareTo(entry2.getKey()),
                entry -> TextComponent.of(entry.getKey()),
                Map.Entry::getValue,
                brushProperties,
                "voxelsniper.command.voxel-sniper.brushes"
        ));
    }

    @Command("brusheslong")
    public void onVoxelSniperBrusheslong(
            final @NotNull SniperCommander commander
    ) {
        Toolkit toolkit = commander instanceof Sniper sniper ? sniper.getCurrentToolkit() : null;
        BrushProperties brushProperties = toolkit == null ? null : toolkit.getCurrentBrushProperties();

        commander.print(VoxelSniperText.formatListWithCurrent(
                this.plugin.getBrushRegistry().getUniqueBrushesProperties(),
                (properties, properties2) -> properties.getName().compareTo(properties2.getName()),
                entry -> TextComponent.of(entry.getName()),
                properties -> properties,
                brushProperties,
                "voxelsniper.command.voxel-sniper.brushes-long"
        ));
    }

    @RequireToolkit
    @Command(value = "range [range]")
    public void onVoxelSniperRange(
            final @NotNull Sniper sniper,
            final @NotNull Toolkit toolkit,
            final @Nullable  @Range(min = "1") Integer range
    ) {
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        toolkitProperties.setBlockTracerRange(range);

        sniper.print(Caption.of(
                "voxelsniper.command.voxel-sniper.distance-restriction",
                VoxelSniperText.getStatus(range != null), range == null ? -1 : range
        ));
    }

    @Command("perf|performer")
    public void onVoxelSniperPerformer(
            final @NotNull SniperCommander commander
    ) {
        Toolkit toolkit = commander instanceof Sniper sniper ? sniper.getCurrentToolkit() : null;
        PerformerProperties performerProperties = toolkit == null ? null :
                toolkit.getCurrentBrush() instanceof Performer performer ? performer.getProperties() : null;

        commander.print(VoxelSniperText.formatListWithCurrent(
                this.plugin.getPerformerRegistry().getPerformerProperties().keySet(),
                String::compareTo,
                TextComponent::of,
                name -> name,
                performerProperties == null ? null : performerProperties.getName(),
                "voxelsniper.command.voxel-sniper.performer"
        ));
    }

    @Command("perflong|performerlong")
    public void onVoxelSniperPerformerlong(
            final @NotNull SniperCommander commander
    ) {
        Toolkit toolkit = commander instanceof Sniper sniper ? sniper.getCurrentToolkit() : null;
        PerformerProperties performerProperties = toolkit == null ? null :
                toolkit.getCurrentBrush() instanceof Performer performer ? performer.getProperties() : null;

        commander.print(VoxelSniperText.formatListWithCurrent(
                this.plugin.getPerformerRegistry().getUniquePerformerProperties(),
                (properties, properties2) -> properties.getName().compareTo(properties2.getName()),
                properties -> TextComponent.of(properties.getName()),
                properties -> properties,
                performerProperties == null ? null : performerProperties.getName(),
                "voxelsniper.command.voxel-sniper.performer-long"
        ));
    }

    @Command(value = "enable")
    public void onVoxelSniperEnable(
            final @NotNull Sniper sniper
    ) {
        sniper.setEnabled(true);
        sniper.print(Caption.of(
                "voxelsniper.command.voxel-sniper.toggle",
                VoxelSniperText.getStatus(sniper.isEnabled())
        ));
    }

    @Command(value = "disable")
    public void onVoxelSniperDisable(
            final @NotNull Sniper sniper
    ) {
        sniper.setEnabled(false);
        sniper.print(Caption.of(
                "voxelsniper.command.voxel-sniper.toggle",
                VoxelSniperText.getStatus(sniper.isEnabled())
        ));
    }

    @Command(value = "toggle")
    public void onVoxelSniperToggle(
            final @NotNull Sniper sniper
    ) {
        sniper.setEnabled(!sniper.isEnabled());
        sniper.print(Caption.of(
                "voxelsniper.command.voxel-sniper.toggle",
                VoxelSniperText.getStatus(sniper.isEnabled())
        ));
    }

    @SuppressWarnings("deprecation") // Paper deprecation
    @Command("info")
    public void onVoxelSniperInfo(
            final @NotNull SniperCommander commander
    ) {
        PluginDescriptionFile description = plugin.getDescription();
        commander.print(Caption.of("voxelsniper.command.voxel-sniper.admin-info",
                description.getName(), description.getVersion(), description.getDescription(),
                description.getWebsite(), "https://intellectualsites.gitbook.io/fastasyncvoxelsniper/",
                "https://discord.gg/intellectualsites"
        ));
    }

    @Command("reload")
    @Permission("voxelsniper.admin")
    public void onVoxelSniperReload(
            final @NotNull SniperCommander commander
    ) {
        plugin.reload();
        commander.print(Caption.of("voxelsniper.command.voxel-sniper.config-reload"));
    }

    @Command("debugpaste")
    @Permission("voxelsniper.admin")
    public void onVoxelSniperDebugpaste(
            final @NotNull SniperCommander commander
    ) {
        try {
            final File logFile = new File("logs/latest.log");
            final File config = new File(plugin.getDataFolder(), "config.yml");
            final String destination = IncendoPaster.debugPaste(logFile, Fawe.platform().getDebugInfo(), config);
            commander.print(Caption.of(
                    "voxelsniper.command.voxel-sniper.debugpaste-written",
                    TextComponent.of(destination)
                            .clickEvent(ClickEvent.of(ClickEvent.Action.OPEN_URL, destination))
            ));
        } catch (IOException e) {
            commander.print(Caption.of("voxelsniper.command.voxel-sniper.debugpaste-fail", e));
        }
    }

}
