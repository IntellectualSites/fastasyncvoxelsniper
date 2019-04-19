package com.thevoxelbox.voxelsniper.brush;

import java.util.Random;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.block.Block;

/**
 * http://www.voxelwiki.com/minecraft/Voxelsniper#The_Random-Erode_Brush
 *
 * @author Piotr
 * @author Giltwist (Randomized blockPositionY)
 */
public class RandomErodeBrush extends AbstractBrush {

	private static final double TRUE_CIRCLE = 0.5;
	private BlockWrapper[][][] snap;
	private BlockWrapper[][][] firstSnap;
	private int bsize;
	private int erodeFace;
	private int fillFace;
	private int brushSize;
	private int erodeRecursion = 1;
	private int fillRecursion = 1;
	private Random generator = new Random();

	/**
	 *
	 */
	public RandomErodeBrush() {
		this.setName("RandomErode");
	}

	private boolean erode(int x, int y, int z) {
		if (this.snap[x][y][z].isSolid()) {
			int d = 0;
			if (!this.snap[x + 1][y][z].isSolid()) {
				d++;
			}
			if (!this.snap[x - 1][y][z].isSolid()) {
				d++;
			}
			if (!this.snap[x][y + 1][z].isSolid()) {
				d++;
			}
			if (!this.snap[x][y - 1][z].isSolid()) {
				d++;
			}
			if (!this.snap[x][y][z + 1].isSolid()) {
				d++;
			}
			if (!this.snap[x][y][z - 1].isSolid()) {
				d++;
			}
			return (d >= this.erodeFace);
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private boolean fill(int x, int y, int z) {
		if (this.snap[x][y][z].isSolid()) {
			return false;
		} else {
			int d = 0;
			if (this.snap[x + 1][y][z].isSolid()) {
				this.snap[x][y][z].setId(this.snap[x + 1][y][z].getNativeBlock()
					.getTypeId());
				d++;
			}
			if (this.snap[x - 1][y][z].isSolid()) {
				this.snap[x][y][z].setId(this.snap[x - 1][y][z].getNativeBlock()
					.getTypeId());
				d++;
			}
			if (this.snap[x][y + 1][z].isSolid()) {
				this.snap[x][y][z].setId(this.snap[x][y + 1][z].getNativeBlock()
					.getTypeId());
				d++;
			}
			if (this.snap[x][y - 1][z].isSolid()) {
				this.snap[x][y][z].setId(this.snap[x][y - 1][z].getNativeBlock()
					.getTypeId());
				d++;
			}
			if (this.snap[x][y][z + 1].isSolid()) {
				this.snap[x][y][z].setId(this.snap[x][y][z + 1].getNativeBlock()
					.getTypeId());
				d++;
			}
			if (this.snap[x][y][z - 1].isSolid()) {
				this.snap[x][y][z].setId(this.snap[x][y][z - 1].getNativeBlock()
					.getTypeId());
				d++;
			}
			return (d >= this.fillFace);
		}
	}

	private void getMatrix() {
		this.brushSize = ((this.bsize + 1) * 2) + 1;
		if (this.snap.length == 0) {
			this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];
			int sx = this.getTargetBlock()
				.getX() - (this.bsize + 1);
			int sy = this.getTargetBlock()
				.getY() - (this.bsize + 1);
			int sz = this.getTargetBlock()
				.getZ() - (this.bsize + 1);
			for (int x = 0; x < this.snap.length; x++) {
				sz = this.getTargetBlock()
					.getZ() - (this.bsize + 1);
				for (int z = 0; z < this.snap.length; z++) {
					sy = this.getTargetBlock()
						.getY() - (this.bsize + 1);
					for (int y = 0; y < this.snap.length; y++) {
						this.snap[x][y][z] = new BlockWrapper(this.clampY(sx, sy, sz));
						sy++;
					}
					sz++;
				}
				sx++;
			}
			this.firstSnap = this.snap.clone();
		} else {
			this.snap = new BlockWrapper[this.brushSize][this.brushSize][this.brushSize];
			int sx = this.getTargetBlock()
				.getX() - (this.bsize + 1);
			int sy = this.getTargetBlock()
				.getY() - (this.bsize + 1);
			int sz = this.getTargetBlock()
				.getZ() - (this.bsize + 1);
			for (int x = 0; x < this.snap.length; x++) {
				sz = this.getTargetBlock()
					.getZ() - (this.bsize + 1);
				for (int z = 0; z < this.snap.length; z++) {
					sy = this.getTargetBlock()
						.getY() - (this.bsize + 1);
					for (int y = 0; y < this.snap.length; y++) {
						this.snap[x][y][z] = new BlockWrapper(this.clampY(sx, sy, sz));
						sy++;
					}
					sz++;
				}
				sx++;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void rerosion(SnipeData v) {
		Undo undo = new Undo();
		if (this.erodeFace >= 0 && this.erodeFace <= 6) {
			for (int currentErodeRecursion = 0; currentErodeRecursion < this.erodeRecursion; currentErodeRecursion++) {
				this.getMatrix();
				double brushSizeSquared = Math.pow(this.bsize + TRUE_CIRCLE, 2);
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.bsize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.bsize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.bsize + 1), 2) + zSquared) <= brushSizeSquared)) {
								if (this.erode(x, y, z)) {
									this.snap[x][y][z].getNativeBlock()
										.setTypeId(0);
								}
							}
						}
					}
				}
			}
		}
		if (this.fillFace >= 0 && this.fillFace <= 6) {
			double brushSizeSquared = Math.pow(this.bsize + 0.5, 2);
			for (int currentFillRecursion = 0; currentFillRecursion < this.fillRecursion; currentFillRecursion++) {
				this.getMatrix();
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.bsize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.bsize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.bsize + 1), 2) + zSquared) <= brushSizeSquared)) {
								if (this.fill(x, y, z)) {
									this.snap[x][y][z].getNativeBlock()
										.setTypeId(this.snap[x][y][z].getId());
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
					if (block.getI() != block.getNativeBlock()
						.getTypeId()) {
						undo.put(block.getNativeBlock());
					}
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@SuppressWarnings("deprecation")
	private void rfilling(SnipeData v) {
		Undo undo = new Undo();
		if (this.fillFace >= 0 && this.fillFace <= 6) {
			double bSquared = Math.pow(this.bsize + 0.5, 2);
			for (int currentFillRecursion = 0; currentFillRecursion < this.fillRecursion; currentFillRecursion++) {
				this.getMatrix();
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.bsize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.bsize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.bsize + 1), 2) + zSquared) <= bSquared)) {
								if (this.fill(x, y, z)) {
									this.snap[x][y][z].getNativeBlock()
										.setTypeId(this.snap[x][y][z].getId());
								}
							}
						}
					}
				}
			}
		}
		if (this.erodeFace >= 0 && this.erodeFace <= 6) {
			double bSquared = Math.pow(this.bsize + TRUE_CIRCLE, 2);
			for (int currentErodeRecursion = 0; currentErodeRecursion < this.erodeRecursion; currentErodeRecursion++) {
				this.getMatrix();
				for (int z = 1; z < this.snap.length - 1; z++) {
					double zSquared = Math.pow(z - (this.bsize + 1), 2);
					for (int x = 1; x < this.snap.length - 1; x++) {
						double xSquared = Math.pow(x - (this.bsize + 1), 2);
						for (int y = 1; y < this.snap.length - 1; y++) {
							if (((xSquared + Math.pow(y - (this.bsize + 1), 2) + zSquared) <= bSquared)) {
								if (this.erode(x, y, z)) {
									this.snap[x][y][z].getNativeBlock()
										.setTypeId(0);
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
					if (block.getI() != block.getNativeBlock()
						.getTypeId()) {
						undo.put(block.getNativeBlock());
					}
				}
			}
		}
		v.owner()
			.storeUndo(undo);
	}

	@Override
	protected final void arrow(SnipeData v) {
		this.bsize = v.getBrushSize();
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
		this.rerosion(v);
	}

	@Override
	protected final void powder(SnipeData v) {
		this.bsize = v.getBrushSize();
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
		this.rfilling(v);
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
	}

	/**
	 * @author unknown
	 */
	private class BlockWrapper {

		private boolean solid;
		private Block nativeBlock;
		private int id;
		private int i;

		/**
		 *
		 */
		@SuppressWarnings("deprecation")
		private BlockWrapper(Block bl) {
			this.nativeBlock = bl;
			this.i = bl.getTypeId();
			switch (bl.getType()) {
				case AIR:
				case WATER:
				case STATIONARY_WATER:
				case STATIONARY_LAVA:
				case LAVA:
					this.solid = false;
					break;
				default:
					this.solid = true;
			}
		}

		public boolean isSolid() {
			return this.solid;
		}

		public void setSolid(boolean solid) {
			this.solid = solid;
		}

		public Block getNativeBlock() {
			return this.nativeBlock;
		}

		public void setNativeBlock(Block nativeBlock) {
			this.nativeBlock = nativeBlock;
		}

		public int getId() {
			return this.id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getI() {
			return this.i;
		}

		public void setI(int i) {
			this.i = i;
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.randomerode";
	}
}
