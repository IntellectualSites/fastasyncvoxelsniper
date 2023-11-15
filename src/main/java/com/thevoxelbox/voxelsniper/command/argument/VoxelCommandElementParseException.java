package com.thevoxelbox.voxelsniper.command.argument;

import com.sk89q.worldedit.util.formatting.text.Component;

import java.io.Serial;

public class VoxelCommandElementParseException extends IllegalArgumentException {

    @Serial
    private static final long serialVersionUID = -2010839991649577883L;

    private final String input;
    private final Component errorMessage;

    /**
     * Creates a voxel comment element parse exception.
     *
     * @param input        the input
     * @param errorMessage the error message
     * @since 3.0.0
     */
    public VoxelCommandElementParseException(String input, Component errorMessage) {
        this.input = input;
        this.errorMessage = errorMessage;
    }

    /**
     * Return the input.
     *
     * @return the input
     * @since 3.0.0
     */
    public String getInput() {
        return input;
    }

    /**
     * Return the error message.
     *
     * @return the error message
     * @since 3.0.0
     */
    public Component getErrorMessage() {
        return errorMessage;
    }

}
