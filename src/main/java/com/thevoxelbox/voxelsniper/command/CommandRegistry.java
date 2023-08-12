package com.thevoxelbox.voxelsniper.command;

import cloud.commandframework.Command;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.MethodCommandExecutionHandler;
import cloud.commandframework.annotations.injection.ParameterInjectorRegistry;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.exceptions.parsing.ParserException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.services.types.ConsumerService;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.argument.VoxelCommandElementParseException;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.ReflectionsUtils;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import io.leangen.geantyref.TypeToken;
import org.apache.commons.lang3.ClassUtils;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommandRegistry {

    public static final CloudKey<Snipe> SNIPE_KEY = createTypeKey(
            "snipe", Snipe.class
    );
    public static final CloudKey<PerformerSnipe> PERFORMER_SNIPE_KEY = createTypeKey(
            "snipe", PerformerSnipe.class
    );
    public static final CloudKey<Toolkit> TOOLKIT_KEY = createTypeKey(
            "toolkit", Toolkit.class
    );

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final String NO_DESCRIPTION = "No Description.";
    private static final CommandMeta.Key<Boolean> REQUIRE_TOOLKIT = createMetaKey(
            "require-toolkit",
            Boolean.class
    );

    private final VoxelSniperPlugin plugin;
    private final Map<Class<?>, Map<String, MethodHandle>> dynamicRangeFields;

    private BukkitCommandManager<SniperCommander> commandManager;
    private AnnotationParser<SniperCommander> annotationParser;

    /**
     * Create a new command registry.
     *
     * @param plugin the plugin
     */
    public CommandRegistry(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
        this.dynamicRangeFields = new HashMap<>();
    }

    /**
     * Initialize the command registry.
     *
     * @throws Exception initialization exception
     * @since TODO
     */
    public void initialize() throws Exception {
        this.commandManager = createCommandManager();
        this.annotationParser = createAnnotationParser(this.commandManager);
    }

    private BukkitCommandManager<SniperCommander> createCommandManager() throws Exception {
        // Creates the command manager according to the server platform.
        PaperCommandManager<SniperCommander> commandManager = new PaperCommandManager<>(
                plugin,
                CommandExecutionCoordinator.simpleCoordinator(),
                commandSender -> plugin.getSniperRegistry().getSniperCommander(commandSender),
                SniperCommander::getCommandSender
        );

        // Handles extra registrations.
        commandManager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<SniperCommander>contains(true).andTrimBeforeLastSpace()
        ));
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        // Handles parameter injectors.
        ParameterInjectorRegistry<SniperCommander> parameterInjectorRegistry = commandManager.parameterInjectorRegistry();
        parameterInjectorRegistry.registerInjector(
                Snipe.class,
                (context, annotations) -> context.get(SNIPE_KEY)
        );
        parameterInjectorRegistry.registerInjector(
                PerformerSnipe.class,
                (context, annotations) -> context.get(PERFORMER_SNIPE_KEY)
        );
        parameterInjectorRegistry.registerInjector(
                Toolkit.class,
                (context, annotations) -> context.get(TOOLKIT_KEY)
        );

        // Handles post-processor.
        commandManager.registerCommandPostProcessor(context -> {
            // Ensures that we are working with a voxel sniper annotated command.
            CommandContext<SniperCommander> commandContext = context.getCommandContext();
            Command<SniperCommander> command = context.getCommand();
            if (!(commandContext.getSender() instanceof Sniper sniper)
                    || !(command.getCommandExecutionHandler() instanceof MethodCommandExecutionHandler<SniperCommander> handler)) {
                return;
            }

            Toolkit toolkit;
            // Toolkit requirement relies on the custom annotation.
            if (command.getCommandMeta()
                    .get(REQUIRE_TOOLKIT)
                    .orElse(false)) {
                if ((toolkit = sniper.getCurrentToolkit()) == null) {
                    sniper.print(Caption.of("voxelsniper.command.missing-toolkit"));
                    ConsumerService.interrupt();
                }
                commandContext.store(TOOLKIT_KEY, toolkit);
            } else {
                toolkit = null;
            }

            MethodCommandExecutionHandler.CommandMethodContext<SniperCommander> methodContext = handler.context();
            Object executorInstance = methodContext.instance();
            if (toolkit == null) {
                // Handles the custom dynamic range annotations.
                handleDynamicRanges(commandContext, handler, executorInstance);
                return;
            }

            if (executorInstance instanceof Brush brushExecutor) {
                postprocessBrush(commandContext, sniper, toolkit, brushExecutor);

                // Handles the custom dynamic range annotations.
                handleDynamicRanges(commandContext, handler, commandContext.get(SNIPE_KEY).getBrush());
            } else if (executorInstance instanceof Performer performerExecutor) {
                postprocessPerformer(commandContext, sniper, toolkit, performerExecutor);

                // Handles the custom dynamic range annotations.
                handleDynamicRanges(commandContext, handler, commandContext.get(PERFORMER_SNIPE_KEY).getPerformer());
            }
        });

        // Handles exceptions.
        commandManager.registerExceptionHandler(InvalidSyntaxException.class, (commander, e) ->
                commander.print(Caption.of(
                        "voxelsniper.command.invalid-command-syntax",
                        e.getCorrectSyntax()
                )));
        commandManager.registerExceptionHandler(InvalidCommandSenderException.class, (commander, e) ->
                commander.print(Caption.of(
                        "voxelsniper.command.invalid-sender-type",
                        e.getRequiredSender().getSimpleName()
                )));
        commandManager.registerExceptionHandler(NoPermissionException.class, (commander, e) ->
                commander.print(Caption.of(
                        "voxelsniper.command.missing-permission",
                        e.getMissingPermission()
                )));
        commandManager.registerExceptionHandler(ArgumentParseException.class, (commander, e) -> {
            Throwable t = e.getCause();

            if (t instanceof VoxelCommandElementParseException ve) {
                commander.print(ve.getErrorMessage());
            } else if (t instanceof EnumArgument.EnumParseException ee) {
                commander.print(Caption.of(
                        "voxelsniper.command.invalid-enum",
                        ee.getInput(),
                        VoxelSniperText.formatList(
                                Arrays.stream(ee.getEnumClass().getEnumConstants()).toList(),
                                (value, value2) -> Integer.compare(value.ordinal(), value2.ordinal()),
                                value -> TextComponent.of(value.name().toLowerCase(Locale.ROOT)),
                                "voxelsniper.command.invalid-enum"
                        )
                ));
            } else if (t instanceof ParserException pe) {
                commander.print(Caption.of(
                        pe.errorCaption()
                                .getKey()
                                .replace("argument.parse.failure.", "voxelsniper.command.invalid-"),
                        Arrays.stream(pe.captionVariables())
                                .map(CaptionVariable::getValue)
                                .toArray(Object[]::new)
                ));
            } else {
                commander.print(Caption.of("voxelsniper.error.unexpected"));
                e.printStackTrace();
            }
        });
        commandManager.registerExceptionHandler(CommandExecutionException.class, (commander, e) -> {
            commander.print(Caption.of("voxelsniper.error.unexpected"));
            e.printStackTrace();
        });

        return commandManager;
    }

    private AnnotationParser<SniperCommander> createAnnotationParser(BukkitCommandManager<SniperCommander> commandManager) {
        // Creates the annotation parser.
        AnnotationParser<SniperCommander> annotationParser = new AnnotationParser<>(
                commandManager,
                SniperCommander.class,
                parserParameters -> CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, parserParameters
                                .get(StandardParameters.DESCRIPTION, NO_DESCRIPTION))
                        .build()
        );

        // Handles the custom annotations.
        annotationParser.registerBuilderModifier(
                RequireToolkit.class,
                (requireToolkit, builder) -> builder
                        .senderType(Sniper.class)
                        .meta(REQUIRE_TOOLKIT, true)
        );

        // Handles the custom command execution method factories.
        annotationParser.registerCommandExecutionMethodFactory(
                method -> Brush.class.isAssignableFrom(method.getDeclaringClass()),
                context -> {
                    try {
                        return new VoxelMethodCommandExecutionHandler<>(
                                context,
                                commandContext -> commandContext.get(SNIPE_KEY).getBrush()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        annotationParser.registerCommandExecutionMethodFactory(
                method -> Performer.class.isAssignableFrom(method.getDeclaringClass()),
                context -> {
                    try {
                        return new VoxelMethodCommandExecutionHandler<>(
                                context,
                                commandContext -> commandContext.get(PERFORMER_SNIPE_KEY).getPerformer()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return annotationParser;
    }

    @SuppressWarnings("unchecked")
    private void handleDynamicRanges(
            CommandContext<SniperCommander> commandContext,
            MethodCommandExecutionHandler<SniperCommander> handler,
            Object instance
    ) {
        SniperCommander commander = commandContext.getSender();
        MethodCommandExecutionHandler.CommandMethodContext<SniperCommander> methodContext = handler.context();

        // Dynamic range is based on executor instance fields, we must postprocess them manually.
        for (Parameter parameter : handler.parameters()) {
            Class<?> parameterType;
            if (!parameter.isAnnotationPresent(Argument.class)
                    || (!parameter.isAnnotationPresent(DynamicRange.class)
                    || !Number.class.isAssignableFrom(ClassUtils.primitiveToWrapper((parameterType = parameter.getType()))))) {
                continue;
            }

            Argument argumentAnnotation = parameter.getAnnotation(Argument.class);
            String argumentName;
            if (argumentAnnotation.value().equals(AnnotationParser.INFERRED_ARGUMENT_NAME)) {
                argumentName = parameter.getName();
            } else {
                argumentName = this.annotationParser.processString(argumentAnnotation.value());
            }

            CommandArgument<SniperCommander, Number> argument =
                    (CommandArgument<SniperCommander, Number>) methodContext.commandArguments().get(argumentName);
            double number = commandContext.get(argument).doubleValue();

            DynamicRange dynamicRangeAnnotation = parameter.getAnnotation(DynamicRange.class);
            String min = dynamicRangeAnnotation.min();
            String max = dynamicRangeAnnotation.max();
            double minNumber;
            double maxNumber;
            try {
                minNumber = getDynamicRangeNumber(instance, min).doubleValue();
                maxNumber = getDynamicRangeNumber(instance, max).doubleValue();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            // Uses type min/max if undefined.
            if (Double.isNaN(minNumber)) {
                minNumber = MathHelper.minNumberType(parameterType).doubleValue();
            }
            if (Double.isNaN(maxNumber)) {
                maxNumber = MathHelper.maxNumberType(parameterType).doubleValue();
            }

            if (number < minNumber || number > maxNumber) {
                commander.print(Caption.of("voxelsniper.command.invalid-number",
                        number, minNumber, maxNumber
                ));
                ConsumerService.interrupt();
            }
        }
    }

    private Number getDynamicRangeNumber(Object instance, String key) throws Throwable {
        if (key.isEmpty()) {
            return Double.NaN;
        }

        try {
            return Double.parseDouble(key);
        } catch (NumberFormatException ignored) {
            Class<?> clazz = instance.getClass();
            MethodHandle getter = dynamicRangeFields.computeIfAbsent(clazz, aClazz -> new HashMap<>())
                    .computeIfAbsent(key, aKey -> {
                        try {
                            return LOOKUP.unreflectGetter(ReflectionsUtils.getField(clazz, key));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });

            return (double) getter.invoke(instance);
        }
    }

    private void postprocessBrush(
            CommandContext<SniperCommander> commandContext, Sniper sniper,
            Toolkit toolkit, Brush brushExecutor
    ) {
        Player player = sniper.getPlayer();
        BrushProperties newBrush = brushExecutor.getProperties();
        String permission = newBrush.getPermission();
        if (permission != null && !player.hasPermission(permission)) {
            sniper.print(Caption.of("voxelsniper.command.missing-permission", permission));
            ConsumerService.interrupt();
        }

        // Creates and stores the snipe for brush processing.
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        Brush brush = toolkit.useBrush(newBrush);
        Snipe snipe = new Snipe(
                sniper, toolkit, toolkitProperties,
                newBrush, brush
        );
        commandContext.store(SNIPE_KEY, snipe);
    }

    private void postprocessPerformer(
            CommandContext<SniperCommander> commandContext, Sniper sniper,
            Toolkit toolkit, Performer performerExecutor
    ) {
        Brush brush = toolkit.getCurrentBrush();
        if (!(brush instanceof PerformerBrush performerBrush)) {
            sniper.print(Caption.of("voxelsniper.command.performer.invalid-brush"));
            ConsumerService.interrupt();
            return;
        }

        // Creates and sets the performer.
        PerformerProperties properties = performerExecutor.getProperties();
        Performer performer = toolkit.usePerformer(properties);
        performerBrush.setPerformer(performer);

        // Creates and stores the snipe performer for performer brush processing.
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        BrushProperties brushProperties = toolkit.getCurrentBrushProperties();
        PerformerSnipe snipe = new PerformerSnipe(
                sniper, toolkit, toolkitProperties,
                brushProperties, brush,
                properties, performer
        );
        commandContext.store(PERFORMER_SNIPE_KEY, snipe);
    }

    /**
     * Register the elements from a voxel command element into the command manager.
     *
     * @param voxelCommandElement the voxel command element
     * @since TODO
     */
    public void register(VoxelCommandElement voxelCommandElement) {
        annotationParser.parse(voxelCommandElement);
    }

    /**
     * Return the command manager.
     *
     * @return the command manager
     * @since TODO
     */
    public BukkitCommandManager<SniperCommander> getCommandManager() {
        return commandManager;
    }

    /**
     * Return the annotation parser.
     *
     * @return the annotation parser
     * @since TODO
     */
    public AnnotationParser<SniperCommander> getAnnotationParser() {
        return annotationParser;
    }

    private static <T> CloudKey<T> createTypeKey(String id, Class<T> clazz) {
        return SimpleCloudKey.of("voxelsniper-" + id, TypeToken.get(clazz));
    }

    private static <T> CommandMeta.Key<T> createMetaKey(String id, Class<T> clazz) {
        return CommandMeta.Key.of(TypeToken.get(clazz), "voxelsniper-" + id);
    }

}
