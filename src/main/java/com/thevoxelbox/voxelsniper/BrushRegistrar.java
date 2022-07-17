package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.property.BrushPatternType;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.brush.type.BiomeBrush;
import com.thevoxelbox.voxelsniper.brush.type.BlockResetBrush;
import com.thevoxelbox.voxelsniper.brush.type.BlockResetSurfaceBrush;
import com.thevoxelbox.voxelsniper.brush.type.CleanSnowBrush;
import com.thevoxelbox.voxelsniper.brush.type.CometBrush;
import com.thevoxelbox.voxelsniper.brush.type.CopyPastaBrush;
import com.thevoxelbox.voxelsniper.brush.type.DomeBrush;
import com.thevoxelbox.voxelsniper.brush.type.DrainBrush;
import com.thevoxelbox.voxelsniper.brush.type.EraserBrush;
import com.thevoxelbox.voxelsniper.brush.type.ErodeBlendBrush;
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
import com.thevoxelbox.voxelsniper.brush.type.RulerBrush;
import com.thevoxelbox.voxelsniper.brush.type.ScannerBrush;
import com.thevoxelbox.voxelsniper.brush.type.SignOverwriteBrush;
import com.thevoxelbox.voxelsniper.brush.type.SnowConeBrush;
import com.thevoxelbox.voxelsniper.brush.type.SpiralStaircaseBrush;
import com.thevoxelbox.voxelsniper.brush.type.TreeSnipeBrush;
import com.thevoxelbox.voxelsniper.brush.type.VoltmeterBrush;
import com.thevoxelbox.voxelsniper.brush.type.WarpBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendBallBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.canyon.CanyonBrush;
import com.thevoxelbox.voxelsniper.brush.type.canyon.CanyonSelectionBrush;
import com.thevoxelbox.voxelsniper.brush.type.entity.EntityBrush;
import com.thevoxelbox.voxelsniper.brush.type.entity.EntityRemovalBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.BallBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.BlobBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.CheckerVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.CylinderBrush;
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
import com.thevoxelbox.voxelsniper.brush.type.performer.SplineBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.ThreePointCircleBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.TriangleBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.UnderlayBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.VoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.DiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.DiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.VoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.disc.VoxelDiscFaceBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterBallBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterOverlayBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.performer.splatter.SplatterVoxelDiscBrush;
import com.thevoxelbox.voxelsniper.brush.type.redstone.SetRedstoneFlipBrush;
import com.thevoxelbox.voxelsniper.brush.type.redstone.SetRedstoneRotateBrush;
import com.thevoxelbox.voxelsniper.brush.type.rotation.Rotation2DBrush;
import com.thevoxelbox.voxelsniper.brush.type.rotation.Rotation2DVerticalBrush;
import com.thevoxelbox.voxelsniper.brush.type.rotation.Rotation3DBrush;
import com.thevoxelbox.voxelsniper.brush.type.shell.ShellBallBrush;
import com.thevoxelbox.voxelsniper.brush.type.shell.ShellSetBrush;
import com.thevoxelbox.voxelsniper.brush.type.shell.ShellVoxelBrush;
import com.thevoxelbox.voxelsniper.brush.type.stamp.CloneStampBrush;
import com.thevoxelbox.voxelsniper.brush.type.stencil.StencilBrush;
import com.thevoxelbox.voxelsniper.brush.type.stencil.StencilListBrush;

import java.io.File;

public class BrushRegistrar {

    public static final BrushProperties DEFAULT_BRUSH_PROPERTIES = BrushProperties.builder()
            .name("Snipe")
            .permission("voxelsniper.brush.snipe")
            .alias("s")
            .alias("snipe")
            .brushPatternType(BrushPatternType.ANY)
            .creator(SnipeBrush::new)
            .build();

    private final BrushRegistry registry;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final File pluginDataFolder;

