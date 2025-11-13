package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;

@Mod(BaseModsLib.MOD_ID)
public final class BaseModsLibNeoForgeMod
{
    public BaseModsLibNeoForgeMod(IEventBus event_bus) {
        BaseModsLib.InitializeBaseModsLibrary(new NeoForgeModLoaderLayer(event_bus));
    }
}
