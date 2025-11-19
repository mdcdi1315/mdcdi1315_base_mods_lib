package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.DotNetLayer.System.Func2;
import com.github.mdcdi1315.DotNetLayer.System.Action1;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.ForgeUtils;
import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModLoaderLayer;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.eventapi.mods.registries.*;
import com.github.mdcdi1315.basemodslib.menu.ForgeMenuTypeRegistrar;
import com.github.mdcdi1315.basemodslib.world.ForgeWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.registries.IModLoaderRegistry;
import com.github.mdcdi1315.basemodslib.eventapi.mods.CommonSetupEvent;
import com.github.mdcdi1315.basemodslib.commands.ForgeCommandRegistrar;
import com.github.mdcdi1315.basemodslib.entity.ForgeEntityTypeRegistrar;
import com.github.mdcdi1315.basemodslib.network.ForgeBasedNetworkManager;
import com.github.mdcdi1315.basemodslib.block_item.BlocksAndItemsRegistrar;
import com.github.mdcdi1315.basemodslib.registries.ForgeRegistriesRegistrar;
import com.github.mdcdi1315.basemodslib.eventapi.mods.RegistryFinalizedEvent;
import com.github.mdcdi1315.basemodslib.commands.libcmd.BaseModsLibraryCommand;
import com.github.mdcdi1315.basemodslib.registries.ForgeRegistryWrappedInRegistry;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.List;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public final class ForgeModLoaderLayer
    implements IModLoaderLayer
{
    private List<IModInfo> forge_mod_info;
    // private DisposableObjectsTracker tracker;
    private FMLJavaModLoadingContext baselibmodcontext;
    private ForgeCommandRegistrar global_command_registrar;
    private Version minecraft_version, forge_modloader_version;

    public ForgeModLoaderLayer(FMLJavaModLoadingContext baselibmodcontext) {
        forge_mod_info = ModList.get().getMods();
        this.baselibmodcontext = baselibmodcontext;
        global_command_registrar = new ForgeCommandRegistrar();
        global_command_registrar.RegisterByCommand(BaseModsLibraryCommand::new);
        minecraft_version = new Version(1 , 21, 1);
        Version fg_ver;
        try {
            fg_ver = Version.Parse(ForgeVersion.getVersion());
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve Forge version due to an exception. Setting version values to 0,0.", e);
            fg_ver = new Version(0 , 0);
        }
        forge_modloader_version = fg_ver;
        // tracker = new DisposableObjectsTracker();
        IEventBus bus = this.baselibmodcontext.getModEventBus();
        ForgeUtils.AddListener(bus, FMLCommonSetupEvent.class, this::OnCommonSetupEvent);
        ForgeUtils.AddListener(bus, FMLLoadCompleteEvent.class, this::OnModLoadingComplete);
    }

    private IEventBus GetEventBusOrFail(Object mod_object) {
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
    }

    // REGISTRY FINALIZATION BEGIN
    // The below 3 public methods are called in by the DispatchFinalizeRegistriesEventLoadingState class. See that class for more information.

    public static int RegistryFinalization_GetEventCount() { return 6; } // 6 stages in total

    private record EventManagerFire_1<T>(IForgeRegistry<T> registry, Func2<IModLoaderRegistry<T> , RegistryFinalizedEvent<T>> event_getter, Runnable increment_meter_handler)
        implements Action1<Void>
    {
        @Override
        public void action(Void obj) {
            BaseModsLib.GetEventsManager().FireEvent(event_getter.function(new ForgeRegistryWrappedInRegistry<>(registry)));
            increment_meter_handler.run();
        }
    }

    private record EventManagerFire_2<T>(IForgeRegistry<T> registry, Func2<IModLoaderRegistry<T> , RegistryFinalizedEvent<T>> event_getter, Runnable increment_meter_handler)
        implements Func2<Void, Void>
    {
        @Override
        public Void function(Void input) {
            BaseModsLib.GetEventsManager().FireEvent(event_getter.function(new ForgeRegistryWrappedInRegistry<>(registry)));
            increment_meter_handler.run();
            return input;
        }
    }

    public static CompletableFuture<Void> RegistryFinalization_GetSynchronizedTasks(CompletableFuture<Void> root, Runnable increment_meter_handler)
    {
        // The below tasks must be executed one after the other
        root = root.thenAcceptAsync(new EventManagerFire_1<>(ForgeRegistries.BLOCKS, BlockRegistryFinalizedEvent::new , increment_meter_handler));
        root = root.thenAcceptAsync(new EventManagerFire_1<>(ForgeRegistries.ITEMS, ItemRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenAcceptAsync(new EventManagerFire_1<>(ForgeRegistries.BLOCK_ENTITY_TYPES, BlockEntityTypeRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenAcceptAsync(new EventManagerFire_1<>(ForgeRegistries.FLUIDS, FluidRegistryFinalizedEvent::new, increment_meter_handler));
        return root;
    }

    public static CompletableFuture<Void> RegistryFinalization_GetParallelTasks(CompletableFuture<Void> root, Runnable increment_meter_handler)
    {
        // The below tasks can be dispatched at the same time.
        root = root.thenApplyAsync(new EventManagerFire_2<>(ForgeRegistries.ENTITY_TYPES, EntityTypeRegistryFinalizedEvent::new, increment_meter_handler));
        root = root.thenApplyAsync(new EventManagerFire_2<>(ForgeRegistries.MENU_TYPES, MenuTypeRegistryFinalizedEvent::new, increment_meter_handler));
        return root;
    }

    // REGISTRY FINALIZATION END

    private void OnCommonSetupEvent(FMLCommonSetupEvent event) {
        BaseModsLib.LOGGER.info("Common setup event realized. Dispatching common setup to implementing mods.");
        CommonSetupEvent cse = new CommonSetupEvent();
        BaseModsLib.GetEventsManager().FireEvent(cse);
        event.enqueueWork(cse::Run);
    }

    private void OnModLoadingComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(BaseModsLib::Destroy);
        event.enqueueWork(this::DestroyLayerData);
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance instance, Object mod_object) {
        IEventBus mod_event_bus = GetEventBusOrFail(mod_object);
        String mod_id = instance.GetModId();

        // Initialize sensitive things - blocks, items, registries, etc.
        BlocksAndItemsRegistrar reg = new BlocksAndItemsRegistrar(mod_id);
        instance.RegisterBlocks(reg);
        instance.RegisterBlockEntities(reg);
        instance.RegisterItems(reg);
        instance.RegisterFluids(reg);
        reg.RegisterToEventBus(mod_event_bus);
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

        ForgeMenuTypeRegistrar reg5 = new ForgeMenuTypeRegistrar(mod_id);
        instance.RegisterMenuTypes(reg5);
        reg5.RegisterToEventBus(mod_event_bus);

        instance.RegisterCommands(global_command_registrar);
    }

    @Override
    public boolean IsModLoaded(String mod_id) {
        for (var i : forge_mod_info) {
            if (i.getModId().equals(mod_id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> GetLoadedMods() {
        List<String> mod_ids = new ArrayList<>(forge_mod_info.size());
        for (var i : forge_mod_info) {
            mod_ids.add(i.getModId());
        }
        return mod_ids;
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
    public Version GetMinecraftVersion() { return minecraft_version; }

    @Override
    public Version GetModLoaderVersion() { return forge_modloader_version; }

    @Override
    public Path GetConfigurationDirectory() { return FMLPaths.CONFIGDIR.get(); }

    @Override
    public Path GetMinecraftDirectory() { return FMLPaths.GAMEDIR.get(); }

    @Override
    public void Dispose() {
        // this.tracker = null;
        this.forge_mod_info = null;
        this.minecraft_version = null;
        this.baselibmodcontext = null;
        this.forge_modloader_version = null;
        this.global_command_registrar = null;
    }
}
