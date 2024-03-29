package com.thevoxelbox.voxelsniper.sniper.snipe.message;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.brush.property.BrushPattern;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.MessageSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SnipeMessageSender {

    private final ToolkitProperties toolkitProperties;
    private final BrushProperties brushProperties;
    private final MessageSender messageSender;

    public SnipeMessageSender(ToolkitProperties toolkitProperties, BrushProperties brushProperties, Player player) {
        this.toolkitProperties = toolkitProperties;
        this.brushProperties = brushProperties;
        this.messageSender = new MessageSender(player);
    }

    public SnipeMessageSender brushNameMessage() {
        String brushName = this.brushProperties.getName();
        this.messageSender.brushNameMessage(brushName);
        return this;
    }

    public SnipeMessageSender patternMessage() {
        BrushPattern pattern = this.toolkitProperties.getPattern();
        this.messageSender.patternMessage(pattern);
        return this;
    }

    public SnipeMessageSender replacePatternMessage() {
        BrushPattern replacePattern = this.toolkitProperties.getReplacePattern();
        this.messageSender.replacePatternMessage(replacePattern);
        return this;
    }

    public SnipeMessageSender brushSizeMessage() {
        int brushSize = this.toolkitProperties.getBrushSize();
        this.messageSender.brushSizeMessage(brushSize);
        return this;
    }

    public SnipeMessageSender cylinderCenterMessage() {
        int cylinderCenter = this.toolkitProperties.getCylinderCenter();
        this.messageSender.cylinderCenterMessage(cylinderCenter);
        return this;
    }

    public SnipeMessageSender voxelHeightMessage() {
        int voxelHeight = this.toolkitProperties.getVoxelHeight();
        this.messageSender.voxelHeightMessage(voxelHeight);
        return this;
    }

    public SnipeMessageSender voxelListMessage() {
        Collection<BlockState> voxelList = this.toolkitProperties.getVoxelList();
        this.messageSender.voxelListMessage(voxelList);
        return this;
    }

    public SnipeMessageSender message(Component component) {
        this.messageSender.message(component);
        return this;
    }

    public void send() {
        this.messageSender.send();
    }

    public MessageSender getMessageSender() {
        return this.messageSender;
    }

}
