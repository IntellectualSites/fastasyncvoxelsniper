package com.thevoxelbox.voxelsniper.util.io;

import com.sk89q.worldedit.util.io.ResourceLoader;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;

import java.nio.file.Path;

public class VoxelSniperResourceLoader implements ResourceLoader {

    private final VoxelSniperPlugin voxelSniperPlugin;

    public VoxelSniperResourceLoader(VoxelSniperPlugin voxelSniperPlugin) {
        this.voxelSniperPlugin = voxelSniperPlugin;

        FileUtils.copyFile("lang/strings.json", this.voxelSniperPlugin.getDataFolder());
    }

    @Override
    public Path getLocalResource(String pathName) {
        return this.voxelSniperPlugin.getDataFolder().toPath().resolve(pathName);
    }

}
