package com.github.mdcdi1315.basemodslib.integration.modmenu;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import java.util.Map;

public final class ModMenuEntryPoint
    implements ModMenuApi
{
    private final ModMenuLazyLoadedMap map;

    public ModMenuEntryPoint() {
        if (BaseModsLib.LOGGER == null) {
            throw new InvalidOperationException("Mod menu integration cannot be initialized - library has not been initialized yet.");
        }
        map = new ModMenuLazyLoadedMap();
        BaseModsLib.LOGGER.info("Mod menu integration enabled!");
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() { return map; }
}
