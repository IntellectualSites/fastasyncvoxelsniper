package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;

public interface PerformerBrush extends Brush {

    void initialize(Snipe snipe);

    void sendPerformerInfo(Snipe snipe);

    /**
     * Return the performer.
     *
     * @return the performer
     * @since TODO
     */
    Performer getPerformer();

    /**
     * Set the performer.
     *
     * @param performer the performer
     * @since TODO
     */
    void setPerformer(Performer performer);

}
