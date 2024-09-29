package com.thevoxelbox.voxelsniper.command;

import org.incendo.cloud.annotations.MethodCommandExecutionHandler;
import org.incendo.cloud.annotations.method.ParameterValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.CommandExecutionException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VoxelMethodCommandExecutionHandler<C> extends MethodCommandExecutionHandler<C> {

    private final MethodHandle methodHandle;
    private final Function<CommandContext<C>, Object> executorInstanceSupplier;

    /**
     * Create a voxel method command execution handler.
     *
     * @param context the context
     * @throws Exception instantiation exception
     * @since 3.0.0
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
                            this.createParameterValues(commandContext)
                                .stream()
                                .map(ParameterValue::value)
                                .collect(Collectors.toList())
                    );
        } catch (final Error e) {
            throw e;
        } catch (final Throwable throwable) {
            throw new CommandExecutionException(throwable, commandContext);
        }
    }


}
