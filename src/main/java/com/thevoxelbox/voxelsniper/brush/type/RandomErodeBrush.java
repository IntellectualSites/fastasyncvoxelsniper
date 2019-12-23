package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Random;

public class RandomErodeBrush extends AbstractBrush {

	private static final double TRUE_CIRCLE = 0.5;

	private BlockWrapper[][][] snap;
	private BlockWrapper[][][] firstSnap;
	private int brushSize;
	private int erodeFace;
	private int fillFace;
	private int erodeRecursion = 1;
	private int fillRecursion = 1;
	private Random generator = new Random();

	@Override
	public void handleArrowAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.brushSize = toolkitProperties.getBrushSize();
		this.snap = new BlockWrapper[0][0][0];
		this.erodeFace = this.generator.nextInt(5) + 1;
		this.fillFace = this.generator.nextInt(3) + 3;
		this.erodeRecursion = this.generator.nextInt(3);
		this.fillRecursion = this.generator.nextInt(3);
		if (this.fillRecursion == 0 && this.erodeRecursion == 0) { // if they are both zero, it will lead to a null pointer exception. Still want to give them a
			// chance to be zero though, for more interestingness -Gav
			this.erodeRecursion = this.generator.nextInt(2) + 1;
			this.fillRecursion = this.generator.nextInt(2) + 1;
		}
		randomErosion(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.brushSize = toolkitProperties.getBrushSize();
		this.snap = new BlockWrapper[0][0][0];
		this.erodeFace = this.generator.nextInt(3) + 3;
		this.fillFace = this.generator.nextInt(5) + 1;
		this.erodeRecursion = this.generator.nextInt(3);
		this.fillRecursion = this.generator.nextInt(3);
		if (this.fillRecursion == 0 && this.erodeRecursion == 0) { // if they are both zero, it will lead to a null pointer exception. Still want to give them a
			// chance to be zero though, for more interestingness -Gav
			this.erodeRecursion = this.generator.nextInt(2) + 1;
			this.fillRecursion = this.generator.nextInt(2) + 1;
		}
		randomFilling(snipe);
	}

	private boolean erode(int x, int y, int z) {
		if (this.snap[x][y][z].isSolid()) {
			int i = 0;
			if (!this.snap[x + 1][y][z].isSolid()) {
				i++;
			}
			if (!this.snap[x - 1][y][z].isSolid()) {
				i++;
			}
			if (!this.snap[x][y + 1][z].isSolid()) {
				i++;
			}
			if (!this.snap[x][y - 1][z].isSolid()) {
				i++;
			}
			if (!this.snap[x][y][z + 1].isSolid()) {
				i++;
			}
			if (!this.snap[x][y][z - 1].isSolid()) {
				i++;
			}
			return (i >= this.erodeFace);
		} else {
			return false;
		}
	}

	private boolean fill(int x, int y, int z) {
		if (this.snap[x][y][z].isSolid()) {
			return false;
		} else {
			int d = 0;
			if (this.snap[x + 1][y][z].isSolid()) {
				Block block = this.snap[x + 1][y][z].getNativeBlock();
				this.snap[x][y][z].setType(block.getType());
				d++;
			}
			if (this.snap[x - 1][y][z].isSolid()) {
				Block block = this.snap[x - 1][y][z].getNativeBlock();
				this.snap[x][y][z].setType(block.getType());
				d++;
			}
			if (this.snap[x][y + 1][z].isSolid()) {
				Block block = this.snap[x][y + 1][z].getNativeBlock();
				this.snap[x][y][z].setType(block.getType());
				d++;
			}
			if (this.snap[x][y - 1][z].isSolid()) {
				Block block = this.snap[x][y - 1][z].getNativeBlock();
				this.snap[x][y][z].setType(block.getType());
				d++;
			}
			if (this.snap[x][y][z + 1].isSolid()) {
				Block block = this.snap[x][y][z + 1].getNativeBlock();
				this.snap[x][y][z].setType(block.getType());
				d++;
			}
			if (this.snap[x][y][z - 1].isSolid()) {
				Block block = this.snap[x][y][z - 1].getNativeBlock();
				this.snap[x][y][z].setType(block.getType());
				d++;
			}
			return (d >= this.fillFace);
		}
	}

	private void getMatrix() {
		int brushSize = (this.brushSize + 1) * 2 + 1;
		Block targetBlock = getTargetBlock();
		if (this.snap.length == 0) {
			setSnap(brushSize, targetBlock);
			this.firstSnap = this.snap.clone();
		} else {
			setSnap(brushSize, targetBlock);
		}
	}

