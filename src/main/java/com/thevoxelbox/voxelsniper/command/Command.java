package com.thevoxelbox.voxelsniper.command;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class Command extends org.bukkit.command.Command {

    private final CommandProperties properties;
    private final CommandExecutor executor;
    private TabCompleter tabCompleter;

    public Command(CommandProperties properties, CommandExecutor executor) {
        super(properties.getName(), properties.getDescriptionOrDefault(), properties.getUsage(), properties.getAliases());
        setupPermission(properties);
        this.properties = properties;
        this.executor = executor;
        if (executor instanceof TabCompleter) {
            this.tabCompleter = (TabCompleter) executor;
        }
    }

    @SuppressWarnings("deprecation") // Deprecated on Paper in favor of adventure
    private void setupPermission(CommandProperties properties) {
        String permission = properties.getPermission();
        setPermission(permission);
        setPermissionMessage(
                LegacyComponentSerializer.legacy().serialize(
                        VoxelSniperText.format(Caption.of("voxelsniper.command.missing-permission", permission),
                                Locale.ROOT, true
                        )
                )
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Class<? extends CommandSender> senderType = this.properties.getSenderTypeOrDefault();
        if (!senderType.isInstance(sender)) {
            VoxelSniperText.print(
                    sender,
                    Caption.of("voxelsniper.command.wrong-sender-type", senderType.getSimpleName())
            );
            return true;
        }
        String permission = this.properties.getPermission();
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            VoxelSniperText.print(sender, Caption.of("voxelsniper.command.missing-permission", permission));
            return true;
        }
        this.executor.executeCommand(sender, args);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(
            @NotNull CommandSender sender,
            @NotNull String alias,
            @NotNull String[] args,
            @Nullable Location location
    ) {
        Class<? extends CommandSender> senderType = this.properties.getSenderTypeOrDefault();
        if (!senderType.isInstance(sender)) {
            return Collections.emptyList();
        }
        String permission = this.properties.getPermission();
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            return Collections.emptyList();
        }
        if (this.tabCompleter == null) {
            return super.tabComplete(sender, alias, args, location);
        }
        return this.tabCompleter.complete(sender, args);
    }

}
