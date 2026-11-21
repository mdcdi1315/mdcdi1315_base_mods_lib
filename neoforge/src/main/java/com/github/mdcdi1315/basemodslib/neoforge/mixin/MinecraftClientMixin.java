package com.github.mdcdi1315.basemodslib.neoforge.mixin;

import com.github.mdcdi1315.basemodslib.eventapi.client.ClientEventHooks;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class MinecraftClientMixin
{
    @Inject(method = "onResourceLoadFinished", at = @At("TAIL"))
    private void OnClientReloaded(CallbackInfo ci) { ClientEventHooks.ClientReloaded((Minecraft) (Object)this); }
}