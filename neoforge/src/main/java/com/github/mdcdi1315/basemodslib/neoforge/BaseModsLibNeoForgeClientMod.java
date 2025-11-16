package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;

@Mod(value = BaseModsLib.MOD_ID, dist = Dist.CLIENT)
public final class BaseModsLibNeoForgeClientMod
{
    public BaseModsLibNeoForgeClientMod(IEventBus event_bus) {
        BaseModsLibClient.InitializeBaseModsLibClient(new NeoForgeClientModLoaderLayer(event_bus));
    }
}
