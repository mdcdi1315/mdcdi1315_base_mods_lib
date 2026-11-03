package com.github.mdcdi1315.basemodslib.neoforge.resource;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.IModResourceLookup;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.neoforged.neoforgespi.locating.IModFile;

import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import java.io.IOException;

public final class JarFileResourceLookup
    implements IModResourceLookup
{
    private final Path file;
    private final FileSystem file_contents;
    private final IServerModInstance instance;

    public JarFileResourceLookup(IModFile mod_file, IServerModInstance instance)
    {
        this.instance = instance;
        this.file = mod_file.getFilePath();
        try {
            file_contents = FileSystems.newFileSystem(file, Map.of());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path GetRootPath() { return file; }

    @Override
    public IServerModInstance GetMod() { return instance; }

    @Override
    public Path GetResource(String path)
            throws ArgumentNullException
    {
        Path result = file_contents.getPath(path);
        if (Files.exists(result)) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void Dispose()
    {
        try {
            file_contents.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
