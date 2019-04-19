package com.thevoxelbox.voxelsniper;

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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit extension point.
 */
public class VoxelSniper extends JavaPlugin {

	private static VoxelSniper instance;
	private SniperManager sniperManager = new SniperManager(this);
	private final VoxelSniperListener voxelSniperListener = new VoxelSniperListener(this);
	private VoxelSniperConfiguration voxelSniperConfiguration;

	/**
	 * Returns {@link com.thevoxelbox.voxelsniper.Brushes} for current instance.
	 *
	 * @return Brush Manager for current instance.
	 */
	public Brushes getBrushManager() {
		return this.brushManager;
	}

	private Brushes brushManager = new Brushes();

	public static VoxelSniper getInstance() {
		return instance;
	}

	/**
	 * Returns object for accessing global VoxelSniper options.
	 *
	 * @return {@link VoxelSniperConfiguration} object for accessing global VoxelSniper options.
	 */
	public VoxelSniperConfiguration getVoxelSniperConfiguration() {
		return this.voxelSniperConfiguration;
	}

	/**
	 * Returns {@link com.thevoxelbox.voxelsniper.SniperManager} for current instance.
	 *
	 * @return SniperManager
	 */
	public SniperManager getSniperManager() {
		return this.sniperManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			String[] arguments = args;
			if (arguments == null) {
				arguments = new String[0];
			}
			return this.voxelSniperListener.onCommand((Player) sender, arguments, command.getName());
		}
		getLogger().info("Only Players can execute commands.");
		return true;
	}

	@Override
	public void onEnable() {
		instance = this;
		registerBrushes();
		getLogger().info("Registered " + this.brushManager.registeredSniperBrushes() + " Sniper Brushes with " + this.brushManager.registeredSniperBrushHandles() + " handles.");
		saveDefaultConfig();
		this.voxelSniperConfiguration = new VoxelSniperConfiguration(getConfig());
		Bukkit.getPluginManager()
			.registerEvents(this.voxelSniperListener, this);
		getLogger().info("Registered Sniper Listener.");
	}

	/**
	 * Registers all brushes.
	 */
	public void registerBrushes() {
		this.brushManager.registerSniperBrush(BallBrush.class, "b", "ball");
		this.brushManager.registerSniperBrush(BiomeBrush.class, "bio", "biome");
		this.brushManager.registerSniperBrush(BlendBallBrush.class, "bb", "blendball");
		this.brushManager.registerSniperBrush(BlendDiscBrush.class, "bd", "blenddisc");
		this.brushManager.registerSniperBrush(BlendVoxelBrush.class, "bv", "blendvoxel");
		this.brushManager.registerSniperBrush(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc");
		this.brushManager.registerSniperBrush(BlobBrush.class, "blob", "splatblob");
		this.brushManager.registerSniperBrush(BlockResetBrush.class, "brb", "blockresetbrush");
		this.brushManager.registerSniperBrush(BlockResetSurfaceBrush.class, "brbs", "blockresetbrushsurface");
		this.brushManager.registerSniperBrush(CanyonBrush.class, "ca", "canyon");
		this.brushManager.registerSniperBrush(CanyonSelectionBrush.class, "cas", "canyonselection");
		this.brushManager.registerSniperBrush(CheckerVoxelDiscBrush.class, "cvd", "checkervoxeldisc");
		this.brushManager.registerSniperBrush(CleanSnowBrush.class, "cls", "cleansnow");
		this.brushManager.registerSniperBrush(CloneStampBrush.class, "cs", "clonestamp");
		this.brushManager.registerSniperBrush(CometBrush.class, "com", "comet");
		this.brushManager.registerSniperBrush(CopyPastaBrush.class, "cp", "copypasta");
		this.brushManager.registerSniperBrush(CylinderBrush.class, "c", "cylinder");
		this.brushManager.registerSniperBrush(DiscBrush.class, "d", "disc");
		this.brushManager.registerSniperBrush(DiscFaceBrush.class, "df", "discface");
		this.brushManager.registerSniperBrush(DomeBrush.class, "dome", "domebrush");
		this.brushManager.registerSniperBrush(DrainBrush.class, "drain");
		this.brushManager.registerSniperBrush(EllipseBrush.class, "el", "ellipse");
		this.brushManager.registerSniperBrush(EllipsoidBrush.class, "elo", "ellipsoid");
		this.brushManager.registerSniperBrush(EntityBrush.class, "en", "entity");
		this.brushManager.registerSniperBrush(EntityRemovalBrush.class, "er", "entityremoval");
		this.brushManager.registerSniperBrush(EraserBrush.class, "erase", "eraser");
		this.brushManager.registerSniperBrush(ErodeBrush.class, "e", "erode");
		this.brushManager.registerSniperBrush(ExtrudeBrush.class, "ex", "extrude");
		this.brushManager.registerSniperBrush(FillDownBrush.class, "fd", "filldown");
		this.brushManager.registerSniperBrush(FlatOceanBrush.class, "fo", "flatocean");
		this.brushManager.registerSniperBrush(GenerateTreeBrush.class, "gt", "generatetree");
		this.brushManager.registerSniperBrush(HeatRayBrush.class, "hr", "heatray");
		this.brushManager.registerSniperBrush(JaggedLineBrush.class, "j", "jagged");
		this.brushManager.registerSniperBrush(JockeyBrush.class, "jockey");
		this.brushManager.registerSniperBrush(LightningBrush.class, "light", "lightning");
		this.brushManager.registerSniperBrush(LineBrush.class, "l", "line");
		this.brushManager.registerSniperBrush(MoveBrush.class, "mv", "move");
		this.brushManager.registerSniperBrush(OceanBrush.class, "o", "ocean");
		this.brushManager.registerSniperBrush(OverlayBrush.class, "over", "overlay");
		this.brushManager.registerSniperBrush(PaintingBrush.class, "paint", "painting");
		this.brushManager.registerSniperBrush(PullBrush.class, "pull");
		this.brushManager.registerSniperBrush(PunishBrush.class, "p", "punish");
		this.brushManager.registerSniperBrush(RandomErodeBrush.class, "re", "randomerode");
		this.brushManager.registerSniperBrush(RegenerateChunkBrush.class, "gc", "generatechunk");
		this.brushManager.registerSniperBrush(RingBrush.class, "ri", "ring");
		this.brushManager.registerSniperBrush(Rot2DBrush.class, "rot2", "rotation2d");
		this.brushManager.registerSniperBrush(Rot2DvertBrush.class, "rot2v", "rotation2dvertical");
		this.brushManager.registerSniperBrush(Rot3DBrush.class, "rot3", "rotation3d");
		this.brushManager.registerSniperBrush(RulerBrush.class, "r", "ruler");
		this.brushManager.registerSniperBrush(ScannerBrush.class, "sc", "scanner");
		this.brushManager.registerSniperBrush(SetBrush.class, "set");
		this.brushManager.registerSniperBrush(SetRedstoneFlipBrush.class, "setrf", "setredstoneflip");
		this.brushManager.registerSniperBrush(ShellBallBrush.class, "shb", "shellball");
		this.brushManager.registerSniperBrush(ShellSetBrush.class, "shs", "shellset");
		this.brushManager.registerSniperBrush(ShellVoxelBrush.class, "shv", "shellvoxel");
		this.brushManager.registerSniperBrush(SignOverwriteBrush.class, "sio", "signoverwriter");
		this.brushManager.registerSniperBrush(SnipeBrush.class, "s", "snipe");
		this.brushManager.registerSniperBrush(SnowConeBrush.class, "snow", "snowcone");
		this.brushManager.registerSniperBrush(SpiralStaircaseBrush.class, "sstair", "spiralstaircase");
		this.brushManager.registerSniperBrush(SplatterBallBrush.class, "sb", "splatball");
		this.brushManager.registerSniperBrush(SplatterDiscBrush.class, "sd", "splatdisc");
		this.brushManager.registerSniperBrush(SplatterOverlayBrush.class, "sover", "splatteroverlay");
		this.brushManager.registerSniperBrush(SplatterVoxelBrush.class, "sv", "splattervoxel");
		this.brushManager.registerSniperBrush(SplatterDiscBrush.class, "svd", "splatvoxeldisc");
		this.brushManager.registerSniperBrush(SplineBrush.class, "sp", "spline");
		this.brushManager.registerSniperBrush(StencilBrush.class, "st", "stencil");
		this.brushManager.registerSniperBrush(StencilListBrush.class, "sl", "stencillist");
		this.brushManager.registerSniperBrush(ThreePointCircleBrush.class, "tpc", "threepointcircle");
		this.brushManager.registerSniperBrush(TreeSnipeBrush.class, "t", "tree", "treesnipe");
		this.brushManager.registerSniperBrush(TriangleBrush.class, "tri", "triangle");
		this.brushManager.registerSniperBrush(UnderlayBrush.class, "under", "underlay");
		this.brushManager.registerSniperBrush(VoltMeterBrush.class, "volt", "voltmeter");
		this.brushManager.registerSniperBrush(VoxelBrush.class, "v", "voxel");
		this.brushManager.registerSniperBrush(VoxelDiscBrush.class, "vd", "voxeldisc");
		this.brushManager.registerSniperBrush(VoxelDiscFaceBrush.class, "vdf", "voxeldiscface");
		this.brushManager.registerSniperBrush(WarpBrush.class, "world", "warp");
	}
}
