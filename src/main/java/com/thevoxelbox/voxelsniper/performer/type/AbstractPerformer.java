package com.thevoxelbox.voxelsniper.performer.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;

public abstract class AbstractPerformer implements Performer {

    private PerformerProperties properties;

    public void setBlock(EditSession editSession, int x, int y, int z, Pattern pattern) {
        if (pattern instanceof BlockType blockType) {
            setBlockData(editSession, x, y, z, blockType.getDefaultState());
        } else {
            editSession.setBlock(x, y, z, pattern);
        }
    }

    public void setBlockData(EditSession editSession, int x, int y, int z, BlockState blockState) {
        editSession.setBlock(x, y, z, blockState);
        if (blockState.getMaterial().isTile()) {
            try {
                editSession.setTile(x, y, z, blockState.getMaterial().getDefaultTile());
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public BaseBlock simulateSetBlock(int x, int y, int z, Pattern pattern) {
        return pattern.applyBlock(BlockVector3.at(x, y, z));
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
