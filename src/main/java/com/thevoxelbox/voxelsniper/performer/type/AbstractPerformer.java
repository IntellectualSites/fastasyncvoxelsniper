package com.thevoxelbox.voxelsniper.performer.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;

public abstract class AbstractPerformer implements Performer {

    private PerformerProperties properties;

    protected void setBlockType(EditSession editSession, int x, int y, int z, BlockType type) {
        setBlockData(editSession, x, y, z, type.getDefaultState());
    }

    protected void setBlockData(EditSession editSession, int x, int y, int z, BlockState blockState) {
        editSession.setBlock(x, y, z, blockState);
        if (blockState.getMaterial().isTile()) {
            try {
                editSession.setTile(x, y, z, blockState.getMaterial().getDefaultTile());
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public PerformerProperties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(final PerformerProperties properties) {
        this.properties = properties;
    }

    @Override
    public void loadProperties() {
    }

}
