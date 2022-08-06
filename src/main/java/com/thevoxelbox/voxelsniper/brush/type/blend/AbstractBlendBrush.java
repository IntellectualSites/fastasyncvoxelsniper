package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public abstract class AbstractBlendBrush extends AbstractBrush {

    private boolean airExcluded = true;
    private boolean waterExcluded = true;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("water")) {
            this.waterExcluded = !this.waterExcluded;
            messenger.sendMessage(Caption.of("voxelsniper.brush.blend.set-water-mode", getStatus(this.waterExcluded)));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("water"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        this.airExcluded = false;
        blend(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        this.airExcluded = true;
        blend(snipe);
    }

    public abstract void blend(Snipe snipe);

    protected void setBlocks(Map<BlockVector3, BlockType> blockTypes) {
        for (Entry<BlockVector3, BlockType> entry : blockTypes.entrySet()) {
            BlockVector3 position = entry.getKey();
            BlockType type = entry.getValue();
            if (checkExclusions(type)) {
                BlockType currentBlockType = getBlockType(position);
                if (currentBlockType != type) {
                    clampY(position);
                    setBlock(position, type);
                }
            }
        }
    }

    protected CommonMaterial findCommonMaterial(Map<BlockType, Integer> blockTypesFrequencies) {
        CommonMaterial commonMaterial = new CommonMaterial();
        for (Entry<BlockType, Integer> entry : blockTypesFrequencies.entrySet()) {
            BlockType type = entry.getKey();
            int frequency = entry.getValue();
            if (frequency > commonMaterial.getFrequency() && checkExclusions(type)) {
                commonMaterial.setBlockType(type);
                commonMaterial.setFrequency(frequency);
            }
        }
        return commonMaterial;
    }

    private boolean checkExclusions(BlockType type) {
        return (!this.airExcluded || !Materials.isEmpty(type)) && (!this.waterExcluded || type != BlockTypes.WATER);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .patternMessage()
                .message(Caption.of("voxelsniper.brush.blend.set-water-mode", getStatus(this.waterExcluded)))
                .send();
    }

    public void setAirExcluded(boolean airExcluded) {
        this.airExcluded = airExcluded;
    }

    private Component getStatus(boolean status) {
        return Caption.of(status ? "voxelsniper.sniper.include" : "voxelsniper.sniper.exclude");
    }

}
