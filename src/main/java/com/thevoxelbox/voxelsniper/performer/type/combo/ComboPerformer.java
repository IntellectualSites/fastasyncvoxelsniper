package com.thevoxelbox.voxelsniper.performer.type.combo;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@CommandMethod(value = "performer|perf|p combo|c")
@CommandPermission("voxelsniper.sniper")
public class ComboPerformer extends AbstractPerformer {

    private Pattern pattern;

    @CommandMethod("")
    public void onPerformer(
            final @NotNull PerformerSnipe snipe,
            final @NotNull ComboPerformer performer
    ) {
        performer.onPerformerCommand(snipe);
    }

    @Override
    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.pattern = toolkitProperties.getPattern().getPattern();
    }

    @Override
    public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
        setBlock(editSession, x, y, z, this.pattern);
    }

    @Override
    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender()
                .performerNameMessage()
                .patternMessage()
                .send();
    }

}
