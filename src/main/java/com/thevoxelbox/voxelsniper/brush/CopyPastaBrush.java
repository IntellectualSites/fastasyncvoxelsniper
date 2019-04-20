package com.thevoxelbox.voxelsniper.brush;

import java.util.stream.Stream;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#CopyPasta_Brush
 *
 * @author giltwist
 */
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

	/**
	 *
	 */
	public CopyPastaBrush() {
		super("CopyPasta");
	}

	private void doCopy(SnipeData snipeData) {
		for (int i = 0; i < 3; i++) {
			this.arraySize[i] = Math.abs(this.firstPoint[i] - this.secondPoint[i]) + 1;
			this.minPoint[i] = Math.min(this.firstPoint[i], this.secondPoint[i]);
			this.offsetPoint[i] = this.minPoint[i] - this.firstPoint[i]; // will always be negative or zero
		}
		this.numBlocks = (this.arraySize[0]) * (this.arraySize[1]) * (this.arraySize[2]);
		if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
			this.blockArray = new Material[this.numBlocks];
			this.dataArray = new BlockData[this.numBlocks];
			for (int i = 0; i < this.arraySize[0]; i++) {
				for (int j = 0; j < this.arraySize[1]; j++) {
					for (int k = 0; k < this.arraySize[2]; k++) {
						int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
						World world = this.getWorld();
						Block block = world.getBlockAt(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
						this.blockArray[currentPosition] = block.getType();
						Block clamp = this.clampY(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
						this.dataArray[currentPosition] = clamp.getBlockData();
					}
				}
			}
			snipeData.sendMessage(ChatColor.AQUA + String.valueOf(this.numBlocks) + " blocks copied.");
		} else {
			snipeData.sendMessage(ChatColor.RED + "Copy area too big: " + this.numBlocks + "(Limit: " + BLOCK_LIMIT + ")");
		}
	}

	private void doPasta(SnipeData snipeData) {
		Undo undo = new Undo();
		for (int i = 0; i < this.arraySize[0]; i++) {
			for (int j = 0; j < this.arraySize[1]; j++) {
				for (int k = 0; k < this.arraySize[2]; k++) {
					int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
					Block block;
					switch (this.pivot) {
						case 180:
							block = this.clampY(this.pastePoint[0] - this.offsetPoint[0] - i, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] - this.offsetPoint[2] - k);
							break;
						case 270:
							block = this.clampY(this.pastePoint[0] + this.offsetPoint[2] + k, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] - this.offsetPoint[0] - i);
							break;
						case 90:
							block = this.clampY(this.pastePoint[0] - this.offsetPoint[2] - k, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] + this.offsetPoint[0] + i);
							break;
						default: // assume no rotation
							block = this.clampY(this.pastePoint[0] + this.offsetPoint[0] + i, this.pastePoint[1] + this.offsetPoint[1] + j, this.pastePoint[2] + this.offsetPoint[2] + k);
							break;
					}
					if (!(this.blockArray[currentPosition].isEmpty() && !this.pasteAir)) {
						BlockData blockData = block.getBlockData();
						if (block.getType() != this.blockArray[currentPosition] || !blockData.equals(this.dataArray[currentPosition])) {
							undo.put(block);
						}
						block.setBlockData(this.dataArray[currentPosition]);
					}
				}
			}
		}
		snipeData.sendMessage(ChatColor.AQUA + String.valueOf(this.numBlocks) + " blocks pasted.");
		Sniper owner = snipeData.getOwner();
		owner.storeUndo(undo);
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		Block targetBlock = this.getTargetBlock();
		if (this.points == 0) {
			this.firstPoint[0] = targetBlock.getX();
			this.firstPoint[1] = targetBlock.getY();
			this.firstPoint[2] = targetBlock.getZ();
			snipeData.sendMessage(ChatColor.GRAY + "First point");
			this.points = 1;
		} else if (this.points == 1) {
			this.secondPoint[0] = targetBlock.getX();
			this.secondPoint[1] = targetBlock.getY();
			this.secondPoint[2] = targetBlock.getZ();
			snipeData.sendMessage(ChatColor.GRAY + "Second point");
			this.points = 2;
		} else {
			this.firstPoint = new int[3];
			this.secondPoint = new int[3];
			this.numBlocks = 0;
			this.blockArray = new Material[1];
			this.dataArray = new BlockData[1];
			this.points = 0;
			snipeData.sendMessage(ChatColor.GRAY + "Points cleared.");
		}
	}

	@Override
	public final void powder(SnipeData snipeData) {
		if (this.points == 2) {
			if (this.numBlocks == 0) {
				this.doCopy(snipeData);
			} else if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
				Block targetBlock = this.getTargetBlock();
				this.pastePoint[0] = targetBlock.getX();
				this.pastePoint[1] = targetBlock.getY();
				this.pastePoint[2] = targetBlock.getZ();
				this.doPasta(snipeData);
			} else {
				snipeData.sendMessage(ChatColor.RED + "Error");
			}
		} else {
			snipeData.sendMessage(ChatColor.RED + "You must select exactly two points.");
		}
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.custom(ChatColor.GOLD + "Paste air: " + this.pasteAir);
		message.custom(ChatColor.GOLD + "Pivot angle: " + this.pivot);
	}

	@Override
	public final void parameters(String[] parameters, com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		String parameter = parameters[1];
		if (parameter.equalsIgnoreCase("info")) {
			snipeData.sendMessage(ChatColor.GOLD + "CopyPasta Parameters:");
			snipeData.sendMessage(ChatColor.AQUA + "/b cp air -- toggle include (default) or exclude  air during paste");
			snipeData.sendMessage(ChatColor.AQUA + "/b cp 0|90|180|270 -- toggle rotation (0 default)");
			return;
		}
		if (parameter.equalsIgnoreCase("air")) {
			this.pasteAir = !this.pasteAir;
			snipeData.sendMessage(ChatColor.GOLD + "Paste air: " + this.pasteAir);
			return;
		}
		if (Stream.of("90", "180", "270", "0")
			.anyMatch(parameter::equalsIgnoreCase)) {
			this.pivot = Integer.parseInt(parameter);
			snipeData.sendMessage(ChatColor.GOLD + "Pivot angle: " + this.pivot);
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.copypasta";
	}
}
