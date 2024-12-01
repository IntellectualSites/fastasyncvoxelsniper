package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.stream.Stream;

public class VoxelListBlocksArgument implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    /**
     * Create a voxel list blocks argument.
     *
     * @param plugin the plugin
     * @since 3.0.0
     */
    public VoxelListBlocksArgument(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Suggestions("voxel-list-block_suggestions")
    public List<String> suggestVoxelListBlocks(CommandContext<SniperCommander> commandContext, String input) {
        return WorldEdit.getInstance().getBlockFactory()
                .getSuggestions(input.startsWith("-") ? input.substring(1) : input, new ParserContext()).stream()
                .flatMap(id -> Stream.of(id, "-" + id))
                .toList();
    }

    @Parser(suggestions = "voxel-list-block_suggestions")
    public BlockWrapper[] parseVoxelListBlock(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        if (inputQueue.isEmpty()) {
            throw new NoInputProvidedException(VoxelListBlocksArgument.class, commandContext);
        }

        BlockWrapper[] blockWrappers = new BlockWrapper[inputQueue.size()];
        int i = 0;
        while (!inputQueue.isEmpty()) {
            String input = inputQueue.peek();
            SniperCommander commander = commandContext.getSender();
            ParserContext parserContext = commander.createParserContext();

            boolean remove = input.startsWith("-");
            if (remove) {
                input = input.substring(1);
            }
            try {
                BaseBlock baseBlock = WorldEdit.getInstance().getBlockFactory().parseFromInput(
                        input.toLowerCase(Locale.ROOT),
                        parserContext
                );

                inputQueue.remove();
                blockWrappers[i++] = new BlockWrapper(baseBlock.toBlockState(), remove);
            } catch (InputParseException e) {
                throw new VoxelCommandElementParseException(input, Caption.of(
                        "voxelsniper.command.invalid-block-type",
                        input
                ));
            }
        }

        return blockWrappers;
    }

    /**
     * Voxel list block wrapper.
     *
     * @param block  the block
     * @param remove whether the block should be removed or not
     * @since 3.0.0
     */
    public record BlockWrapper(BlockState block, boolean remove) {

    }

}
