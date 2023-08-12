package com.thevoxelbox.voxelsniper.performer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;

public interface Performer extends VoxelCommandElement {

    /**
     * Initialize performer data.
     *
     * @param snipe Snipe
     */
    void initialize(PerformerSnipe snipe);

    /**
     * Perform performer action.
     *
     * @param editSession EditSession
     * @param x           Block x
     * @param y           Block y
     * @param z           Block z
     * @param block       BlockState
     */
    void perform(EditSession editSession, int x, int y, int z, BlockState block);

    /**
     * Send performer information.
     *
     * @param snipe Snipe
     */
    void sendInfo(PerformerSnipe snipe);

    /**
     * Return performer properties.
     *
     * @return performer properties
     */
    PerformerProperties getProperties();

    /**
     * Set performer properties.
     *
     * @param properties performer properties
     */
    void setProperties(PerformerProperties properties);

    /**
     * Load brush properties.
     */
    void loadProperties();

}
