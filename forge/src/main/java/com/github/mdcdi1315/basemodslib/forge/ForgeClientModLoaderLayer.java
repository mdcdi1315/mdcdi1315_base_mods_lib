package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.client.ForgeClientArtifactsRegistrar;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Optional;

public final class ForgeClientModLoaderLayer
    implements IClientModLoaderLayer
{
    private FMLJavaModLoadingContext context;

    public ForgeClientModLoaderLayer(FMLJavaModLoadingContext context) {
        this.context = context;
        BaseModsLib.GetEventsManager().AddEventListener(ModLoadingCompleteEvent.class, ForgeClientModLoaderLayer::RegisterConfigScreensToMods);
    }

    private static void RegisterConfigScreensToMods(ModLoadingCompleteEvent completed)
    {
        var mod_list = ModList.get();
        var en = BaseModsLibClient.GetConfigurationScreens().GetEnumerator();
        try {
            Pair<String, ConfigurationScreenFactory<?>> pair;
            Optional<? extends ModContainer> container;
            while (en.MoveNext()) {
                pair = en.getCurrent();
                if ((container = mod_list.getModContainerById(pair.first())).isEmpty()) {
                    BaseModsLib.LOGGER.error("Cannot find mod container with ID {}! This means that your mod is misconfigured." , pair.first());
                } else {
                    container.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, new ConfigScreenFactorySupplierImplementation<>(pair.second()));
                }
            }
        } finally {
            en.Dispose();
        }
    }

    private static IEventBus GetEventBusOrFail(Object mod_object) {
        try {
            return (IEventBus) mod_object;
        } catch (ClassCastException cce) {
            throw new InvalidOperationException(String.format("The mod object was not of type IEventBus!!!!\nActual type: %s", mod_object.getClass().getName()));
        }
    }

    @Override
    public void InitializeClientModInstance(IClientModInstance instance, Object mod_object) {
        IEventBus mod_event_bus = GetEventBusOrFail(mod_object);

        ForgeClientArtifactsRegistrar reg = new ForgeClientArtifactsRegistrar();

        instance.RegisterModelDefinitions(reg);
        instance.RegisterEntityRenderers(reg);
        instance.RegisterBlockEntityRenderers(reg);
        instance.RegisterColorHandlers(reg);
        instance.RegisterParticleProviders(reg);

        reg.RegisterToEventBus(mod_event_bus);

    }

    @Override
    public void Dispose() {
        context = null;
    }
}
