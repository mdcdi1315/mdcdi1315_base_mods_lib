package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.Func1;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModLoaderLayer;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;

@Mod(BaseModsLib.MOD_ID)
public final class BaseModsLibNeoForgeMod
{
    private record ServerModLoaderLayerInitializer(IEventBus event_bus)
            implements Func1<IModLoaderLayer>
    {
        @Override
        public IModLoaderLayer function() {
            return new NeoForgeModLoaderLayer(event_bus);
        }
    }

    public BaseModsLibNeoForgeMod(IEventBus event_bus) {
        BaseModsLib.InitializeBaseModsLibrary(new ServerModLoaderLayerInitializer(event_bus));
    }
}
