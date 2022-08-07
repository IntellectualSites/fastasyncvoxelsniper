package com.thevoxelbox.voxelsniper.command.executor;

import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.command.TabCompleter;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.util.ArtHelper;
import org.bukkit.Art;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PaintExecutor implements CommandExecutor, TabCompleter {

    private static final List<String> ART_NAMES = Arrays.stream(Art.values())
            .map(Art::name)
            .map(String::toLowerCase)
            .toList();

    private final VoxelSniperPlugin plugin;

    public PaintExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.registerAndGetSniper(player);
        if (sniper == null) {
            return;
        }
        if (arguments.length == 1) {
            if (arguments[0].equalsIgnoreCase("back")) {
                ArtHelper.paintAuto(player, true);
            } else {
                Art art = Art.getByName(arguments[0]);
                if (art == null) {
                    sniper.print(Caption.of("voxelsniper.command.paint.invalid-art", arguments[0]));
                    return;
                }
                ArtHelper.paint(player, art);
            }
        } else {
            ArtHelper.paintAuto(player, false);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] arguments) {
        if (arguments.length == 1) {
            String argument = arguments[0];
            String argumentLowered = argument.toLowerCase(Locale.ROOT);
            return ART_NAMES.stream()
                    .filter(artName -> artName.startsWith(argumentLowered))
                    .toList();
        }
        return Collections.emptyList();
    }

}
