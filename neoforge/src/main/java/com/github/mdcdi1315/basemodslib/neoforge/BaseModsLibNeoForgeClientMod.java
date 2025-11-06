package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.fml.common.Mod;

@Mod(value = "mdcdi1315_base_mods_lib", dist = Dist.CLIENT)
public final class BaseModsLibNeoForgeClientMod
{
    public BaseModsLibNeoForgeClientMod(IEventBus event_bus) {
        BaseModsLibClient.InitializeBaseModsLibClient(new NeoForgeClientModLoaderLayer());
    }
}
