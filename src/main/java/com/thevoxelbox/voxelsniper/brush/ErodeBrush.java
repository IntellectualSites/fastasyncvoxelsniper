package com.thevoxelbox.voxelsniper.brush;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Objects;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Undo;
import com.thevoxelbox.voxelsniper.jsap.HelpJSAP;
import com.thevoxelbox.voxelsniper.jsap.NullableIntegerStringParser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.Vector;

/**
 * http://www.voxelwiki.com/minecraft/VoxelSniper#The_Erosion_Brush
 *
 * @author Piotr
 * @author MikeMatrix
 */
public class ErodeBrush extends AbstractBrush {

	private static final Vector[] FACES_TO_CHECK = {new Vector(0, 0, 1), new Vector(0, 0, -1), new Vector(0, 1, 0), new Vector(0, -1, 0), new Vector(1, 0, 0), new Vector(-1, 0, 0)};
	private final HelpJSAP parser = new HelpJSAP("/b e", "Brush for eroding landscape.", ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH);
	private ErosionPreset currentPreset = new ErosionPreset(0, 1, 0, 1);

	/**
	 *
	 */
	public ErodeBrush() {
		this.setName("Erode");
		try {
			this.parser.registerParameter(new UnflaggedOption("preset", EnumeratedStringParser.getParser(Preset.getValuesString(";"), false), null, false, false, "Preset options: " + Preset.getValuesString(", ")));
			this.parser.registerParameter(new FlaggedOption("fill", NullableIntegerStringParser.getParser(), null, false, 'f', "fill", "Surrounding blocks required to fill the block."));
			this.parser.registerParameter(new FlaggedOption("erode", NullableIntegerStringParser.getParser(), null, false, 'e', "erode", "Surrounding air required to erode the block."));
			this.parser.registerParameter(new FlaggedOption("fillrecursion", NullableIntegerStringParser.getParser(), null, false, 'F', "fillrecursion", "Repeated fill iterations."));
			this.parser.registerParameter(new FlaggedOption("eroderecursion", NullableIntegerStringParser.getParser(), null, false, 'E', "eroderecursion", "Repeated erode iterations."));
		} catch (JSAPException ignored) {
		}
	}

	/**
	 * @return if a message was sent.
	 */
	public static boolean sendHelpOrErrorMessageToPlayer(JSAPResult result, Player player, HelpJSAP helpJSAP) {
		List<String> output = helpJSAP.writeHelpOrErrorMessageIfRequired(result);
		if (!output.isEmpty()) {
			for (String string : output) {
				player.sendMessage(string);
			}
			return true;
		}
		return false;
	}

	@Override
	protected final void arrow(SnipeData snipeData) {
		this.erosion(snipeData, this.currentPreset);
	}

	private void erosion(SnipeData v, ErosionPreset erosionPreset) {
		BlockChangeTracker blockChangeTracker = new BlockChangeTracker(this.getTargetBlock()
			.getWorld());
		Vector targetBlockVector = this.getTargetBlock()
			.getLocation()
			.toVector();
		for (int i = 0; i < erosionPreset.getErosionRecursion(); ++i) {
			erosionIteration(v, erosionPreset, blockChangeTracker, targetBlockVector);
		}
		for (int i = 0; i < erosionPreset.getFillRecursion(); ++i) {
			fillIteration(v, erosionPreset, blockChangeTracker, targetBlockVector);
		}
		Undo undo = new Undo();
		for (BlockWrapper blockWrapper : blockChangeTracker.getAll()) {
			undo.put(blockWrapper.getBlock());
			blockWrapper.getBlock()
				.setTypeIdAndData(blockWrapper.getMaterial()
					.getId(), blockWrapper.getData(), true);
		}
		v.getOwner()
			.storeUndo(undo);
	}

