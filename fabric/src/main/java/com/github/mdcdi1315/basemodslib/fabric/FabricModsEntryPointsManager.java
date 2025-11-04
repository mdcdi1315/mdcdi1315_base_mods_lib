package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.EmptyModObject;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import net.fabricmc.loader.api.FabricLoader;

public final class FabricModsEntryPointsManager
{
    public static final String ENTRYPOINT_BASE = "mdcdi1315_basemodslib_";
    public static final String ENTRYPOINT_SERVER = ENTRYPOINT_BASE + "server";
    public static final String ENTRYPOINT_CLIENT = ENTRYPOINT_BASE + "client";

    private FabricModsEntryPointsManager() {}

    public static void InitializeServerSideMods()
    {
        for (IServerModInstance instance : FabricLoader.getInstance().getEntrypoints(ENTRYPOINT_SERVER, IServerModInstance.class))
        {
            BaseModsLib.InitializeServerSideMod(instance , EmptyModObject.INSTANCE);
        }
    }

    public static void InitializeClientSideMods()
    {
        for (IClientModInstance instance : FabricLoader.getInstance().getEntrypoints(ENTRYPOINT_CLIENT, IClientModInstance.class))
        {
            BaseModsLibClient.InitializeClientSideMod(instance , EmptyModObject.INSTANCE);
        }
    }
}
