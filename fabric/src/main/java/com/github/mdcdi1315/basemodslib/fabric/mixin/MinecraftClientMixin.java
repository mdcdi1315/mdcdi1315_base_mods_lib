package com.github.mdcdi1315.basemodslib.fabric.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.mods.ClientSetupEvent;
import com.github.mdcdi1315.basemodslib.eventapi.mods.CommonSetupEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class MinecraftClientMixin
{
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
    private void OnConstructingHead(GameConfig gameConfig, CallbackInfo info) {
        BaseModsLib.LOGGER.info("Common setup event realized. Dispatching common setup to implementing mods.");
        CommonSetupEvent cse = new CommonSetupEvent();
        BaseModsLib.GetEventsManager().FireEvent(cse);
        cse.Run();
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/telemetry/events/GameLoadTimesEvent;beginStep(Lnet/minecraft/client/telemetry/TelemetryProperty;)V"))
    private void OnConstructing(GameConfig gameConfig, CallbackInfo ci) {
        BaseModsLib.LOGGER.info("Client setup event realized. Dispatching client setup to implementing mods.");
        ClientSetupEvent cse = new ClientSetupEvent();
        BaseModsLib.GetEventsManager().FireEvent(cse);
        cse.Run();
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void RunInternal(CallbackInfo ci) {
        BaseModsLib.Destroy();
    }
}
