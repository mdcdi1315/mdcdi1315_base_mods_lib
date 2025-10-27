package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientDisconnectedFromServerEvent;

import net.minecraft.client.multiplayer.ClientPacketListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin
{
    @Inject(method = "close", at = @At("HEAD"))
    private void OnPlayerDisconnectedAllOK(CallbackInfo ci)
    {
        BaseModsLib.LOGGER.info("Received disconnection event. Dispatching server disconnection event.");
        BaseModsLib.GetEventsManager().FireEvent(new ClientDisconnectedFromServerEvent(null));
    }
}
