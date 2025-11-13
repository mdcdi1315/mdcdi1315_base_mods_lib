package com.github.mdcdi1315.basemodslib.fabric;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.Version;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;

import com.github.mdcdi1315.basemodslib.*;
import com.github.mdcdi1315.basemodslib.utils.Action2ToRunnable;
import com.github.mdcdi1315.basemodslib.mods.IServerModInstance;
import com.github.mdcdi1315.basemodslib.network.ServerBoundModInfoPacket;
import com.github.mdcdi1315.basemodslib.commands.FabricCommandsRegistrar;
import com.github.mdcdi1315.basemodslib.network.FabricBasedNetworkManager;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerStoppingEvent;
import com.github.mdcdi1315.basemodslib.commands.libcmd.BaseModsLibraryCommand;
import com.github.mdcdi1315.basemodslib.registries.FabricCommonRegistryItemsRegistrar;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.nio.file.Path;
import java.util.ArrayList;

public final class FabricModLoaderLayer
    implements IModLoaderLayer
{
    private List<String> mod_ids;
    private Path config_dir, minecraft_dir;
    private ModdingEnvironment environment;
    private ResourceLocation mod_verifier_channel_name;
    private Map<String, Version> networking_versions_map;
    private FabricCommandsRegistrar global_commands_registrar;
    private Version minecraft_version, fabric_modloader_version;

    public FabricModLoaderLayer()
    {
        minecraft_version = new Version(1, 20, 1);

        mod_verifier_channel_name = ResourceLocation.tryBuild(BaseModsLib.MOD_ID, "mod_version_verifier");

        if (mod_verifier_channel_name == null) {
            throw new InvalidOperationException("Cannot construct the mod version verifier channel!");
        }

        mod_ids = new ArrayList<>(10);
        networking_versions_map = new HashMap<>(10);
        var loader = FabricLoader.getInstance();
        config_dir = loader.getConfigDir();
        minecraft_dir = loader.getGameDir();
        environment = switch (loader.getEnvironmentType()) {
            case CLIENT -> ModdingEnvironment.CLIENT;
            case SERVER -> ModdingEnvironment.SERVER;
        };

        Version fb_ver;
        try {
            fb_ver = Version.Parse(FabricLoaderImpl.VERSION);
        } catch (Exception e) {
            BaseModsLib.LOGGER.warn("Cannot retrieve Fabric version due to an exception. Setting version values to 0,0.", e);
            fb_ver = new Version(0 , 0);
        }

        fabric_modloader_version = fb_ver;

        global_commands_registrar = new FabricCommandsRegistrar();
        global_commands_registrar.RegisterByCommand(BaseModsLibraryCommand::new);

        ServerPlayNetworking.registerGlobalReceiver(
                mod_verifier_channel_name,
                new ChannelHandler(this::ServerModInfoPacketHandler)
        );

        if (environment == ModdingEnvironment.SERVER) {
            // On dedicated server environments, make sure to destroy the channel once the server has started shutting down.
            BaseModsLib.GetEventsManager().AddEventListener(ServerStoppingEvent.class, this::OnServerClosing);
        }
    }

    private record ChannelHandler(Action2<ServerPlayer , ServerBoundModInfoPacket> action)
        implements ServerPlayNetworking.PlayChannelHandler
    {
        @Override
        public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            server.execute(new Action2ToRunnable<>(action, player, ServerBoundModInfoPacket.Decode(buf)));
        }
    }

    private void OnServerClosing(ServerStoppingEvent sse) {
        BaseModsLib.LOGGER.debug("Unregistering mod verifier network handler.");
        ServerPlayNetworking.unregisterGlobalReceiver(mod_verifier_channel_name);
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
        FabricClientModLoaderLayer.RegisterModInfoPacketDispatcher(p , mod_verifier_channel_name);
    }

    @Override
    public void InitializeServerModInstance(IServerModInstance mod_instance, Object o) {
        if (!(o instanceof EmptyModObject)) {
            throw new InvalidOperationException(String.format("The mod object was not of type EmptyModObject!!!!\nActual type: %s", o.getClass().getName()));
        }

        String mod_id = mod_instance.GetModId();

        FabricCommonRegistryItemsRegistrar registrar = new FabricCommonRegistryItemsRegistrar(mod_id);

        mod_instance.RegisterBlocks(registrar);
        mod_instance.RegisterBlockEntities(registrar);
        mod_instance.RegisterItems(registrar);
        mod_instance.RegisterWorldGenItems(registrar);
        mod_instance.RegisterRegistryItems(registrar);
        mod_instance.RegisterEntityTypes(registrar);
        mod_instance.RegisterFluids(registrar);

        mod_instance.RegisterCommands(global_commands_registrar);

        FabricBasedNetworkManager manager = new FabricBasedNetworkManager(mod_id);

        mod_instance.InitializeNetwork(manager);

        var builder = manager.GetBuilderAndDestroy();
        manager.InitializeNetworkManager(builder);
        if (builder != null) {
            networking_versions_map.put(manager.Mod_Info.Mod_ID, manager.Mod_Info.Mod_Network_Version);
            if (environment == ModdingEnvironment.CLIENT) {
                Client_RegisterModInfoHandshakePacketOnServerConnection(manager.Mod_Info);
            }
        }


    }

    public void Dispose() {
        this.global_commands_registrar = null;
        this.mod_verifier_channel_name = null;
        this.fabric_modloader_version = null;
        this.networking_versions_map = null;
        this.minecraft_version = null;
        this.minecraft_dir = null;
        this.environment = null;
        this.config_dir = null;
        this.mod_ids = null;
    }

    @Override
    public boolean IsModLoaded(String s) {
        return mod_ids.contains(s);
    }

    @Override
    public List<String> GetLoadedMods() {
        return mod_ids;
    }

    @Override
    public ModdingEnvironment GetEnvironment() {
        return environment;
    }

    @Override
    public String GetModLoaderBranding() {
        return "Fabric";
    }

    @Override
    public Version GetMinecraftVersion() {
        return minecraft_version;
    }

    @Override
    public Version GetModLoaderVersion() {
        return fabric_modloader_version;
    }

    @Override
    public Path GetConfigurationDirectory() {
        return config_dir;
    }

    @Override
    public Path GetMinecraftDirectory() { return minecraft_dir; }
}
