package com.thevoxelbox.voxelsniper.performer.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.performer.Performer;

public abstract class AbstractPerformer implements Performer {

    public void setBlockType(EditSession editSession, int x, int y, int z, BlockType type) {
        setBlockData(editSession, x, y, z, type.getDefaultState());
    }

    public void setBlockData(EditSession editSession, int x, int y, int z, BlockState blockState) {
        try {
            editSession.setBlock(x, y, z, blockState);
            if (blockState.getMaterial().isTile()) {
                editSession.setTile(x, y, z, blockState.getMaterial().getDefaultTile());
            }
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

}
