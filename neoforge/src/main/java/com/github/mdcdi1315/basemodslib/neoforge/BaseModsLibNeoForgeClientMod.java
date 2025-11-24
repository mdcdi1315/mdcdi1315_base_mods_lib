package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.Func1;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;

@Mod(value = BaseModsLib.MOD_ID, dist = Dist.CLIENT)
public final class BaseModsLibNeoForgeClientMod
{
    private record ClientModLoaderLayerInitializer(IEventBus event_bus)
            implements Func1<IClientModLoaderLayer>
    {
        @Override
        public IClientModLoaderLayer function() {
            return new NeoForgeClientModLoaderLayer(event_bus);
        }
    }

    public BaseModsLibNeoForgeClientMod(IEventBus event_bus) {
        BaseModsLibClient.InitializeBaseModsLibClient(new ClientModLoaderLayerInitializer(event_bus));
    }
}
