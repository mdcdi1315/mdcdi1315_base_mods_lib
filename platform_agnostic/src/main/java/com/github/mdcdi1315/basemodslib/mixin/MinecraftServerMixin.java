package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.eventapi.server.ServerReloadedEvent;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Unique
    private void MDCDI1315$BML$EventAccepter(Void v) {
        EventManager.FireEventSafe(new ServerReloadedEvent((MinecraftServer) (Object) this));
    }
}
