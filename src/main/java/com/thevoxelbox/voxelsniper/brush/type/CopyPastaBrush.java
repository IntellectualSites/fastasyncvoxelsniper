package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.stream.Stream;

public class CopyPastaBrush extends AbstractBrush {

	private static final int BLOCK_LIMIT = 10000;

	private boolean pasteAir = true; // False = no air, true = air
	private int points; //
	private int numBlocks;
	private int[] firstPoint = new int[3];
	private int[] secondPoint = new int[3];
	private int[] pastePoint = new int[3];
	private int[] minPoint = new int[3];
	private int[] offsetPoint = new int[3];
	private Material[] blockArray;
	private BlockData[] dataArray;
	private int[] arraySize = new int[3];
	private int pivot; // ccw degrees

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		String parameter = parameters[0];
		if (parameter.equalsIgnoreCase("info")) {
			snipe.createMessageSender()
				.message(ChatColor.GOLD + "CopyPasta Parameters:")
				.message(ChatColor.AQUA + "/b cp air -- toggle include (default) or exclude  air during paste")
				.message(ChatColor.AQUA + "/b cp 0|90|180|270 -- toggle rotation (0 default)")
				.send();
			return;
		}
		if (parameter.equalsIgnoreCase("air")) {
			this.pasteAir = !this.pasteAir;
			messenger.sendMessage(ChatColor.GOLD + "Paste air: " + this.pasteAir);
			return;
		}
		if (Stream.of("90", "180", "270", "0")
			.anyMatch(parameter::equalsIgnoreCase)) {
			this.pivot = Integer.parseInt(parameter);
			messenger.sendMessage(ChatColor.GOLD + "Pivot angle: " + this.pivot);
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		Block targetBlock = getTargetBlock();
		if (this.points == 0) {
			this.firstPoint[0] = targetBlock.getX();
			this.firstPoint[1] = targetBlock.getY();
			this.firstPoint[2] = targetBlock.getZ();
			messenger.sendMessage(ChatColor.GRAY + "First point");
			this.points = 1;
		} else if (this.points == 1) {
			this.secondPoint[0] = targetBlock.getX();
			this.secondPoint[1] = targetBlock.getY();
			this.secondPoint[2] = targetBlock.getZ();
			messenger.sendMessage(ChatColor.GRAY + "Second point");
			this.points = 2;
		} else {
			this.firstPoint = new int[3];
			this.secondPoint = new int[3];
			this.numBlocks = 0;
			this.blockArray = new Material[1];
			this.dataArray = new BlockData[1];
			this.points = 0;
			messenger.sendMessage(ChatColor.GRAY + "Points cleared.");
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (this.points == 2) {
			if (this.numBlocks == 0) {
				doCopy(snipe);
			} else if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
				Block targetBlock = this.getTargetBlock();
				this.pastePoint[0] = targetBlock.getX();
				this.pastePoint[1] = targetBlock.getY();
				this.pastePoint[2] = targetBlock.getZ();
				doPasta(snipe);
			} else {
				messenger.sendMessage(ChatColor.RED + "Error");
			}
		} else {
			messenger.sendMessage(ChatColor.RED + "You must select exactly two points.");
		}
	}

	private void doCopy(Snipe snipe) {
		for (int i = 0; i < 3; i++) {
			this.arraySize[i] = Math.abs(this.firstPoint[i] - this.secondPoint[i]) + 1;
			this.minPoint[i] = Math.min(this.firstPoint[i], this.secondPoint[i]);
			this.offsetPoint[i] = this.minPoint[i] - this.firstPoint[i]; // will always be negative or zero
		}
		this.numBlocks = (this.arraySize[0]) * (this.arraySize[1]) * (this.arraySize[2]);
		SnipeMessenger messenger = snipe.createMessenger();
		if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
			this.blockArray = new Material[this.numBlocks];
			this.dataArray = new BlockData[this.numBlocks];
			for (int i = 0; i < this.arraySize[0]; i++) {
				for (int j = 0; j < this.arraySize[1]; j++) {
					for (int k = 0; k < this.arraySize[2]; k++) {
						int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
						World world = getWorld();
						Block block = world.getBlockAt(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
						this.blockArray[currentPosition] = block.getType();
						Block clamp = this.clampY(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
						this.dataArray[currentPosition] = clamp.getBlockData();
					}
				}
			}
			messenger.sendMessage(ChatColor.AQUA + String.valueOf(this.numBlocks) + " blocks copied.");
		} else {
			messenger.sendMessage(ChatColor.RED + "Copy area too big: " + this.numBlocks + "(Limit: " + BLOCK_LIMIT + ")");
		}
	}

	private void doPasta(Snipe snipe) {
		Undo undo = new Undo();
		for (int i = 0; i < this.arraySize[0]; i++) {
			for (int j = 0; j < this.arraySize[1]; j++) {
				for (int k = 0; k < this.arraySize[2]; k++) {
					int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
					Block block = switch (this.pivot) {
						case 180 -> clampY(this.pastePoint[0] - this.offsetPoint[0] - i, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] - this.offsetPoint[2] - k);
						case 270 -> clampY(this.pastePoint[0] + this.offsetPoint[2] + k, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] - this.offsetPoint[0] - i);
						case 90 -> clampY(this.pastePoint[0] - this.offsetPoint[2] - k, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] + this.offsetPoint[0] + i);
						default -> // assume no rotation
							clampY(this.pastePoint[0] + this.offsetPoint[0] + i, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] + this.offsetPoint[2] + k);
					};
					if (!(Materials.isEmpty(this.blockArray[currentPosition]) && !this.pasteAir)) {
						BlockData blockData = block.getBlockData();
						if (block.getType() != this.blockArray[currentPosition] || !blockData.equals(this.dataArray[currentPosition])) {
							undo.put(block);
						}
						block.setBlockData(this.dataArray[currentPosition]);
					}
				}
			}
		}
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage(ChatColor.AQUA + String.valueOf(this.numBlocks) + " blocks pasted.");
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.message(ChatColor.GOLD + "Paste air: " + this.pasteAir)
			.message(ChatColor.GOLD + "Pivot angle: " + this.pivot)
			.send();
	}
}
