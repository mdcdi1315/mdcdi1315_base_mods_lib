package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.utils.DirectlyMappedList;
import com.github.mdcdi1315.basemodslib.network.ServerBoundModInfoPacket;
import com.github.mdcdi1315.basemodslib.commands.FabricCommandsRegistrar;
import com.github.mdcdi1315.basemodslib.network.FabricBasedNetworkManager;
import com.github.mdcdi1315.basemodslib.commands.libcmd.BaseModsLibraryCommand;
import com.github.mdcdi1315.basemodslib.registries.FabricCommonRegistryItemsRegistrar;

import com.google.common.collect.ImmutableList;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.nio.file.Path;

public final class FabricModLoaderLayer
    implements IModLoaderLayer
{
    private List<String> mod_ids;
    private final boolean dev_env;
    private Path config_dir, minecraft_dir;
    private ModdingEnvironment environment;
    private Version fabric_modloader_version;
    private FabricNetworkingHandler networking_handler;

    public FabricModLoaderLayer()
    {
        var loader = FabricLoader.getInstance();
        config_dir = loader.getConfigDir();
        minecraft_dir = loader.getGameDir();
        dev_env = loader.isDevelopmentEnvironment();
        String version =
                loader.getModContainer("fabricloader")
                        .get()
                        .getMetadata()
                        .getVersion()
                        .getFriendlyString();
        var mod_containers = loader.getAllMods();
        if (mod_containers instanceof List<ModContainer> direct_list)
        {
            mod_ids = new DirectlyMappedList<>(direct_list, FabricModLoaderLayer::ModContainerToId);
        }
        else
        {
            // Slow path, adding the id's one by one to the list
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            for (var mc : mod_containers) { builder.add(mc.getMetadata().getId()); }
            mod_ids = builder.build();
        }
        environment = switch (loader.getEnvironmentType()) {
            case CLIENT -> ModdingEnvironment.CLIENT;
            case SERVER -> ModdingEnvironment.SERVER;
        };

        try {
            fabric_modloader_version = Version.Parse(version);
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve Fabric version due to an exception. Setting version values to 0,0.", e);
            fabric_modloader_version = new Version(0 , 0);
        }

        networking_handler = new FabricNetworkingHandler(FabricModLoaderLayer::ServerModInfoPacketHandler);

        var cmd_register = new FabricCommandsRegistrar(BaseModsLib.MOD_ID);
        BaseModsLibraryCommand.InitializeLibraryCommandSupport(cmd_register);
        cmd_register.RegistrationFinalized();

        ServerLifecycleEvents.SERVER_STOPPED.register(this::OnServerStopped);
        ServerLifecycleEvents.SERVER_STOPPING.register(FabricModLoaderLayer::OnServerStopping);
        ServerLifecycleEvents.SERVER_STARTING.register(this::OnServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(FabricModLoaderLayer::OnServerStarted);
    }

    private void OnServerStarting(MinecraftServer msr)
    {
        // Server env starts only once, make sure to finalize the library.
        if (environment == ModdingEnvironment.SERVER) { BaseModsLib.Destroy(); }
        EventManager.FireEventSafe(new ServerStartingEvent(msr));
    }

    private void OnServerStopped(MinecraftServer msr)
    {
        EventManager.FireEventSafe(new ServerStoppedEvent(msr));
        if (environment == ModdingEnvironment.SERVER) { BaseModsLib.DestroySelf(); }
    }

    private static void OnServerStopping(MinecraftServer msr) { EventManager.FireEventSafe(new ServerStoppingEvent(msr)); }

    private static void OnServerStarted(MinecraftServer msr) { EventManager.FireEventSafe(new ServerStartedEvent(msr)); }

    private static void ServerModInfoPacketHandler(
            FabricNetworkingHandler the_handler,
            ServerPlayer sp,
            ServerBoundModInfoPacket p
    ) {
        // Server mod networking version
        // Lookup stored networking version.
        Version found_net_version = p.Mod_ID == null ? null : the_handler.LookupVersion(p.Mod_ID);
        // If we have a null version it means that the mod is absent on server side. Check if we can continue.
        if (found_net_version == null) {
            if (!p.OptionalOnClient()) {
                // The client requires the server mod to have been implemented but that was not found. Kick the offending player from the server.
                sp.connection.disconnect(
                        Component.translatable(
                                "mdcdi1315_base_mods_lib.disconnect_mod_missing",
                                p.Mod_ID
                        )
                );
                return;
            }
        } else if (!p.OptionalOnServer() && !found_net_version.Equals(p.Mod_Network_Version)) // If the mod requires exact version, we must negotiate it.
        {
            sp.connection.disconnect(
                    Component.translatable(
                            "mdcdi1315_base_mods_lib.mod_network_version_mismatch",
                            p.Mod_ID,
                            p.Mod_Network_Version,
                            found_net_version
                    )
            );
            return;
        }
        BaseModsLib.LOGGER.info("NETWORKING_MANAGER: ModInfoPacket: Successfully negotiated mod ID {} with client version {} to server mod version {}" , p.Mod_ID , p.Mod_Network_Version , found_net_version == null ? "<Non-existent>" : found_net_version);
    }

    private static void Client_RegisterModInfoHandshakePacketOnServerConnection(ServerBoundModInfoPacket p) { FabricClientModLoaderLayer.RegisterModInfoPacketDispatcher(p); }

    private static String ModContainerToId(ModContainer mc) { return mc.getMetadata().getId(); }

    @Override
    public boolean IsModLoaded(String mod_id)
    {
        return FabricLoader
                .getInstance()
                .getModContainer(mod_id)
                .isPresent();
    }

    @Override
    @SuppressWarnings("OptionalIsPresent")
    public IModResourceLookup GetResourceLookupByID(String mod_id)
    {
        Optional<ModContainer> mc = FabricLoader.getInstance().getModContainer(mod_id);
        return mc.isPresent() ? new FabricModResourceLookup(mc.get(), true) : null;
    }

    @Override
    @SuppressWarnings("OptionalIsPresent")
    public IModResourceLookup GetBMLResourceLookup()
    {
        Optional<ModContainer> mc = FabricLoader.getInstance().getModContainer(BaseModsLib.MOD_ID);
        return mc.isPresent() ? new FabricModResourceLookup(mc.get(), false) : null;
    }

    @Override
    public List<String> GetLoadedMods() { return mod_ids; }

    @Override
    public String GetModLoaderBranding() { return "Fabric"; }

    @Override
    public Path GetMinecraftDirectory() { return minecraft_dir; }

    @Override
    public Path GetConfigurationDirectory() { return config_dir; }

    @Override
    public boolean IsDevelopmentEnvironmentBuild() { return dev_env; }

    @Override
    public ModdingEnvironment GetEnvironment() { return environment; }

    @Override
    public Version GetModLoaderVersion() { return fabric_modloader_version; }

    @Override
    public void Dispose()
    {
        this.mod_ids = null;
        this.config_dir = null;
        this.environment = null;
        this.minecraft_dir = null;
        if (this.networking_handler != null)
        {
            this.networking_handler.Dispose();
            this.networking_handler = null;
        }
        this.fabric_modloader_version = null;
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance mod_instance, Object o)
    {
        if (!(o instanceof EmptyModObject)) {
            throw new InvalidOperationException(String.format("The mod object was not of type EmptyModObject!!!!\nActual type: %s", o == null ? "<NULL>" : o.getClass().getName()));
        }

        String mod_id = mod_instance.GetModId();

        FabricCommonRegistryItemsRegistrar registrar = new FabricCommonRegistryItemsRegistrar(mod_id);

        mod_instance.RegisterBlocks(registrar);
        mod_instance.RegisterItems(registrar);
        mod_instance.RegisterBlockEntities(registrar);
        mod_instance.RegisterFluids(registrar);
        registrar.ApplyFabricModifyEntries();

        mod_instance.RegisterAlchemyRelatedObjects(registrar);
        mod_instance.RegisterWorldGenItems(registrar);
        mod_instance.RegisterRegistryItems(registrar);
        mod_instance.RegisterEntityTypes(registrar);
        mod_instance.RegisterMenuTypes(registrar);
        mod_instance.RegisterSoundObjects(registrar);

        FabricBasedNetworkManager manager = new FabricBasedNetworkManager(mod_id);

        mod_instance.InitializeNetwork(manager);

        var builder = manager.GetBuilderAndDestroy();
        manager.InitializeNetworkManager(builder);
        if (builder != null)
        {
            synchronized (EmptyModObject.INSTANCE) {
                networking_handler.RegisterVersionByPacket(manager.Mod_Info);
            }
            if (environment == ModdingEnvironment.CLIENT) {
                Client_RegisterModInfoHandshakePacketOnServerConnection(manager.Mod_Info);
            }
        }

        var cmd_register = new FabricCommandsRegistrar(mod_id);
        mod_instance.RegisterCommands(cmd_register);
        cmd_register.RegistrationFinalized();
    }
}
