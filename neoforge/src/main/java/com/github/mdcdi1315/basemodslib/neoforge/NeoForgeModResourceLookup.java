package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.AllowNull;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModResourceLookup;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Path;

public final class NeoForgeModResourceLookup
    implements IModResourceLookup
{
    private IModInfo mod_info;
    @AllowNull
    private IServerModInstance bml_mod_instance;

    public NeoForgeModResourceLookup(IModInfo neoforge_mod_info)
    {
        this.mod_info = neoforge_mod_info;
        bml_mod_instance = BaseModsLib.GetBMLModInstance(mod_info.getModId());
    }

    @Override
    public Path GetRootPath() {
        return mod_info.getOwningFile().getFile().findResource("root").getParent();
    }

    @Override
    public Path GetResource(String path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull("path", path);
        return mod_info.getOwningFile().getFile().findResource(path);
    }

    @Override
    public IServerModInstance GetMod() {
        return bml_mod_instance;
    }

    @Override
    public void Dispose()
    {
        mod_info = null;
        bml_mod_instance = null;
    }
}
