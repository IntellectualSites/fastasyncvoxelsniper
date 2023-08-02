package com.thevoxelbox.voxelsniper.brush.type.stencil;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.BlockFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.registry.LegacyMapper;

import java.io.DataInputStream;
import java.io.IOException;

public class StencilReader {

    private static final BlockFactory BLOCK_FACTORY;
    private static final ParserContext PARSER_CONTEXT;
    private static final LegacyMapper LEGACY_MAPPER;

    static {
        BLOCK_FACTORY = WorldEdit.getInstance().getBlockFactory();
        PARSER_CONTEXT = new ParserContext();
        PARSER_CONTEXT.setRestricted(false);
        LEGACY_MAPPER = LegacyMapper.getInstance();
    }

    /**
     * Tries to fetch a modern block data from its legacy ids.
     *
     * @param material the material id
     * @param data     the data id
     * @return the corresponding block data or air, as a WE block state
     */
    private static BlockState getBlockData(int material, int data) {
        return LEGACY_MAPPER.getBlockFromLegacy(material + ":" + data);
    }

    protected enum BlockDataReader {
        BLOCK_DATA {
            @Override
            protected BlockState readBlockData(DataInputStream in) throws IOException {
                String blockDataString = in.readUTF();
                return BLOCK_FACTORY
                        .parseFromInput(blockDataString, PARSER_CONTEXT)
                        .toBlockState();
            }
        },
        LEGACY_IDS {
            @Override
            protected BlockState readBlockData(DataInputStream in) throws IOException {
                int id = in.readByte() + 128;
                int data = in.readByte() + 128;
                return getBlockData(id, data);
            }
        };

        /**
         * Reads block data from a given input stream.
         *
         * @param in the data input stream to read from
         * @return the block data, as a WE block state
         * @throws IOException IO error
         */
        protected abstract BlockState readBlockData(DataInputStream in) throws IOException;
    }

}
