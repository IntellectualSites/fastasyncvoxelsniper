package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
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

public class TreeSnipeBrush extends AbstractBrush {

	private TreeType treeType = TreeType.TREE;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(ChatColor.GOLD + "Tree snipe brush:");
				messenger.sendMessage(ChatColor.AQUA + "/b t treetype");
				printTreeType(messenger);
				return;
			}
			try {
				this.treeType = TreeType.valueOf(parameter.toUpperCase());
				printTreeType(messenger);
			} catch (IllegalArgumentException exception) {
				messenger.sendMessage(ChatColor.LIGHT_PURPLE + "No such tree type.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock().getRelative(0, getYOffset(), 0);
		single(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		single(snipe, getTargetBlock());
	}

	private void single(Snipe snipe, Block targetBlock) {
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
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
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

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		printTreeType(messenger);
	}

	private void printTreeType(SnipeMessenger messenger) {
		String printout = Arrays.stream(TreeType.values())
			.map(treeType -> ((treeType == this.treeType) ? ChatColor.GRAY + treeType.name()
				.toLowerCase() : ChatColor.DARK_GRAY + treeType.name()
				.toLowerCase()) + ChatColor.WHITE)
			.collect(Collectors.joining(", "));
		messenger.sendMessage(printout);
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
