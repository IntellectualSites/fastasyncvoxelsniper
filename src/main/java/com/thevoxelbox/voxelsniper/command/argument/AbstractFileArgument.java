package com.thevoxelbox.voxelsniper.command.argument;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.SniperCommander;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * @since 3.0.0
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

    protected Stream<String> suggestFiles(CommandContext<SniperCommander> commandContext, String input) {
        Path inputPath = rootPath.resolve(input);
        try (Stream<Path> files = Files.list(Files.isDirectory(inputPath) ? inputPath : inputPath.getParent())) {
            return files.map(path -> path.getFileName().toString())
                    .flatMap(path -> Stream.of(path, path.replace(extension, "")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File parseFile(CommandContext<SniperCommander> commandContext, CommandInput input) {
        final String fileName = input.readString();
        try {
            return rootPath.resolve(fileName.endsWith(extension) ? fileName : fileName + extension).toFile();
        } catch (Exception e) {
            throw new VoxelCommandElementParseException(fileName, Caption.of(
                    "voxelsniper.command.invalid-file",
                    input
            ));
        }
    }

}
