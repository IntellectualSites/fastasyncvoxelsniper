package com.thevoxelbox.voxelsniper.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;

public interface Brush extends VoxelCommandElement {

    /**
     * Load brush properties.
     */
    void loadProperties();

    /**
     * Perform brush action.
     *
     * @param snipe       Snipe
     * @param action      ToolAction
     * @param editSession EditSession
     * @param targetBlock Target Block
     * @param lastBlock   Last Block, preceding Target Block
     */
    void perform(Snipe snipe, ToolAction action, EditSession editSession, BlockVector3 targetBlock, BlockVector3 lastBlock);

    /**
     * The arrow action. Executed when a player right clicks with an arrow
     *
     * @param snipe Snipe
     */
    void handleArrowAction(Snipe snipe);

    /**
     * The gunpowder action. Executed when a player right clicks with gunpowder
     *
     * @param snipe Snipe
     */
    void handleGunpowderAction(Snipe snipe);

    /**
     * Send brush information.
     *
     * @param snipe Snipe
     */
    void sendInfo(Snipe snipe);

    /**
     * Return brush properties.
     *
     * @return brush properties
     */
    BrushProperties getProperties();

    /**
     * Set brush properties.
     *
     * @param properties brush properties
     */
    void setProperties(BrushProperties properties);

}
