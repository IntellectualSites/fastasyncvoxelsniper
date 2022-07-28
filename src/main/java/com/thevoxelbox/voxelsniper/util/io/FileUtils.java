package com.thevoxelbox.voxelsniper.util.io;

import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
                byte[] buffer = new byte[2048];
                File parent = newFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                newFile.createNewFile();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = stream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                return newFile;
            }
        } catch (IOException e) {
            LOGGER.error("Could not save {}, {}", resource, e);
        }
        return null;
    }

}
