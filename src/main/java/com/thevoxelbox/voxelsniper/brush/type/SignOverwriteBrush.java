package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

/**
 * Overwrites signs. (Wiki:
 * http://www.voxelwiki.com/minecraft/VoxelSniper#Sign_Overwrite_Brush)
 *
 * @author Monofraps
 */
public class SignOverwriteBrush extends AbstractBrush {

	private static final int MAX_SIGN_LINE_LENGTH = 15;
	private static final int NUM_SIGN_LINES = 4;
	// these are no array indices
	private static final int SIGN_LINE_1 = 1;
	private static final int SIGN_LINE_2 = 2;
	private static final int SIGN_LINE_3 = 3;
	private static final int SIGN_LINE_4 = 4;
	private String[] signTextLines = new String[NUM_SIGN_LINES];
	private boolean[] signLinesEnabled = new boolean[NUM_SIGN_LINES];
	private boolean rangedMode;

	public SignOverwriteBrush() {
		super("Sign Overwrite Brush");
		clearBuffer();
		resetStates();
	}

	/**
	 * Sets the text of a given sign.
	 */
	private void setSignText(Sign sign) {
		for (int i = 0; i < this.signTextLines.length; i++) {
			if (this.signLinesEnabled[i]) {
				sign.setLine(i, this.signTextLines[i]);
			}
		}
		sign.update();
	}

	/**
	 * Sets the text of the target sign if the target block is a sign.
	 */
	private void setSingle(ToolkitProperties toolkitProperties) {
		if (this.getTargetBlock()
			.getState() instanceof Sign) {
			setSignText((Sign) this.getTargetBlock()
				.getState());
		} else {
			toolkitProperties.sendMessage(ChatColor.RED + "Target block is not a sign.");
		}
	}

