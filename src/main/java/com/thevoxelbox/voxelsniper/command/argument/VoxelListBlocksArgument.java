package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.util.Locale;
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
    public Stream<String> suggestVoxelListBlocks(CommandContext<SniperCommander> commandContext, String input) {
        return WorldEdit.getInstance().getBlockFactory()
                .getSuggestions(input.startsWith("-") ? input.substring(1) : input).stream()
                .flatMap(id -> Stream.of(id, "-" + id));
    }

    @Parser(suggestions = "voxel-list-block_suggestions")
    public BlockWrapper[] parseVoxelListBlock(CommandContext<SniperCommander> commandContext, CommandInput commandInput) {
        BlockWrapper[] blockWrappers = new BlockWrapper[commandInput.remainingTokens()];
        int i = 0;
        while (commandInput.hasRemainingInput()) {
            SniperCommander commander = commandContext.sender();
            ParserContext parserContext = commander.createParserContext();

            boolean remove = commandInput.peekString().startsWith("-");
            if (remove) {
                commandInput.moveCursor(1);
            }

            String input = commandInput.readString();
            try {
                BaseBlock baseBlock = WorldEdit.getInstance().getBlockFactory().parseFromInput(
                        commandInput.readString().toLowerCase(Locale.ROOT),
                        parserContext
                );

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
