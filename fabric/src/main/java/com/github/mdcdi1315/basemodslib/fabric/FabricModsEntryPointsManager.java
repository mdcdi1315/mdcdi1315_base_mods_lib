package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.EmptyModObject;
import com.github.mdcdi1315.basemodslib.BaseModsLibClient;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.mods.IClientModInstance;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.registries.BuiltInRegistries;

public final class FabricModsEntryPointsManager
{
    public static final String ENTRYPOINT_BASE = "mdcdi1315_basemodslib_";
    public static final String ENTRYPOINT_SERVER = ENTRYPOINT_BASE + "server";
    public static final String ENTRYPOINT_CLIENT = ENTRYPOINT_BASE + "client";

    private FabricModsEntryPointsManager() {}

    public static void InitializeServerSideMods()
    {
        for (IServerModInstance instance : FabricLoader.getInstance().getEntrypoints(ENTRYPOINT_SERVER, IServerModInstance.class)) {
            BaseModsLib.InitializeServerSideMod(instance , EmptyModObject.INSTANCE);
        }
        // For Fabric, we do not have a way to listen to a 'Registries Ready!' event.
        // The best way to handle this is after all the mods using the BML have been initialized.
        // This will ensure that the events are fired at the right place and time, and will also avoid non-loaded issues with client-side mod instances.
        // Otherwise, client side mod instances will run just right after this method finishes execution.
        EventManager manager = BaseModsLib.GetEventsManager();
        BaseModsLib.LOGGER.info("Dispatching registry finalization events.");
        manager.FireEvent(new SoundEventRegistryFinalizedEvent(BuiltInRegistries.SOUND_EVENT));
        manager.FireEvent(new FluidRegistryFinalizedEvent(BuiltInRegistries.FLUID));
        manager.FireEvent(new BlockRegistryFinalizedEvent(BuiltInRegistries.BLOCK));
        manager.FireEvent(new EntityTypeRegistryFinalizedEvent(BuiltInRegistries.ENTITY_TYPE));
        manager.FireEvent(new ItemRegistryFinalizedEvent(BuiltInRegistries.ITEM));
        manager.FireEvent(new PotionRegistryFinalizedEvent(BuiltInRegistries.POTION));
        manager.FireEvent(new ParticleTypeRegistryFinalizedEvent(BuiltInRegistries.PARTICLE_TYPE));
        manager.FireEvent(new BlockEntityTypeRegistryFinalizedEvent(BuiltInRegistries.BLOCK_ENTITY_TYPE));
        manager.FireEvent(new MenuTypeRegistryFinalizedEvent(BuiltInRegistries.MENU));
        manager.FireEvent(new EntityAttributeRegistryFinalizedEvent(BuiltInRegistries.ATTRIBUTE));
        BaseModsLib.LOGGER.info("Registry finalization events dispatched successfully.");
    }

    public static void InitializeClientSideMods()
    {
        for (IClientModInstance instance : FabricLoader.getInstance().getEntrypoints(ENTRYPOINT_CLIENT, IClientModInstance.class)) {
            BaseModsLibClient.InitializeClientSideMod(instance , EmptyModObject.INSTANCE);
        }
    }
}
