package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStartedEvent;
import com.github.mdcdi1315.basemodslib.eventapi.client.ClientStoppingEvent;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin
{
    @Inject(method = "destroy", at = @At("HEAD"))
    private void OnDestroy(CallbackInfo ci) {
        BaseModsLib.GetEventsManager().FireEvent(new ClientStoppingEvent((Minecraft) ((Object)this)));
        BaseModsLib.LOGGER.info("Stopping mdcdi1315's Base Mods Library.");
        BaseModsLib.DestroySelf();
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void OnRun(CallbackInfo ci) {
        BaseModsLib.GetEventsManager().FireEvent(new ClientStartedEvent((Minecraft) ((Object)this)));
    }
}
