package com.github.mdcdi1315.basemodslib.neoforge.resource;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.IModResourceLookup;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.neoforged.neoforgespi.locating.IModFile;

import java.nio.file.Path;
import java.nio.file.Files;

public final class FolderBasedResourceLookup
    implements IModResourceLookup
{
    private final Path path;
    private final IModFile mod_file;
    private final IServerModInstance instance;

    public FolderBasedResourceLookup(IModFile file, IServerModInstance i) { this.path = (mod_file = file).getFilePath(); this.instance = i; }

    @Override
    public Path GetResource(String path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        Path constructed = this.path.resolve(path);
        if (Files.exists(constructed)) {
            return constructed;
        } else {
            // If we are on development environment or any other way provided
            // through the FML, it is worth checking the content roots
            // as they can contain folders which from the root file path
            // alone cannot be accessed.
            for (var p : mod_file.getContents().getContentRoots())
            {
                constructed = p.resolve(path);
                if (Files.exists(constructed)) {
                    return constructed;
                }
            }
            return null;
        }
    }

    @Override
    public void Dispose() { }

    @Override
    public Path GetRootPath() { return path; }

    @Override
    public IServerModInstance GetMod() { return instance; }
}
