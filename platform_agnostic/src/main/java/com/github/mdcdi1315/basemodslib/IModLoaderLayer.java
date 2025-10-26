package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;

import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import java.util.List;
import java.nio.file.Path;

/**
 * Internal interface. <br />
 * Provided for interconnecting mod loaders to the abstracted library.
 */
public interface IModLoaderLayer
{
    void InitializeServerModInstance(IServerModInstance instance, Object mod_object);

    boolean IsModLoaded(String mod_id);

    @NotNull
    List<String> GetLoadedMods();

    @NotNull
    ModdingEnvironment GetEnvironment();

    @NotNull
    String GetModLoaderBranding();

    @NotNull
    Version GetMinecraftVersion();

    @NotNull
    Version GetModLoaderVersion();

    @NotNull
    Path GetConfigurationDirectory();
}
