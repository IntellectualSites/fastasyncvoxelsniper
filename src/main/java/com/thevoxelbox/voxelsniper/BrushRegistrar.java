package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.brush.type.BiomeBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.BallBrush;

public class BrushRegistrar {

	private BrushRegistry registry;

	public BrushRegistrar(BrushRegistry registry) {
		this.registry = registry;
	}

	public void registerBrushes() {
		registerBallBrush();
		registerBiomeBrush();
		//TODO: register all brushes
		/*
		//this.registry.registerBrushType(BallBrush.class, "b", "ball");
		//this.registry.registerBrushType(BiomeBrush.class, "bio", "biome");
		this.registry.registerBrushType(BlendBallBrush.class, "bb", "blendball");
		this.registry.registerBrushType(BlendDiscBrush.class, "bd", "blenddisc");
		this.registry.registerBrushType(BlendVoxelBrush.class, "bv", "blendvoxel");
		this.registry.registerBrushType(BlendVoxelDiscBrush.class, "bvd", "blendvoxeldisc");
		this.registry.registerBrushType(BlobBrush.class, "blob", "splatblob");
		this.registry.registerBrushType(BlockResetBrush.class, "brb", "blockresetbrush");
		this.registry.registerBrushType(BlockResetSurfaceBrush.class, "brbs", "blockresetbrushsurface");
		this.registry.registerBrushType(CanyonBrush.class, "ca", "canyon");
		this.registry.registerBrushType(CanyonSelectionBrush.class, "cas", "canyonselection");
		this.registry.registerBrushType(CheckerVoxelDiscBrush.class, "cvd", "checkervoxeldisc");
		this.registry.registerBrushType(CleanSnowBrush.class, "cls", "cleansnow");
		this.registry.registerBrushType(CloneStampBrush.class, "cs", "clonestamp");
		this.registry.registerBrushType(CometBrush.class, "com", "comet");
		this.registry.registerBrushType(CopyPastaBrush.class, "cp", "copypasta");
		this.registry.registerBrushType(CylinderBrush.class, "c", "cylinder");
		this.registry.registerBrushType(DiscBrush.class, "d", "disc");
		this.registry.registerBrushType(DiscFaceBrush.class, "df", "discface");
		this.registry.registerBrushType(DomeBrush.class, "dome", "domebrush");
		this.registry.registerBrushType(DrainBrush.class, "drain");
		this.registry.registerBrushType(EllipseBrush.class, "el", "ellipse");
		this.registry.registerBrushType(EllipsoidBrush.class, "elo", "ellipsoid");
		this.registry.registerBrushType(EntityBrush.class, "en", "entity");
		this.registry.registerBrushType(EntityRemovalBrush.class, "er", "entityremoval");
		this.registry.registerBrushType(EraserBrush.class, "erase", "eraser");
		this.registry.registerBrushType(ErodeBrush.class, "e", "erode");
		this.registry.registerBrushType(ExtrudeBrush.class, "ex", "extrude");
		this.registry.registerBrushType(FillDownBrush.class, "fd", "filldown");
		this.registry.registerBrushType(FlatOceanBrush.class, "fo", "flatocean");
		this.registry.registerBrushType(GenerateTreeBrush.class, "gt", "generatetree");
		this.registry.registerBrushType(HeatRayBrush.class, "hr", "heatray");
		this.registry.registerBrushType(JaggedLineBrush.class, "j", "jagged");
		this.registry.registerBrushType(JockeyBrush.class, "jockey");
		this.registry.registerBrushType(LightningBrush.class, "light", "lightning");
		this.registry.registerBrushType(LineBrush.class, "l", "line");
		this.registry.registerBrushType(MoveBrush.class, "mv", "move");
		this.registry.registerBrushType(OceanBrush.class, "o", "ocean");
		this.registry.registerBrushType(OverlayBrush.class, "over", "overlay");
		this.registry.registerBrushType(PaintingBrush.class, "paint", "painting");
		this.registry.registerBrushType(PullBrush.class, "pull");
		this.registry.registerBrushType(PunishBrush.class, "p", "punish");
		this.registry.registerBrushType(RandomErodeBrush.class, "re", "randomerode");
		this.registry.registerBrushType(RegenerateChunkBrush.class, "gc", "generatechunk");
		this.registry.registerBrushType(RingBrush.class, "ri", "ring");
		this.registry.registerBrushType(Rotation2DBrush.class, "rot2", "rotation2d");
		this.registry.registerBrushType(Rotation2DVerticalBrush.class, "rot2v", "rotation2dvertical");
		this.registry.registerBrushType(Rotation3DBrush.class, "rot3", "rotation3d");
		this.registry.registerBrushType(RulerBrush.class, "r", "ruler");
		this.registry.registerBrushType(ScannerBrush.class, "sc", "scanner");
		this.registry.registerBrushType(SetBrush.class, "set");
		this.registry.registerBrushType(SetRedstoneFlipBrush.class, "setrf", "setredstoneflip");
		this.registry.registerBrushType(ShellBallBrush.class, "shb", "shellball");
		this.registry.registerBrushType(ShellSetBrush.class, "shs", "shellset");
		this.registry.registerBrushType(ShellVoxelBrush.class, "shv", "shellvoxel");
		this.registry.registerBrushType(SignOverwriteBrush.class, "sio", "signoverwriter");
		this.registry.registerBrushType(SnipeBrush.class, "s", "snipe");
		this.registry.registerBrushType(SnowConeBrush.class, "snow", "snowcone");
		this.registry.registerBrushType(SpiralStaircaseBrush.class, "sstair", "spiralstaircase");
		this.registry.registerBrushType(SplatterBallBrush.class, "sb", "splatball");
		this.registry.registerBrushType(SplatterDiscBrush.class, "sd", "splatdisc");
		this.registry.registerBrushType(SplatterOverlayBrush.class, "sover", "splatteroverlay");
		this.registry.registerBrushType(SplatterVoxelBrush.class, "sv", "splattervoxel");
		this.registry.registerBrushType(SplatterDiscBrush.class, "svd", "splatvoxeldisc");
		this.registry.registerBrushType(SplineBrush.class, "sp", "spline");
		this.registry.registerBrushType(StencilBrush.class, "st", "stencil");
		this.registry.registerBrushType(StencilListBrush.class, "sl", "stencillist");
		this.registry.registerBrushType(ThreePointCircleBrush.class, "tpc", "threepointcircle");
		this.registry.registerBrushType(TreeSnipeBrush.class, "t", "tree", "treesnipe");
		this.registry.registerBrushType(TriangleBrush.class, "tri", "triangle");
		this.registry.registerBrushType(UnderlayBrush.class, "under", "underlay");
		this.registry.registerBrushType(VoltMeterBrush.class, "volt", "voltmeter");
		this.registry.registerBrushType(VoxelBrush.class, "v", "voxel");
		this.registry.registerBrushType(VoxelDiscBrush.class, "vd", "voxeldisc");
		this.registry.registerBrushType(VoxelDiscFaceBrush.class, "vdf", "voxeldiscface");
		this.registry.registerBrushType(WarpBrush.class, "world", "warp");
		*/
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
}
