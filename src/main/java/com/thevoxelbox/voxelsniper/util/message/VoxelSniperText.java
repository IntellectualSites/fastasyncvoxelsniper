package com.thevoxelbox.voxelsniper.util.message;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.TextAdapter;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enginehub.piston.config.ConfigHolder;
import org.enginehub.piston.config.ConfigRenderer;
import org.enginehub.piston.util.TextHelper;

import java.util.Collection;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Component text utilities for VoxelSniper.
 */
public class VoxelSniperText {

    private static final ConfigRenderer RENDERER = ConfigRenderer.getInstance();

    public static final ConfigHolder CONFIG_HOLDER = ConfigHolder.create();

    /**
     * Print.
     *
     * @param target    the target
     * @param component the component
     * @since 2.7.0
     */
    public static void print(CommandSender target, Component component) {
        print(target, component, true);
    }

    /**
     * Prints a component.
     *
     * @param target    the target
     * @param component the component
     * @param prefix    the prefix
     * @since 2.7.0
     */
    public static void print(CommandSender target, Component component, boolean prefix) {
        print(target, component, target instanceof Player player
                ? TextUtils.getLocaleByMinecraftTag(player.getLocale())
                : Locale.ROOT, prefix);
    }

    /**
     * Prints a component.
     *
     * @param target    the target
     * @param component the component
     * @param locale    the locale
     * @param prefix    the prefix
     * @since 2.7.0
     */
    public static void print(CommandSender target, Component component, Locale locale, boolean prefix) {
        TextAdapter.sendMessage(target, format(component, locale, prefix));
    }

    /**
     * Formats a component according to locale, prefix presence and children.
     *
     * @param component the component
     * @param locale    the locale
     * @param prefix    the prefix
     * @return the component
     * @since 2.7.0
     */
    public static Component format(Component component, Locale locale, boolean prefix) {
        if (prefix) {
            component = TranslatableComponent.of("prefix", component);
        }
        return Caption.color(
                VoxelSniperPlugin.plugin.getTranslationManager().convertText(
                        RENDERER.render(component, CONFIG_HOLDER),
                        locale
                ));
    }

    /**
     * Reduces a caption to plain text.
     *
     * @param component the component
     * @param locale    the locale
     * @param prefix    the prefix
     * @return the string
     * @since 2.7.0
     */
    public static String reduceToText(Component component, Locale locale, boolean prefix) {
        return TextHelper.reduceToText(format(component, locale, prefix));
    }

    /**
     * Gets corresponding status.
     *
     * @param status the status
     * @return the status component
     * @since 2.7.0
     */
    public static Component getStatus(boolean status) {
        return Caption.of(status ? "voxelsniper.sniper.enabled" : "voxelsniper.sniper.disabled");
    }

    /**
     * Formats simple list component (same delimiter).
     *
     * @param <T>               the provided collection type
     * @param collection        the collection
     * @param compareFunction   the compare function, compares two collection objects
     * @param transformFunction the transform function, transforms a collection object to a component
     * @param path              the path
     * @return the component
     * @since 2.7.0
     */
    public static <T> Component formatList(
            Collection<T> collection,
            BiFunction<T, T, Integer> compareFunction,
            Function<T, Component> transformFunction,
            String path
    ) {
        return Caption.of(path + ".list-prefix", collection.stream()
                .sorted(compareFunction::apply)
                .reduce(
                        TextComponent.empty(), (component, element) -> {
                            if (!component.isEmpty()) {
                                component = component.append(Caption.of("voxelsniper.messenger.list-delimiter"));
                            }
                            return component.append(
                                    Caption.of(
                                            "voxelsniper.messenger.list-element",
                                            transformFunction.apply(element)
                                    )
                            );
                        },
                        ScopedComponent::append
                )
        );
    }

    /**
     * Formats list with current component (delimiter may differ).
     *
     * @param <T>               the provided collection type
     * @param collection        the collection
     * @param compareFunction   the compare function, compares two collection objects
     * @param transformFunction the transform function, transforms a collection object to a component
     * @param extractFunction   the extract function, transforms a collection object to a current comparable object
     * @param current           the current
     * @param path              the path
     * @return the component
     * @since 2.7.0
     */
    public static <T> Component formatListWithCurrent(
            Collection<T> collection,
            BiFunction<T, T, Integer> compareFunction,
            Function<T, Component> transformFunction,
            Function<T, Object> extractFunction,
            Object current,
            String path
    ) {
        return Caption.of(path + ".list-prefix", collection.stream()
                .sorted(compareFunction::apply)
                .reduce(
                        TextComponent.empty(), (component, element) -> {
                            if (!component.isEmpty()) {
                                component = component.append(Caption.of("voxelsniper.messenger.list-delimiter"));
                            }
                            return component.append(
                                    Caption.of(
                                            extractFunction.apply(element) == current
                                                    ? "voxelsniper.messenger.list-current" : "voxelsniper.messenger.list-other",
                                            transformFunction.apply(element)
                                    )
                            );
                        },
                        ScopedComponent::append
                )
        );
    }

    private VoxelSniperText() {
    }

}
