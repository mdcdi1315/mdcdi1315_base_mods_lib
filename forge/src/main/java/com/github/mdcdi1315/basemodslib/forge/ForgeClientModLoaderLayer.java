package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.client.ForgeClientArtifactsRegistrar;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ForgeClientModLoaderLayer
    implements IClientModLoaderLayer
{
    private final FMLJavaModLoadingContext context;

    public ForgeClientModLoaderLayer(FMLJavaModLoadingContext context) {
        this.context = context;
    }

    private IEventBus GetEventBusOrFail(Object mod_object) {
        try {
            return (IEventBus) mod_object;
        } catch (ClassCastException cce) {
            throw new InvalidOperationException(String.format("The mod object was not of type IEventBus!!!!\nActual type: %s", mod_object.getClass().getName()));
        }
    }

    @Override
    public void InitializeClientModInstance(IClientModInstance instance, Object mod_object) {
        IEventBus mod_event_bus = GetEventBusOrFail(mod_object);

        instance.RegisterEvents(BaseModsLib.GetEventsManager());

        ForgeClientArtifactsRegistrar reg = new ForgeClientArtifactsRegistrar();

        instance.RegisterModelDefinitions(reg);
        instance.RegisterEntityRenderers(reg);
        instance.RegisterBlockEntityRenderers(reg);
        instance.RegisterColorHandlers(reg);

        reg.RegisterToEventBus(mod_event_bus);

    }
}
