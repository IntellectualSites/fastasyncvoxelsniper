package com.thevoxelbox.voxelsniper.util.material;

import org.bukkit.Material;
import org.bukkit.Tag;

public final class MaterialSets {

	public static final MaterialSet AIRS = MaterialSet.builder()
		.add(Material.AIR)
		.add(Material.CAVE_AIR)
		.add(Material.VOID_AIR)
		.build();

	public static final MaterialSet CHESTS = MaterialSet.builder()
		.add(Material.CHEST)
		.add(Material.TRAPPED_CHEST)
		.add(Material.ENDER_CHEST)
		.build();

	public static final MaterialSet FENCE_GATES = MaterialSet.builder()
		.add(Material.ACACIA_FENCE_GATE)
		.add(Material.BIRCH_FENCE_GATE)
		.add(Material.DARK_OAK_FENCE_GATE)
		.add(Material.JUNGLE_FENCE_GATE)
		.add(Material.OAK_FENCE_GATE)
		.add(Material.SPRUCE_FENCE_GATE)
		.build();

	public static final MaterialSet SNOWS = MaterialSet.builder()
		.add(Material.SNOW)
		.add(Material.SNOW_BLOCK)
		.build();

	public static final MaterialSet WOODEN_FENCES = MaterialSet.builder()
		.add(Material.ACACIA_FENCE)
		.add(Material.BIRCH_FENCE)
		.add(Material.DARK_OAK_FENCE)
		.add(Material.JUNGLE_FENCE)
		.add(Material.OAK_FENCE)
		.add(Material.SPRUCE_FENCE)
		.build();

	public static final MaterialSet TORCHES = MaterialSet.builder()
		.add(Material.TORCH)
		.add(Material.WALL_TORCH)
		.build();

	public static final MaterialSet REDSTONE_TORCHES = MaterialSet.builder()
		.add(Material.REDSTONE_TORCH)
		.add(Material.REDSTONE_WALL_TORCH)
		.build();

	public static final MaterialSet FLOWERS = MaterialSet.builder()
		.add(Material.DANDELION)
		.add(Material.POPPY)
		.add(Material.BLUE_ORCHID)
		.add(Material.ALLIUM)
		.add(Material.AZURE_BLUET)
		.add(Material.RED_TULIP)
		.add(Material.ORANGE_TULIP)
		.add(Material.WHITE_TULIP)
		.add(Material.PINK_TULIP)
		.add(Material.OXEYE_DAISY)
		.build();

	public static final MaterialSet MUSHROOMS = MaterialSet.builder()
		.add(Material.BROWN_MUSHROOM)
		.add(Material.RED_MUSHROOM)
		.build();

	public static final MaterialSet STEMS = MaterialSet.builder()
		.add(Material.ATTACHED_MELON_STEM)
		.add(Material.ATTACHED_PUMPKIN_STEM)
		.add(Material.MELON_STEM)
		.add(Material.PUMPKIN_STEM)
		.build();

	public static final MaterialSet FLORA = MaterialSet.builder()
		.with(FLOWERS)
		.with(MUSHROOMS)
		.with(STEMS)
		.add(Material.GRASS)
		.add(Material.TALL_GRASS)
		.add(Material.DEAD_BUSH)
		.add(Material.WHEAT)
		.add(Material.SUGAR_CANE)
		.add(Material.VINE)
		.add(Material.LILY_PAD)
		.add(Material.CACTUS)
		.add(Material.NETHER_WART)
		.build();

	public static final MaterialSet STONES = MaterialSet.builder()
		.add(Material.STONE)
		.add(Material.GRANITE)
		.add(Material.DIORITE)
		.add(Material.ANDESITE)
		.build();

	public static final MaterialSet GRASSES = MaterialSet.builder()
		.add(Material.GRASS_BLOCK)
		.add(Material.PODZOL)
		.build();

	public static final MaterialSet DIRT = MaterialSet.builder()
		.add(Material.DIRT)
		.add(Material.COARSE_DIRT)
		.build();

	public static final MaterialSet LIQUIDS = MaterialSet.builder()
		.add(Material.WATER)
		.add(Material.LAVA)
		.build();

