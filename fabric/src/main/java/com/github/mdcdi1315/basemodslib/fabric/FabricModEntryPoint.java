package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import net.fabricmc.api.ModInitializer;

public final class FabricModEntryPoint
    implements ModInitializer
{
    @Override
    public void onInitialize() {
        BaseModsLib.InitializeBaseModsLibrary(new FabricModLoaderLayer());
        FabricModsEntryPointsManager.InitializeServerSideMods();
    }
}
