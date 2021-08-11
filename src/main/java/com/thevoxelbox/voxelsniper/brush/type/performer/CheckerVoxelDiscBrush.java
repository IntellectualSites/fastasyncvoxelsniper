package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Stream;

public class CheckerVoxelDiscBrush extends AbstractPerformerBrush {

    private boolean useWorldCoordinates = true;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage("CheckerVoxelDisc Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b cvd [true|false] -- Enables or disables using World Coordinates.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("true")) {
                    this.useWorldCoordinates = true;
                    messenger.sendMessage(ChatColor.AQUA + "Enabled using World Coordinates.");
                } else if (firstParameter.equalsIgnoreCase("false")) {
                    this.useWorldCoordinates = false;
                    messenger.sendMessage(ChatColor.AQUA + "Disabled using World Coordinates.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display " +
                        "parameter info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("true", "false"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        applyBrush(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        applyBrush(snipe, lastBlock);
    }

    private void applyBrush(Snipe snipe, BlockVector3 target) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        for (int x = brushSize; x >= -brushSize; x--) {
            for (int y = brushSize; y >= -brushSize; y--) {
                int sum = this.useWorldCoordinates ? target.getX() + x + target.getZ() + y : x + y;
                if (sum % 2 != 0) {
                    this.performer.perform(
                            getEditSession(),
                            target.getX() + x,
                            clampY(target.getY()),
                            target.getZ() + y,
                            this.clampY(target.getX() + x, target.getY(), target.getZ() + y)
                    );
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .send();
    }

}