	private void fillIteration(SnipeData v, ErosionPreset erosionPreset, BlockChangeTracker blockChangeTracker, Vector targetBlockVector) {
		int currentIteration = blockChangeTracker.nextIteration();
		for (int x = this.getTargetBlock()
			.getX() - v.getBrushSize(); x <= this.getTargetBlock()
			.getX() + v.getBrushSize(); ++x) {
			for (int z = this.getTargetBlock()
				.getZ() - v.getBrushSize(); z <= this.getTargetBlock()
				.getZ() + v.getBrushSize(); ++z) {
				for (int y = this.getTargetBlock()
					.getY() - v.getBrushSize(); y <= this.getTargetBlock()
					.getY() + v.getBrushSize(); ++y) {
					Vector currentPosition = new Vector(x, y, z);
					if (currentPosition.isInSphere(targetBlockVector, v.getBrushSize())) {
						BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
						if (!(currentBlock.isEmpty() || currentBlock.isLiquid())) {
							continue;
						}
						int count = 0;
						Map<BlockWrapper, Integer> blockCount = new HashMap<>();
						for (Vector vector : FACES_TO_CHECK) {
							Vector relativePosition = currentPosition.clone()
								.add(vector);
							BlockWrapper relativeBlock = blockChangeTracker.get(relativePosition, currentIteration);
							if (!(relativeBlock.isEmpty() || relativeBlock.isLiquid())) {
								count++;
								BlockWrapper typeBlock = new BlockWrapper(null, relativeBlock.getMaterial(), relativeBlock.getData());
								if (blockCount.containsKey(typeBlock)) {
									blockCount.put(typeBlock, blockCount.get(typeBlock) + 1);
								} else {
									blockCount.put(typeBlock, 1);
								}
							}
						}
						BlockWrapper currentMaterial = new BlockWrapper(null, Material.AIR, (byte) 0);
						int amount = 0;
						for (BlockWrapper wrapper : blockCount.keySet()) {
							Integer currentCount = blockCount.get(wrapper);
							if (amount <= currentCount) {
								currentMaterial = wrapper;
								amount = currentCount;
							}
						}
						if (count >= erosionPreset.getFillFaces()) {
							blockChangeTracker.put(currentPosition, new BlockWrapper(currentBlock.getBlock(), currentMaterial.getMaterial(), currentMaterial.getData()), currentIteration);
						}
					}
				}
			}
		}
	}

	private void erosionIteration(SnipeData snipeData, ErosionPreset erosionPreset, BlockChangeTracker blockChangeTracker, Vector targetBlockVector) {
		int currentIteration = blockChangeTracker.nextIteration();
		for (int x = this.getTargetBlock()
			.getX() - snipeData.getBrushSize(); x <= this.getTargetBlock()
			.getX() + snipeData.getBrushSize(); ++x) {
			for (int z = this.getTargetBlock()
				.getZ() - snipeData.getBrushSize(); z <= this.getTargetBlock()
				.getZ() + snipeData.getBrushSize(); ++z) {
				for (int y = this.getTargetBlock()
					.getY() - snipeData.getBrushSize(); y <= this.getTargetBlock()
					.getY() + snipeData.getBrushSize(); ++y) {
					Vector currentPosition = new Vector(x, y, z);
					if (currentPosition.isInSphere(targetBlockVector, snipeData.getBrushSize())) {
						BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
						if (currentBlock.isEmpty() || currentBlock.isLiquid()) {
							continue;
						}
						int count = 0;
						for (Vector vector : FACES_TO_CHECK) {
							Vector relativePosition = currentPosition.clone()
								.add(vector);
							BlockWrapper relativeBlock = blockChangeTracker.get(relativePosition, currentIteration);
							if (relativeBlock.isEmpty() || relativeBlock.isLiquid()) {
								count++;
							}
						}
						if (count >= erosionPreset.getErosionFaces()) {
							blockChangeTracker.put(currentPosition, new BlockWrapper(currentBlock.getBlock(), Material.AIR, (byte) 0), currentIteration);
						}
					}
				}
			}
		}
	}

	@Override
	protected final void powder(SnipeData snipeData) {
		this.erosion(snipeData, this.currentPreset.getInverted());
	}

