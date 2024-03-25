package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;

public class BlockArgument extends AbstractPatternArgument<BaseBlock> {

    /**
     * Create a block argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public BlockArgument(VoxelSniperPlugin plugin) {
        super(plugin, WorldEdit.getInstance().getBlockFactory(), "voxelsniper.command.invalid-block");
    }

    @Suggestions("block_suggestions")
    public List<String> suggestBlocks(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestPatterns(commandContext, input);
    }

    @Parser(name = "block_parser", suggestions = "block_suggestions")
    public BrushPattern parseBlock(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        return super.parsePattern(commandContext, commandInput);
    }

    @Override
    protected String getPatternResource(String input, BaseBlock pattern) {
        return pattern.getBlockType().getResource();
    }

}
