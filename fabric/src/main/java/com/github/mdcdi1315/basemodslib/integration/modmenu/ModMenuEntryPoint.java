package com.github.mdcdi1315.basemodslib.integration.modmenu;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import java.util.Map;
import java.util.HashMap;

public final class ModMenuEntryPoint
    implements ModMenuApi
{
    public ModMenuEntryPoint() {
        if (BaseModsLib.LOGGER == null) {
            throw new InvalidOperationException("Mod menu integration cannot be initialized - library has not been initialized yet.");
        }
        BaseModsLib.LOGGER.info("Mod menu integration enabled!");
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() { return new ModMenuLazyLoadedMap(); }
}
