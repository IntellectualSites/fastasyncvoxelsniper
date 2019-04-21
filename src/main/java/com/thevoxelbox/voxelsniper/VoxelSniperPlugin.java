package com.thevoxelbox.voxelsniper;

import java.util.logging.Logger;
import com.thevoxelbox.voxelsniper.brush.BallBrush;
import com.thevoxelbox.voxelsniper.brush.BiomeBrush;
import com.thevoxelbox.voxelsniper.brush.BlendBallBrush;
import com.thevoxelbox.voxelsniper.brush.BlendDiscBrush;
import com.thevoxelbox.voxelsniper.brush.BlendVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.BlendVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.BlobBrush;
import com.thevoxelbox.voxelsniper.brush.BlockResetBrush;
import com.thevoxelbox.voxelsniper.brush.BlockResetSurfaceBrush;
import com.thevoxelbox.voxelsniper.brush.CanyonBrush;
import com.thevoxelbox.voxelsniper.brush.CanyonSelectionBrush;
import com.thevoxelbox.voxelsniper.brush.CheckerVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.CleanSnowBrush;
import com.thevoxelbox.voxelsniper.brush.CloneStampBrush;
import com.thevoxelbox.voxelsniper.brush.CometBrush;
import com.thevoxelbox.voxelsniper.brush.CopyPastaBrush;
import com.thevoxelbox.voxelsniper.brush.CylinderBrush;
import com.thevoxelbox.voxelsniper.brush.DiscBrush;
import com.thevoxelbox.voxelsniper.brush.DiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.DomeBrush;
import com.thevoxelbox.voxelsniper.brush.DrainBrush;
import com.thevoxelbox.voxelsniper.brush.EllipseBrush;
import com.thevoxelbox.voxelsniper.brush.EllipsoidBrush;
import com.thevoxelbox.voxelsniper.brush.EntityBrush;
import com.thevoxelbox.voxelsniper.brush.EntityRemovalBrush;
import com.thevoxelbox.voxelsniper.brush.EraserBrush;
import com.thevoxelbox.voxelsniper.brush.ErodeBrush;
import com.thevoxelbox.voxelsniper.brush.ExtrudeBrush;
import com.thevoxelbox.voxelsniper.brush.FillDownBrush;
import com.thevoxelbox.voxelsniper.brush.FlatOceanBrush;
import com.thevoxelbox.voxelsniper.brush.GenerateTreeBrush;
import com.thevoxelbox.voxelsniper.brush.HeatRayBrush;
import com.thevoxelbox.voxelsniper.brush.JaggedLineBrush;
import com.thevoxelbox.voxelsniper.brush.JockeyBrush;
import com.thevoxelbox.voxelsniper.brush.LightningBrush;
import com.thevoxelbox.voxelsniper.brush.LineBrush;
import com.thevoxelbox.voxelsniper.brush.MoveBrush;
import com.thevoxelbox.voxelsniper.brush.OceanBrush;
import com.thevoxelbox.voxelsniper.brush.OverlayBrush;
import com.thevoxelbox.voxelsniper.brush.PaintingBrush;
import com.thevoxelbox.voxelsniper.brush.PullBrush;
import com.thevoxelbox.voxelsniper.brush.PunishBrush;
import com.thevoxelbox.voxelsniper.brush.RandomErodeBrush;
import com.thevoxelbox.voxelsniper.brush.RegenerateChunkBrush;
import com.thevoxelbox.voxelsniper.brush.RingBrush;
import com.thevoxelbox.voxelsniper.brush.Rot2DBrush;
import com.thevoxelbox.voxelsniper.brush.Rot2DvertBrush;
import com.thevoxelbox.voxelsniper.brush.Rot3DBrush;
import com.thevoxelbox.voxelsniper.brush.RulerBrush;
import com.thevoxelbox.voxelsniper.brush.ScannerBrush;
import com.thevoxelbox.voxelsniper.brush.SetBrush;
import com.thevoxelbox.voxelsniper.brush.SetRedstoneFlipBrush;
import com.thevoxelbox.voxelsniper.brush.ShellBallBrush;
import com.thevoxelbox.voxelsniper.brush.ShellSetBrush;
import com.thevoxelbox.voxelsniper.brush.ShellVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.SignOverwriteBrush;
import com.thevoxelbox.voxelsniper.brush.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.SnowConeBrush;
import com.thevoxelbox.voxelsniper.brush.SpiralStaircaseBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterBallBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterDiscBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterOverlayBrush;
import com.thevoxelbox.voxelsniper.brush.SplatterVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.SplineBrush;
import com.thevoxelbox.voxelsniper.brush.StencilBrush;
import com.thevoxelbox.voxelsniper.brush.StencilListBrush;
import com.thevoxelbox.voxelsniper.brush.ThreePointCircleBrush;
import com.thevoxelbox.voxelsniper.brush.TreeSnipeBrush;
import com.thevoxelbox.voxelsniper.brush.TriangleBrush;
import com.thevoxelbox.voxelsniper.brush.UnderlayBrush;
import com.thevoxelbox.voxelsniper.brush.VoltMeterBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.VoxelDiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.WarpBrush;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit extension point.
 */
