package com.thevoxelbox.voxelsniper.performer.type.material;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "performer|perf|p mat-nophys|mp")
@CommandPermission("voxelsniper.sniper")
public class MaterialNoPhysicsPerformer extends AbstractPerformer {

    private Pattern pattern;

    @CommandMethod("")
    public void onPerformer(
            final @NotNull PerformerSnipe snipe
    ) {
        super.onPerformerCommand(snipe);
    }

    @Override
    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.pattern = toolkitProperties.getPattern().getPattern();
    }

    @Override
    public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
        BaseBlock baseBlock = simulateSetBlock(x, y, z, pattern);
        if (block.getBlockType() != baseBlock.getBlockType()) {
            setBlock(editSession, x, y, z, baseBlock.toBlockState());
        }
    }

    @Override
    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender()
                .performerNameMessage()
                .patternMessage()
                .send();
    }

}
