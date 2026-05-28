package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;
import com.github.mdcdi1315.basemodslib.sounds.ForgeSoundRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;
import com.github.mdcdi1315.basemodslib.menu.ForgeMenuTypeRegistrar;
import com.github.mdcdi1315.basemodslib.world.ForgeWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.alchemy.ForgeAlchemyRegistrar;
import com.github.mdcdi1315.basemodslib.commands.ForgeCommandRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.mods.CommonSetupEvent;
import com.github.mdcdi1315.basemodslib.entity.ForgeEntityTypeRegistrar;
import com.github.mdcdi1315.basemodslib.network.ForgeBasedNetworkManager;
import com.github.mdcdi1315.basemodslib.block_item.BlocksAndItemsRegistrar;
import com.github.mdcdi1315.basemodslib.registries.ForgeRegistriesRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;
import com.github.mdcdi1315.basemodslib.commands.libcmd.BaseModsLibraryCommand;
import com.github.mdcdi1315.basemodslib.registries.ForgeRegistryWrappedInRegistry;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.List;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class ForgeModLoaderLayer
    implements IModLoaderLayer
{
    private List<IModInfo> forge_mod_info;
    // private DisposableObjectsTracker tracker;
    private Version forge_modloader_version;
    private ForgeCommandRegistrar global_command_registrar;

    public ForgeModLoaderLayer(FMLJavaModLoadingContext baselibmodcontext)
    {
        forge_mod_info = ModList.get().getMods();
        global_command_registrar = new ForgeCommandRegistrar();
        global_command_registrar.RegisterByCommand(BaseModsLibraryCommand::new);
        Version fg_ver;
        try {
            fg_ver = Version.Parse(ForgeVersion.getVersion());
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve Forge version due to an exception. Setting version values to 0,0.", e);
            fg_ver = new Version(0 , 0);
        }
        forge_modloader_version = fg_ver;
        IEventBus bus = baselibmodcontext.getModEventBus();
        ForgeUtils.AddListener(bus, FMLCommonSetupEvent.class, this::OnCommonSetupEvent);
        ForgeUtils.AddListener(bus, FMLLoadCompleteEvent.class, this::OnModLoadingComplete);
        ForgeUtils.AddListener(MinecraftForge.EVENT_BUS, net.minecraftforge.event.server.ServerStartedEvent.class, ForgeModLoaderLayer::OnServerStarted);
        ForgeUtils.AddListener(MinecraftForge.EVENT_BUS, net.minecraftforge.event.server.ServerStoppedEvent.class, ForgeModLoaderLayer::OnServerStopped);
        ForgeUtils.AddListener(MinecraftForge.EVENT_BUS, net.minecraftforge.event.server.ServerStartingEvent.class, ForgeModLoaderLayer::OnServerStarting);
        ForgeUtils.AddListener(MinecraftForge.EVENT_BUS, net.minecraftforge.event.server.ServerStoppingEvent.class, ForgeModLoaderLayer::OnServerStopping);
    }

    private IEventBus GetEventBusOrFail(Object mod_object) {
        try {
            return (IEventBus) mod_object;
        } catch (ClassCastException cce) {
            throw new InvalidOperationException(String.format("The mod object was not of type IEventBus!!!!\nActual type: %s", mod_object.getClass().getName()));
        }
    }

    private void DestroyInternalResources() { global_command_registrar = null; }

    // REGISTRY FINALIZATION BEGIN
    // The below 3 public methods are called in by the DispatchFinalizeRegistriesEventLoadingState class. See that class for more information.

    public static int RegistryFinalization_GetEventCount() { return 10; } // 10 stages in total

    private record EventManagerFire<T>(IForgeRegistry<T> registry, Func2<IModLoaderRegistry<T>, RegistryFinalizedEvent<T>> event_getter, Runnable increment_meter_handler)
            implements Func2<Void, Void>
    {
        @Override
        public Void function(Void input) {
            BaseModsLib.LOGGER.info("Dispatching registry finalized event for {}", registry.getRegistryName());
            EventManager.FireEventSafe(event_getter.function(new ForgeRegistryWrappedInRegistry<>(registry)));
            increment_meter_handler.run();
            BaseModsLib.LOGGER.info("Finished dispatching registry finalized event for {}", registry.getRegistryName());
            return input;
        }
    }

    public static CompletableFuture<Void> RegistryFinalization_GetTasks(CompletableFuture<Void> root, Runnable increment_meter_handler)
    {
        // The below tasks are the registry finalized events.
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.SOUND_EVENTS, SoundEventRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.FLUIDS, FluidRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.BLOCKS, BlockRegistryFinalizedEvent::new , increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.ENTITY_TYPES, EntityTypeRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.ITEMS, ItemRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.POTIONS, PotionRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.PARTICLE_TYPES, ParticleTypeRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.BLOCK_ENTITY_TYPES, BlockEntityTypeRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.MENU_TYPES, MenuTypeRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire<>(ForgeRegistries.ATTRIBUTES, EntityAttributeRegistryFinalizedEvent::new, increment_meter_handler));
        return root;
    }

    // REGISTRY FINALIZATION END

    private static void OnServerStarting(net.minecraftforge.event.server.ServerStartingEvent e) {
        EventManager.FireEventSafe(new ServerStartingEvent(e.getServer()));
    }

    private static void OnServerStopping(net.minecraftforge.event.server.ServerStoppingEvent e) {
        EventManager.FireEventSafe(new ServerStoppingEvent(e.getServer()));
    }

    private static void OnServerStopped(net.minecraftforge.event.server.ServerStoppedEvent e) {
        EventManager.FireEventSafe(new ServerStoppedEvent(e.getServer()));
        // In server env, we need to dispose the BML itself.
        // On servers however, it is pretty much OK to do that when the server stopped event is dispatched.
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) { BaseModsLib.DestroySelf(); }
    }

    private static void OnServerStarted(net.minecraftforge.event.server.ServerStartedEvent e) {
        EventManager.FireEventSafe(new ServerStartedEvent(e.getServer()));
    }

    private void OnCommonSetupEvent(FMLCommonSetupEvent event) {
        BaseModsLib.LOGGER.info("Common setup event realized. Dispatching common setup to implementing mods.");
        CommonSetupEvent cse = new CommonSetupEvent();
        EventManager.FireEventSafe(cse);
        event.enqueueWork(cse::Run);
    }

    private void OnModLoadingComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(BaseModsLib::Destroy);
        event.enqueueWork(this::DestroyInternalResources);
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance instance, Object mod_object)
    {
        IEventBus mod_event_bus = GetEventBusOrFail(mod_object);
        String mod_id = instance.GetModId();

        // Initialize sensitive things - blocks, items, registries, etc.
        BlocksAndItemsRegistrar reg = new BlocksAndItemsRegistrar(mod_id);
        instance.RegisterBlocks(reg);
        instance.RegisterBlockEntities(reg);
        instance.RegisterItems(reg);
        instance.RegisterFluids(reg);
        reg.RegisterToEventBus(mod_event_bus);
        ForgeAlchemyRegistrar reg6 = new ForgeAlchemyRegistrar(mod_id);
        instance.RegisterAlchemyRelatedObjects(reg6);
        reg6.RegisterToEventBus(mod_event_bus);
        // tracker.AddDisposable(reg);
        ForgeRegistriesRegistrar reg2 = new ForgeRegistriesRegistrar(mod_id);
        instance.RegisterRegistryItems(reg2);
        reg2.RegisterToEventBus(mod_event_bus);
        ForgeWorldGenRegistrar reg3 = new ForgeWorldGenRegistrar(mod_id);
        instance.RegisterWorldGenItems(reg3);
        reg3.RegisterToEventBus(mod_event_bus);
        ForgeEntityTypeRegistrar reg4 = new ForgeEntityTypeRegistrar(mod_id);
        instance.RegisterEntityTypes(reg4);
        reg4.RegisterToEventBus(mod_event_bus);

        // Initialize non-sensitive things, but do still need to be done after all sensitive things have completed.
        ForgeBasedNetworkManager net_manager = new ForgeBasedNetworkManager(mod_id);
        instance.InitializeNetwork(net_manager);
        net_manager.InitializeNetworkManager(net_manager.GetBuilderAndDestroy());

        ForgeSoundRegistrar reg7 = new ForgeSoundRegistrar(mod_id);
        instance.RegisterSoundObjects(reg7);
        reg7.RegisterToEventBus(mod_event_bus);

        ForgeMenuTypeRegistrar reg5 = new ForgeMenuTypeRegistrar(mod_id);
        instance.RegisterMenuTypes(reg5);
        reg5.RegisterToEventBus(mod_event_bus);

        instance.RegisterCommands(global_command_registrar);
    }

    @Override
    public boolean IsModLoaded(String mod_id)
    {
        for (var i : forge_mod_info) {
            if (i.getModId().equals(mod_id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IModResourceLookup GetResourceLookupByID(String mod_id)
    {
        for (var i : forge_mod_info) {
            if (i.getModId().equals(mod_id)) {
                return new ForgeModResourceLookup(i);
            }
        }
        return null;
    }

    @Override
    public ModdingEnvironment GetEnvironment()
    {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> ModdingEnvironment.CLIENT;
            case DEDICATED_SERVER -> ModdingEnvironment.SERVER;
        };
    }

    @Override
    public String GetModLoaderBranding() { return "Forge"; }

    @Override
    public Path GetMinecraftDirectory() { return FMLPaths.GAMEDIR.get(); }

    @Override
    public Version GetModLoaderVersion() { return forge_modloader_version; }

    @Override
    public Path GetConfigurationDirectory() { return FMLPaths.CONFIGDIR.get(); }

    @Override
    public boolean IsDevelopmentEnvironmentBuild() { return !FMLEnvironment.production; }

    @Override
    public List<String> GetLoadedMods() { return new DirectlyMappedList<>(forge_mod_info, IModInfo::getModId); }

    @Override
    public void Dispose()
    {
        this.global_command_registrar = null;
        this.forge_modloader_version = null;
        this.forge_mod_info = null;
    }
}
