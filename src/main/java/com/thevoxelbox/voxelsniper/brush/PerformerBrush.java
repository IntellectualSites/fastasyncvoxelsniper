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
     * @since 3.0.0
     */
    Performer getPerformer();

    /**
     * Set the performer.
     *
     * @param performer the performer
     * @since 3.0.0
     */
    void setPerformer(Performer performer);

}
