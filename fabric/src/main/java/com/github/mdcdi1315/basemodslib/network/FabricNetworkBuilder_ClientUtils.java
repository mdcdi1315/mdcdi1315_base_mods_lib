package com.github.mdcdi1315.basemodslib.network;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.basemodslib.ClientOnlyEnvironment;
import com.github.mdcdi1315.basemodslib.utils.Action2ToRunnable;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.world.entity.player.Player;

@ClientOnlyEnvironment
public final class FabricNetworkBuilder_ClientUtils
{
    private FabricNetworkBuilder_ClientUtils() {}

    private record ClientPlayChannelInfoHandling<T extends CustomPacketPayload>(Action2<Player, T> handler)
            implements ClientPlayNetworking.PlayPayloadHandler<T>
    {
        @Override
        public void receive(T t, ClientPlayNetworking.Context context) {
            var client = context.client();
            client.execute(new Action2ToRunnable<>(this.handler, client.player, t));
        }
    }

    public static <T extends CustomPacketPayload> void RegisterClientBoundPacketInternal_ClientImpl(ClientSideNetworkPacketRegistrationInfo<T> info)
    {
        ClientPlayNetworking.registerGlobalReceiver(info.type(), new ClientPlayChannelInfoHandling<>(info.handler()));
    }
}
