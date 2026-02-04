package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.NeoForgeUtils;
import com.github.mdcdi1315.basemodslib.IModLoaderLayer;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;
import com.github.mdcdi1315.basemodslib.sounds.NeoForgeSoundRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.mods.CommonSetupEvent;
import com.github.mdcdi1315.basemodslib.menu.NeoForgeMenuTypeRegistrar;
import com.github.mdcdi1315.basemodslib.network.NeoForgeNetworkBuilder;
import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;
import com.github.mdcdi1315.basemodslib.world.NeoForgeWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.alchemy.NeoForgeAlchemyRegistrar;
import com.github.mdcdi1315.basemodslib.network.NeoForgeNetworkingManager;
import com.github.mdcdi1315.basemodslib.commands.NeoForgeCommandRegistrar;
import com.github.mdcdi1315.basemodslib.block_item.BlocksAndItemsRegistrar;
import com.github.mdcdi1315.basemodslib.entity.NeoForgeEntityTypeRegistrar;
import com.github.mdcdi1315.basemodslib.commands.libcmd.BaseModsLibraryCommand;
import com.github.mdcdi1315.basemodslib.registries.NeoForgeRegistriesRegistrar;

import net.neoforged.fml.ModList;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.nio.file.Path;

public final class NeoForgeModLoaderLayer
    implements IModLoaderLayer
{
    private IEventBus event_bus;
    private List<IModInfo> mods;
    // Boolean tracking down whether mod loading has been actually completed.
    // Helps to avoid calling the bake callbacks more than one times.
    // See NeoForgeUtils class for the usage of this.
    public static boolean mod_loading_complete;
    // private DisposableObjectsTracker tracker;
    private Version minecraft_version, neoforge_version;
    private NeoForgeCommandRegistrar global_command_registrar;

    public NeoForgeModLoaderLayer(IEventBus event_bus) {
        this.event_bus = event_bus;
        mod_loading_complete = false;
        mods = ModList.get().getMods();
        minecraft_version = new Version(1, 21, 1);
        global_command_registrar = new NeoForgeCommandRegistrar();
        global_command_registrar.RegisterByCommand(BaseModsLibraryCommand::new);
        Version fg_ver;
        try {
            fg_ver = Version.Parse(FMLLoader.versionInfo().neoForgeVersion());
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve NeoForge version due to an exception. Setting version values to 0,0.", e);
            fg_ver = new Version(0 , 0);
        }
        neoforge_version = fg_ver;
        // tracker = new DisposableObjectsTracker();
        NeoForgeUtils.AddListener(this.event_bus, FMLCommonSetupEvent.class, this::OnCommonSetupEvent);
        NeoForgeUtils.AddListener(this.event_bus, FMLLoadCompleteEvent.class, this::OnModLoadingCompleteEvent);
        // Register bake callbacks instead. This does not require a mixin, and it is OK since this will call in as appropriate.
        // Also, it is far more practical than the Forge solution.
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.BLOCK, BlockRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.BLOCK_ENTITY_TYPE, BlockEntityTypeRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.ITEM, ItemRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.FLUID, FluidRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.ENTITY_TYPE, EntityTypeRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.MENU, MenuTypeRegistryFinalizedEvent::new);
        NeoForgeUtils.AddRegistryBakeCallback(BuiltInRegistries.SOUND_EVENT, SoundEventRegistryFinalizedEvent::new);
    }

    private static IEventBus GetEventBusOrFail(Object mod_object) {
        try {
            return (IEventBus) mod_object;
        } catch (ClassCastException cce) {
            throw new InvalidOperationException(String.format("The mod object was not of type IEventBus!!!!\nActual type: %s", mod_object.getClass().getName()));
        }
    }

    private void DestroyLayerData() {
        /*
        tracker.Dispose();
        tracker = null;
         */
        global_command_registrar = null;
        mod_loading_complete = true;
    }

    private void OnCommonSetupEvent(FMLCommonSetupEvent event) {
        BaseModsLib.LOGGER.info("Common setup event realized. Dispatching common setup to implementing mods.");
        CommonSetupEvent cse = new CommonSetupEvent();
        BaseModsLib.GetEventsManager().FireEvent(cse);
        event.enqueueWork(cse::Run);
    }

    private void OnModLoadingCompleteEvent(FMLLoadCompleteEvent event) {
        event.enqueueWork(BaseModsLib::Destroy);
        event.enqueueWork(this::DestroyLayerData);
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance instance, Object mod_object) {
        IEventBus mod_event_bus = GetEventBusOrFail(mod_object);
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

        instance.RegisterCommands(global_command_registrar);

    }

    @Override
    public boolean IsModLoaded(String mod_id) {
        for (var i : mods) {
            if (i.getModId().equals(mod_id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> GetLoadedMods() {
        return new DirectlyMappedList<>(mods, IModInfo::getModId);
    }

    @Override
    public ModdingEnvironment GetEnvironment() {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> ModdingEnvironment.CLIENT;
            case DEDICATED_SERVER -> ModdingEnvironment.SERVER;
        };
    }

    @Override
    public String GetModLoaderBranding() { return "NeoForge"; }

    @Override
    public Version GetMinecraftVersion() { return minecraft_version; }

    @Override
    public Version GetModLoaderVersion() { return neoforge_version; }

    @Override
    public Path GetConfigurationDirectory() { return FMLPaths.CONFIGDIR.get(); }

    @Override
    public Path GetMinecraftDirectory() { return FMLPaths.GAMEDIR.get(); }

    @Override
    public boolean IsDevelopmentEnvironmentBuild() { return !FMLEnvironment.production; }

    @Override
    public void Dispose() {
        // this.tracker = null;
        this.mods = null;
        this.event_bus = null;
        this.neoforge_version = null;
        this.minecraft_version = null;
        this.global_command_registrar = null;
    }
}
