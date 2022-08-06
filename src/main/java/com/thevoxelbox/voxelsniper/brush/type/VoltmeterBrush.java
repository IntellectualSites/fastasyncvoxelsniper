package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class VoltmeterBrush extends AbstractBrush {

    @Override
    public void handleArrowAction(Snipe snipe) {
        volt(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        data(snipe);
    }

    private void data(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        BlockState blockData = getBlock(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ());
        BlockType type = blockData.getBlockType();
        Property<Integer> powerProperty = type.getProperty("power");
        if (powerProperty == null) {
            return;
        }
        int power = blockData.getState(powerProperty);
        messenger.sendMessage(Caption.of("voxelsniper.brush.voltmeter.data", power));
    }

    private void volt(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        TaskManager.taskManager().sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            Block block = world.getBlockAt(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ());
            boolean indirect = block.isBlockIndirectlyPowered();
            boolean direct = block.isBlockPowered();
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.voltmeter.direct",
                    VoxelSniperText.getStatus(direct),
                    VoxelSniperText.getStatus(indirect)
            ));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.voltmeter.top",
                    VoxelSniperText.getStatus(block.isBlockFacePowered(BlockFace.UP)),
                    VoxelSniperText.getStatus(block.isBlockFaceIndirectlyPowered(BlockFace.UP))
            ));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.voltmeter.bottom",
                    VoxelSniperText.getStatus(block.isBlockFacePowered(BlockFace.DOWN)),
                    VoxelSniperText.getStatus(block.isBlockFaceIndirectlyPowered(BlockFace.DOWN))
            ));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.voltmeter.east",
                    VoxelSniperText.getStatus(block.isBlockFacePowered(BlockFace.EAST)),
                    VoxelSniperText.getStatus(block.isBlockFaceIndirectlyPowered(BlockFace.EAST))
            ));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.voltmeter.west",
                    VoxelSniperText.getStatus(block.isBlockFacePowered(BlockFace.WEST)),
                    VoxelSniperText.getStatus(block.isBlockFaceIndirectlyPowered(BlockFace.WEST))
            ));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.voltmeter.north",
                    VoxelSniperText.getStatus(block.isBlockFacePowered(BlockFace.NORTH)),
                    VoxelSniperText.getStatus(block.isBlockFaceIndirectlyPowered(BlockFace.NORTH))
            ));
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.voltmeter.south",
                    VoxelSniperText.getStatus(block.isBlockFacePowered(BlockFace.SOUTH)),
                    VoxelSniperText.getStatus(block.isBlockFaceIndirectlyPowered(BlockFace.SOUTH))
            ));
            return null;
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of("voxelsniper.brush.voltmeter.usage"))
                .send();
    }

}
