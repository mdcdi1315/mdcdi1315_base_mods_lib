package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerStartedEvent;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerStartingEvent;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerStoppingEvent;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerReloadedEvent;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin
{
    @Inject(method = "reloadResources", at = @At("RETURN"))
    private void reloadResources(Collection<String> p_129862_, CallbackInfoReturnable<CompletableFuture<Void>> callback_info) {
        callback_info.getReturnValue().thenAccept(this::MDCDI1315$BML$EventAccepter);
    }

    @Inject(method = "stopServer" , at = @At("HEAD"))
    private void OnServerStopping(CallbackInfo callback_info) {
        BaseModsLib.GetEventsManager().FireEvent(new ServerStoppingEvent((MinecraftServer) ((Object) this)));
    }

    @Inject(method = "runServer", at = @At("HEAD"))
    private void OnServerStarting(CallbackInfo callback_info) {
        BaseModsLib.GetEventsManager().FireEvent(new ServerStartingEvent((MinecraftServer) ((Object) this)));
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z", ordinal = 0))
    private void OnServerStarted(CallbackInfo callback_info) {
        BaseModsLib.GetEventsManager().FireEvent(new ServerStartedEvent((MinecraftServer) ((Object) this)));
    }

    @Unique
    private void MDCDI1315$BML$EventAccepter(Void v) {
        BaseModsLib.GetEventsManager().FireEvent(new ServerReloadedEvent((MinecraftServer) ((Object) this) ));
    }
}
