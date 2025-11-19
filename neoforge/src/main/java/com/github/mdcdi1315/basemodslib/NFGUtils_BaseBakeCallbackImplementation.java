package com.github.mdcdi1315.basemodslib;

import com.github.mdcdi1315.DotNetLayer.System.Func2;

import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;
import com.github.mdcdi1315.basemodslib.registries.MinecraftWrappedModLoaderRegistry;

import net.minecraft.core.Registry;

import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.registries.callback.BakeCallback;

import static com.github.mdcdi1315.basemodslib.neoforge.NeoForgeModLoaderLayer.mod_loading_complete;

record NFGUtils_BaseBakeCallbackImplementation<T>(Func2<IModLoaderRegistry<T>, RegistryFinalizedEvent<T>> registry_event_to_invoke)
            implements BakeCallback<T>
{
    @Override
    public void onBake(Registry<T> registry) {
        // Check that we have not been reached to an erroring state so far
        // We might also reach to an erroring state when these events are dispatched.
        if (!(mod_loading_complete || ModLoader.hasErrors())) {
            BaseModsLib.LOGGER.info("Dispatching registry finalized event for {}" , registry.key().location());
            BaseModsLib.GetEventsManager().FireEvent(registry_event_to_invoke.function(new MinecraftWrappedModLoaderRegistry<>(registry)));
        }
    }
}