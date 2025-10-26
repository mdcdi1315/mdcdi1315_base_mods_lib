package com.github.mdcdi1315.basemodslib.forge;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.IModLoaderLayer;
import com.github.mdcdi1315.basemodslib.ModdingEnvironment;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.world.ForgeWorldGenRegistrar;
import com.github.mdcdi1315.basemodslib.commands.ForgeCommandRegistrar;
import com.github.mdcdi1315.basemodslib.network.ForgeBasedNetworkManager;
import com.github.mdcdi1315.basemodslib.block_item.BlocksAndItemsRegistrar;
import com.github.mdcdi1315.basemodslib.registries.ForgeRegistriesRegistrar;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.List;
import java.nio.file.Path;
import java.util.ArrayList;

public final class ForgeModLoaderLayer
    implements IModLoaderLayer
{
    private final List<IModInfo> forge_mod_info;
    private final FMLJavaModLoadingContext baselibmodcontext;
    private final ForgeCommandRegistrar global_command_registrar;
    private final Version minecraft_version, forge_modloader_version;

    public ForgeModLoaderLayer(FMLJavaModLoadingContext baselibmodcontext) {
        forge_mod_info = ModList.get().getMods();
        this.baselibmodcontext = baselibmodcontext;
        global_command_registrar = new ForgeCommandRegistrar();
        this.baselibmodcontext.getModEventBus().addListener(ForgeModLoaderLayer::OnModLoadingComplete);
        minecraft_version = new Version(1 , 20, 1);
        Version fg_ver;
        try {
            fg_ver = Version.Parse(ForgeVersion.getVersion());
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve Forge version due to an exception. Setting version values to 0,0.", e);
            fg_ver = new Version(0 , 0);
        }
        forge_modloader_version = fg_ver;
    }

    private IEventBus GetEventBusOrFail(Object mod_object) {
        try {
            return (IEventBus) mod_object;
        } catch (ClassCastException cce) {
            throw new InvalidOperationException(String.format("The mod object was not of type IEventBus!!!!\nActual type: %s", mod_object.getClass().getName()));
        }
    }

    private static void OnModLoadingComplete(FMLLoadCompleteEvent mlce) {
        mlce.enqueueWork(BaseModsLib::Destroy);
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance instance, Object mod_object) {
        IEventBus mod_event_bus = GetEventBusOrFail(mod_object);
        String mod_id = instance.GetModId();

        // Initialize event handling - may be needed so early to assure that all events will be properly fired later.
        instance.RegisterEvents(BaseModsLib.GetEventsManager());

        // Initialize sensitive things - blocks, items, registries, etc.
        BlocksAndItemsRegistrar reg = new BlocksAndItemsRegistrar(mod_id);
        instance.RegisterBlocks(reg);
        instance.RegisterItems(reg);
        reg.RegisterToEventBus(mod_event_bus);
        ForgeRegistriesRegistrar reg2 = new ForgeRegistriesRegistrar(mod_id);
        instance.RegisterRegistryItems(reg2);
        reg2.RegisterToEventBus(mod_event_bus);
        ForgeWorldGenRegistrar reg3 = new ForgeWorldGenRegistrar(mod_id);
        instance.RegisterWorldGenItems(reg3);
        reg3.RegisterToEventBus(mod_event_bus);

        // Initialize non-sensitive things, but do still need to be done after all sensitive things have completed.
        ForgeBasedNetworkManager net_manager = new ForgeBasedNetworkManager(mod_id);
        instance.InitializeNetwork(net_manager);
        net_manager.InitializeNetworkManager(net_manager.GetBuilderAndDestroy());

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
        switch (FMLEnvironment.dist)
        {
            case CLIENT -> {
                return ModdingEnvironment.CLIENT;
            }
            case DEDICATED_SERVER -> {
                return ModdingEnvironment.SERVER;
            }
        }
        return ModdingEnvironment.UNKNOWN;
    }

    @Override
    public String GetModLoaderBranding() {
        return "Forge";
    }

    @Override
    public Version GetMinecraftVersion() {
        return minecraft_version;
    }

    @Override
    public Version GetModLoaderVersion() {
        return forge_modloader_version;
    }

    @Override
    public Path GetConfigurationDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
