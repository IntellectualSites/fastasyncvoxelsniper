package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

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
    public Stream<String> suggestBlockTypes(CommandContext<SniperCommander> commandContext, String input) {
        return super.suggestValues(commandContext, input);
    }

    @Parser(suggestions = "block-type_suggestions")
    public BlockType parseBlockType(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        return super.parseValue(commandContext, commandInput);
    }

}