    public BrushRegistrar(BrushRegistry registry, File pluginDataFolder) {
        this.registry = registry;
        this.pluginDataFolder = pluginDataFolder;
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
        registerErodeBlendBrush();
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
        registerStencilBrush();
        registerStencilListBrush();
        registerThreePointCircleBrush();
        registerTreeSnipeBrush();
        registerTriangleBrush();
        registerUnderlayBrush();
        registerVoltmeterBrush();
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
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(BallBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerBiomeBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Biome")
                .permission("voxelsniper.brush.biome")
                .alias("bio")
                .alias("biome")
                .brushPatternType(BrushPatternType.ANY)
                .creator(BiomeBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerBlendBallBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Blend Ball")
                .permission("voxelsniper.brush.blendball")
                .alias("bb")
                .alias("blendball")
                .alias("blend_ball")
                .brushPatternType(BrushPatternType.ANY)
                .creator(BlendBallBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerBlendDiscBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Blend Disc")
                .permission("voxelsniper.brush.blenddisc")
                .alias("bd")
                .alias("blenddisc")
                .alias("blend_disc")
                .brushPatternType(BrushPatternType.ANY)
                .creator(BlendDiscBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerBlendVoxelBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Blend Voxel")
                .permission("voxelsniper.brush.blendvoxel")
                .alias("bv")
                .alias("blendvoxel")
                .alias("blend_voxel")
                .brushPatternType(BrushPatternType.ANY)
                .creator(BlendVoxelBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerBlendVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Blend Voxel Disc")
                .permission("voxelsniper.brush.blendvoxeldisc")
                .alias("bvd")
                .alias("blendvoxeldisc")
                .alias("blend_voxel_disc")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(BlobBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerBlockResetBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Block Reset")
                .permission("voxelsniper.brush.blockreset")
                .alias("brb")
                .alias("blockresetbrush")
                .alias("block_reset")
                .brushPatternType(BrushPatternType.ANY)
                .creator(BlockResetBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerBlockResetSurfaceBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Block Reset Surface")
                .permission("voxelsniper.brush.blockresetsurface")
                .alias("brbs")
                .alias("blockresetbrushsurface")
                .alias("block_reset_surface")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.ANY)
                .creator(CanyonBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerCanyonSelectionBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Canyon Selection")
                .permission("voxelsniper.brush.canyonselection")
                .alias("cas")
                .alias("canyonselection")
                .alias("canyon_selection")
                .brushPatternType(BrushPatternType.ANY)
                .creator(CanyonSelectionBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerCheckerVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Checker Voxel Disc")
                .permission("voxelsniper.brush.checkervoxeldisc")
                .alias("cvd")
                .alias("checkervoxeldisc")
                .alias("checker_voxel_disc")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(CheckerVoxelDiscBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerCleanSnowBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Clean Snow")
                .permission("voxelsniper.brush.cleansnow")
                .alias("cls")
                .alias("cleansnow")
                .alias("clean_snow")
                .brushPatternType(BrushPatternType.ANY)
                .creator(CleanSnowBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerCloneStampBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Clone Stamp")
                .permission("voxelsniper.brush.clonestamp")
                .alias("cs")
                .alias("clonestamp")
                .alias("clone_stamp")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.ANY)
                .creator(CometBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerCopyPastaBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Copy Pasta")
                .permission("voxelsniper.brush.copypasta")
                .alias("cp")
                .alias("copypasta")
                .alias("copy_pasta")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(DiscBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerDiscFaceBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Disc Face")
                .permission("voxelsniper.brush.discface")
                .alias("df")
                .alias("discface")
                .alias("disc_face")
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(DomeBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerDrainBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Drain")
                .permission("voxelsniper.brush.drain")
                .alias("drain")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.ANY)
                .creator(EntityBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerEntityRemovalBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Entity Removal")
                .permission("voxelsniper.brush.entityremoval")
                .alias("er")
                .alias("entityremoval")
                .alias("entity_removal")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.ANY)
                .creator(EraserBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerErodeBlendBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Erode BlendBall")
                .permission("voxelsniper.brush.erodeblend")
                .alias("eb")
                .alias("erodeblend")
                .alias("erodeblendball")
                .brushPatternType(BrushPatternType.ANY)
                .creator(ErodeBlendBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerErodeBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Erode")
                .permission("voxelsniper.brush.erode")
                .alias("e")
                .alias("erode")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.ANY)
                .creator(ExtrudeBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerFillDownBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Fill Down")
                .permission("voxelsniper.brush.filldown")
                .alias("fd")
                .alias("filldown")
                .alias("fill_down")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(FillDownBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerFlatOceanBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Flat Ocean")
                .permission("voxelsniper.brush.flatocean")
                .alias("fo")
                .alias("flatocean")
                .alias("flat_ocean")
                .brushPatternType(BrushPatternType.ANY)
                .creator(FlatOceanBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerGenerateTreeBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Generate Tree")
                .permission("voxelsniper.brush.generatetree")
                .alias("gt")
                .alias("generatetree")
                .alias("generate_tree")
                .brushPatternType(BrushPatternType.ANY)
                .creator(GenerateTreeBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerHeatRayBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Heat Ray")
                .permission("voxelsniper.brush.heatray")
                .alias("hr")
                .alias("heatray")
                .alias("heat_ray")
                .brushPatternType(BrushPatternType.ANY)
                .creator(HeatRayBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerJaggedLineBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Jagged Line")
                .permission("voxelsniper.brush.jaggedline")
                .alias("j")
                .alias("jagged")
                .alias("jagged_line")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(JaggedLineBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerJockeyBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Jockey")
                .permission("voxelsniper.brush.jockey")
                .alias("jockey")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.ANY)
                .creator(PaintingBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerPullBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Pull")
                .permission("voxelsniper.brush.pull")
                .alias("pull")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.SINGLE_BLOCK)
                .creator(PunishBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerRandomErodeBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Random Erode")
                .permission("voxelsniper.brush.randomerode")
                .alias("re")
                .alias("randomerode")
                .alias("randome_rode")
                .brushPatternType(BrushPatternType.ANY)
                .creator(RandomErodeBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerRegenerateChunkBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Regenerate Chunk")
                .permission("voxelsniper.brush.regeneratechunk")
                .alias("gc")
                .alias("generatechunk")
                .alias("regenerate_chunk")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(RingBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerRotation2DBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Rotation 2D")
                .permission("voxelsniper.brush.rot2d")
                .alias("rot2")
                .alias("rotation2d")
                .alias("rotation_2d")
                .brushPatternType(BrushPatternType.ANY)
                .creator(Rotation2DBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerRotation2DVerticalBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Rotation 2D Vertical")
                .permission("voxelsniper.brush.rot2dvert")
                .alias("rot2v")
                .alias("rotation2dvertical")
                .alias("rotation_2d_vertical")
                .brushPatternType(BrushPatternType.ANY)
                .creator(Rotation2DVerticalBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerRotation3DBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Rotation 3D")
                .permission("voxelsniper.brush.rot3d")
                .alias("rot3")
                .alias("rotation3d")
                .alias("rotation_3d")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.SINGLE_BLOCK)
                .creator(ScannerBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSetBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Set")
                .permission("voxelsniper.brush.set")
                .alias("set")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(SetBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSetRedstoneFlipBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Set Redstone Flip")
                .permission("voxelsniper.brush.setredstoneflip")
                .alias("setrf")
                .alias("setredstoneflip")
                .alias("set_redstone_flip")
                .brushPatternType(BrushPatternType.ANY)
                .creator(SetRedstoneFlipBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSetRedstoneRotateBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Set Redstone Rotate")
                .permission("voxelsniper.brush.setredstonerotate")
                .alias("setrr")
                .alias("setredstonerotate")
                .alias("set_redstone_rotate")
                .brushPatternType(BrushPatternType.ANY)
                .creator(SetRedstoneRotateBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerShellBallBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Shell Ball")
                .permission("voxelsniper.brush.shellball")
                .alias("shb")
                .alias("shellball")
                .alias("shell_ball")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(ShellBallBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerShellSetBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Shell Set")
                .permission("voxelsniper.brush.shellset")
                .alias("shs")
                .alias("shellset")
                .alias("shell_set")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(ShellSetBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerShellVoxelBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Shell Voxel")
                .permission("voxelsniper.brush.shellvoxel")
                .alias("shv")
                .alias("shellvoxel")
                .alias("shell_voxel")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(ShellVoxelBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSignOverwriteBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Sign Overwrite")
                .permission("voxelsniper.brush.signoverwrite")
                .alias("sio")
                .alias("signoverwriter")
                .alias("sign_overwrite")
                .brushPatternType(BrushPatternType.ANY)
                .creator(SignOverwriteBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSnipeBrush() {
        this.registry.register(DEFAULT_BRUSH_PROPERTIES);
    }

    private void registerSnowConeBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Snow Cone")
                .permission("voxelsniper.brush.snowcone")
                .alias("snow")
                .alias("snowcone")
                .alias("snow_cone")
                .brushPatternType(BrushPatternType.ANY)
                .creator(SnowConeBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSpiralStaircaseBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Spiral Staircase")
                .permission("voxelsniper.brush.spiralstaircase")
                .alias("sstair")
                .alias("spiralstaircase")
                .alias("spiral_staircase")
                .brushPatternType(BrushPatternType.SINGLE_BLOCK)
                .creator(SpiralStaircaseBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSplatterBallBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Splatter Ball")
                .permission("voxelsniper.brush.splatterball")
                .alias("sb")
                .alias("splatball")
                .alias("splatter_ball")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(SplatterBallBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSplatterDiscBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Splatter Disc")
                .permission("voxelsniper.brush.splatterdisc")
                .alias("sd")
                .alias("splatdisc")
                .alias("splatter_disc")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(SplatterDiscBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSplatterOverlayBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Splatter Overlay")
                .permission("voxelsniper.brush.splatteroverlay")
                .alias("sover")
                .alias("splatteroverlay")
                .alias("splatter_overlay")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(SplatterOverlayBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSplatterVoxelBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Splatter Voxel")
                .permission("voxelsniper.brush.splattervoxel")
                .alias("sv")
                .alias("splattervoxel")
                .alias("splatter_voxel")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(SplatterVoxelBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerSplatterVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Splatter Voxel Disc")
                .permission("voxelsniper.brush.splattervoxeldisc")
                .alias("svd")
                .alias("splatvoxeldisc")
                .alias("splatter_voxel_disc")
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(SplineBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerStencilBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Stencil")
                .permission("voxelsniper.brush.stencil")
                .alias("st")
                .alias("stencil")
                .brushPatternType(BrushPatternType.ANY)
                .creator(StencilBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerStencilListBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Stencil List")
                .permission("voxelsniper.brush.stencillist")
                .alias("sl")
                .alias("stencillist")
                .alias("stencil_list")
                .brushPatternType(BrushPatternType.ANY)
                .creator(StencilListBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerThreePointCircleBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Three Point Circle")
                .permission("voxelsniper.brush.threepointcircle")
                .alias("tpc")
                .alias("threepointcircle")
                .alias("three_point_circle")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(ThreePointCircleBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerTreeSnipeBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Tree Snipe")
                .permission("voxelsniper.brush.treesnipe")
                .alias("t")
                .alias("tree")
                .alias("treesnipe")
                .alias("tree_snipe")
                .brushPatternType(BrushPatternType.ANY)
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
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(UnderlayBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerVoltmeterBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Voltmeter")
                .permission("voxelsniper.brush.voltmeter")
                .alias("volt")
                .alias("voltmeter")
                .brushPatternType(BrushPatternType.ANY)
                .creator(VoltmeterBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerVoxelBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Voxel")
                .permission("voxelsniper.brush.voxel")
                .alias("v")
                .alias("voxel")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(VoxelBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Voxel Disc")
                .permission("voxelsniper.brush.voxeldisc")
                .alias("vd")
                .alias("voxeldisc")
                .alias("voxel_disc")
                .brushPatternType(BrushPatternType.PATTERN)
                .creator(VoxelDiscBrush::new)
                .build();
        this.registry.register(properties);
    }

    private void registerVoxelDiscFaceBrush() {
        BrushProperties properties = BrushProperties.builder()
                .name("Voxel Disc Face")
                .permission("voxelsniper.brush.voxeldiscface")
                .alias("vdf")
                .alias("voxeldiscface")
                .alias("voxel_disc_face")
                .brushPatternType(BrushPatternType.PATTERN)
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
                .brushPatternType(BrushPatternType.ANY)
                .creator(WarpBrush::new)
                .build();
        this.registry.register(properties);
    }

}
