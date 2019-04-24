package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.brush.type.BiomeBrush;
import com.thevoxelbox.voxelsniper.brush.type.BlockResetBrush;
import com.thevoxelbox.voxelsniper.brush.type.BlockResetSurfaceBrush;
import com.thevoxelbox.voxelsniper.brush.type.CleanSnowBrush;
import com.thevoxelbox.voxelsniper.brush.type.CometBrush;
import com.thevoxelbox.voxelsniper.brush.type.CopyPastaBrush;
import com.thevoxelbox.voxelsniper.brush.type.DomeBrush;
import com.thevoxelbox.voxelsniper.brush.type.DrainBrush;
import com.thevoxelbox.voxelsniper.brush.type.entity.EntityBrush;
import com.thevoxelbox.voxelsniper.brush.type.entity.EntityRemovalBrush;
import com.thevoxelbox.voxelsniper.brush.type.EraserBrush;
import com.thevoxelbox.voxelsniper.brush.type.ErodeBrush;
import com.thevoxelbox.voxelsniper.brush.type.ExtrudeBrush;
import com.thevoxelbox.voxelsniper.brush.type.FlatOceanBrush;
import com.thevoxelbox.voxelsniper.brush.type.GenerateTreeBrush;
import com.thevoxelbox.voxelsniper.brush.type.HeatRayBrush;
import com.thevoxelbox.voxelsniper.brush.type.JockeyBrush;
import com.thevoxelbox.voxelsniper.brush.type.LightningBrush;
import com.thevoxelbox.voxelsniper.brush.type.MoveBrush;
import com.thevoxelbox.voxelsniper.brush.type.OceanBrush;
import com.thevoxelbox.voxelsniper.brush.type.PaintingBrush;
import com.thevoxelbox.voxelsniper.brush.type.PullBrush;
import com.thevoxelbox.voxelsniper.brush.type.RandomErodeBrush;
import com.thevoxelbox.voxelsniper.brush.type.RegenerateChunkBrush;
import com.thevoxelbox.voxelsniper.brush.type.rotation.Rotation2DBrush;
import com.thevoxelbox.voxelsniper.brush.type.rotation.Rotation2DVerticalBrush;
import com.thevoxelbox.voxelsniper.brush.type.rotation.Rotation3DBrush;
import com.thevoxelbox.voxelsniper.brush.type.RulerBrush;
import com.thevoxelbox.voxelsniper.brush.type.ScannerBrush;
import com.thevoxelbox.voxelsniper.brush.type.redstone.SetRedstoneFlipBrush;
import com.thevoxelbox.voxelsniper.brush.type.redstone.SetRedstoneRotateBrush;
import com.thevoxelbox.voxelsniper.brush.type.shell.ShellBallBrush;
import com.thevoxelbox.voxelsniper.brush.type.shell.ShellSetBrush;
import com.thevoxelbox.voxelsniper.brush.type.shell.ShellVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.SignOverwriteBrush;
import com.thevoxelbox.voxelsniper.brush.type.SnowConeBrush;
import com.thevoxelbox.voxelsniper.brush.type.SpiralStaircaseBrush;
import com.thevoxelbox.voxelsniper.brush.type.stencil.StencilBrush;
import com.thevoxelbox.voxelsniper.brush.type.stencil.StencilListBrush;
import com.thevoxelbox.voxelsniper.brush.type.TreeSnipeBrush;
import com.thevoxelbox.voxelsniper.brush.type.VoltMeterBrush;
import com.thevoxelbox.voxelsniper.brush.type.WarpBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendBallBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.canyon.CanyonBrush;
import com.thevoxelbox.voxelsniper.brush.type.canyon.CanyonSelectionBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.BallBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.BlobBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.CheckerVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.CylinderBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.DiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.DiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.EllipseBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.EllipsoidBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.FillDownBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.JaggedLineBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.LineBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.OverlayBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.PunishBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.RingBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.SetBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.SnipeBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterBallBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterOverlayBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.SplineBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.ThreePointCircleBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.TriangleBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.UnderlayBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.VoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.VoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.VoxelDiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.type.stamp.CloneStampBrush;
import com.thevoxelbox.voxelsniper.brush.type.stamp.StampBrush;

