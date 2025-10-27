package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientConnectedToServerEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientDisconnectedFromServerEvent;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class ClientHandshakePacketListenerImplMixin
{
    @Accessor("connection")
    protected abstract Connection GetConnection();

    @Inject(method = "handleGameProfile", at = @At("TAIL"))
    private void OnConnectedSuccessfully(ClientboundGameProfilePacket packet, CallbackInfo ci)
    {
        BaseModsLib.LOGGER.info("Successfully connected to the server {}. Dispatching server connection event." , GetConnection().getRemoteAddress());
        BaseModsLib.GetEventsManager().FireEvent(new ClientConnectedToServerEvent());
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void OnDisconnected(Component reason, CallbackInfo ci)
    {
        BaseModsLib.LOGGER.info("Received abrupt disconnection event. Dispatching server disconnection event.");
        BaseModsLib.GetEventsManager().FireEvent(new ClientDisconnectedFromServerEvent(reason));
    }
}
