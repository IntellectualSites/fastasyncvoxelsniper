package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@CommandMethod(value = "brush_toolkit|brushtoolkit|btool")
@CommandDescription("Brush toolkit.")
@CommandPermission("voxelsniper.sniper")
public class BrushToolkitExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public BrushToolkitExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("assign <action> <toolkit-name>")
    public void onBrushToolkitAssign(
            final @NotNull Sniper sniper,
            final @NotNull @Argument("action") ToolAction action,
            final @NotNull @Argument("toolkit-name") String name
    ) {
        Player player = sniper.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();
        ItemType itemType = BukkitAdapter.asItemType(itemInHand.getType());
        if (itemType == null || itemType == ItemTypes.AIR) {
            sniper.print(Caption.of("voxelsniper.command.toolkit.assign-help"));
            return;
        }

        Toolkit toolkit = sniper.getToolkit(name);
        if (toolkit == null) {
            toolkit = new Toolkit(name);
        }
        if (toolkit.isDefault()) {
            sniper.print(Caption.of("voxelsniper.command.toolkit.default-tool"));
            return;
        }

        toolkit.addToolAction(itemType, action);
        sniper.addToolkit(toolkit);
        sniper.print(Caption.of("voxelsniper.command.toolkit.assigned", itemType.getRichName(),
                toolkit.getToolkitName(), action.name()
        ));
    }

    @CommandMethod("remove <toolkit>")
    public void onBrushToolkitRemove(
            final @NotNull Sniper sniper,
            final @NotNull @Argument("toolkit") Toolkit toolkit
    ) {
        if (toolkit.isDefault()) {
            sniper.print(Caption.of("voxelsniper.command.toolkit.default-tool"));
            return;
        }

        sniper.removeToolkit(toolkit);
        sniper.print(Caption.of("voxelsniper.command.toolkit.removed", toolkit.getToolkitName()));
    }

    @CommandMethod("remove")
    public void onBrushToolkitRemove(
            final @NotNull Sniper sniper
    ) {
        Player player = sniper.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();
        ItemType itemType = BukkitAdapter.asItemType(itemInHand.getType());
        if (itemType == null || itemType == ItemTypes.AIR) {
            sniper.print(Caption.of("voxelsniper.command.toolkit.empty-hands"));
            return;
        }

        Toolkit toolkit = sniper.getCurrentToolkit();
        if (toolkit == null || toolkit.isDefault()) {
            sniper.print(Caption.of("voxelsniper.command.toolkit.default-tool"));
            return;
        }

        toolkit.removeToolAction(itemType);
        sniper.print(Caption.of("voxelsniper.command.toolkit.unassigned", itemType.getRichName(),
                toolkit.getToolkitName()
        ));
    }

}
