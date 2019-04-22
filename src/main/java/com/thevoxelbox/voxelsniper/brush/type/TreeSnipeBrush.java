package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

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
		World world = getWorld();
		world.generateTree(targetBlock.getLocation(), this.treeType, undoDelegate);
		Undo undo = undoDelegate.getUndo();
		blockBelow.setBlockData(currentState.getBlockData());
		undo.put(blockBelow);
		snipeData.getOwner()
			.storeUndo(undo);
	}

	private int getYOffset() {
		Block targetBlock = getTargetBlock();
		World world = targetBlock.getWorld();
		return IntStream.range(1, (world.getMaxHeight() - 1 - targetBlock.getY()))
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
			String parameter = parameters[i];
			Message message = snipeData.getMessage();
			if (parameter.equalsIgnoreCase("info")) {
				snipeData.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
				snipeData.sendMessage(ChatColor.AQUA + "/b t treetype");
				this.printTreeType(message);
				return;
			}
			try {
				this.treeType = TreeType.valueOf(parameter.toUpperCase());
				this.printTreeType(message);
			} catch (IllegalArgumentException exception) {
				message.brushMessage("No such tree type.");
			}
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.treesnipe";
	}

	private static final class UndoDelegate implements BlockChangeDelegate {

		private World targetWorld;
		private Undo currentUndo;

		private UndoDelegate(World targetWorld) {
			this.targetWorld = targetWorld;
			this.currentUndo = new Undo();
		}

		public Undo getUndo() {
			Undo pastUndo = this.currentUndo;
			this.currentUndo = new Undo();
			return pastUndo;
		}

		public void setBlock(Block block) {
			Location location = block.getLocation();
			Block blockAtLocation = this.targetWorld.getBlockAt(location);
			this.currentUndo.put(blockAtLocation);
			BlockData blockData = block.getBlockData();
			blockAtLocation.setBlockData(blockData);
		}

		@Override
		public boolean setBlockData(int x, int y, int z, @NotNull BlockData blockData) {
			Block block = this.targetWorld.getBlockAt(x, y, z);
			this.currentUndo.put(block);
			block.setBlockData(blockData);
			return true;
		}

		@NotNull
		@Override
		public BlockData getBlockData(int x, int y, int z) {
			Block block = this.targetWorld.getBlockAt(x, y, z);
			return block.getBlockData();
		}

		@Override
		public int getHeight() {
			return this.targetWorld.getMaxHeight();
		}

		@Override
		public boolean isEmpty(int x, int y, int z) {
			Block block = this.targetWorld.getBlockAt(x, y, z);
			return block.isEmpty();
		}
	}
}
