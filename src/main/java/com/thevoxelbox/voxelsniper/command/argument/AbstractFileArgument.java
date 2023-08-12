package com.thevoxelbox.voxelsniper.command.argument;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

public abstract class AbstractFileArgument implements VoxelCommandElement {

    protected final VoxelSniperPlugin plugin;
    protected final Path rootPath;
    protected final String extension;

    /**
     * Create an abstract file argument.
     *
     * @param plugin    the plugin
     * @param rootPath  the root path
     * @param extension the extension
     * @since TODO
     */
    public AbstractFileArgument(VoxelSniperPlugin plugin, Path rootPath, String extension) {
        this.plugin = plugin;
        this.rootPath = rootPath;
        this.extension = extension;

        // Initializes the folder if needed.
        File rootFile = rootPath.toFile();
        if (!rootFile.exists() && !rootFile.mkdirs()) {
            throw new IllegalArgumentException("Invalid file argument root path: " + rootPath);
        }
    }

    protected List<String> suggestFiles(CommandContext<SniperCommander> commandContext, String input) {
        Path inputPath = rootPath.resolve(input);
        try (Stream<Path> files = Files.list(Files.isDirectory(inputPath) ? inputPath : inputPath.getParent())) {
            return files.map(path -> path.getFileName().toString())
                    .flatMap(path -> Stream.of(path, path.replace(extension, "")))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File parseFile(CommandContext<SniperCommander> commandContext, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            throw new NoInputProvidedException(AbstractFileArgument.class, commandContext);
        }

        try {
            File file = rootPath.resolve(input.endsWith(extension) ? input : input + extension).toFile();
            inputQueue.remove();
            return file;
        } catch (Exception e) {
            throw new VoxelCommandElementParseException(input, Caption.of(
                    "voxelsniper.command.invalid-file",
                    input
            ));
        }
    }

}
