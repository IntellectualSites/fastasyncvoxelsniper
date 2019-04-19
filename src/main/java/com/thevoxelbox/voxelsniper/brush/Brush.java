package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import org.bukkit.block.Block;

/**
 * Brush Interface.
 */
public interface Brush {

	/**
	 * @param message Message object
	 */
	void info(Message message);

	/**
	 * Handles parameters passed to brushes.
	 *
	 * @param parameters Array of string containing parameters
	 * @param snipeData Snipe Data
	 */
	void parameters(String[] parameters, SnipeData snipeData);

	boolean perform(SnipeAction action, SnipeData data, Block targetBlock, Block lastBlock);

	/**
	 * @return The name of the Brush
	 */
	String getName();

	/**
	 * @param name New name for the Brush
	 */
	void setName(String name);

	/**
	 * @return The name of the category the brush is in.
	 */
	String getBrushCategory();

	/**
	 * @return Permission node required to use this brush
	 */
	String getPermissionNode();
}
