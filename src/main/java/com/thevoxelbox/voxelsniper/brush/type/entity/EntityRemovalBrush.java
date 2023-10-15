package com.thevoxelbox.voxelsniper.brush.type.entity;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.EntityClassArgument;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequireToolkit
@CommandMethod(value = "brush|b entity_removal|entityremoval|er")
@CommandPermission("voxelsniper.brush.entityremoval")
public class EntityRemovalBrush extends AbstractBrush {

    private static final List<String> DEFAULT_EXEMPTIONS = Arrays.asList(
            "org.bukkit.entity.Player",
            "org.bukkit.entity.Hanging",
            "org.bukkit.entity.NPC"
    );

    private List<String> exemptions;

    @SuppressWarnings("unchecked")
    @Override
    public void loadProperties() {
        this.exemptions = new ArrayList<>(
                (List<String>) getListProperty("default-exemptions", DEFAULT_EXEMPTIONS)
        );
    }

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.entity.removal"));
    }

    @CommandMethod("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatList(
                EntityClassArgument.ENTITY_CLASSES,
                String::compareTo,
                TextComponent::of,
                "voxelsniper.brush.entity-removal"
        ));
    }

    @CommandMethod("add <entity-class>")
    public void onBrushPlus(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "entity-class", parserName = "entity-class_parser") Class<? extends Entity> entityClass
    ) {
        this.exemptions.add(entityClass.getCanonicalName());

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.entity-removal.add-entity-class",
                entityClass
        ));
    }

    @CommandMethod("remove <entity-class>")
    public void onBrushMinus(
            final @NotNull Snipe snipe,
            final @NotNull @Argument(value = "entity-class", parserName = "entity-class_parser") Class<? extends Entity> entityClass
    ) {
        this.exemptions.remove(entityClass.getCanonicalName());

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.entity-removal.remove-entity-class",
                entityClass
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        radialRemoval(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        radialRemoval(snipe);
    }

    private void radialRemoval(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        int entityCount = 0;
        int chunkCount = 0;
        int radius = Math.round(toolkitProperties.getBrushSize() / 16.0F);
        for (int x = chunkX - radius; x <= chunkX + radius; x++) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                entityCount += removeEntities(x, z);
                chunkCount++;
            }
        }
        messenger.sendMessage(Caption.of("voxelsniper.brush.entity-removal.removed", entityCount, chunkCount));
    }

    private int removeEntities(int chunkX, int chunkZ) {
        return TaskManager.taskManager().sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                return 0;
            }

            int entityCount = 0;
            for (Entity entity : world.getChunkAt(chunkX, chunkZ).getEntities()) {
                if (!isEntityClassInExemptionList(entity.getClass())) {
                    entity.remove();
                    entityCount++;
                }
            }
            return entityCount;
        });
    }

    private boolean isEntityClassInExemptionList(Class<? extends Entity> entityClass) {
        // Creates a list of superclasses and interfaces implemented by the current entity type.
        List<Class<?>> entityClassHierarchy = EntityClassArgument.getEntityClassHierarchy(entityClass);
        return this.exemptions.stream()
                .anyMatch(exemption -> entityClassHierarchy.stream()
                        .map(Class::getCanonicalName)
                        .anyMatch(typeName -> typeName.equals(exemption)));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(VoxelSniperText.formatList(
                        exemptions,
                        String::compareTo,
                        TextComponent::of,
                        "voxelsniper.brush.entity-removal"
                ))
                .brushSizeMessage()
                .send();
    }

}
