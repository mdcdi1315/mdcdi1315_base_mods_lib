package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.Version;

import com.github.mdcdi1315.basemodslib.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;
import com.github.mdcdi1315.basemodslib.sounds.NeoForgeSoundRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.mods.CommonSetupEvent;
import com.github.mdcdi1315.basemodslib.menu.NeoForgeMenuTypeRegistrar;
import com.github.mdcdi1315.basemodslib.network.NeoForgeNetworkBuilder;
import com.github.mdcdi1315.basemodslib.world.NeoForgeWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.alchemy.NeoForgeAlchemyRegistrar;
import com.github.mdcdi1315.basemodslib.network.NeoForgeNetworkingManager;
import com.github.mdcdi1315.basemodslib.commands.NeoForgeCommandRegistrar;
import com.github.mdcdi1315.basemodslib.block_item.BlocksAndItemsRegistrar;
import com.github.mdcdi1315.basemodslib.entity.NeoForgeEntityTypeRegistrar;
import com.github.mdcdi1315.basemodslib.commands.libcmd.BaseModsLibraryCommand;
import com.github.mdcdi1315.basemodslib.registries.NeoForgeRegistriesRegistrar;

import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.nio.file.Path;
import java.util.Optional;

public final class NeoForgeModLoaderLayer
    implements IModLoaderLayer
{
    private IEventBus event_bus;
    private Version neoforge_version;
    // Boolean tracking down whether mod loading has been actually completed.
    // Helps to avoid calling the bake callbacks more than one times.
    // See NeoForgeUtils class for the usage of this.
    public static boolean mod_loading_complete;

    public NeoForgeModLoaderLayer(IEventBus event_bus)
    {
        this.event_bus = event_bus;
        mod_loading_complete = false;
        Version fg_ver;
        try {
            fg_ver = Version.Parse(FMLLoader.versionInfo().neoForgeVersion());
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve NeoForge version due to an exception. Setting version values to 0,0.", e);
            fg_ver = new Version(0 , 0);
        }
        neoforge_version = fg_ver;

        // Setup event bus listeners
        NeoForgeUtils.AddListener(this.event_bus, FMLCommonSetupEvent.class, NeoForgeModLoaderLayer::OnCommonSetupEvent);
        NeoForgeUtils.AddListener(this.event_bus, FMLLoadCompleteEvent.class, NeoForgeModLoaderLayer::OnModLoadingCompleteEvent);
        NeoForgeUtils.AddListener(NeoForge.EVENT_BUS, net.neoforged.neoforge.event.server.ServerStartedEvent.class, NeoForgeModLoaderLayer::OnServerStarted);
        NeoForgeUtils.AddListener(NeoForge.EVENT_BUS, net.neoforged.neoforge.event.server.ServerStoppedEvent.class, NeoForgeModLoaderLayer::OnServerStopped);
        NeoForgeUtils.AddListener(NeoForge.EVENT_BUS, net.neoforged.neoforge.event.server.ServerStartingEvent.class, NeoForgeModLoaderLayer::OnServerStarting);
        NeoForgeUtils.AddListener(NeoForge.EVENT_BUS, net.neoforged.neoforge.event.server.ServerStoppingEvent.class, NeoForgeModLoaderLayer::OnServerStopping);

        // Register bake callbacks instead. This does not require a mixin, and it is OK since this will call in as appropriate.
        // Also, it is far more practical than the Forge solution.
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.ITEM, ItemRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.FLUID, FluidRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.BLOCK, BlockRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.MENU, MenuTypeRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.POTION, PotionRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.ENTITY_TYPE, EntityTypeRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.SOUND_EVENT, SoundEventRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.ATTRIBUTE, EntityAttributeRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.PARTICLE_TYPE, ParticleTypeRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.BLOCK_ENTITY_TYPE, BlockEntityTypeRegistryFinalizedEvent::new);

        // Now, register our commands as well...
        var lib_register = new NeoForgeCommandRegistrar(BaseModsLib.MOD_ID);
        BaseModsLibraryCommand.InitializeLibraryCommandSupport(lib_register);
        lib_register.RegisterToEventBus(this.event_bus);
    }

    private static void OnCommonSetupEvent(FMLCommonSetupEvent event)
    {
        BaseModsLib.LOGGER.info("Common setup event realized. Dispatching common setup to implementing mods.");
        CommonSetupEvent cse = new CommonSetupEvent();
        EventManager.FireEventSafe(cse);
        event.enqueueWork(cse::Run);
    }

    private static void OnModLoadingCompleteEvent(FMLLoadCompleteEvent event)
    {
        event.enqueueWork(BaseModsLib::Destroy);
        event.enqueueWork(NeoForgeModLoaderLayer::DestroyLayerData);
    }

    private static void OnServerStopped(net.neoforged.neoforge.event.server.ServerStoppedEvent e)
    {
        EventManager.FireEventSafe(new ServerStoppedEvent(e.getServer()));
        // In server env, we need to dispose the BML itself.
        // On servers however, it is pretty much OK to do that when the server stopped event is dispatched.
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) { BaseModsLib.DestroySelf(); }
    }

    private static void DestroyLayerData() { mod_loading_complete = true; }

    private static void OnServerStarted(net.neoforged.neoforge.event.server.ServerStartedEvent e) { EventManager.FireEventSafe(new ServerStartedEvent(e.getServer())); }

    private static void OnServerStarting(net.neoforged.neoforge.event.server.ServerStartingEvent e) { EventManager.FireEventSafe(new ServerStartingEvent(e.getServer())); }

    private static void OnServerStopping(net.neoforged.neoforge.event.server.ServerStoppingEvent e) { EventManager.FireEventSafe(new ServerStoppingEvent(e.getServer())); }

    @Override
    public boolean IsModLoaded(String mod_id)
    {
        return ModList.get().getModContainerById(mod_id).isPresent();
    }

    @Override
    @SuppressWarnings("OptionalIsPresent")
    public IModResourceLookup GetResourceLookupByID(String mod_id)
    {
        Optional<? extends ModContainer> mc = ModList.get().getModContainerById(mod_id);
        return mc.isPresent() ? new NeoForgeModResourceLookup(mc.get().getModInfo()) : null;
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
    public String GetModLoaderBranding() { return "NeoForge"; }

    @Override
    public Version GetModLoaderVersion() { return neoforge_version; }

    @Override
    public Path GetMinecraftDirectory() { return FMLPaths.GAMEDIR.get(); }

    @Override
    public Path GetConfigurationDirectory() { return FMLPaths.CONFIGDIR.get(); }

    @Override
    public IModResourceLookup GetBMLResourceLookup() { return new BMLModSpecialRLP(); }

    @Override
    public boolean IsDevelopmentEnvironmentBuild() { return !FMLEnvironment.production; }

    @Override
    public List<String> GetLoadedMods() { return new DirectlyMappedList<>(ModList.get().getMods(), IModInfo::getModId); }

    @Override
    public void Dispose()
    {
        this.event_bus = null;
        this.neoforge_version = null;
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance instance, Object mod_object)
    {
        IEventBus mod_event_bus = NeoForgeUtils.GetEventBusOrFail(mod_object);
        String mod_id = instance.GetModId();

        // Initialize sensitive things - blocks, items, registries, etc.
        BlocksAndItemsRegistrar reg_1 = new BlocksAndItemsRegistrar(mod_id);
        instance.RegisterBlocks(reg_1);
        instance.RegisterItems(reg_1);
        instance.RegisterBlockEntities(reg_1);
        instance.RegisterFluids(reg_1);
        reg_1.RegisterToEventBus(mod_event_bus);
        // tracker.AddDisposable(reg_1);

        NeoForgeRegistriesRegistrar reg_2 = new NeoForgeRegistriesRegistrar(mod_id);
        instance.RegisterRegistryItems(reg_2);
        reg_2.RegisterToEventBus(mod_event_bus);

        NeoForgeAlchemyRegistrar reg_7 = new NeoForgeAlchemyRegistrar(mod_id);
        instance.RegisterAlchemyRelatedObjects(reg_7);
        reg_7.RegisterToEventBus(mod_event_bus);

        NeoForgeWorldGenRegistrar reg_3 = new NeoForgeWorldGenRegistrar(mod_id);
        instance.RegisterWorldGenItems(reg_3);
        reg_3.RegisterToEventBus(mod_event_bus);

        NeoForgeEntityTypeRegistrar reg_5 = new NeoForgeEntityTypeRegistrar(mod_id);
        instance.RegisterEntityTypes(reg_5);
        reg_5.RegisterToEventBus(mod_event_bus);

        // Initialize non-sensitive things, but do still need to be done after all sensitive things have completed.
        NeoForgeNetworkingManager reg_4 = new NeoForgeNetworkingManager();
        instance.InitializeNetwork(reg_4);
        var builder = reg_4.GetBuilderAndDestroy();
        if (builder != null) {
            ((NeoForgeNetworkBuilder)builder).Build(mod_event_bus);
        }

        NeoForgeSoundRegistrar reg_8 = new NeoForgeSoundRegistrar(mod_id);
        instance.RegisterSoundObjects(reg_8);
        reg_8.RegisterToEventBus(mod_event_bus);

        NeoForgeMenuTypeRegistrar reg_6 = new NeoForgeMenuTypeRegistrar(mod_id);
        instance.RegisterMenuTypes(reg_6);
        reg_6.RegisterToEventBus(mod_event_bus);

        var reg_9 = new NeoForgeCommandRegistrar(mod_id);
        instance.RegisterCommands(reg_9);
        reg_9.RegisterToEventBus(mod_event_bus);
    }
}
