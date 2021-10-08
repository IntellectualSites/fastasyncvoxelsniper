package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.PerformerRegistrar;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.performer.property.PerformerCreator;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;

import java.util.Arrays;
import java.util.Random;

public abstract class AbstractPerformerBrush extends AbstractBrush implements PerformerBrush {

    protected static final int SEED_PERCENT_MIN = 1;
    protected static final int SEED_PERCENT_MAX = 9999;
    protected static final int GROWTH_PERCENT_MIN = 1;
    protected static final int GROWTH_PERCENT_MAX = 9999;
    protected static final int SPLATTER_RECURSIONS_MIN = 1;
    protected static final int SPLATTER_RECURSIONS_MAX = 10;

    protected static final int DEFAULT_SEED_PERCENT = 1000;
    protected static final int DEFAULT_GROWTH_PERCENT = 1000;
    protected static final int DEFAULT_SPLATTER_RECURSIONS = 3;

    protected final Random generator = new Random();

    protected Performer performer;
    protected int seedPercentMin;
    protected int seedPercentMax;
    protected int growthPercentMin;
    protected int growthPercentMax;
    protected int splatterRecursionsMin;
    protected int splatterRecursionsMax;
    protected int seedPercent;
    protected int growthPercent;
    protected int splatterRecursions;
    private PerformerProperties performerProperties;

    public AbstractPerformerBrush() {
        this.performerProperties = PerformerRegistrar.DEFAULT_PERFORMER_PROPERTIES;
        PerformerCreator performerCreator = this.performerProperties.getCreator();
        this.performer = performerCreator.create();
        this.performer.setProperties(this.performerProperties);
        this.performer.loadProperties();
    }

    @Override
    public void loadProperties() {
        this.seedPercentMin = getIntegerProperty("seed-percent-min", SEED_PERCENT_MIN);
        this.seedPercentMax = getIntegerProperty("seed-percent-max", SEED_PERCENT_MAX);
        this.growthPercentMin = getIntegerProperty("growth-percent-min", GROWTH_PERCENT_MIN);
        this.growthPercentMax = getIntegerProperty("growth-percent-max", GROWTH_PERCENT_MAX);
        this.splatterRecursionsMin = getIntegerProperty("splatter-recursions-min", SPLATTER_RECURSIONS_MIN);
        this.splatterRecursionsMax = getIntegerProperty("splatter-recursions-max", SPLATTER_RECURSIONS_MAX);

        this.seedPercent = getIntegerProperty("default-seed-percent", DEFAULT_SEED_PERCENT);
        this.growthPercent = getIntegerProperty("default-growth-percent", DEFAULT_GROWTH_PERCENT);
        this.splatterRecursions = getIntegerProperty("default-splatter-recursions", DEFAULT_SPLATTER_RECURSIONS);
    }

    @Override
    public void handlePerformerCommand(String[] parameters, Snipe snipe, PerformerRegistry performerRegistry) {
        String parameter = parameters[0];
        PerformerProperties performerProperties = performerRegistry.getPerformerProperties(parameter);
        if (performerProperties == null) {
            super.handleCommand(parameters, snipe);
            return;
        }
        this.performerProperties = performerProperties;
        PerformerCreator performerCreator = this.performerProperties.getCreator();
        this.performer = performerCreator.create();
        this.performer.setProperties(this.performerProperties);
        this.performer.loadProperties();
        sendInfo(snipe);
        PerformerSnipe performerSnipe = new PerformerSnipe(snipe, this.performerProperties, this.performer);
        this.performer.sendInfo(performerSnipe);
        if (parameters.length > 1) {
            String[] additionalArguments = Arrays.copyOfRange(parameters, 1, parameters.length);
            super.handleCommand(additionalArguments, snipe);
        }
    }

    @Override
    public void initialize(Snipe snipe) {
        PerformerSnipe performerSnipe = new PerformerSnipe(snipe, this.performerProperties, this.performer);
        this.performer.initialize(performerSnipe);
    }

    @Override
    public void sendPerformerInfo(Snipe snipe) {
        PerformerSnipe performerSnipe = new PerformerSnipe(snipe, this.performerProperties, this.performer);
        this.performer.sendInfo(performerSnipe);
    }

    public Performer getPerformer() {
        return this.performer;
    }

}