	@Override
	public final void info(Message message) {
		message.brushName(this.getName());
		message.size();
		message.custom(ChatColor.AQUA + "Erosion minimum exposed faces set to " + this.currentPreset.getErosionFaces());
		message.custom(ChatColor.BLUE + "Fill minumum touching faces set to " + this.currentPreset.getFillFaces());
		message.custom(ChatColor.DARK_BLUE + "Erosion recursion amount set to " + this.currentPreset.getErosionRecursion());
		message.custom(ChatColor.DARK_GREEN + "Fill recursion amount set to " + this.currentPreset.getFillRecursion());
	}

	@Override
	public final void parameters(String[] parameters, SnipeData snipeData) {
		JSAPResult result = this.parser.parse(Arrays.copyOfRange(parameters, 1, parameters.length));
		if (sendHelpOrErrorMessageToPlayer(result, snipeData.getOwner()
			.getPlayer(), this.parser)) {
			return;
		}
		if (result.getString("preset") != null) {
			try {
				this.currentPreset = Preset.valueOf(result.getString("preset")
					.toUpperCase())
					.getPreset();
				snipeData.getMessage()
					.brushMessage("Brush preset set to " + result.getString("preset"));
				return;
			} catch (IllegalArgumentException exception) {
				snipeData.getMessage()
					.brushMessage("No such preset.");
				return;
			}
		}
		ErosionPreset currentPresetBackup = this.currentPreset;
		if (result.getObject("fill") != null) {
			this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(), result.getInt("fill"), this.currentPreset.getFillRecursion());
		}
		if (result.getObject("erode") != null) {
			this.currentPreset = new ErosionPreset(result.getInt("erode"), this.currentPreset.getErosionRecursion(), this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
		}
		if (result.getObject("fillrecursion") != null) {
			this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), this.currentPreset.getErosionRecursion(), this.currentPreset.getFillFaces(), result.getInt("fillrecursion"));
		}
		if (result.getObject("eroderecursion") != null) {
			this.currentPreset = new ErosionPreset(this.currentPreset.getErosionFaces(), result.getInt("eroderecursion"), this.currentPreset.getFillFaces(), this.currentPreset.getFillRecursion());
		}
		if (!this.currentPreset.equals(currentPresetBackup)) {
			if (this.currentPreset.getErosionFaces() != currentPresetBackup.getErosionFaces()) {
				snipeData.sendMessage(ChatColor.AQUA + "Erosion faces set to: " + ChatColor.WHITE + this.currentPreset.getErosionFaces());
			}
			if (this.currentPreset.getFillFaces() != currentPresetBackup.getFillFaces()) {
				snipeData.sendMessage(ChatColor.AQUA + "Fill faces set to: " + ChatColor.WHITE + this.currentPreset.getFillFaces());
			}
			if (this.currentPreset.getErosionRecursion() != currentPresetBackup.getErosionRecursion()) {
				snipeData.sendMessage(ChatColor.AQUA + "Erosion recursions set to: " + ChatColor.WHITE + this.currentPreset.getErosionRecursion());
			}
			if (this.currentPreset.getFillRecursion() != currentPresetBackup.getFillRecursion()) {
				snipeData.sendMessage(ChatColor.AQUA + "Fill recursions set to: " + ChatColor.WHITE + this.currentPreset.getFillRecursion());
			}
		}
	}

	/**
	 * @author MikeMatrix
	 */
	private enum Preset {

		MELT(new ErosionPreset(2, 1, 5, 1)),
		FILL(new ErosionPreset(5, 1, 2, 1)),
		SMOOTH(new ErosionPreset(3, 1, 3, 1)),
		LIFT(new ErosionPreset(6, 0, 1, 1)),
		FLOAT_CLEAN(new ErosionPreset(6, 1, 6, 1));

		private ErosionPreset preset;

		Preset(ErosionPreset preset) {
			this.preset = preset;
		}

		/**
		 * Generates a concat string of all options.
		 *
		 * @param seperator Seperator for delimiting entries.
		 */
		public static String getValuesString(String seperator) {
			StringBuilder valuesString = new StringBuilder();
			boolean delimiterHelper = true;
			for (Preset preset : Preset.values()) {
				if (delimiterHelper) {
					delimiterHelper = false;
				} else {
					valuesString.append(seperator);
				}
				valuesString.append(preset.name());
			}
			return valuesString.toString();
		}

		public ErosionPreset getPreset() {
			return this.preset;
		}
	}

	/**
	 * @author MikeMatrix
	 */
	private static final class BlockChangeTracker {

		private final Map<Integer, Map<Vector, BlockWrapper>> blockChanges;
		private final Map<Vector, BlockWrapper> flatChanges;
		private final World world;
		private int nextIterationId;

		private BlockChangeTracker(World world) {
			this.blockChanges = new HashMap<>();
			this.flatChanges = new HashMap<>();
			this.world = world;
		}

		public BlockWrapper get(Vector position, int iteration) {
			for (int i = iteration - 1; i >= 0; --i) {
				if (this.blockChanges.containsKey(i) && this.blockChanges.get(i)
					.containsKey(position)) {
					return this.blockChanges.get(i)
						.get(position);
				}
			}
			return new BlockWrapper(position.toLocation(this.world)
				.getBlock());
		}

		public Collection<BlockWrapper> getAll() {
			return this.flatChanges.values();
		}

		public int nextIteration() {
			return this.nextIterationId++;
		}

		public void put(Vector position, BlockWrapper changedBlock, int iteration) {
			if (!this.blockChanges.containsKey(iteration)) {
				this.blockChanges.put(iteration, new HashMap<>());
			}
			this.blockChanges.get(iteration)
				.put(position, changedBlock);
			this.flatChanges.put(position, changedBlock);
		}
	}

	/**
	 * @author MikeMatrix
	 */
	private static final class BlockWrapper {

		private final Block block;
		private final Material material;
		private final byte data;

		private BlockWrapper(Block block) {
			this.block = block;
			this.data = block.getData();
			this.material = block.getType();
		}

		private BlockWrapper(Block block, Material material, byte data) {
			this.block = block;
			this.material = material;
			this.data = data;
		}

		/**
		 * @return the block
		 */
		public Block getBlock() {
			return this.block;
		}

		/**
		 * @return the data
		 */
		public byte getData() {
			return this.data;
		}

		/**
		 * @return the material
		 */
		public Material getMaterial() {
			return this.material;
		}

		/**
		 * @return if the block is Empty.
		 */
		public boolean isEmpty() {
			return this.material == Material.AIR;
		}

		/**
		 * @return if the block is a Liquid.
		 */
		public boolean isLiquid() {
			switch (this.material) {
				case LEGACY_WATER:
				case LEGACY_STATIONARY_WATER:
				case LEGACY_LAVA:
				case LEGACY_STATIONARY_LAVA:
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * @author MikeMatrix
	 */
	private static final class ErosionPreset implements Serializable {

		private static final long serialVersionUID = 8997952776355430411L;

		private final int erosionFaces;
		private final int erosionRecursion;
		private final int fillFaces;
		private final int fillRecursion;

		private ErosionPreset(int erosionFaces, int erosionRecursion, int fillFaces, int fillRecursion) {
			this.erosionFaces = erosionFaces;
			this.erosionRecursion = erosionRecursion;
			this.fillFaces = fillFaces;
			this.fillRecursion = fillRecursion;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(this.erosionFaces, this.erosionRecursion, this.fillFaces, this.fillRecursion);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ErosionPreset) {
				ErosionPreset other = (ErosionPreset) obj;
				return Objects.equal(this.erosionFaces, other.erosionFaces) && Objects.equal(this.erosionRecursion, other.erosionRecursion) && Objects.equal(this.fillFaces, other.fillFaces) && Objects.equal(this.fillRecursion, other.fillRecursion);
			}
			return false;
		}

		/**
		 * @return the erosionFaces
		 */
		public int getErosionFaces() {
			return this.erosionFaces;
		}

		/**
		 * @return the erosionRecursion
		 */
		public int getErosionRecursion() {
			return this.erosionRecursion;
		}

		/**
		 * @return the fillFaces
		 */
		public int getFillFaces() {
			return this.fillFaces;
		}

		/**
		 * @return the fillRecursion
		 */
		public int getFillRecursion() {
			return this.fillRecursion;
		}

		public ErosionPreset getInverted() {
			return new ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
		}
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.erode";
	}
}
