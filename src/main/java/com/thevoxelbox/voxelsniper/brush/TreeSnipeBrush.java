package com.thevoxelbox.voxelsniper.brush;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.util.UndoDelegate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Tree_Brush
 *
 * @author Mick
 */
public class TreeSnipeBrush extends AbstractBrush {

	private TreeType treeType = TreeType.TREE;


	public TreeSnipeBrush() {
		super("Tree Snipe");
	}

	private void single(SnipeData snipeData, Block targetBlock) {
		UndoDelegate undoDelegate = new UndoDelegate(targetBlock.getWorld());
		Block blockBelow = targetBlock.getRelative(BlockFace.DOWN);
		BlockState currentState = blockBelow.getState();
		undoDelegate.setBlock(blockBelow);
		blockBelow.setType(Material.GRASS_BLOCK);
		this.getWorld()
			.generateTree(targetBlock.getLocation(), this.treeType, undoDelegate);
		Undo undo = undoDelegate.getUndo();
		blockBelow.setBlockData(currentState.getBlockData());
		undo.put(blockBelow);
		snipeData.getOwner()
			.storeUndo(undo);
	}

	private int getYOffset() {
		Block targetBlock = getTargetBlock();
		return IntStream.range(1, (targetBlock.getWorld()
			.getMaxHeight() - 1 - targetBlock.getY()))
			.filter(i -> targetBlock.getRelative(0, i + 1, 0)
				.isEmpty())
			.findFirst()
			.orElse(0);
	}

	private void printTreeType(Message message) {
		String printout = Arrays.stream(TreeType.values())
			.map(treeType -> ((treeType == this.treeType) ? ChatColor.GRAY + treeType.name()
				.toLowerCase() : ChatColor.DARK_GRAY + treeType.name()
				.toLowerCase()) + ChatColor.WHITE)
			.collect(Collectors.joining(", "));
		message.custom(printout);
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		Block targetBlock = getTargetBlock().getRelative(0, getYOffset(), 0);
		this.single(snipeData, targetBlock);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.single(snipeData, getTargetBlock());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		this.printTreeType(message);
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		for (int i = 1; i < parameters.length; i++) {
			if (parameters[i].equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
				snipeData.sendMessage(ChatColor.AQUA + "/b t treetype");
				this.printTreeType(snipeData.getMessage());
				return;
			}
			try {
				this.treeType = TreeType.valueOf(parameters[i].toUpperCase());
				this.printTreeType(snipeData.getMessage());
			} catch (IllegalArgumentException exception) {
				snipeData.getMessage()
					.brushMessage("No such tree type.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.treesnipe";
	}
}
