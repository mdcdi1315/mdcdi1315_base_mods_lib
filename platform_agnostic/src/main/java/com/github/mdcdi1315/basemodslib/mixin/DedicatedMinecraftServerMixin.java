package com.github.mdcdi1315.basemodslib.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import net.minecraft.server.dedicated.DedicatedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServer.class)
public class DedicatedMinecraftServerMixin
{
    @Inject(at = @At("HEAD") , method = "onServerExit")
    private void OnServerExiting(CallbackInfo ci) {
        BaseModsLib.LOGGER.info("Stopping mdcdi1315's Base Mods Library.");
        BaseModsLib.DestroySelf();
    }
}
