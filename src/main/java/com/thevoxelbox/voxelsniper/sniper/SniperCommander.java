package com.thevoxelbox.voxelsniper.sniper;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import org.bukkit.command.CommandSender;

public interface SniperCommander {

    /**
     * Return the command sender
     *
     * @return the command sender
     * @since TODO
     */
    CommandSender getCommandSender();

    /**
     * Create a parser context from the sniper commander.
     *
     * @return the parser context
     * @since TODO
     */
    default ParserContext createParserContext() {
        CommandSender sender = getCommandSender();
        Actor actor = BukkitAdapter.adapt(sender);
        ParserContext parserContext = new ParserContext();
        parserContext.setSession(actor.getSession());
        parserContext.setWorld(actor instanceof Player wePlayer ? wePlayer.getWorld() : null);
        parserContext.setActor(actor);
        parserContext.setRestricted(false);
        return parserContext;
    }

    /**
     * Sends a component to a sniper. This method adds the prefix and handle translations.
     *
     * @param component component
     * @since 2.7.0
     */
    default void print(Component component) {
        print(component, true);
    }

    /**
     * Sends a component to a sniper. This method potentially adds the prefix and handle translations.
     *
     * @param component component
     * @param prefix    prefix
     * @since 2.7.0
     */
    void print(Component component, boolean prefix);

}
