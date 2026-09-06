package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModResourceLookup;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.nio.file.Path;

public final class BMLModSpecialRLP
    implements IModResourceLookup
{
    private IModFileInfo mod_info;

    public BMLModSpecialRLP()
    {
        mod_info = ModList
                .get()
                .getModFileById(BaseModsLib.MOD_ID);
    }

    @Override
    public Path GetRootPath()
    {
        return mod_info.getFile().findResource("root").getParent();
    }

    @Override
    public Path GetResource(String path)
            throws ArgumentNullException
    {
        ArgumentNullException.ThrowIfNull("path", path);
        return mod_info.getFile().findResource(path);
    }

    @Override
    public IServerModInstance GetMod() { return null; }

    @Override
    public void Dispose() { mod_info = null; }
}