	/**
	 * Sets all signs in a range of box{x=z=brushSize*2+1 ; z=voxelHeight*2+1}.
	 */
	private void setRanged(ToolkitProperties toolkitProperties) {
		Block targetBlock = getTargetBlock();
		int minX = targetBlock.getX() - toolkitProperties.getBrushSize();
		int maxX = targetBlock.getX() + toolkitProperties.getBrushSize();
		int minY = targetBlock.getY() - toolkitProperties.getVoxelHeight();
		int maxY = targetBlock.getY() + toolkitProperties.getVoxelHeight();
		int minZ = targetBlock.getZ() - toolkitProperties.getBrushSize();
		int maxZ = targetBlock.getZ() + toolkitProperties.getBrushSize();
		boolean signFound = false; // indicates whether or not a sign was set
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockState blockState = this.getWorld()
						.getBlockAt(x, y, z)
						.getState();
					if (blockState instanceof Sign) {
						setSignText((Sign) blockState);
						signFound = true;
					}
				}
			}
		}
		if (!signFound) {
			toolkitProperties.sendMessage(ChatColor.RED + "Did not found any sign in selection box.");
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		if (this.rangedMode) {
			setRanged(toolkitProperties);
		} else {
			setSingle(toolkitProperties);
		}
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		if (this.getTargetBlock()
			.getState() instanceof Sign) {
			Sign sign = (Sign) this.getTargetBlock()
				.getState();
			for (int i = 0; i < this.signTextLines.length; i++) {
				if (this.signLinesEnabled[i]) {
					this.signTextLines[i] = sign.getLine(i);
				}
			}
			displayBuffer(toolkitProperties);
		} else {
			toolkitProperties.sendMessage(ChatColor.RED + "Target block is not a sign.");
		}
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		boolean textChanged = false;
		for (int i = 0; i < parameters.length; i++) {
			String parameter = parameters[i];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					toolkitProperties.sendMessage(ChatColor.AQUA + "Sign Overwrite Brush Powder/Arrow:");
					toolkitProperties.sendMessage(ChatColor.BLUE + "The arrow writes the internal line buffer to the tearget sign.");
					toolkitProperties.sendMessage(ChatColor.BLUE + "The powder reads the text of the target sign into the internal buffer.");
					toolkitProperties.sendMessage(ChatColor.AQUA + "Sign Overwrite Brush Parameters:");
					toolkitProperties.sendMessage(ChatColor.GREEN + "-1[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the first sign line. (e.g. -1 Blah Blah)");
					toolkitProperties.sendMessage(ChatColor.GREEN + "-2[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the second sign line. (e.g. -2 Blah Blah)");
					toolkitProperties.sendMessage(ChatColor.GREEN + "-3[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the third sign line. (e.g. -3 Blah Blah)");
					toolkitProperties.sendMessage(ChatColor.GREEN + "-4[:(enabled|disabled)] ... " + ChatColor.BLUE + "-- Sets the text of the fourth sign line. (e.g. -4 Blah Blah)");
					toolkitProperties.sendMessage(ChatColor.GREEN + "-clear " + ChatColor.BLUE + "-- Clears the line buffer. (Alias: -c)");
					toolkitProperties.sendMessage(ChatColor.GREEN + "-clearall " + ChatColor.BLUE + "-- Clears the line buffer and sets all lines back to enabled. (Alias: -ca)");
					toolkitProperties.sendMessage(ChatColor.GREEN + "-multiple [on|off] " + ChatColor.BLUE + "-- Enables or disables ranged mode. (Alias: -m) (see Wiki for more information)");
				} else if (parameter.startsWith("-1")) {
					textChanged = true;
					i = parseSignLineFromParam(parameters, SIGN_LINE_1, toolkitProperties, i);
				} else if (parameter.startsWith("-2")) {
					textChanged = true;
					i = parseSignLineFromParam(parameters, SIGN_LINE_2, toolkitProperties, i);
				} else if (parameter.startsWith("-3")) {
					textChanged = true;
					i = parseSignLineFromParam(parameters, SIGN_LINE_3, toolkitProperties, i);
				} else if (parameter.startsWith("-4")) {
					textChanged = true;
					i = parseSignLineFromParam(parameters, SIGN_LINE_4, toolkitProperties, i);
				} else if (parameter.equalsIgnoreCase("-clear") || parameter.equalsIgnoreCase("-c")) {
					clearBuffer();
					toolkitProperties.sendMessage(ChatColor.BLUE + "Internal text buffer cleard.");
				} else if (parameter.equalsIgnoreCase("-clearall") || parameter.equalsIgnoreCase("-ca")) {
					clearBuffer();
					resetStates();
					toolkitProperties.sendMessage(ChatColor.BLUE + "Internal text buffer cleard and states back to enabled.");
				} else if (parameter.equalsIgnoreCase("-multiple") || parameter.equalsIgnoreCase("-m")) {
					if ((i + 1) >= parameters.length) {
						toolkitProperties.sendMessage(ChatColor.RED + String.format("Missing parameter after %s.", parameter));
						continue;
					}
					this.rangedMode = (parameters[++i].equalsIgnoreCase("on") || parameters[++i].equalsIgnoreCase("yes"));
					toolkitProperties.sendMessage(ChatColor.BLUE + String.format("Ranged mode is %s", ChatColor.GREEN + (this.rangedMode ? "enabled" : "disabled")));
					if (this.rangedMode) {
						toolkitProperties.sendMessage(ChatColor.GREEN + "Brush size set to " + ChatColor.RED + toolkitProperties.getBrushSize());
						toolkitProperties.sendMessage(ChatColor.AQUA + "Brush height set to " + ChatColor.RED + toolkitProperties.getVoxelHeight());
					}
				}
			} catch (RuntimeException exception) {
				toolkitProperties.sendMessage(ChatColor.RED + String.format("Error while parsing parameter %s", parameter));
				exception.printStackTrace();
			}
		}
		if (textChanged) {
			displayBuffer(toolkitProperties);
		}
	}

	/**
	 * Parses parameter input text of line [param:lineNumber].
	 * Iterates though the given array until the next top level param (a parameter which starts
	 * with a dash -) is found.
	 */
	private int parseSignLineFromParam(String[] params, int lineNumber, ToolkitProperties toolkitProperties, int i) {
		int lineIndex = lineNumber - 1;
		String parameter = params[i];
		boolean statusSet = false;
		if (parameter.contains(":")) {
			this.signLinesEnabled[lineIndex] = parameter.substring(parameter.indexOf(':'))
				.equalsIgnoreCase(":enabled");
			toolkitProperties.sendMessage(ChatColor.BLUE + "Line " + lineNumber + " is " + ChatColor.GREEN + (this.signLinesEnabled[lineIndex] ? "enabled" : "disabled"));
			statusSet = true;
		}
		if ((i + 1) >= params.length) {
			// return if the user just wanted to set the status
			if (statusSet) {
				return i;
			}
			toolkitProperties.sendMessage(ChatColor.RED + "Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
			this.signTextLines[lineIndex] = "";
			return i;
		}
		StringBuilder newText = new StringBuilder();
		// go through the array until the next top level parameter is found
		for (i++; i < params.length; i++) {
			String currentParameter = params[i];
			if (currentParameter.startsWith("-")) {
				i--;
				break;
			} else {
				newText.append(currentParameter)
					.append(" ");
			}
		}
		newText = new StringBuilder(ChatColor.translateAlternateColorCodes('&', newText.toString()));
		// remove last space or return if the string is empty and the user just wanted to set the status
		if ((newText.length() > 0) && newText.toString()
			.endsWith(" ")) {
			newText = new StringBuilder(newText.substring(0, newText.length() - 1));
		} else if (newText.length() == 0) {
			if (statusSet) {
				return i;
			}
			toolkitProperties.sendMessage(ChatColor.RED + "Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
		}
		// check the line length and cut the text if needed
		if (newText.length() > MAX_SIGN_LINE_LENGTH) {
			toolkitProperties.sendMessage(ChatColor.RED + "Warning: Text on line " + lineNumber + " exceeds the maximum line length of " + MAX_SIGN_LINE_LENGTH + " characters. Your text will be cut.");
			newText = new StringBuilder(newText.substring(0, MAX_SIGN_LINE_LENGTH));
		}
		this.signTextLines[lineIndex] = newText.toString();
		return i;
	}

	private void displayBuffer(ToolkitProperties v) {
		v.sendMessage(ChatColor.BLUE + "Buffer text set to: ");
		for (int i = 0; i < this.signTextLines.length; i++) {
			v.sendMessage((this.signLinesEnabled[i] ? ChatColor.GREEN + "(E): " : ChatColor.RED + "(D): ") + ChatColor.BLACK + this.signTextLines[i]);
		}
	}

	/**
	 * Clears the internal text buffer. (Sets it to empty strings)
	 */
	private void clearBuffer() {
		Arrays.fill(this.signTextLines, "");
	}

	/**
	 * Resets line enabled states to enabled.
	 */
	private void resetStates() {
		for (int i = 0; i < this.signLinesEnabled.length; i++) {
			this.signLinesEnabled[i] = true;
		}
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName("Sign Overwrite Brush");
		messages.custom(ChatColor.BLUE + "Buffer text: ");
		for (int i = 0; i < this.signTextLines.length; i++) {
			messages.custom((this.signLinesEnabled[i] ? ChatColor.GREEN + "(E): " : ChatColor.RED + "(D): ") + ChatColor.BLACK + this.signTextLines[i]);
		}
		messages.custom(ChatColor.BLUE + String.format("Ranged mode is %s", ChatColor.GREEN + (this.rangedMode ? "enabled" : "disabled")));
		if (this.rangedMode) {
			messages.size();
			messages.height();
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.signoverwrite";
	}
}
