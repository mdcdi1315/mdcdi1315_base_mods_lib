package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.utils.Pair;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.IClientModLoaderLayer;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ClientSetupEvent;
import com.github.mdcdi1315.basemodslib.config.gui.ConfigurationScreenFactory;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ModLoadingCompleteEvent;
import com.github.mdcdi1315.basemodslib.client.NeoForgeClientArtifactsRegistrar;
import com.github.mdcdi1315.basemodslib.client.NeoForgeClientRegistriesRegistrar;

import net.minecraft.client.gui.screens.Screen;

import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Optional;

public final class NeoForgeClientModLoaderLayer
    implements IClientModLoaderLayer
{
    public NeoForgeClientModLoaderLayer(IEventBus event_bus) {
        BaseModsLib.GetEventsManager().AddEventListener(ModLoadingCompleteEvent.class , NeoForgeClientModLoaderLayer::RegisterConfigScreensToMods);
        NeoForgeUtils.AddListener(event_bus, FMLClientSetupEvent.class, this::OnClientSetupEvent);
    }

    private static IEventBus GetEventBusOrFail(Object mod_object) {
        try {
            return (IEventBus) mod_object;
        } catch (ClassCastException cce) {
            throw new InvalidOperationException(String.format("The mod object was not of type IEventBus!!!!\nActual type: %s", mod_object.getClass().getName()));
        }
    }

    private void OnClientSetupEvent(FMLClientSetupEvent event) {
        BaseModsLib.LOGGER.info("Client setup event realized. Dispatching client setup to implementing mods.");
        ClientSetupEvent cse = new ClientSetupEvent();
        BaseModsLib.GetEventsManager().FireEvent(cse);
        event.enqueueWork(cse::Run);
    }

    @Override
    public void Dispose() {

    }

    private record LibConfigScreenFactoryToNeoForgeScreenFactory(ConfigurationScreenFactory<?> factory)
        implements IConfigScreenFactory
    {
        @Override
        public Screen createScreen(ModContainer mc, Screen screen) {
            return factory.Create(screen);
        }
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
                    container.get().registerExtensionPoint(IConfigScreenFactory.class, new LibConfigScreenFactoryToNeoForgeScreenFactory(pair.second()));
                }
            }
        } finally {
            en.Dispose();
        }
    }

    @Override
    public void InitializeClientModInstance(IClientModInstance instance, Object mod_object)
    {
        IEventBus mod_bus = GetEventBusOrFail(mod_object);

        NeoForgeClientArtifactsRegistrar registrar = new NeoForgeClientArtifactsRegistrar();

        instance.RegisterColorHandlers(registrar);
        instance.RegisterModelDefinitions(registrar);
        instance.RegisterEntityRenderers(registrar);
        instance.RegisterBlockEntityRenderers(registrar);
        instance.RegisterParticleProviders(registrar);
        instance.RegisterMenuScreens(registrar);
        instance.RegisterSpecialModelRenderers(registrar);
        registrar.RegisterToEventBus(mod_bus);

        NeoForgeClientRegistriesRegistrar registrar_2 = new NeoForgeClientRegistriesRegistrar(instance.GetModId());

        instance.RegisterClientRegistryItems(registrar_2);

        registrar_2.RegisterToEventBus(mod_bus);
    }


}
