package com.github.mdcdi1315.basemodslib.fabric.mixin;

import com.github.mdcdi1315.basemodslib.BaseModsLib;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public final class MinecraftClientMixin
{
    @Inject(method = "run", at = @At("HEAD"))
    private void RunInternal(CallbackInfo ci) {
        BaseModsLib.Destroy();
    }
}
