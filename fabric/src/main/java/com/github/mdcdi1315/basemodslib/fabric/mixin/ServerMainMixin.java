package com.github.mdcdi1315.basemodslib.fabric.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;
import com.github.mdcdi1315.basemodslib.eventapi.EventManager;
import com.github.mdcdi1315.basemodslib.eventapi.mods.CommonSetupEvent;

import net.minecraft.server.Main;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public final class ServerMainMixin
{
    // Forge/Neo inject somewhere around before the Dedicated Server Settings object is created.
    // So, inject common setup there instead.
    // Server setup falls here as well.
    @Inject(method = "main" , at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServerSettings;<init>(Ljava/nio/file/Path;)V"))
    private static void OnMain(String[] strings, CallbackInfo ci) {
        BaseModsLib.LOGGER.info("Common setup event realized. Dispatching common setup to implementing mods.");
        CommonSetupEvent cse = new CommonSetupEvent();
        EventManager.FireEventSafe(cse);
        cse.Run();
    }
}