	private void setSnap(int brushSize, Block targetBlock) {
		this.snap = new BlockWrapper[brushSize][brushSize][brushSize];
		int sx = targetBlock.getX() - (this.brushSize + 1);
		for (int x = 0; x < this.snap.length; x++) {
			int sz = targetBlock.getZ() - (this.brushSize + 1);
			for (int z = 0; z < this.snap.length; z++) {
				int sy = targetBlock.getY() - (this.brushSize + 1);
				for (int y = 0; y < this.snap.length; y++) {
					this.snap[x][y][z] = new BlockWrapper(clampY(sx, sy, sz));
					sy++;
				}
				sz++;
			}
			sx++;
		}
	}

	private void randomErosion(Snipe snipe) {
		Undo undo = new Undo();
		if (this.erodeFace >= 0 && this.erodeFace <= 6) {
			for (int currentErodeRecursion = 0; currentErodeRecursion < this.erodeRecursion; currentErodeRecursion++) {
				getMatrix();
				double brushSizeSquared = Math.pow(this.brushSize + TRUE_CIRCLE, 2);
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.brushSize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.brushSize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.brushSize + 1), 2) + zSquared) <= brushSizeSquared)) {
								if (this.erode(x, y, z)) {
									Block block = this.snap[x][y][z].getNativeBlock();
									block.setType(Material.AIR);
								}
							}
						}
					}
				}
			}
		}
		if (this.fillFace >= 0 && this.fillFace <= 6) {
			double brushSizeSquared = Math.pow(this.brushSize + 0.5, 2);
			for (int currentFillRecursion = 0; currentFillRecursion < this.fillRecursion; currentFillRecursion++) {
				this.getMatrix();
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.brushSize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.brushSize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.brushSize + 1), 2) + zSquared) <= brushSizeSquared)) {
								if (this.fill(x, y, z)) {
									Block block = this.snap[x][y][z].getNativeBlock();
									block.setType(this.snap[x][y][z].getType());
								}
							}
						}
					}
				}
			}
		}
		for (BlockWrapper[][] firstSnapSlice : this.firstSnap) {
			for (BlockWrapper[] firstSnapString : firstSnapSlice) {
				for (BlockWrapper block : firstSnapString) {
					Block nativeBlock = block.getNativeBlock();
					if (block.getNativeType() != nativeBlock.getType()) {
						undo.put(nativeBlock);
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	private void randomFilling(Snipe snipe) {
		Undo undo = new Undo();
		if (this.fillFace >= 0 && this.fillFace <= 6) {
			double bSquared = Math.pow(this.brushSize + 0.5, 2);
			for (int currentFillRecursion = 0; currentFillRecursion < this.fillRecursion; currentFillRecursion++) {
				this.getMatrix();
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.brushSize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.brushSize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.brushSize + 1), 2) + zSquared) <= bSquared)) {
								if (this.fill(x, y, z)) {
									Block block = this.snap[x][y][z].getNativeBlock();
									block.setType(this.snap[x][y][z].getType());
								}
							}
						}
					}
				}
			}
		}
		if (this.erodeFace >= 0 && this.erodeFace <= 6) {
			double bSquared = Math.pow(this.brushSize + TRUE_CIRCLE, 2);
			for (int currentErodeRecursion = 0; currentErodeRecursion < this.erodeRecursion; currentErodeRecursion++) {
				this.getMatrix();
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.brushSize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.brushSize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.brushSize + 1), 2) + zSquared) <= bSquared)) {
								if (this.erode(x, y, z)) {
									Block block = this.snap[x][y][z].getNativeBlock();
									block.setType(Material.AIR);
								}
							}
						}
					}
				}
			}
		}
		for (BlockWrapper[][] firstSnapSlice : this.firstSnap) {
			for (BlockWrapper[] firstSnapString : firstSnapSlice) {
				for (BlockWrapper block : firstSnapString) {
					Block nativeBlock = block.getNativeBlock();
					if (block.getNativeType() != nativeBlock.getType()) {
						undo.put(nativeBlock);
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
	}

	private static final class BlockWrapper {

		private Block nativeBlock;
		private Material nativeType;
		private boolean solid;
		private Material type;

		private BlockWrapper(Block block) {
			this.nativeBlock = block;
			this.nativeType = block.getType();
			this.solid = !Materials.isEmpty(this.nativeType) && this.nativeType != Material.WATER && this.nativeType != Material.LAVA;
		}

		public Block getNativeBlock() {
			return this.nativeBlock;
		}

		public Material getNativeType() {
			return this.nativeType;
		}

		public boolean isSolid() {
			return this.solid;
		}

		public Material getType() {
			return this.type;
		}

		public void setType(Material type) {
			this.type = type;
		}
	}
}
