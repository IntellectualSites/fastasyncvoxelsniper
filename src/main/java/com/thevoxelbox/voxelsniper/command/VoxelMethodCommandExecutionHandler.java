package com.thevoxelbox.voxelsniper.command;

import cloud.commandframework.annotations.MethodCommandExecutionHandler;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.CommandExecutionException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.function.Function;

public class VoxelMethodCommandExecutionHandler<C> extends MethodCommandExecutionHandler<C> {

    private final MethodHandle methodHandle;
    private final Function<CommandContext<C>, Object> executorInstanceSupplier;

    /**
     * Create a voxel method command execution handler.
     *
     * @param context the context
     * @throws Exception instantiation exception
     * @since TODO
     */
    public VoxelMethodCommandExecutionHandler(
            CommandMethodContext<C> context,
            Function<CommandContext<C>, Object> executorInstanceSupplier
    ) throws Exception {
        super(context);
        this.methodHandle = MethodHandles.lookup().unreflect(context.method());
        this.executorInstanceSupplier = executorInstanceSupplier;
    }

    @Override
    public void execute(@NonNull CommandContext<C> commandContext) {
        try {
            this.methodHandle
                    .bindTo(executorInstanceSupplier.apply(commandContext))
                    .invokeWithArguments(
                            this.createParameterValues(
                                    commandContext,
                                    commandContext.flags(),
                                    super.parameters()
                            )
                    );
        } catch (final Error e) {
            throw e;
        } catch (final Throwable throwable) {
            throw new CommandExecutionException(throwable, commandContext);
        }
    }


}
