package com.github.mdcdi1315.basemodslib.forge.mixin;

import com.github.mdcdi1315.basemodslib.eventapi.client.ClientEventHooks;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class MinecraftClientMixin
{
    @Inject(method = "run", at = @At("HEAD"))
    private void OnRun(CallbackInfo ci) { ClientEventHooks.ClientStarted((Minecraft) ((Object)this)); }

    @Inject(method = "destroy", at = @At("HEAD"))
    private void OnDestroy(CallbackInfo ci) { ClientEventHooks.ClientStopping((Minecraft) ((Object)this)); }
}