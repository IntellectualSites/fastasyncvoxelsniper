package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.block.Block;

/**
 * Brush Interface.
 */
public interface Brush {

	/**
	 * @param messages Messages object
	 */
	void info(Messages messages);

	/**
	 * Handles parameters passed to brushes.
	 *
	 * @param parameters Array of string containing parameters
	 * @param toolkitProperties Snipe Data
	 */
	void parameters(String[] parameters, ToolkitProperties toolkitProperties);

	boolean perform(ToolAction action, ToolkitProperties data, Block targetBlock, Block lastBlock);

	/**
	 * The arrow action. Executed when a player RightClicks with an Arrow
	 *
	 * @param toolkitProperties Sniper caller
	 */
	void arrow(ToolkitProperties toolkitProperties);

	/**
	 * The powder action. Executed when a player RightClicks with Gunpowder
	 *
	 * @param toolkitProperties Sniper caller
	 */
	void powder(ToolkitProperties toolkitProperties);

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