	public static final MaterialSet FALLING = MaterialSet.builder()
		.with(LIQUIDS)
		.with(Tag.SAND)
		.add(Material.GRAVEL)
		.build();

	public static final MaterialSet SANDSTONES = MaterialSet.builder()
		.add(Material.SANDSTONE)
		.add(Material.CHISELED_SANDSTONE)
		.add(Material.CUT_SANDSTONE)
		.add(Material.SMOOTH_SANDSTONE)
		.build();

	public static final MaterialSet RED_SANDSTONES = MaterialSet.builder()
		.add(Material.RED_SANDSTONE)
		.add(Material.CHISELED_RED_SANDSTONE)
		.add(Material.CUT_RED_SANDSTONE)
		.add(Material.SMOOTH_RED_SANDSTONE)
		.build();

	public static final MaterialSet OVERRIDEABLE = MaterialSet.builder()
		.with(STONES)
		.with(GRASSES)
		.with(DIRT)
		.with(SANDSTONES)
		.with(RED_SANDSTONES)
		.with(Tag.SAND)
		.add(Material.GRAVEL)
		.add(Material.MOSSY_COBBLESTONE)
		.add(Material.OBSIDIAN)
		.add(Material.SNOW)
		.add(Material.CLAY)
		.build();

	public static final MaterialSet ORES = MaterialSet.builder()
		.add(Material.COAL_ORE)
		.add(Material.DIAMOND_ORE)
		.add(Material.EMERALD_ORE)
		.add(Material.GOLD_ORE)
		.add(Material.IRON_ORE)
		.add(Material.LAPIS_ORE)
		.add(Material.NETHER_QUARTZ_ORE)
		.add(Material.REDSTONE_ORE)
		.build();

	public static final MaterialSet OVERRIDEABLE_WITH_ORES = MaterialSet.builder()
		.with(OVERRIDEABLE)
		.with(ORES)
		.build();

	public static final MaterialSet PISTONS = MaterialSet.builder()
		.add(Material.MOVING_PISTON)
		.add(Material.PISTON)
		.add(Material.PISTON_HEAD)
		.add(Material.STICKY_PISTON)
		.build();

	public static final MaterialSet PRESSURE_PLATES = MaterialSet.builder()
		.add(Material.ACACIA_PRESSURE_PLATE)
		.add(Material.BIRCH_PRESSURE_PLATE)
		.add(Material.DARK_OAK_PRESSURE_PLATE)
		.add(Material.JUNGLE_PRESSURE_PLATE)
		.add(Material.OAK_PRESSURE_PLATE)
		.add(Material.SPRUCE_PRESSURE_PLATE)
		.add(Material.STONE_PRESSURE_PLATE)
		.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
		.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.build();

	public static final MaterialSet SIGNS = MaterialSet.builder()
		.add(Material.ACACIA_SIGN)
		.add(Material.ACACIA_WALL_SIGN)
		.add(Material.BIRCH_SIGN)
		.add(Material.BIRCH_WALL_SIGN)
		.add(Material.DARK_OAK_SIGN)
		.add(Material.DARK_OAK_WALL_SIGN)
		.add(Material.JUNGLE_SIGN)
		.add(Material.JUNGLE_WALL_SIGN)
		.add(Material.OAK_SIGN)
		.add(Material.OAK_WALL_SIGN)
		.add(Material.SPRUCE_SIGN)
		.add(Material.SPRUCE_WALL_SIGN)
		.build();

	public static final MaterialSet BEDS = MaterialSet.builder()
		.add(Material.BLACK_BED)
		.add(Material.BLUE_BED)
		.add(Material.BROWN_BED)
		.add(Material.CYAN_BED)
		.add(Material.GRAY_BED)
		.add(Material.GREEN_BED)
		.add(Material.LIGHT_GRAY_BED)
		.add(Material.LIGHT_BLUE_BED)
		.add(Material.LIME_BED)
		.add(Material.MAGENTA_BED)
		.add(Material.ORANGE_BED)
		.add(Material.PINK_BED)
		.add(Material.PURPLE_BED)
		.add(Material.RED_BED)
		.add(Material.WHITE_BED)
		.add(Material.YELLOW_BED)
		.build();

	private MaterialSets() {
		throw new UnsupportedOperationException("Cannot create an instance of this class");
	}
}
