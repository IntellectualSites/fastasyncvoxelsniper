package com.thevoxelbox.voxelsniper.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;

import java.util.List;

public interface Brush {

    /**
     * Handles parameters passed to brushes.
     *
     * @param parameters Array of string containing parameters
     * @param snipe      Snipe
     */
    void handleCommand(String[] parameters, Snipe snipe);

    /**
     * Handles parameters completers passed to brushes.
     *
     * @param parameters Array of string containing parameters
     * @param snipe      Snipe
     */
    List<String> handleCompletions(String[] parameters, Snipe snipe);

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
     * The arrow action. Executed when a player RightClicks with an Arrow
     *
     * @param snipe Snipe
     */
    void handleArrowAction(Snipe snipe);

    /**
     * The gunpowder action. Executed when a player RightClicks with Gunpowder
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

}
