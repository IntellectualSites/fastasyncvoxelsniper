package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.command.argument.BiomeTypeArgument;
import com.thevoxelbox.voxelsniper.command.argument.BlockArgument;
import com.thevoxelbox.voxelsniper.command.argument.BlockTypeArgument;
import com.thevoxelbox.voxelsniper.command.argument.BrushPropertiesArgument;
import com.thevoxelbox.voxelsniper.command.argument.EntityClassArgument;
import com.thevoxelbox.voxelsniper.command.argument.EntityTypeArgument;
import com.thevoxelbox.voxelsniper.command.argument.PatternArgument;
import com.thevoxelbox.voxelsniper.command.argument.SignFileArgument;
import com.thevoxelbox.voxelsniper.command.argument.StencilFileArgument;
import com.thevoxelbox.voxelsniper.command.argument.StencilListFileArgument;
import com.thevoxelbox.voxelsniper.command.argument.ToolkitArgument;
import com.thevoxelbox.voxelsniper.command.argument.VoxelListBlocksArgument;
import com.thevoxelbox.voxelsniper.command.executor.BrushExecutor;
import com.thevoxelbox.voxelsniper.command.executor.BrushToolkitExecutor;
import com.thevoxelbox.voxelsniper.command.executor.DefaultExecutor;
import com.thevoxelbox.voxelsniper.command.executor.GotoExecutor;
import com.thevoxelbox.voxelsniper.command.executor.PaintExecutor;
import com.thevoxelbox.voxelsniper.command.executor.PerformerExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelCenterExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelChunkExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelHeightExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelListExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelReplaceExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelSniperExecutor;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;

public class CommandRegistrar {

    private final VoxelSniperPlugin plugin;
    private final CommandRegistry registry;

    public CommandRegistrar(VoxelSniperPlugin plugin, CommandRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    /**
     * Register the suggestions and parsers into the command manager.
     *
     * @since TODO
     */
    public void registerSuggestionsAndParsers() {
        registerBiomeTypeArgument();
        registerBlockArgument();
        registerBlockTypeArgument();
        registerBrushPropertiesArgument();
        registerEntityClassArgument();
        registerEntityTypeArgument();
        registerPatternArgument();
        registerSignFileArgument();
        registerStencilFileArgument();
        registerStencilListFileArgument();
        registerVoxelListBlocksArgument();
        registerToolkitArgument();
    }

    private void registerBiomeTypeArgument() {
        BiomeTypeArgument argument = new BiomeTypeArgument(plugin);
        this.registry.register(argument);
    }

    private void registerBlockArgument() {
        BlockArgument argument = new BlockArgument(plugin);
        this.registry.register(argument);
    }

    private void registerBlockTypeArgument() {
        BlockTypeArgument argument = new BlockTypeArgument(plugin);
        this.registry.register(argument);
    }

    private void registerBrushPropertiesArgument() {
        BrushPropertiesArgument argument = new BrushPropertiesArgument(plugin);
        this.registry.register(argument);
    }

    private void registerEntityClassArgument() {
        EntityClassArgument argument = new EntityClassArgument(plugin);
        this.registry.register(argument);
    }

    private void registerEntityTypeArgument() {
        EntityTypeArgument argument = new EntityTypeArgument(plugin);
        this.registry.register(argument);
    }

    private void registerPatternArgument() {
        PatternArgument argument = new PatternArgument(plugin);
        this.registry.register(argument);
    }

    private void registerSignFileArgument() {
        SignFileArgument argument = new SignFileArgument(plugin);
        this.registry.register(argument);
    }

    private void registerStencilFileArgument() {
        StencilFileArgument argument = new StencilFileArgument(plugin);
        this.registry.register(argument);
    }

    private void registerStencilListFileArgument() {
        StencilListFileArgument argument = new StencilListFileArgument(plugin);
        this.registry.register(argument);
    }

    private void registerVoxelListBlocksArgument() {
        VoxelListBlocksArgument argument = new VoxelListBlocksArgument(plugin);
        this.registry.register(argument);
    }

    private void registerToolkitArgument() {
        ToolkitArgument argument = new ToolkitArgument(plugin);
        this.registry.register(argument);
    }

    public void registerCommands() {
        registerBrushCommand();
        registerBrushToolCommand();
        registerVoxelCenterCommand();
        registerDefaultCommand();
        registerGotoCommand();
        registerVoxelHeightCommand();
        registerVoxelListCommand();
        registerPaintCommand();
        registerPerformerCommand();
        registerVoxelReplaceCommand();
        registerVoxelSniperCommand();
        registerVoxelCommand();
        registerVoxelChunkCommand();
    }

    private void registerBrushCommand() {
        // Registers the main executor.
        BrushExecutor executor = new BrushExecutor(this.plugin);
        this.registry.register(executor);

        // Registers the brushes.
        for (BrushProperties properties : this.plugin.getBrushRegistry().getUniqueBrushesProperties()) {
            Brush brushExecutor = properties.getCreator().create();
            brushExecutor.setProperties(properties);
            brushExecutor.loadProperties();
            this.registry.register(brushExecutor);
        }
    }

    private void registerBrushToolCommand() {
        BrushToolkitExecutor executor = new BrushToolkitExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerVoxelCenterCommand() {
        VoxelCenterExecutor executor = new VoxelCenterExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerDefaultCommand() {
        DefaultExecutor executor = new DefaultExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerGotoCommand() {
        GotoExecutor executor = new GotoExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerVoxelHeightCommand() {
        VoxelHeightExecutor executor = new VoxelHeightExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerVoxelListCommand() {
        VoxelListExecutor executor = new VoxelListExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerPaintCommand() {
        PaintExecutor executor = new PaintExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerPerformerCommand() {
        PerformerExecutor executor = new PerformerExecutor(this.plugin);
        this.registry.register(executor);

        // Registers the performers
        for (PerformerProperties properties : this.plugin.getPerformerRegistry().getUniquePerformerProperties()) {
            Performer performerExecutor = properties.getCreator().create();
            performerExecutor.setProperties(properties);
            performerExecutor.loadProperties();
            this.registry.register(performerExecutor);
        }
    }

    private void registerVoxelReplaceCommand() {
        VoxelReplaceExecutor executor = new VoxelReplaceExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerVoxelSniperCommand() {
        VoxelSniperExecutor executor = new VoxelSniperExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerVoxelCommand() {
        VoxelExecutor executor = new VoxelExecutor(this.plugin);
        this.registry.register(executor);
    }

    private void registerVoxelChunkCommand() {
        VoxelChunkExecutor executor = new VoxelChunkExecutor(this.plugin);
        this.registry.register(executor);
    }

}
