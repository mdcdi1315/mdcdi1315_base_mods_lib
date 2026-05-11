package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.*;
import com.github.mdcdi1315.basemodslib.eventapi.server.*;
import com.github.mdcdi1315.basemodslib.utils.Action2ToRunnable;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.network.ServerBoundModInfoPacket;
import com.github.mdcdi1315.basemodslib.commands.FabricCommandsRegistrar;
import com.github.mdcdi1315.basemodslib.network.FabricBasedNetworkManager;
import com.github.mdcdi1315.basemodslib.commands.libcmd.BaseModsLibraryCommand;
import com.github.mdcdi1315.basemodslib.registries.FabricCommonRegistryItemsRegistrar;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

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
    private Map<String, Version> networking_versions_map;
    private FabricCommandsRegistrar global_commands_registrar;

    public FabricModLoaderLayer()
    {
        networking_versions_map = new HashMap<>(10);
        var loader = FabricLoader.getInstance();
        config_dir = loader.getConfigDir();
        minecraft_dir = loader.getGameDir();
        dev_env = loader.isDevelopmentEnvironment();
        String id, version = null;
        var l = new ArrayList<String>(10);
        for (ModContainer m : loader.getAllMods())
        {
            if ("fabricloader".equals(id = m.getMetadata().getId())) {
                version = m.getMetadata().getVersion().getFriendlyString();
            }
            l.add(id);
        }
        l.trimToSize();
        mod_ids = l;
        environment = switch (loader.getEnvironmentType()) {
            case CLIENT -> ModdingEnvironment.CLIENT;
            case SERVER -> ModdingEnvironment.SERVER;
        };

        Version fb_ver;
        try {
            fb_ver = Version.Parse(version);
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve Fabric version due to an exception. Setting version values to 0,0.", e);
            fb_ver = new Version(0 , 0);
        }

        fabric_modloader_version = fb_ver;

        var mod_verifier_type = new CustomPacketPayload.Type<ServerBoundModInfoPacket>(ServerBoundModInfoPacket.LOCATION);
        PayloadTypeRegistry.playC2S().register(mod_verifier_type , new ServerBoundModInfoPacket.NetCodec());
        ServerPlayNetworking.registerGlobalReceiver(
                mod_verifier_type,
                new ChannelHandler(this::ServerModInfoPacketHandler)
        );

        global_commands_registrar = new FabricCommandsRegistrar();
        global_commands_registrar.RegisterByCommand(BaseModsLibraryCommand::new);

        ServerLifecycleEvents.SERVER_STOPPED.register(this::OnServerStopped);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::OnServerStopping);
        ServerLifecycleEvents.SERVER_STARTING.register(this::OnServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(FabricModLoaderLayer::OnServerStarted);
    }

    @Override
    public void Dispose() {
        this.mod_ids = null;
        this.config_dir = null;
        this.environment = null;
        this.minecraft_dir = null;
        this.networking_versions_map = null;
        this.fabric_modloader_version = null;
        this.global_commands_registrar = null;
    }

    private record ChannelHandler(Action2<ServerPlayer , ServerBoundModInfoPacket> action)
        implements ServerPlayNetworking.PlayPayloadHandler<ServerBoundModInfoPacket>
    {
        @Override
        public void receive(ServerBoundModInfoPacket payload, ServerPlayNetworking.Context context) {
            context.server().execute(new Action2ToRunnable<>(action, context.player(), payload));
        }
    }

    private void OnServerStarting(MinecraftServer msr)
    {
        // Server env starts only once, make sure to finalize the library.
        if (environment == ModdingEnvironment.SERVER) {
            BaseModsLib.Destroy();
            // Since we have now reached mod loading completed stage, we can just destroy the global command registrar.
            this.global_commands_registrar = null;
        }
        BaseModsLib.GetEventsManager().FireEvent(new ServerStartingEvent(msr));
    }

    private void OnServerStopping(MinecraftServer msr)
    {
        if (environment == ModdingEnvironment.SERVER) {
            // On dedicated server environments, make sure to destroy the channel once the server has started shutting down.
            BaseModsLib.LOGGER.debug("Unregistering mod verifier network handler.");
            ServerPlayNetworking.unregisterGlobalReceiver(ServerBoundModInfoPacket.LOCATION);
        }
        BaseModsLib.GetEventsManager().FireEvent(new ServerStoppingEvent(msr));
    }

    private static void OnServerStarted(MinecraftServer msr) {
        BaseModsLib.GetEventsManager().FireEvent(new ServerStartedEvent(msr));
    }

    private void OnServerStopped(MinecraftServer msr)
    {
        BaseModsLib.GetEventsManager().FireEvent(new ServerStoppedEvent(msr));
        if (environment == ModdingEnvironment.SERVER) {
            BaseModsLib.DestroySelf();
        }
    }

    private void ServerModInfoPacketHandler(ServerPlayer sp , ServerBoundModInfoPacket p)
    {
        Version found_net_version = null; // Server mod networking version
        // Lookup stored networking version.
        for (var i : networking_versions_map.entrySet())
        {
            if (p.Mod_ID.equals(i.getKey())) {
                found_net_version = i.getValue();
                break;
            }
        }
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

    private void Client_RegisterModInfoHandshakePacketOnServerConnection(ServerBoundModInfoPacket p) {
        FabricClientModLoaderLayer.RegisterModInfoPacketDispatcher(p);
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance mod_instance, Object o) {
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

        mod_instance.RegisterCommands(global_commands_registrar);

        FabricBasedNetworkManager manager = new FabricBasedNetworkManager(mod_id);

        mod_instance.InitializeNetwork(manager);

        var builder = manager.GetBuilderAndDestroy();
        manager.InitializeNetworkManager(builder);
        if (builder != null) {
            synchronized (networking_versions_map) {
                networking_versions_map.put(manager.Mod_Info.Mod_ID, manager.Mod_Info.Mod_Network_Version);
            }
            if (environment == ModdingEnvironment.CLIENT) {
                Client_RegisterModInfoHandshakePacketOnServerConnection(manager.Mod_Info);
            }
        }
    }

    @Override
    public boolean IsModLoaded(String s) { return mod_ids.contains(s); }

    @Override
    public IModResourceLookup GetResourceLookupByID(String mod_id)
    {
        for (ModContainer m : FabricLoader.getInstance().getAllMods())
        {
            if (m.getMetadata().getId().equals(mod_id)) {
                return new FabricModResourceLookup(m);
            }
        }
        return null;
    }

    @Override
    public List<String> GetLoadedMods() { return mod_ids; }

    @Override
    public ModdingEnvironment GetEnvironment() { return environment; }

    @Override
    public String GetModLoaderBranding() { return "Fabric"; }

    @Override
    public Version GetModLoaderVersion() { return fabric_modloader_version; }

    @Override
    public Path GetConfigurationDirectory() { return config_dir; }

    @Override
    public Path GetMinecraftDirectory() { return minecraft_dir; }

    @Override
    public boolean IsDevelopmentEnvironmentBuild() { return dev_env; }
}
