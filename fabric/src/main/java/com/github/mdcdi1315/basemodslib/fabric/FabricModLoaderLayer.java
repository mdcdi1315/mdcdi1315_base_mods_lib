package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.basemodslib.*;
import com.github.mdcdi1315.basemodslib.commands.FabricCommandsRegistrar;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.util.List;
import java.nio.file.Path;
import java.util.ArrayList;

public final class FabricModLoaderLayer
    implements IModLoaderLayer
{
    private final Path config_dir;
    private final List<String> mod_ids;
    private final ModdingEnvironment environment;
    private final Version minecraft_version, fabric_modloader_version;

    public FabricModLoaderLayer()
    {
        var loader = FabricLoader.getInstance();
        mod_ids = new ArrayList<>(10);
        for (ModContainer ctr : loader.getAllMods()) {
            mod_ids.add(ctr.getMetadata().getId());
        }
        config_dir = loader.getConfigDir();
        environment = switch (loader.getEnvironmentType()) {
            case CLIENT -> ModdingEnvironment.CLIENT;
            case SERVER -> ModdingEnvironment.SERVER;
        };
        minecraft_version = new Version(1, 20, 1);

        Version fb_ver;
        try {
            fb_ver = Version.Parse(FabricLoaderImpl.VERSION);
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve Fabric version due to an exception. Setting version values to 0,0.", e);
            fb_ver = new Version(0 , 0);
        }

        fabric_modloader_version = fb_ver;
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance mod_instance, Object o) {
        if (!(o instanceof EmptyModObject)) {
            throw new InvalidOperationException(String.format("The mod object was not of type EmptyModObject!!!!\nActual type: %s", o.getClass().getName()));
        }

        String mod_id = mod_instance.GetModId();

        FabricCommonRegistryItemsRegistrar registrar = new FabricCommonRegistryItemsRegistrar(mod_id);

        mod_instance.RegisterBlocks(registrar);
        mod_instance.RegisterBlockEntities(registrar);
        mod_instance.RegisterItems(registrar);
        mod_instance.RegisterWorldGenItems(registrar);
        mod_instance.RegisterRegistryItems(registrar);

        mod_instance.RegisterCommands(new FabricCommandsRegistrar());


    }

    @Override
    public boolean IsModLoaded(String s) {
        return mod_ids.contains(s);
    }

    @Override
    public List<String> GetLoadedMods() {
        return mod_ids;
    }

    @Override
    public ModdingEnvironment GetEnvironment() {
        return environment;
    }

    @Override
    public String GetModLoaderBranding() {
        return "Fabric";
    }

    @Override
    public Version GetMinecraftVersion() {
        return minecraft_version;
    }

    @Override
    public Version GetModLoaderVersion() {
        return fabric_modloader_version;
    }

    @Override
    public Path GetConfigurationDirectory() {
        return config_dir;
    }
}
