package com.thevoxelbox.voxelsniper.sniper;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.command.CommandSender;

public class SniperSender implements SniperCommander {

    protected final CommandSender commandSender;

    /**
     * Create a sniper sender from a command sender.
     *
     * @param commandSender the command sender
     * @since TODO
     */
    public SniperSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Override
    public CommandSender getCommandSender() {
        return commandSender;
    }

    @Override
    public void print(Component component, boolean prefix) {
        VoxelSniperText.print(commandSender, component, prefix);
    }

}
