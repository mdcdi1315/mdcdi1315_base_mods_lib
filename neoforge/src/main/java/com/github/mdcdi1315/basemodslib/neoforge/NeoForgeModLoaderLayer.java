package com.github.mdcdi1315.basemodslib.neoforge;

import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Version;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModLoaderLayer;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.block_item.BlocksAndItemsRegistrar;
import com.github.mdcdi1315.basemodslib.commands.NeoForgeCommandRegistrar;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;

import com.github.mdcdi1315.basemodslib.network.NeoForgeNetworkBuilder;
import com.github.mdcdi1315.basemodslib.network.NeoForgeNetworkingManager;
import com.github.mdcdi1315.basemodslib.registries.NeoForgeRegistriesRegistrar;
import com.github.mdcdi1315.basemodslib.world.NeoForgeWorldGenRegistrar;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.List;
import java.nio.file.Path;
import java.util.ArrayList;

public final class NeoForgeModLoaderLayer
    implements IModLoaderLayer
{
    private final IEventBus event_bus;
    private final List<IModInfo> mods;
    private final Version minecraft_version, neoforge_version;
    private final NeoForgeCommandRegistrar global_command_registrar;

    public NeoForgeModLoaderLayer(IEventBus event_bus) {
        this.event_bus = event_bus;
        mods = ModList.get().getMods();
        minecraft_version = new Version(1, 21, 5);
        global_command_registrar = new NeoForgeCommandRegistrar();
        Version fg_ver;
        try {
            fg_ver = Version.Parse(FMLLoader.getCurrent().getVersionInfo().neoForgeVersion());
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve NeoForge version due to an exception. Setting version values to 0,0.", e);
            fg_ver = new Version(0 , 0);
        }
        neoforge_version = fg_ver;
        this.event_bus.addListener(NeoForgeModLoaderLayer::OnModLoadingCompleteEvent);
    }

    private static IEventBus GetEventBusOrFail(Object mod_object) {
        try {
            return (IEventBus) mod_object;
        } catch (ClassCastException cce) {
            throw new InvalidOperationException(String.format("The mod object was not of type IEventBus!!!!\nActual type: %s", mod_object.getClass().getName()));
        }
    }

    private static void OnModLoadingCompleteEvent(FMLLoadCompleteEvent event) {
        event.enqueueWork(BaseModsLib::Destroy);
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
        reg_1.RegisterToEventBus(mod_event_bus);

        NeoForgeRegistriesRegistrar reg_2 = new NeoForgeRegistriesRegistrar(mod_id);
        instance.RegisterRegistryItems(reg_2);
        reg_2.RegisterToEventBus(mod_event_bus);

        NeoForgeWorldGenRegistrar reg_3 = new NeoForgeWorldGenRegistrar(mod_id);
        instance.RegisterWorldGenItems(reg_3);
        reg_3.RegisterToEventBus(mod_event_bus);

        // Initialize non-sensitive things, but do still need to be done after all sensitive things have completed.
        NeoForgeNetworkingManager reg_4 = new NeoForgeNetworkingManager();
        instance.InitializeNetwork(reg_4);
        var builder = reg_4.GetBuilderAndDestroy();
        if (builder != null) {
            ((NeoForgeNetworkBuilder)builder).Build(mod_event_bus);
        }

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
        List<String> mod_ids = new ArrayList<>(mods.size());
        for (var i : mods) {
            mod_ids.add(i.getModId());
        }
        return mod_ids;
    }

    @Override
    public ModdingEnvironment GetEnvironment() {
        return switch (FMLEnvironment.getDist()) {
            case CLIENT -> ModdingEnvironment.CLIENT;
            case DEDICATED_SERVER -> ModdingEnvironment.SERVER;
        };
    }

    @Override
    public String GetModLoaderBranding() {
        return "NeoForge";
    }

    @Override
    public Version GetMinecraftVersion() {
        return minecraft_version;
    }

    @Override
    public Version GetModLoaderVersion() {
        return neoforge_version;
    }

    @Override
    public Path GetConfigurationDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
