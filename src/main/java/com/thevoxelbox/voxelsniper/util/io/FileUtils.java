package com.thevoxelbox.voxelsniper.util.io;

import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileUtils {

    private static final Logger LOGGER = LogManagerCompat.getLogger();

    public static File copyFile(String resource, File output) {
        return copyFile(resource, output, resource);
    }

    public static File copyFile(String resource, File output, String fileName) {
        try {
            if (output == null) {
                output = VoxelSniperPlugin.plugin.getDataFolder();
            }
            if (!output.exists()) {
                output.mkdirs();
            }
            File newFile = new File(output, fileName);
            if (newFile.exists()) {
                return newFile;
            }
            try (InputStream stream = VoxelSniperPlugin.class.getResourceAsStream(resource.startsWith("/") ? resource :
                    "/" + resource)) {
                if (stream == null) {
                    throw new NullPointerException("Unable to get the resource " + resource);
                }
                File parent = newFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
                try (OutputStream outputStream = Files.newOutputStream(newFile.toPath())) {
                    stream.transferTo(outputStream);
                }
                return newFile;
            }
        } catch (IOException e) {
            LOGGER.error("Could not save {}, {}", resource, e);
        }
        return null;
    }

}