public class BrushRegistrar {

	private BrushRegistry registry;

	public BrushRegistrar(BrushRegistry registry) {
		this.registry = registry;
	}

	public void registerBrushes() {
		registerBallBrush();
		registerBiomeBrush();
		registerBlendBallBrush();
		registerBlendDiscBrush();
		registerBlendVoxelBrush();
		registerBlendVoxelDiscBrush();
		registerBlobBrush();
		registerBlockResetBrush();
		registerBlockResetSurfaceBrush();
		registerCanyonBrush();
		registerCanyonSelectionBrush();
		registerCheckerVoxelDiscBrush();
		registerCleanSnowBrush();
		registerCloneStampBrush();
		registerCometBrush();
		registerCopyPastaBrush();
		registerCylinderBrush();
		registerDiscBrush();
		registerDiscFaceBrush();
		registerDomeBrush();
		registerDrainBrush();
		registerEllipseBrush();
		registerEllipsoidBrush();
		registerEntityBrush();
		registerEntityRemovalBrush();
		registerEraserBrush();
		registerErodeBrush();
		registerExtrudeBrush();
		registerFillDownBrush();
		registerFlatOceanBrush();
		registerGenerateTreeBrush();
		registerHeatRayBrush();
		registerJaggedLineBrush();
		registerJockeyBrush();
		registerLightningBrush();
		registerLineBrush();
		registerMoveBrush();
		registerOceanBrush();
		registerOverlayBrush();
		registerPaintingBrush();
		registerPullBrush();
		registerPunishBrush();
		registerRandomErodeBrush();
		registerRegenerateChunkBrush();
		registerRingBrush();
		registerRotation2DBrush();
		registerRotation2DVerticalBrush();
		registerRotation3DBrush();
		registerRulerBrush();
		registerScannerBrush();
		registerSetBrush();
		registerSetRedstoneFlipBrush();
		registerSetRedstoneRotateBrush();
		registerShellBallBrush();
		registerShellSetBrush();
		registerShellVoxelBrush();
		registerSignOverwriteBrush();
		registerSnipeBrush();
		registerSnowConeBrush();
		registerSpiralStaircaseBrush();
		registerSplatterBallBrush();
		registerSplatterDiscBrush();
		registerSplatterOverlayBrush();
		registerSplatterVoxelBrush();
		registerSplatterVoxelDiscBrush();
		registerSplineBrush();
		registerStampBrush();
		registerStencilBrush();
		registerStencilListBrush();
		registerThreePointCircleBrush();
		registerTreeSnipeBrush();
		registerTriangleBrush();
		registerUnderlayBrush();
		registerVoltMeterBrush();
		registerVoxelBrush();
		registerVoxelDiscBrush();
		registerVoxelDiscFaceBrush();
		registerWarpBrush();
	}

