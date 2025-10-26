package com.github.mdcdi1315.basemodslib.fabric.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import net.minecraft.server.dedicated.DedicatedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public final class DedicatedMinecraftServerMixin
{
    @Inject(method = "initServer", at = @At("HEAD"))
    private void InitServerInternal(CallbackInfoReturnable<Boolean> cir) {
        BaseModsLib.Destroy();
    }
}
