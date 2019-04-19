package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

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
	private int[] blockArray;
	private byte[] dataArray;
	private int[] arraySize = new int[3];
	private int pivot; // ccw degrees

	/**
	 *
	 */
	public CopyPastaBrush() {
		this.setName("CopyPasta");
	}

	@SuppressWarnings("deprecation")
	private void doCopy(SnipeData v) {
		for (int i = 0; i < 3; i++) {
			this.arraySize[i] = Math.abs(this.firstPoint[i] - this.secondPoint[i]) + 1;
			this.minPoint[i] = Math.min(this.firstPoint[i], this.secondPoint[i]);
			this.offsetPoint[i] = this.minPoint[i] - this.firstPoint[i]; // will always be negative or zero
		}
		this.numBlocks = (this.arraySize[0]) * (this.arraySize[1]) * (this.arraySize[2]);
		if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
			this.blockArray = new int[this.numBlocks];
			this.dataArray = new byte[this.numBlocks];
			for (int i = 0; i < this.arraySize[0]; i++) {
				for (int j = 0; j < this.arraySize[1]; j++) {
					for (int k = 0; k < this.arraySize[2]; k++) {
						int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
						this.blockArray[currentPosition] = this.getWorld()
							.getBlockTypeIdAt(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
						this.dataArray[currentPosition] = this.clampY(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k)
							.getData();
					}
				}
			}
			v.sendMessage(ChatColor.AQUA + "" + this.numBlocks + " blocks copied.");
		} else {
			v.sendMessage(ChatColor.RED + "Copy area too big: " + this.numBlocks + "(Limit: " + BLOCK_LIMIT + ")");
		}
	}

	@SuppressWarnings("deprecation")
	private void doPasta(SnipeData v) {
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
					if (!(this.blockArray[currentPosition] == 0 && !this.pasteAir)) {
						if (block.getTypeId() != this.blockArray[currentPosition] || block.getData() != this.dataArray[currentPosition]) {
							undo.put(block);
						}
						block.setTypeIdAndData(this.blockArray[currentPosition], this.dataArray[currentPosition], true);
					}
				}
			}
		}
		v.sendMessage(ChatColor.AQUA + "" + this.numBlocks + " blocks pasted.");
		v.getOwner()
			.storeUndo(undo);
	}

	@Override
	protected final void arrow(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		switch (this.points) {
			case 0:
				this.firstPoint[0] = this.getTargetBlock()
					.getX();
				this.firstPoint[1] = this.getTargetBlock()
					.getY();
				this.firstPoint[2] = this.getTargetBlock()
					.getZ();
				snipeData.sendMessage(ChatColor.GRAY + "First point");
				this.points = 1;
				break;
			case 1:
				this.secondPoint[0] = this.getTargetBlock()
					.getX();
				this.secondPoint[1] = this.getTargetBlock()
					.getY();
				this.secondPoint[2] = this.getTargetBlock()
					.getZ();
				snipeData.sendMessage(ChatColor.GRAY + "Second point");
				this.points = 2;
				break;
			default:
				this.firstPoint = new int[3];
				this.secondPoint = new int[3];
				this.numBlocks = 0;
				this.blockArray = new int[1];
				this.dataArray = new byte[1];
				this.points = 0;
				snipeData.sendMessage(ChatColor.GRAY + "Points cleared.");
				break;
		}
	}

	@Override
	protected final void powder(com.thevoxelbox.voxelsniper.SnipeData snipeData) {
		if (this.points == 2) {
			if (this.numBlocks == 0) {
				this.doCopy(snipeData);
			} else if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
				this.pastePoint[0] = this.getTargetBlock()
					.getX();
				this.pastePoint[1] = this.getTargetBlock()
					.getY();
				this.pastePoint[2] = this.getTargetBlock()
					.getZ();
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
		if (parameter.equalsIgnoreCase("90") || parameter.equalsIgnoreCase("180") || parameter.equalsIgnoreCase("270") || parameter.equalsIgnoreCase("0")) {
			this.pivot = Integer.parseInt(parameter);
			snipeData.sendMessage(ChatColor.GOLD + "Pivot angle: " + this.pivot);
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.copypasta";
	}
}