	private void registerBallBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Ball")
			.permission("voxelsniper.brush.ball")
			.alias("b")
			.alias("ball")
			.creator(BallBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBiomeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Biome (/b biome [Biome Name])")
			.permission("voxelsniper.brush.biome")
			.alias("bio")
			.alias("biome")
			.creator(BiomeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBlendBallBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("BlendBall")
			.permission("voxelsniper.brush.blendball")
			.alias("bb")
			.alias("blendball")
			.creator(BlendBallBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBlendDiscBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("BlendDisc")
			.permission("voxelsniper.brush.blenddisc")
			.alias("bd")
			.alias("blenddisc")
			.creator(BlendDiscBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBlendVoxelBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("BlendVoxel")
			.permission("voxelsniper.brush.blendvoxel")
			.alias("bv")
			.alias("blendvoxel")
			.creator(BlendVoxelBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBlendVoxelDiscBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("BlendVoxelDisc")
			.permission("voxelsniper.brush.blendvoxeldisc")
			.alias("bvd")
			.alias("blendvoxeldisc")
			.creator(BlendVoxelDiscBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBlobBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Blob")
			.permission("voxelsniper.brush.blob")
			.alias("blob")
			.alias("splatblob")
			.creator(BlobBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBlockResetBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("BlockReset")
			.permission("voxelsniper.brush.blockreset")
			.alias("brb")
			.alias("blockresetbrush")
			.creator(BlockResetBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerBlockResetSurfaceBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("BlockResetSurface")
			.permission("voxelsniper.brush.blockresetsurface")
			.alias("brbs")
			.alias("blockresetbrushsurface")
			.creator(BlockResetSurfaceBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCanyonBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Canyon")
			.permission("voxelsniper.brush.canyon")
			.alias("ca")
			.alias("canyon")
			.creator(CanyonBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCanyonSelectionBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("CanyonSelection")
			.permission("voxelsniper.brush.canyonselection")
			.alias("cas")
			.alias("canyonselection")
			.creator(CanyonSelectionBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCheckerVoxelDiscBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("CheckerVoxelDisc")
			.permission("voxelsniper.brush.checkervoxeldisc")
			.alias("cvd")
			.alias("checkervoxeldisc")
			.creator(CheckerVoxelDiscBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCleanSnowBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("CleanSnow")
			.permission("voxelsniper.brush.cleansnow")
			.alias("cls")
			.alias("cleansnow")
			.creator(CleanSnowBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCloneStampBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("CloneStamp")
			.permission("voxelsniper.brush.clonestamp")
			.alias("cs")
			.alias("clonestamp")
			.creator(CloneStampBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCometBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Comet")
			.permission("voxelsniper.brush.comet")
			.alias("com")
			.alias("comet")
			.creator(CometBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCopyPastaBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("CopyPasta")
			.permission("voxelsniper.brush.copypasta")
			.alias("cp")
			.alias("copypasta")
			.creator(CopyPastaBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerCylinderBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Cylinder")
			.permission("voxelsniper.brush.cylinder")
			.alias("c")
			.alias("cylinder")
			.creator(CylinderBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerDiscBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Disc")
			.permission("voxelsniper.brush.disc")
			.alias("d")
			.alias("disc")
			.creator(DiscBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerDiscFaceBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("DiscFace")
			.permission("voxelsniper.brush.discface")
			.alias("df")
			.alias("discface")
			.creator(DiscFaceBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerDomeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Dome")
			.permission("voxelsniper.brush.dome")
			.alias("dome")
			.alias("domebrush")
			.creator(DomeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerDrainBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Drain")
			.permission("voxelsniper.brush.drain")
			.alias("drain")
			.creator(DrainBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerEllipseBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Ellipse")
			.permission("voxelsniper.brush.ellipse")
			.alias("el")
			.alias("ellipse")
			.creator(EllipseBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerEllipsoidBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Ellipsoid")
			.permission("voxelsniper.brush.ellipsoid")
			.alias("elo")
			.alias("ellipsoid")
			.creator(EllipsoidBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerEntityBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Entity")
			.permission("voxelsniper.brush.entity")
			.alias("en")
			.alias("entity")
			.creator(EntityBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerEntityRemovalBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("EntityRemoval")
			.permission("voxelsniper.brush.entityremoval")
			.alias("er")
			.alias("entityremoval")
			.creator(EntityRemovalBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerEraserBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Eraser")
			.permission("voxelsniper.brush.eraser")
			.alias("erase")
			.alias("eraser")
			.creator(EraserBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerErodeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Erode")
			.permission("voxelsniper.brush.erode")
			.alias("e")
			.alias("erode")
			.creator(ErodeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerExtrudeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Extrude")
			.permission("voxelsniper.brush.extrude")
			.alias("ex")
			.alias("extrude")
			.creator(ExtrudeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerFillDownBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("FillDown")
			.permission("voxelsniper.brush.filldown")
			.alias("fd")
			.alias("filldown")
			.creator(FillDownBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerFlatOceanBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("FlatOcean")
			.permission("voxelsniper.brush.flatocean")
			.alias("fo")
			.alias("flatocean")
			.creator(FlatOceanBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerGenerateTreeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("GenerateTree")
			.permission("voxelsniper.brush.generatetree")
			.alias("gt")
			.alias("generatetree")
			.creator(GenerateTreeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerHeatRayBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("HeatRay")
			.permission("voxelsniper.brush.heatray")
			.alias("hr")
			.alias("heatray")
			.creator(HeatRayBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerJaggedLineBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("JaggedLine")
			.permission("voxelsniper.brush.jaggedline")
			.alias("j")
			.alias("jagged")
			.creator(JaggedLineBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerJockeyBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Jockey")
			.permission("voxelsniper.brush.jockey")
			.alias("jockey")
			.creator(JockeyBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerLightningBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Lightning")
			.permission("voxelsniper.brush.lightning")
			.alias("light")
			.alias("lightning")
			.creator(LightningBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerLineBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Line")
			.permission("voxelsniper.brush.line")
			.alias("l")
			.alias("line")
			.creator(LineBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerMoveBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Move")
			.permission("voxelsniper.brush.move")
			.alias("mv")
			.alias("move")
			.creator(MoveBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerOceanBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Ocean")
			.permission("voxelsniper.brush.ocean")
			.alias("o")
			.alias("ocean")
			.creator(OceanBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerOverlayBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Overlay")
			.permission("voxelsniper.brush.overlay")
			.alias("over")
			.alias("overlay")
			.creator(OverlayBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerPaintingBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Painting")
			.permission("voxelsniper.brush.painting")
			.alias("paint")
			.alias("painting")
			.creator(PaintingBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerPullBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Pull")
			.permission("voxelsniper.brush.pull")
			.alias("pull")
			.creator(PullBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerPunishBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Punish")
			.permission("voxelsniper.brush.punish")
			.alias("p")
			.alias("punish")
			.creator(PunishBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerRandomErodeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("RandomErode")
			.permission("voxelsniper.brush.randomerode")
			.alias("re")
			.alias("randomerode")
			.creator(RandomErodeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerRegenerateChunkBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("RegenerateChunk")
			.permission("voxelsniper.brush.regeneratechunk")
			.alias("gc")
			.alias("generatechunk")
			.creator(RegenerateChunkBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerRingBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Ring")
			.permission("voxelsniper.brush.ring")
			.alias("ri")
			.alias("ring")
			.creator(RingBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerRotation2DBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Rotation2D")
			.permission("voxelsniper.brush.rot2d")
			.alias("rot2")
			.alias("rotation2d")
			.creator(Rotation2DBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerRotation2DVerticalBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Rotation2DVertical")
			.permission("voxelsniper.brush.rot2dvert")
			.alias("rot2v")
			.alias("rotation2dvertical")
			.creator(Rotation2DVerticalBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerRotation3DBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Rotation3D")
			.permission("voxelsniper.brush.rot3d")
			.alias("rot3")
			.alias("rotation3d")
			.creator(Rotation3DBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerRulerBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Ruler")
			.permission("voxelsniper.brush.ruler")
			.alias("r")
			.alias("ruler")
			.creator(RulerBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerScannerBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Scanner")
			.permission("voxelsniper.brush.scanner")
			.alias("sc")
			.alias("scanner")
			.creator(ScannerBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSetBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Set")
			.permission("voxelsniper.brush.set")
			.alias("set")
			.creator(SetBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSetRedstoneFlipBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SetRedstoneFlip")
			.permission("voxelsniper.brush.setredstoneflip")
			.alias("setrf")
			.alias("setredstoneflip")
			.creator(SetRedstoneFlipBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSetRedstoneRotateBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SetRedstoneRotate")
			.permission("voxelsniper.brush.setredstonerotate")
			.alias("setrr")
			.alias("setredstonerotate")
			.creator(SetRedstoneRotateBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerShellBallBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("ShellBall")
			.permission("voxelsniper.brush.shellball")
			.alias("shb")
			.alias("shellball")
			.creator(ShellBallBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerShellSetBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("ShellSet")
			.permission("voxelsniper.brush.shellset")
			.alias("shs")
			.alias("shellset")
			.creator(ShellSetBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerShellVoxelBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("ShellVoxel")
			.permission("voxelsniper.brush.shellvoxel")
			.alias("shv")
			.alias("shellvoxel")
			.creator(ShellVoxelBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSignOverwriteBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SignOverwrite")
			.permission("voxelsniper.brush.signoverwrite")
			.alias("sio")
			.alias("signoverwriter")
			.creator(SignOverwriteBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSnipeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Snipe")
			.permission("voxelsniper.brush.snipe")
			.alias("s")
			.alias("snipe")
			.creator(SnipeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSnowConeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SnowCone")
			.permission("voxelsniper.brush.snowcone")
			.alias("snow")
			.alias("snowcone")
			.creator(SnowConeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSpiralStaircaseBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SpiralStaircase")
			.permission("voxelsniper.brush.spiralstaircase")
			.alias("sstair")
			.alias("spiralstaircase")
			.creator(SpiralStaircaseBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSplatterBallBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SplatterBall")
			.permission("voxelsniper.brush.splatterball")
			.alias("sb")
			.alias("splatball")
			.creator(SplatterBallBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSplatterDiscBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SplatterDisc")
			.permission("voxelsniper.brush.splatterdisc")
			.alias("sd")
			.alias("splatdisc")
			.creator(SplatterDiscBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSplatterOverlayBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SplatterOverlay")
			.permission("voxelsniper.brush.splatteroverlay")
			.alias("sover")
			.alias("splatteroverlay")
			.creator(SplatterOverlayBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSplatterVoxelBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SplatterVoxel")
			.permission("voxelsniper.brush.splattervoxel")
			.alias("sv")
			.alias("splattervoxel")
			.creator(SplatterVoxelBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSplatterVoxelDiscBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("SplatterDisc")
			.permission("voxelsniper.brush.splattervoxeldisc")
			.alias("svd")
			.alias("splatvoxeldisc")
			.creator(SplatterVoxelDiscBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerSplineBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Spline")
			.permission("voxelsniper.brush.spline")
			.alias("sp")
			.alias("spline")
			.creator(SplineBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerStampBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Stamp")
			.permission("voxelsniper.brush.stamp")
			.alias("stamp")
			.creator(StampBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerStencilBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Stencil")
			.permission("voxelsniper.brush.stencil")
			.alias("st")
			.alias("stencil")
			.creator(StencilBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerStencilListBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("StencilList")
			.permission("voxelsniper.brush.stencillist")
			.alias("sl")
			.alias("stencillist")
			.creator(StencilListBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerThreePointCircleBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("ThreePointCircle")
			.permission("voxelsniper.brush.threepointcircle")
			.alias("tpc")
			.alias("threepointcircle")
			.creator(ThreePointCircleBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerTreeSnipeBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("TreeSnipe")
			.permission("voxelsniper.brush.treesnipe")
			.alias("t")
			.alias("tree")
			.alias("treesnipe")
			.creator(TreeSnipeBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerTriangleBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Triangle")
			.permission("voxelsniper.brush.triangle")
			.alias("tri")
			.alias("triangle")
			.creator(TriangleBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerUnderlayBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Underlay")
			.permission("voxelsniper.brush.underlay")
			.alias("under")
			.alias("underlay")
			.creator(UnderlayBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerVoltMeterBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("VoltMeter")
			.permission("voxelsniper.brush.voltmeter")
			.alias("volt")
			.alias("voltmeter")
			.creator(VoltMeterBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerVoxelBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Voxel")
			.permission("voxelsniper.brush.voxel")
			.alias("v")
			.alias("voxel")
			.creator(VoxelBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerVoxelDiscBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("VoxelDisc")
			.permission("voxelsniper.brush.voxeldisc")
			.alias("vd")
			.alias("voxeldisc")
			.creator(VoxelDiscBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerVoxelDiscFaceBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("VoxelDiscFace")
			.permission("voxelsniper.brush.voxeldiscface")
			.alias("vdf")
			.alias("voxeldiscface")
			.creator(VoxelDiscFaceBrush::new)
			.build();
		this.registry.register(properties);
	}

	private void registerWarpBrush() {
		BrushProperties properties = BrushProperties.builder()
			.name("Warp")
			.permission("voxelsniper.brush.warp")
			.alias("world")
			.alias("warp")
			.creator(WarpBrush::new)
			.build();
		this.registry.register(properties);
	}
}
