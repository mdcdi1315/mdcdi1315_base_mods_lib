package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModResourceLookup;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;

public final class FabricModResourceLookup
    implements IModResourceLookup
{
    private ModContainer container;
    @AllowNull
    private IServerModInstance mod_instance;

    public FabricModResourceLookup(ModContainer container, boolean findMod)
    {
        this.container = container;
        if (findMod) {
            this.mod_instance = BaseModsLib.GetBMLModInstance(container.getMetadata().getId());
        } else {
            this.mod_instance = null;
        }
    }

    @Override
    public Path GetRootPath() { return container.getRootPaths().get(0); }

    @Override
    public Path GetResource(String path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull(path, "path");
        return container.findPath(path).orElse(null);
    }

    @Override
    public IServerModInstance GetMod() { return mod_instance; }

    @Override
    public void Dispose()
    {
        this.container = null;
        this.mod_instance = null;
    }
}
