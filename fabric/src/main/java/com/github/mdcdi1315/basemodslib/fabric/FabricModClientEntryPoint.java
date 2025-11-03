package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.basemodslib.BaseModsLibClient;

import net.fabricmc.api.ClientModInitializer;

public final class FabricModClientEntryPoint
    implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        BaseModsLibClient.InitializeBaseModsLibClient(new FabricClientModLoaderLayer());
        FabricModsEntryPointsManager.InitializeClientSideMods();
    }
}
