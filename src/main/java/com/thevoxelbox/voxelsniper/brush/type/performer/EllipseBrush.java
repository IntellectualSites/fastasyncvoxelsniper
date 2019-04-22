package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#Ellipse_Brush
 *
 * @author psanker
 */
public class EllipseBrush extends AbstractPerformerBrush {

	private static final double TWO_PI = (2 * Math.PI);
	private static final int SCL_MIN = 1;
	private static final int SCL_MAX = 9999;
	private static final int SCL_DEFAULT = 10;
	private static final int STEPS_MIN = 1;
	private static final int STEPS_MAX = 2000;
	private static final int STEPS_DEFAULT = 200;

	private int xscl;
	private int yscl;
	private int steps;
	private double stepSize;
	private boolean fill;

	public EllipseBrush() {
		super("Ellipse");
	}

	private void ellipse(ToolkitProperties toolkitProperties, Block targetBlock) {
		try {
			for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
				int x = (int) Math.round(this.xscl * Math.cos(steps));
				int y = (int) Math.round(this.yscl * Math.sin(steps));
				Block lastBlock = getLastBlock();
				if (lastBlock != null) {
					BlockFace face = getTargetBlock().getFace(lastBlock);
					if (face != null) {
						switch (face) {
							case NORTH:
							case SOUTH:
								this.performer.perform(targetBlock.getRelative(0, x, y));
								break;
							case EAST:
							case WEST:
								this.performer.perform(targetBlock.getRelative(x, y, 0));
								break;
							case UP:
							case DOWN:
								this.performer.perform(targetBlock.getRelative(x, 0, y));
								break;
							default:
								break;
						}
					}
				}
				if (steps >= TWO_PI) {
					break;
				}
			}
		} catch (RuntimeException exception) {
			toolkitProperties.sendMessage(ChatColor.RED + "Invalid target.");
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void ellipseFill(ToolkitProperties toolkitProperties, Block targetBlock) {
		int ix = this.xscl;
		int iy = this.yscl;
		this.performer.perform(targetBlock);
		try {
			if (ix >= iy) { // Need this unless you want weird holes
				for (iy = this.yscl; iy > 0; iy--) {
					for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
						int x = (int) Math.round(ix * Math.cos(steps));
						int y = (int) Math.round(iy * Math.sin(steps));
						Block lastBlock = getLastBlock();
						if (lastBlock != null) {
							BlockFace face = getTargetBlock().getFace(lastBlock);
							if (face != null) {
								switch (face) {
									case NORTH:
									case SOUTH:
										this.performer.perform(targetBlock.getRelative(0, x, y));
										break;
									case EAST:
									case WEST:
										this.performer.perform(targetBlock.getRelative(x, y, 0));
										break;
									case UP:
									case DOWN:
										this.performer.perform(targetBlock.getRelative(x, 0, y));
										break;
									default:
										break;
								}
							}
						}
						if (steps >= TWO_PI) {
							break;
						}
					}
					ix--;
				}
			} else {
				for (ix = this.xscl; ix > 0; ix--) {
					for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
						int x = (int) Math.round(ix * Math.cos(steps));
						int y = (int) Math.round(iy * Math.sin(steps));
						Block lastBlock = getLastBlock();
						if (lastBlock != null) {
							BlockFace face = getTargetBlock().getFace(lastBlock);
							if (face != null) {
								switch (face) {
									case NORTH:
									case SOUTH:
										this.performer.perform(targetBlock.getRelative(0, x, y));
										break;
									case EAST:
									case WEST:
										this.performer.perform(targetBlock.getRelative(x, y, 0));
										break;
									case UP:
									case DOWN:
										this.performer.perform(targetBlock.getRelative(x, 0, y));
										break;
									default:
										break;
								}
							}
						}
						if (steps >= TWO_PI) {
							break;
						}
					}
					iy--;
				}
			}
		} catch (RuntimeException exception) {
			toolkitProperties.sendMessage(ChatColor.RED + "Invalid target.");
		}
		toolkitProperties.getOwner()
			.storeUndo(this.performer.getUndo());
	}

	private void execute(ToolkitProperties toolkitProperties, Block targetBlock) {
		this.stepSize = (TWO_PI / this.steps);
		if (this.fill) {
			this.ellipseFill(toolkitProperties, targetBlock);
		} else {
			this.ellipse(toolkitProperties, targetBlock);
		}
	}

	@Override
	public final void arrow(ToolkitProperties toolkitProperties) {
		this.execute(toolkitProperties, this.getTargetBlock());
	}

	@Override
	public final void powder(ToolkitProperties toolkitProperties) {
		Block lastBlock = this.getLastBlock();
		if (lastBlock == null) {
			return;
		}
		this.execute(toolkitProperties, lastBlock);
	}

	@Override
	public final void info(Messages messages) {
		if (this.xscl < SCL_MIN || this.xscl > SCL_MAX) {
			this.xscl = SCL_DEFAULT;
		}
		if (this.yscl < SCL_MIN || this.yscl > SCL_MAX) {
			this.yscl = SCL_DEFAULT;
		}
		if (this.steps < STEPS_MIN || this.steps > STEPS_MAX) {
			this.steps = STEPS_DEFAULT;
		}
		messages.brushName(this.getName());
		messages.custom(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xscl);
		messages.custom(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yscl);
		messages.custom(ChatColor.AQUA + "Render step number set to: " + ChatColor.DARK_AQUA + this.steps);
		if (this.fill) {
			messages.custom(ChatColor.AQUA + "Fill mode is enabled");
		} else {
			messages.custom(ChatColor.AQUA + "Fill mode is disabled");
		}
	}

	@Override
	public final void parameters(String[] parameters, ToolkitProperties toolkitProperties) {
		for (int i = 1; i < parameters.length; i++) {
			String parameter = parameters[i];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					toolkitProperties.sendMessage(ChatColor.GOLD + "Ellipse brush parameters");
					toolkitProperties.sendMessage(ChatColor.AQUA + "x[n]: Set X size modifier to n");
					toolkitProperties.sendMessage(ChatColor.AQUA + "y[n]: Set Y size modifier to n");
					toolkitProperties.sendMessage(ChatColor.AQUA + "t[n]: Set the amount of time steps");
					toolkitProperties.sendMessage(ChatColor.AQUA + "fill: Toggles fill mode");
					return;
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
					int tempXScale = Integer.parseInt(parameters[i].replace("x", ""));
					if (tempXScale < SCL_MIN || tempXScale > SCL_MAX) {
						toolkitProperties.sendMessage(ChatColor.AQUA + "Invalid X scale (" + SCL_MIN + "-" + SCL_MAX + ")");
						continue;
					}
					this.xscl = tempXScale;
					toolkitProperties.sendMessage(ChatColor.AQUA + "X-scale modifier set to: " + this.xscl);
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
					int tempYScale = Integer.parseInt(parameters[i].replace("y", ""));
					if (tempYScale < SCL_MIN || tempYScale > SCL_MAX) {
						toolkitProperties.sendMessage(ChatColor.AQUA + "Invalid Y scale (" + SCL_MIN + "-" + SCL_MAX + ")");
						continue;
					}
					this.yscl = tempYScale;
					toolkitProperties.sendMessage(ChatColor.AQUA + "Y-scale modifier set to: " + this.yscl);
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 't') {
					int tempSteps = Integer.parseInt(parameters[i].replace("t", ""));
					if (tempSteps < STEPS_MIN || tempSteps > STEPS_MAX) {
						toolkitProperties.sendMessage(ChatColor.AQUA + "Invalid step number (" + STEPS_MIN + "-" + STEPS_MAX + ")");
						continue;
					}
					this.steps = tempSteps;
					toolkitProperties.sendMessage(ChatColor.AQUA + "Render step number set to: " + this.steps);
				} else if (parameter.equalsIgnoreCase("fill")) {
					if (this.fill) {
						this.fill = false;
						toolkitProperties.sendMessage(ChatColor.AQUA + "Fill mode is disabled");
					} else {
						this.fill = true;
						toolkitProperties.sendMessage(ChatColor.AQUA + "Fill mode is enabled");
					}
				} else {
					toolkitProperties.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
				}
			} catch (NumberFormatException exception) {
				toolkitProperties.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.ellipse";
	}
}
