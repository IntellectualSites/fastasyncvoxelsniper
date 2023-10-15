package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;
import java.util.Queue;

public class BlockTypeArgument extends AbstractRegistryArgument<BlockType> {

    /**
     * Create a block type argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public BlockTypeArgument(VoxelSniperPlugin plugin) {
        super(plugin, BlockType.REGISTRY, "voxelsniper.command.invalid-block-type");
    }

    @Suggestions("block-type_suggestions")
    public List<String> suggestBlockTypes(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestValues(commandContext, input);
    }

    @Parser(suggestions = "block-type_suggestions")
    public BlockType parseBlockType(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        return super.parseValue(commandContext, inputQueue);
    }

}
