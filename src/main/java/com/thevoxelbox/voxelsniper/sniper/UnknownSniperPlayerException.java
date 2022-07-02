package com.thevoxelbox.voxelsniper.sniper;

import java.io.Serial;

public class UnknownSniperPlayerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3759901632482923871L;

    public UnknownSniperPlayerException() {
        super("Sniper player not found");
    }

}
