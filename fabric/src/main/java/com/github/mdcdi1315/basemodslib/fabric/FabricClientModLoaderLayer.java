package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.EmptyModObject;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;

public final class FabricClientModLoaderLayer
    implements IClientModLoaderLayer
{
    @Override
    public void InitializeClientModInstance(IClientModInstance instance, Object o) {
        if (!(o instanceof EmptyModObject)) {
            throw new InvalidOperationException(String.format("The mod object was not of type EmptyModObject!!!!\nActual type: %s", o.getClass().getName()));
        }

        instance.RegisterEvents(BaseModsLib.GetEventsManager());
    }
}
