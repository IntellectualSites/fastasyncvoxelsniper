package com.thevoxelbox.voxelsniper.performer.type.ink;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@RequireToolkit
@CommandMethod(value = "performer|perf|p exclude-ink|xi")
@CommandPermission("voxelsniper.sniper")
public class ExcludeInkPerformer extends AbstractPerformer {

    private Collection<BlockState> excludeList;
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
        this.excludeList = toolkitProperties.getVoxelList();
    }

    @Override
    public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
        if (!this.excludeList.contains(block)) {
            setBlock(editSession, x, y, z, this.pattern);
        }
    }

    @Override
    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender()
                .performerNameMessage()
                .voxelListMessage()
                .patternMessage()
                .send();
    }

}