public class VoxelSniperPlugin extends JavaPlugin {

	private VoxelSniperConfig voxelSniperConfig;
	private BrushRegistry brushRegistry;
	private SniperManager sniperManager;

	@Override
	public void onEnable() {
		this.voxelSniperConfig = loadConfig();
		this.brushRegistry = loadBrushRegistry();
		this.sniperManager = new SniperManager(this);
		loadCommands();
		loadListeners();
	}

	private VoxelSniperConfig loadConfig() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		return new VoxelSniperConfig(config);
	}

	private BrushRegistry loadBrushRegistry() {
		BrushRegistry brushRegistry = new BrushRegistry();
		registerBrushes(brushRegistry);
		Logger logger = getLogger();
		logger.info("Registered " + brushRegistry.getBrushesCount() + " brushes with " + brushRegistry.getHandlesCount() + " handles.");
		return brushRegistry;
	}

	private void registerBrushes(BrushRegistry brushRegistry) {
		brushRegistry.registerBrush(BallBrush.class, "b", "ball");
		brushRegistry.registerBrush(BiomeBrush.class, "bio", "biome");
		brushRegistry.registerBrush(BlendBallBrush.class, "bb", "blendball");
		brushRegistry.registerBrush(BlendDiscBrush.class, "bd", "blenddisc");
		brushRegistry.registerBrush(BlendVoxelBrush.class, "bv", "blendvoxel");
		brushRegistry.registerBrush(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc");
		brushRegistry.registerBrush(BlobBrush.class, "blob", "splatblob");
		brushRegistry.registerBrush(BlockResetBrush.class, "brb", "blockresetbrush");
		brushRegistry.registerBrush(BlockResetSurfaceBrush.class, "brbs", "blockresetbrushsurface");
		brushRegistry.registerBrush(CanyonBrush.class, "ca", "canyon");
		brushRegistry.registerBrush(CanyonSelectionBrush.class, "cas", "canyonselection");
		brushRegistry.registerBrush(CheckerVoxelDiscBrush.class, "cvd", "checkervoxeldisc");
		brushRegistry.registerBrush(CleanSnowBrush.class, "cls", "cleansnow");
		brushRegistry.registerBrush(CloneStampBrush.class, "cs", "clonestamp");
		brushRegistry.registerBrush(CometBrush.class, "com", "comet");
		brushRegistry.registerBrush(CopyPastaBrush.class, "cp", "copypasta");
		brushRegistry.registerBrush(CylinderBrush.class, "c", "cylinder");
		brushRegistry.registerBrush(DiscBrush.class, "d", "disc");
		brushRegistry.registerBrush(DiscFaceBrush.class, "df", "discface");
		brushRegistry.registerBrush(DomeBrush.class, "dome", "domebrush");
		brushRegistry.registerBrush(DrainBrush.class, "drain");
		brushRegistry.registerBrush(EllipseBrush.class, "el", "ellipse");
		brushRegistry.registerBrush(EllipsoidBrush.class, "elo", "ellipsoid");
		brushRegistry.registerBrush(EntityBrush.class, "en", "entity");
		brushRegistry.registerBrush(EntityRemovalBrush.class, "er", "entityremoval");
		brushRegistry.registerBrush(EraserBrush.class, "erase", "eraser");
		brushRegistry.registerBrush(ErodeBrush.class, "e", "erode");
		brushRegistry.registerBrush(ExtrudeBrush.class, "ex", "extrude");
		brushRegistry.registerBrush(FillDownBrush.class, "fd", "filldown");
		brushRegistry.registerBrush(FlatOceanBrush.class, "fo", "flatocean");
		brushRegistry.registerBrush(GenerateTreeBrush.class, "gt", "generatetree");
		brushRegistry.registerBrush(HeatRayBrush.class, "hr", "heatray");
		brushRegistry.registerBrush(JaggedLineBrush.class, "j", "jagged");
		brushRegistry.registerBrush(JockeyBrush.class, "jockey");
		brushRegistry.registerBrush(LightningBrush.class, "light", "lightning");
		brushRegistry.registerBrush(LineBrush.class, "l", "line");
		brushRegistry.registerBrush(MoveBrush.class, "mv", "move");
		brushRegistry.registerBrush(OceanBrush.class, "o", "ocean");
		brushRegistry.registerBrush(OverlayBrush.class, "over", "overlay");
		brushRegistry.registerBrush(PaintingBrush.class, "paint", "painting");
		brushRegistry.registerBrush(PullBrush.class, "pull");
		brushRegistry.registerBrush(PunishBrush.class, "p", "punish");
		brushRegistry.registerBrush(RandomErodeBrush.class, "re", "randomerode");
		brushRegistry.registerBrush(RegenerateChunkBrush.class, "gc", "generatechunk");
		brushRegistry.registerBrush(RingBrush.class, "ri", "ring");
		brushRegistry.registerBrush(Rot2DBrush.class, "rot2", "rotation2d");
		brushRegistry.registerBrush(Rot2DvertBrush.class, "rot2v", "rotation2dvertical");
		brushRegistry.registerBrush(Rot3DBrush.class, "rot3", "rotation3d");
		brushRegistry.registerBrush(RulerBrush.class, "r", "ruler");
		brushRegistry.registerBrush(ScannerBrush.class, "sc", "scanner");
		brushRegistry.registerBrush(SetBrush.class, "set");
		brushRegistry.registerBrush(SetRedstoneFlipBrush.class, "setrf", "setredstoneflip");
		brushRegistry.registerBrush(ShellBallBrush.class, "shb", "shellball");
		brushRegistry.registerBrush(ShellSetBrush.class, "shs", "shellset");
		brushRegistry.registerBrush(ShellVoxelBrush.class, "shv", "shellvoxel");
		brushRegistry.registerBrush(SignOverwriteBrush.class, "sio", "signoverwriter");
		brushRegistry.registerBrush(SnipeBrush.class, "s", "snipe");
		brushRegistry.registerBrush(SnowConeBrush.class, "snow", "snowcone");
		brushRegistry.registerBrush(SpiralStaircaseBrush.class, "sstair", "spiralstaircase");
		brushRegistry.registerBrush(SplatterBallBrush.class, "sb", "splatball");
		brushRegistry.registerBrush(SplatterDiscBrush.class, "sd", "splatdisc");
		brushRegistry.registerBrush(SplatterOverlayBrush.class, "sover", "splatteroverlay");
		brushRegistry.registerBrush(SplatterVoxelBrush.class, "sv", "splattervoxel");
		brushRegistry.registerBrush(SplatterDiscBrush.class, "svd", "splatvoxeldisc");
		brushRegistry.registerBrush(SplineBrush.class, "sp", "spline");
		brushRegistry.registerBrush(StencilBrush.class, "st", "stencil");
		brushRegistry.registerBrush(StencilListBrush.class, "sl", "stencillist");
		brushRegistry.registerBrush(ThreePointCircleBrush.class, "tpc", "threepointcircle");
		brushRegistry.registerBrush(TreeSnipeBrush.class, "t", "tree", "treesnipe");
		brushRegistry.registerBrush(TriangleBrush.class, "tri", "triangle");
		brushRegistry.registerBrush(UnderlayBrush.class, "under", "underlay");
		brushRegistry.registerBrush(VoltMeterBrush.class, "volt", "voltmeter");
		brushRegistry.registerBrush(VoxelBrush.class, "v", "voxel");
		brushRegistry.registerBrush(VoxelDiscBrush.class, "vd", "voxeldisc");
		brushRegistry.registerBrush(VoxelDiscFaceBrush.class, "vdf", "voxeldiscface");
		brushRegistry.registerBrush(WarpBrush.class, "world", "warp");
	}

	private void loadCommands() {
		CommandRegistry commandRegistry = new CommandRegistry(this);
		CommandRegistrar commandRegistrar = new CommandRegistrar(this, commandRegistry);
		commandRegistrar.registerCommands();
	}

	private void loadListeners() {
		VoxelSniperListener listener = new VoxelSniperListener(this);
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(listener, this);
		Logger logger = getLogger();
		logger.info("Registered Sniper Listener.");
	}

	/**
	 * Returns object for accessing global VoxelSniper options.
	 *
	 * @return {@link VoxelSniperConfig} object for accessing global VoxelSniper options.
	 */
	public VoxelSniperConfig getVoxelSniperConfig() {
		return this.voxelSniperConfig;
	}

	/**
	 * Returns {@link BrushRegistry} for current instance.
	 *
	 * @return Brush Manager for current instance.
	 */
	public BrushRegistry getBrushRegistry() {
		return this.brushRegistry;
	}

	/**
	 * Returns {@link com.thevoxelbox.voxelsniper.SniperManager} for current instance.
	 *
	 * @return SniperManager
	 */
	public SniperManager getSniperManager() {
		return this.sniperManager;
	}
}
